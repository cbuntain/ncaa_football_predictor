/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package learner.ann;

import java.util.List;
import java.util.Random;
import learner.AbstractLearner;
import learner.Sample;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.simple.EncogUtility;

/**
 *
 * @author cbuntain
 */
public class AnnProblem extends AbstractLearner {
    
    public static int MAX_ITERATIONS = 100000;
    public static double EPS = 0.1;
    public static int MINUTES_OF_TRAINING = 60;
    
    private int mInputDimension;
    private int mHiddenLayers;
    private Random mPrng;
    private BasicNetwork mNet;
    
    public AnnProblem(int dim, int numHidden, Random prng) {
        
        mInputDimension = dim;
        mHiddenLayers = numHidden;
        mPrng = prng;
        
        mNet = buildNetwork(dim, numHidden, prng);
        
    }
    
    private BasicNetwork buildNetwork(int dim, int hidden, Random prng) {
        
        BasicNetwork net = new BasicNetwork();
        
        net.addLayer(new BasicLayer(null, true, dim));  // Input layer
        net.addLayer(new BasicLayer(new ActivationTANH(), true, hidden));   // Hidden layers
        net.addLayer(new BasicLayer(new ActivationTANH(), true, hidden));   // Hidden layers
        net.addLayer(new BasicLayer(new ActivationTANH(), true, hidden));   // Hidden layers
        net.addLayer(new BasicLayer(new ActivationTANH(), true, 1));   // Output layer
        
        // Solidify structure
        net.getStructure().finalizeStructure();
        
        // Randomize weights
        net.reset(prng.nextInt());
        
        return net;
    }
    
    @Override
    public void train(List<Sample> trainingSet) {
        
        BasicMLDataSet set = buildDataSet(trainingSet, true);
        
        ResilientPropagation trainer = 
                new ResilientPropagation(mNet, set);
        
        trainer.setThreadCount(5);
        
        EncogUtility.trainConsole(trainer, mNet, set, MINUTES_OF_TRAINING);
        
//        // Train
//        int i=0;
//        for ( i=0; i<MAX_ITERATIONS; i++ ) {
//            
//            trainer.iteration();
//            
//            double error = trainer.getError();
//            
//            System.out.printf("ITER [%d] - Error: %f\n", i, error);
//            
//            if ( error < EPS ) {
//                break;
//            }
//        }
//        
//        if ( i == MAX_ITERATIONS ) {
//            System.err.println("Reached max iterations...");
//        }
    }
    
    private BasicMLDataSet buildDataSet(List<Sample> data, boolean withLabels) {
        
        BasicMLDataSet mlDataSet = new BasicMLDataSet();
        
        for ( Sample x : data ) {
            
            BasicMLData dataPoint = new BasicMLData(x.toArray());
            
            if ( withLabels == true ) {
                double[] idealArr = new double[1];
                idealArr[0] = x.getLabel();

                BasicMLData idealPoint = new BasicMLData(idealArr);

                mlDataSet.add(dataPoint, idealPoint);
            } else {
                mlDataSet.add(dataPoint);
            }
        }
        
        return mlDataSet;
    }
    
    @Override
    public int classify(Sample x) {
        BasicMLData dataPoint = new BasicMLData(x.toArray());
        
        MLData output = mNet.compute(dataPoint);
        
        double outputValue = output.getData()[0];
        
        int result = 0;
        
        if ( outputValue <= 0.0 ) {
            result = -1;
        } else {
            result = 1;
        }
        
        return result;
    }
}
