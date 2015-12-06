package com.xiaoxiguo.fastfoodrecog;

import org.opencv.core.*;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;

/**
 * Created by Shawn on 11/25/15.
 * @version 1.2
 *
 * Food image info, including:
 * -the original food image.
 * -the background mask excluding food part.
 * -keypoints detected in the image.
 */

public class FoodImage extends Image{

    private Mat backgroundMask;
    private MatOfKeyPoint features;

    /**
     * Empty constructor.
     */
    public FoodImage() {
        this(new Mat());
    }

    /**
     * Construct food image from an OpenCV mat(org.opencv.*).
     * @param image Image in OpenCV MAT format.
     */
    public FoodImage(Mat image) {
        super(image);
        this.backgroundMask = new Mat();
        this.features = new MatOfKeyPoint();
    }


    private Mat foregroundMask() {

        // Get threshold binary image
        Mat binaryImage = new Mat();
        Imgproc.cvtColor(image, binaryImage, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(binaryImage, binaryImage, 128, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);
//        Mat foreground = new Mat();
//        Imgproc.morphologyEx(grayImage, foreground, Imgproc.MORPH_OPEN, Mat.ones(50, 50, CvType.CV_8U));

        // Get the distance transform and then normalize and threshold it.
        Mat dist = new Mat();
        Imgproc.distanceTransform(binaryImage, dist, Imgproc.CV_DIST_L2, 3);
//        Core.normalize(dist, dist, 0.0, 1.0, Core.NORM_MINMAX);
        Imgproc.threshold(dist, dist, 128, 255, Imgproc.THRESH_BINARY);

        return binaryImage;
    }

    public Mat extractBackgroundMask() {
        Mat diff = new Mat();
        Imgproc.cvtColor(image, diff, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(diff, diff, 64, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);
        Imgproc.morphologyEx(diff, diff, Imgproc.MORPH_OPEN, Mat.ones(3, 3, CvType.CV_8U), new Point(-1, -1), 10);
        this.backgroundMask = diff;
        return diff;
    }


    /**
     * Extract background from image given a background image.
     * @param background The given background image.
     * @return BackgroundMask extracted.
     */
    public Mat extractBackgroundMask(Image background) {
        if (background.isEmpty()) {
            throw new IllegalArgumentException("Background image is empty");
        }
        Mat diff = new Mat();
        Core.absdiff(image, background.toMat(), diff);
        Imgproc.cvtColor(diff, diff, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(diff, diff, 10, 255, Imgproc.THRESH_BINARY);
        Imgproc.morphologyEx(diff, diff, Imgproc.MORPH_OPEN, Mat.ones(3, 3, CvType.CV_8U), new Point(-1, -1), 10);
        this.backgroundMask = diff;
        return diff;
    }

    /**
     * Extract background from image using GrabCut method.
     * @param rect Rectangular region for GrabCut.
     * @return BackgroundMask extracted.
     */
    public Mat extractBackgroundMask(Rect rect) {
        backgroundMask = new Mat();
        Mat fgModel = new Mat();
        Mat bgModel = new Mat();
        // remove alpha channel
        if (image.channels() > 3) {
            Imgproc.cvtColor(image, image, Imgproc.COLOR_RGBA2BGR);
        }
        Imgproc.grabCut(image, backgroundMask, rect, bgModel, fgModel, 3, Imgproc.GC_INIT_WITH_RECT);
        Core.compare(backgroundMask, new Scalar(3.0), backgroundMask, Core.CMP_EQ);
        return backgroundMask;
    }

    /**
     * Extract features in the ROI of the image.
     * @param mask Excluded region of interest.
     * @param detect Feature detection method used.
     * @param describe Feature description method used.
     * @return Describers of all features extracted.
     */
    public Mat extractFeatures(Mat mask, int detect, int describe) {
        Mat descriptor = new Mat();
        FeatureDetector detector = FeatureDetector.create(detect);
        DescriptorExtractor extractor = DescriptorExtractor.create(describe);

        if (mask.empty()) {
            detector.detect(image, features);
        } else {
            detector.detect(image, features, mask);
        }
        extractor.compute(image, features, descriptor);
        descriptor.convertTo(descriptor, CvType.CV_32FC1);
        return descriptor;
    }

    /**
     * Extract features in the whole image.
     * @param detect Feature detection method used.
     * @param describe Feature description method used.
     * @return Describers of all features extracted.
     */
    public Mat extractFeatures(int detect, int describe) {
        return extractFeatures(new Mat(), detect, describe);
    }


    /**
     * Get the image with detected features drawn.
     * @return The image with features drawn.
     * @throws FoodImage.EmptyContentException
     */
    public Image getImageWithFeatures() throws EmptyContentException {
        if (features.total() == 0) {
            throw new EmptyContentException("features");
        }
        Mat imageWithFeatures = new Mat();
        Features2d.drawKeypoints(image, features, imageWithFeatures);
        return new Image(imageWithFeatures);
    }


    /**
     * Get the image with background masked.
     * @return Image without background
     * @throws EmptyContentException If the background mask is not found.
     */
    public Image getImageWithMask() throws EmptyContentException {
        if (features.total() == 0) {
            throw new EmptyContentException("background");
        }
        Mat imageWithMask = new Mat();
        image.copyTo(imageWithMask, backgroundMask);
        return new Image(imageWithMask);
    }


    /**
     * Exception if the retrived info in the image is empty.
     */
    class EmptyContentException extends Exception {
        public EmptyContentException(String message) {
            super(message);
        }
    }
}

