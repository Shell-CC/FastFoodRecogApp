package com.xiaoxiguo.fastfoodrecog;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.ml.CvKNearest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Shawn on 11/30/15.
 * Class of bag of features of an image, including:
 * - Normalized histogram of features.
 * - Number of features.
 */
public class BagOfFeature {

    private int numOfFeats;
    private List<Double> hist;

    /**
     * Construct an empty bag of features of the image
     * @param numOfFeats The number of features in the image
     * @param size The length of the histogram.
     */
    public BagOfFeature(int numOfFeats, int size) {
        this.numOfFeats = numOfFeats;
        hist = new ArrayList<Double>(Collections.nCopies(size, 0.0));
    }

    /**
     * Construct BagOfFeatures from some range of the labels of features.
     * @param size The size of the histogram.
     * @param labels The labels of surf descriptors
     * @param from The from index of the labels (inclusive).
     * @param to The end index of the labels (exclusive).
     */
    private void getBagOfFeature(int size, Mat labels, int from, int to) {
        if (to <= from) throw new IllegalArgumentException("End index smaller than start index");
        this.numOfFeats = to - from;
        hist = new ArrayList<Double>(Collections.nCopies(size, 0.0));
        for (int i = from; i < to; i++) {
            int label = (int) (labels.get(i, 0)[0]);
            hist.set(label, hist.get(label) + 1);
        }
        for (int i = 0; i < hist.size(); i++) {
             hist.set(i, hist.get(i) / numOfFeats);
        }
    }

    /**
     * Construct BagOfFeature from the labels of features.
     * @param size The size of the histogram.
     * @param labels The labels of surf descriptors
     */
    public BagOfFeature(int size, Mat labels) {
        getBagOfFeature(size, labels, 0, labels.rows());
    }


    /**
     * Construct the BagOfFeatures from the string format
     * @param numOfFeats Number of features
     * @param strBagOfSurf The string format, eg. BagOf{0.1, 0.5, 0.4}
     */
    public BagOfFeature(int numOfFeats, String strBagOfSurf) {
        if (strBagOfSurf.startsWith("BagOfFeat{") && strBagOfSurf.endsWith("}")) {
            String[] sub = strBagOfSurf.substring(10, strBagOfSurf.length()-1).split(";");
            this.numOfFeats = numOfFeats;
            this.hist = new ArrayList<Double>(sub.length);
            for (String s : sub) {
                hist.add(Double.parseDouble(s));
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Calculate bag of features comparing features with dictionary, choose k-NN
     * @param dictionary The dictionary containing fixed size of features as
     * @param features Given feature descriptors of an image.
     * @param k Parameter of k-NN used as labeling algorithm.
     */
    public BagOfFeature(Dictionary dictionary, Mat features, int k) {
        // get number of features
        this.numOfFeats = features.rows();

        // get k-NN train data
        Mat trainData = dictionary.getCenters();
        Mat trainLabel = new Mat(trainData.rows(), 1, CvType.CV_32FC1);
        for (int i = 0; i < trainLabel.rows(); i++) {
            trainLabel.put(i, 0, (double)i);
        }
        CvKNearest kNearest = new CvKNearest(trainData, trainLabel, new Mat(), false, k);


        // Get labels for given features
        Mat labels = new Mat();
        kNearest.find_nearest(features, k, labels, new Mat(), new Mat());

        // construct from labels of features
        getBagOfFeature(trainData.rows(), labels, 0, labels.rows());
    }

    /**
     * Return the string form of the bag of features(normalized)
     * @return String form as BagOfFeat{*, *, *}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("BagOfFeat{");
        if (hist.size() > 0) {
            builder.append(hist.get(0));
            for (int i = 1, N = hist.size(); i < N; i++) {
                builder.append(';').append(hist.get(i));
            }
        }
        builder.append('}');
        return builder.toString();
    }

    /**
     * Return the mat form of the bag of features(normalized)
     * @return Mat form, dictionary size * feature length * CV_32FC1.
     */
    public Mat toMat() {
        Mat mat = new Mat(1, hist.size(), CvType.CV_32FC1);
        for (int i = 0; i < hist.size(); i++) {
            float[] count = new float[]{hist.get(i).floatValue()};
            mat.put(0, i, hist.get(i));
        }
        return mat;
    }
}
