/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package learner.svm;

import edu.berkeley.compbio.jlibsvm.ImmutableSvmParameter;
import edu.berkeley.compbio.jlibsvm.ImmutableSvmParameterGrid;
import edu.berkeley.compbio.jlibsvm.binary.BinaryClassificationSVM;
import edu.berkeley.compbio.jlibsvm.binary.BinaryModel;
import edu.berkeley.compbio.jlibsvm.binary.C_SVC;
import edu.berkeley.compbio.jlibsvm.binary.MutableBinaryClassificationProblemImpl;
import edu.berkeley.compbio.jlibsvm.kernel.GaussianRBFKernel;
import edu.berkeley.compbio.jlibsvm.kernel.KernelFunction;
import edu.berkeley.compbio.jlibsvm.kernel.LinearKernel;
import edu.berkeley.compbio.jlibsvm.scaler.LinearScalingModelLearner;
import edu.berkeley.compbio.jlibsvm.util.SparseVector;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import learner.AbstractLearner;
import learner.Sample;

/**
 *
 * @author cbuntain
 */
public class SvmProblemJ extends AbstractLearner {
    
    private ImmutableSvmParameter<Integer, SparseVector> mSvmParams;
    private MutableBinaryClassificationProblemImpl<Integer, SparseVector> mSvmProblem;
    private BinaryClassificationSVM<Integer, SparseVector> mSvm;
    private BinaryModel<Integer, SparseVector> mModel;
    
    public SvmProblemJ() {
        
        mSvmParams = buildParameters(); // Default parameters
        mSvm = new C_SVC<Integer, SparseVector>(); // General C SVC implementation
        mModel = null;
    }
    
    private ImmutableSvmParameter<Integer, SparseVector> buildParameters() {
        
        ImmutableSvmParameterGrid.Builder<Integer, SparseVector> builder = 
                new ImmutableSvmParameterGrid.Builder<Integer, SparseVector>();
        
        /* Default parameters */
        builder.nu = 0.5f;
        builder.cache_size = 100;
        builder.eps = 0.1f;
        builder.p = 0.1f;
        builder.shrinking = true;
        builder.probability = false;
        builder.redistributeUnbalancedC = true;
        builder.normalizeL2 = true;
        
        /* Set C */
        builder.Cset = new ArrayList<Float>();
        
        for ( int i=0; i<10; i++ ) {
            builder.Cset.add(new Double(java.lang.Math.pow(2, i)).floatValue());
        }
        
        /* We'll start with a linear kernel */
        builder.kernelSet = new HashSet<KernelFunction<SparseVector>>();
        builder.kernelSet.add(new LinearKernel());
        
        for ( int i=0; i<10; i++ ) {
            double gamma = 0.00001 * java.lang.Math.pow(10, i);
            
            builder.kernelSet.add(new GaussianRBFKernel(new Double(gamma).floatValue()));
        }
        
        /* Scaling */
        builder.scalingModelLearner = new LinearScalingModelLearner(100, true);
        
        builder.build();
        ImmutableSvmParameterGrid<Integer, SparseVector> params = 
                new ImmutableSvmParameterGrid<Integer, SparseVector>(builder);
        
        return params;
    }
 
    @Override
    public void train(List<Sample> trainingSet) {
        
        /* Build a new binary problem where Integer is the label class */
        mSvmProblem = new MutableBinaryClassificationProblemImpl<Integer, SparseVector>
                (Integer.class, trainingSet.size());
        
        /* Add the training data */
        for ( Sample x : trainingSet ) {
            
            SparseVector v = getSparseFromSample(x);
            mSvmProblem.addExample(v, x.getLabel());
        }
        
        mSvmProblem.setupLabels();
        
        /* Validate the parameters we set */
        mSvm.validateParam(mSvmParams);
        
        /* Train the model */
        mModel = mSvm.train(mSvmProblem, mSvmParams);
    }
    
    @Override
    public int classify(Sample x) {
        
        if ( mModel == null ) {
            return Integer.MIN_VALUE;
        }
        
        return mModel.predictLabel(getSparseFromSample(x));
    }
    
    private SparseVector getSparseFromSample(Sample x) {
        SparseVector v = new SparseVector(x.getDimension());
            
        for ( int i=0; i<x.getDimension(); i++ ) {
            v.indexes[i] = i;
            v.values[i] = new Double(x.getEntry(i)).floatValue();
        }
        
        return v;
    }
}
