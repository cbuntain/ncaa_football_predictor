/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package learner.svm;

import edu.berkeley.compbio.jlibsvm.kernel.KernelFunction;
import org.apache.commons.math3.linear.ArrayRealVector;

/**
 *
 * @author cbuntain
 */
public class LocalLinearKernel<P extends ArrayRealVector> implements KernelFunction<P> {
    
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("kernel_type linear\n");
        return sb.toString();
    }

    @Override
    public double evaluate(P x, P y) {
        return x.dotProduct(y);
    }
}
