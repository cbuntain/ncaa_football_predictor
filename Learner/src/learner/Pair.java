/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package learner;

/**
 *
 * @author cbuntain
 */
public class Pair<X,Y> {
    
    private X mV1;
    private Y mV2;
    
    public Pair(X v1, Y v2) {
        mV1 = v1;
        mV2 = v2;
    }
    
    public X get1() {
        return mV1;
    }
    
    public void set1(X v) {
        mV1 = v;
    }
    
    public Y get2() {
        return mV2;
    }
    
    public void set2(Y v) {
        mV2 = v;
    }
    
}
