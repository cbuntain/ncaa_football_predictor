/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package learner.svm;

import edu.berkeley.compbio.jlibsvm.scaler.ScalingModel;
import edu.berkeley.compbio.jlibsvm.scaler.ScalingModelLearner;
import java.util.HashMap;
import java.util.Map;
import learner.Sample;
import org.apache.commons.math3.linear.ArrayRealVector;

/**
 *
 * @author cbuntain
 */
public class LocalLinearScaler implements ScalingModelLearner<Sample> {
    
    //ImmutableSvmParameter param;
    private final int maxExamples;
    private final boolean normalizeL2;
    
    public LocalLinearScaler(int scalingExamples, boolean normalizeL2)
    {
        this.maxExamples = scalingExamples;
        this.normalizeL2 = normalizeL2;
    }

    @Override
    public ScalingModel<Sample> learnScaling(Iterable<Sample> examples) {

        Map<Integer, Double> minima = new HashMap<Integer, Double>();
        Map<Integer, Double> sizes = new HashMap<Integer, Double>();

        int count = 0;
        for (ArrayRealVector example : examples) {
            
            if (count >= maxExamples) {
                break;
            }
            
            for ( int i=0; i<example.getDimension(); i++ ) {
                
                double v = example.getEntry(i);

                Double currentMin = minima.get(i);

                if (currentMin == null) {
                    minima.put(i, v);
                    sizes.put(i, 0.0);
                }
                else {
                    minima.put(i, Math.min(minima.get(i), v));
                    sizes.put(i, Math.max(sizes.get(i), v - minima.get(i)));
                }
            }
            count++;
        }

        return new LinearScalingModel(minima, sizes);
    }

    public class LinearScalingModel implements ScalingModel<Sample> {

        Map<Integer, Double> minima;
        Map<Integer, Double> sizes;

        public LinearScalingModel(Map<Integer, Double> minima, Map<Integer, Double> sizes) {
            this.minima = minima;
            this.sizes = sizes;
        }

        @Override
        public Sample scaledCopy(Sample example) {
            
            System.out.println("Scaling...");
            Sample result = new Sample(example.getDimension());

            for (int i = 0; i < example.getDimension(); i++) {
                
                double v = example.getEntry(i);

                Double min = minima.get(i);

                // if this dimension was never seen in the training set, then we can't scale it
                if (min != null)
                {
                    result.setEntry(i, (2.0 * (v - min) / sizes.get(i)) - 1.0);
                }
            }

            if (normalizeL2) {
                double magnitude = result.getNorm();
                result.mapDivideToSelf(magnitude);
            }

            return result;
        }
    }
}

