/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package learner;

import org.apache.commons.math3.linear.ArrayRealVector;

/**
 *
 * @author cbuntain
 */
public class Sample extends ArrayRealVector {
    
    private Game mAssociatedGame;
    private int mLabel;
    
    public Sample(int size) {
        super(size);
    }
    
    public Sample(double[] arr) {
        super(arr);
    }
    
    public void setLabel(int l) {
        mLabel = l;
    }
    
    public int getLabel() {
        return mLabel;
    }
    
    public void setGame(Game g) {
        mAssociatedGame = g;
    }
    
    public Game getGame() {
        return mAssociatedGame;
    }
}
