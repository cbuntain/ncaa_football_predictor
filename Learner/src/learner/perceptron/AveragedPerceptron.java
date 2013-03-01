/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package learner.perceptron;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import learner.AbstractLearner;
import learner.Sample;
import org.apache.commons.math3.linear.*;

/**
 *
 * @author cbuntain
 */
public class AveragedPerceptron extends AbstractLearner {
    
    private static final int DEFAULT_ITERATIONS = 100;
    
    private ArrayRealVector mW; // Weight vector
    private ArrayRealVector mU; // Cached weight vector
    private Double mB;  // Bias
    private Double mBeta;   // Cached bias
    private Double mC;  // Counter
    
    private Random mRandom; // Needed for shuffling data set in training
    
    /*
     * Disallow access to the default constructor. We need a dimension!
     */
    protected AveragedPerceptron() {
        
    }
    
    public AveragedPerceptron(int dimension, Random prng) {
        mW = new ArrayRealVector(dimension, 0.0);
        mU = new ArrayRealVector(dimension, 0.0);
        mB = 0.;
        mBeta = 0.;
        mC = 1.;
        
        mRandom = prng;
    }
    
    @Override
    public void train(List<Sample> trainingSet) {
        train(trainingSet, DEFAULT_ITERATIONS);
    }
    
    public boolean train(List<Sample> trainingSet, int iterations) {
        
        boolean status = true;
        
        // Begin the multistage training process
        for ( int i=0; i<iterations; i++ ) {
            
            // Copy and shuffle the training set between iterations
            ArrayList<Sample> copiedTrainingSet = 
                    new ArrayList<Sample>(trainingSet);
            Collections.shuffle(copiedTrainingSet, mRandom);
            
            if ( trainAux(copiedTrainingSet) == false ) {
                System.err.printf("Training failed at iteration %d!\n", i);
                status = false;
                break;
            }
            
        }
        
        /* Update the weights with the cached values */
        mW = mW.subtract(mU.mapDivide(mC));
        mB = mB - mBeta/mC;

        return status;
    }
    
    protected boolean trainAux(List<Sample> trainingSet) {
        
        boolean status = true;
        
        for (final Sample x: trainingSet ) {
            
            double y = x.getLabel();
            
            double sum = y * (mW.dotProduct(x) + mB);
            
            /* Mistake made in prediction! */
            if ( sum <= 0 ) {
                mW = mW.add(x.mapMultiply(y));
                mB = mB + y;
                
                mU = mU.add(x.mapMultiply(y*mC));
                mBeta = mBeta + y*mC;
            }
            
            mC++;
        }
        
        return status;
    }

    @Override
    public int classify(Sample x) {
        
        int foundLabel = 0;
        
        double sum = mW.dotProduct(x) + mB;
        
        if ( sum > 0 ) {
            foundLabel = 1;
        } else {
            foundLabel = -1;
        }
        
        return foundLabel;
    }
}
