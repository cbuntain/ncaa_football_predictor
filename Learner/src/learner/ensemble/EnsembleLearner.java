/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package learner.ensemble;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import learner.AbstractLearner;
import learner.Accurator;
import learner.Learner;
import learner.Pair;
import learner.Sample;
import learner.ann.AnnProblem;
import learner.perceptron.AveragedPerceptron;
import learner.svm.SvmProblemJ;

/**
 *
 * @author cbuntain
 */
public class EnsembleLearner extends AbstractLearner {
    
    private int mDimension;
    private Random mPrng;
    
    private List<Pair<Learner,Double>> mLearners;
    
    public EnsembleLearner(int dimension, Random prng) {
        mDimension = dimension;
        mPrng = prng;
        
        AveragedPerceptron percept = new AveragedPerceptron(mDimension, prng);
        SvmProblemJ svm = new SvmProblemJ();
        AnnProblem net = new AnnProblem(mDimension, 
                    mDimension,
                    prng);
        
        mLearners = new ArrayList<Pair<Learner, Double>>();
        mLearners.add(new Pair<Learner, Double>(percept, 0.0));
        mLearners.add(new Pair<Learner, Double>(svm, 0.0));
        mLearners.add(new Pair<Learner, Double>(net, 0.0));
    }
    
    @Override
    public void train(List<Sample> set) {
        
        int splitIndex = new Double(set.size() * 0.8).intValue();
        
        List<Sample> train = set.subList(0, splitIndex);
        List<Sample> tune = set.subList(splitIndex, set.size());
        
        trainAux(train);
        tuneAux(tune);
    }
    
    private void trainAux(List<Sample> train) {
     
        ArrayList<Thread> threads = new ArrayList<Thread>();
     
        for ( Pair<Learner, Double> p : mLearners ) {
            Learner l = p.get1();
            
            Thread t = new Thread(new TrainThread(l, train));
            
            threads.add(t);
            t.start();
        }
                
        // Join
        try {
            for ( Thread t : threads ) {
                t.join();
            }
        } catch (InterruptedException ex) {
            System.err.println("ERROR on joining training threads...");
        }
    }
    
    private void tuneAux(List<Sample> tune) {
        
        double accSum = 0;
        HashMap<Learner, Double> accMap = new HashMap<Learner, Double>();
        
        // Get a sum of all the accuracies
        for ( Pair<Learner, Double> p : mLearners ) {
            Learner l = p.get1();
            
            List<Pair<Sample,Integer>> results = l.classify(tune);

            double acc = Accurator.accuracy(results);
//            double prc = Accurator.precision(results);
//            double rcl = Accurator.recall(results);
            
            System.out.printf("Learner [%s] - Tune Accuracy: %f\n", 
                    l.getClass().getCanonicalName(), acc);
            
            accMap.put(l, acc);
            accSum += acc;
        }
        
        // Set the weights for each learner
        for ( Pair<Learner, Double> p : mLearners ) {
            Learner l = p.get1();
            
            double acc = accMap.get(l);
            double w = acc / accSum;
            
            p.set2(w);
        }
    }
    
    @Override
    public int classify(Sample x) {
        
        double sum = 0;
        
        for ( Pair<Learner, Double> p : mLearners ) {
            Learner l = p.get1();
            double w = p.get2();
            
            int thisResult = l.classify(x);
            
            sum += (w * thisResult);
        }
        
        int classValue = -1;
        
        if ( sum > 0 ) {
            classValue = 1;
        }
        
        return classValue;
    }
    
    private class TrainThread implements Runnable {

        private Learner mLearner;
        private List<Sample> mTrainingSet;
        
        public TrainThread(Learner l, List<Sample> set) {
            mLearner = l;
            mTrainingSet = set;
        }
        
        @Override
        public void run() {
            mLearner.train(mTrainingSet);
        }
        
    }
}
