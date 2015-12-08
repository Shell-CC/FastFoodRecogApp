package com.xiaoxiguo.fastfoodrecog;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.TermCriteria;
import org.opencv.ml.CvSVM;
import org.opencv.ml.CvSVMParams;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shawn on 12/2/15.
 */
public class Classifier {
    CvSVM svm;
    CvSVMParams params;

    public Classifier() {
        svm = new CvSVM();
        params = new CvSVMParams();
        params.set_svm_type(CvSVM.C_SVC);
        params.set_C(1.0);
        params.set_kernel_type(CvSVM.RBF);
        params.set_gamma(1.0);
        params.set_term_crit(new TermCriteria(TermCriteria.MAX_ITER, 100000, 0.0001));
    }

    public void train(List<Mat> trainDataList, List<Integer> trainLabelList) {
        System.out.println("Trainning...");
        Mat trainDataMat = new Mat();
        Mat trainLabelMat = new Mat(trainLabelList.size(), 1, CvType.CV_32FC1);
        // combine list into mat
        Core.vconcat(trainDataList, trainDataMat);
        for (int i = 0; i < trainDataList.size(); i++) {
            trainLabelMat.put(i, 0, trainLabelList.get(i).doubleValue());
        }
        svm.train_auto(trainDataMat, trainLabelMat, new Mat(), new Mat(), params);
    }

    public int predict(Mat testData) {
        return (int) svm.predict(testData);
    }

    public List<Integer> predict(List<Mat> testDataList) {
        List<Integer> predictLabelList = new ArrayList<Integer>(testDataList.size());
        for (Mat testData : testDataList) {
            predictLabelList.add(predict(testData));
        }
        return predictLabelList;
    }

    public List<Integer> test(List<Mat> testDataList, List<Integer> testLabelList) {
        List<Integer> predictLabelList = predict(testDataList);
        double count = 0;
        for (int i = 0; i < testDataList.size(); i++) {
            if (testLabelList.get(i).equals(predictLabelList.get(i))) count++;
//            System.out.println(testLabelList.get(i) + " " + predictLabelList.get(i));
        }
        System.out.println("Resubsititution is " + count / testLabelList.size());
        return predictLabelList;
    }

    public void save(String filename) {
        svm.save(filename);
    }

    public void load(String filename) {
        svm.load(filename);
    }
}

