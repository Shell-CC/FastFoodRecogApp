package com.xiaoxiguo.fastfoodrecog;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Shawn on 12/5/15.
 * Basic image class used in FastFoodRecognizer.
 */
public class Image {

    protected Mat image;

    /**
     * Constructor of an empty image.
     */
    public Image() {
        this(new Mat());
    }

    /**
     * Construct an image from org.opencv.Mat
     * @param image Given image as mat.
     */
    public Image(Mat image) {
        this.image = image;
    }

    /**
     * Construct an image from Bitmap
     * @param bitmap Image as Bitmap format.
     */
    public Image(Bitmap bitmap) {
        image = new Mat();
        Utils.bitmapToMat(bitmap, image);
    }

    /**
     * Check if the image is empty.
     * @return True if the image empty.
     */
    public boolean isEmpty() {
        return image.total() == 0;
    }

    /**
     * Return column numbers of the image
     * @return Column numbers
     */
    public int cols() {
        return image.cols();
    }

    /**
     * Return row numbers of the image
     * @return Row numbers
     */
    public int rows() {
        return image.rows();
    }

    /**
     * Return the size of image in string format.
     * @return The size of image.
     */
    public String size() {
        return image.size().toString();
    }

    /**
     * Read image from file.
     * @param filename Path name of the file.
     * @throws java.io.IOException If file path is wrong or image format is not supported.
     */
    public void read(String filename) throws IOException {
        image = Highgui.imread(filename);
        if (image.total() == 0) {
            throw new FileNotFoundException(filename);
        }
    }

    /**
     * Write the image to the file.
     * @param filename File path to be written.
     * @throws IOException If image format is not supported.
     */
    public void write(String filename) throws IOException{
        if (!Highgui.imwrite(filename, image)) {
            throw new IOException("Failed to write " + filename);
        }
    }

    /**
     * Return the mat format of the image
     * @return Image in Mat format
     */
    public Mat toMat() {
        return image;
    }


    /**
     * Return the Bitmap format of the image
     * @return Image in Android Bitmap
     */
    public Bitmap toBitmap(Bitmap.Config config) {
        Bitmap bitmap = Bitmap.createBitmap(image.cols(), image.rows(), config);
        Utils.matToBitmap(image, bitmap);
        return bitmap;
    }
}
