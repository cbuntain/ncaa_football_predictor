/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package learner.svm;

import java.util.ArrayList;
import java.util.List;
import learner.Pair;
import learner.Sample;
import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

/**
 *
 * @author cbuntain
 */
public class SvmProblem {
    
    private svm_parameter mParam;
    private svm_problem mProblem;
    private svm_model mModel;
    
    public SvmProblem() {
        mParam = initParameters();
        mProblem = null;
        mModel = null;
    }
    
    private svm_parameter initParameters() {
        svm_parameter param = new svm_parameter();
        
        // default values
        param.svm_type = svm_parameter.C_SVC;
        param.kernel_type = svm_parameter.LINEAR;
        param.degree = 3;
        param.gamma = 0;	// 1/num_features
        param.coef0 = 0;
        param.nu = 0.5;
        param.cache_size = 100;
        param.C = 1;
        param.eps = 1e-3;
        param.p = 0.1;
        param.shrinking = 1;
        param.probability = 0;
        param.nr_weight = 0;
        param.weight_label = new int[0];
        param.weight = new double[0];
        
        return param;
    }
    
    public boolean train(List<Sample> trainingSet) {
        
        boolean status = true;
        
        mProblem = new svm_problem();
        mProblem.l = trainingSet.size();
        mProblem.x = new svm_node[mProblem.l][];
        mProblem.y = new double[mProblem.l];
        
        for ( int i=0; i<mProblem.l; i++ ) {
            Sample x = trainingSet.get(i);
            
            svm_node[] n = new svm_node[x.getDimension()];
            
            for ( int j=0; j<x.getDimension(); j++ ) {
                n[j] = new svm_node();
                n[j].index = j;
                n[j].value = x.getEntry(j);
            }
            
            mProblem.x[i] = n;
            mProblem.y[i] = x.getLabel();
        }
        
        String err = svm.svm_check_parameter(mProblem, mParam);
        
        if ( err != null ) {
            System.err.println(err);
            
            status = false;
        }
        
        mModel = svm.svm_train(mProblem, mParam);
        
        return status;
    }
    
    public int classify(Sample x) {
        
        if ( mModel == null ) {
            return Integer.MIN_VALUE;
        }
        
        svm_node[] n = new svm_node[x.getDimension()];
            
        for ( int j=0; j<x.getDimension(); j++ ) {
            n[j] = new svm_node();
            n[j].index = j;
            n[j].value = x.getEntry(j);
        }
        
        double y = svm.svm_predict(mModel, n);
        System.out.println(y);
        
        return new Double(y).intValue();
    }
    
    public List<Pair<Sample,Integer>> classify(List<Sample> data) {
        
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
