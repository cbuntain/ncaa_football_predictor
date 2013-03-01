/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package learner;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cbuntain
 */
public abstract class AbstractLearner implements Learner {

    @Override
    public abstract void train(List<Sample> trainingSet);

    @Override
    public abstract int classify(Sample x);

    @Override
    public List<Pair<Sample, Integer>> classify(List<Sample> data) {
        
        ArrayList<Pair<Sample,Integer>> list = 
                new ArrayList<Pair<Sample,Integer>>();
        
        for ( Sample x : data ) {
            int y = classify(x);
            
            Pair<Sample,Integer> pair = new Pair<Sample,Integer>(x,y);
            
            list.add(pair);
        }
        
        return list;
    }
    
}
