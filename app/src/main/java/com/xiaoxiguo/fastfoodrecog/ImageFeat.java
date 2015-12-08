package com.xiaoxiguo.fastfoodrecog;

import org.opencv.core.Mat;

public class ImageFeat {
    private String imgName;
    private Mat features;
    private BagOfFeature bagOfFeat;
    private int foodId;

    public String getImgName() {
        return imgName;
    }

    public ImageFeat setImgName(String imgName) {
        this.imgName = imgName;
        return this;
    }

    public Mat getFeatures() {
        return features;
    }

    public ImageFeat setFeatures(Mat features) {
        this.features = features;
        return this;
    }

    public BagOfFeature getBagOfFeat() {
        return bagOfFeat;
    }

    public ImageFeat setBagOfFeat(BagOfFeature bagOfFeat) {
        this.bagOfFeat = bagOfFeat;
        return this;
    }

    public int getFoodId() {
        return foodId;
    }

    public ImageFeat setFoodId(int foodId) {
        this.foodId = foodId;
        return this;
    }

    @Override
    public String toString() {
        return imgName + "," + features.rows() + ","
                + bagOfFeat.toString() + "," + foodId;
    }
}
