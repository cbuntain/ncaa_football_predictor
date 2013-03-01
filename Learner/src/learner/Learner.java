/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package learner;

import java.util.List;

/**
 *
 * @author cbuntain
 */
public interface Learner {
    
    public void train(List<Sample> trainingSet);
    public int classify(Sample x);
    public List<Pair<Sample,Integer>> classify(List<Sample> data);
    
}
