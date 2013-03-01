/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package learner;

/**
 *
 * @author cbuntain
 */
public class Triplet<X,Y,Z> {
    
    private X mV1;
    private Y mV2;
    private Z mV3;
    
    public Triplet(X v1, Y v2, Z v3) {
        mV1 = v1;
        mV2 = v2;
        mV3 = v3;
    }
    
    public X get1() {
        return mV1;
    }
    
    public Y get2() {
        return mV2;
    }
    
    public Z get3() {
        return mV3;
    }
}
