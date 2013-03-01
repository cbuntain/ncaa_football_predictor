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
public class Accurator {

    public static double accuracy(List<Pair<Sample,Integer>> data) {
        
        int correctCount = 0;
        
        for ( Pair<Sample,Integer> p : data ) {
            if ( p.get1().getLabel() == p.get2() ) {
                correctCount++;
            }
        }
        
        return (double)correctCount/(double)data.size();
    }
    
    public static double precision(List<Pair<Sample,Integer>> data) {
        int truePositives = 0;
        int positives = 0;  // True and false positives
        
        for ( Pair<Sample,Integer> p : data ) {
            if ( p.get2() > 0 ) {
                positives++;    // All positives
                
                // True positives
                if ( p.get1().getLabel() == p.get2() ) {
                    truePositives++;
                }
            }
        }
        
        return (double)truePositives/(double)positives;
    }
    
    public static double recall(List<Pair<Sample,Integer>> data) {
        int truePositives = 0;
        int actuals = 0;  // True positives and false negatives
        
        for ( Pair<Sample,Integer> p : data ) {
            if ( p.get1().getLabel() > 0 ) {
                actuals++;    // All positives
                
                // True positives
                if ( p.get1().getLabel() == p.get2() ) {
                    truePositives++;
                }
            }
        }
        
        return (double)truePositives/(double)actuals;
    }
    
    public static double f1Score(List<Pair<Sample,Integer>> data) {
        
        double precision = Accurator.precision(data);
        double recall = Accurator.recall(data);
        
        return (2 * precision * recall)/(precision + recall);
    }
}
