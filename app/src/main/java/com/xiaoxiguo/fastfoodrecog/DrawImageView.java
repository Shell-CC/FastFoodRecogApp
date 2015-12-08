package com.xiaoxiguo.fastfoodrecog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FeatureDetector;

public class DrawImageView extends View {

    private FoodImage foodImage;

    private Paint paint;

    private Bitmap grabCuttedImage;
    private boolean grabCutted;
    private Mat mask;

    private boolean drawRect = false;
    private boolean drawable = true;

    private float leftTopX;
    private float leftTopY;
    private float rightBottomX;
    private float rightBottomY;

    public DrawImageView(Context context) {
        super(context);
        setPainter();
    }

    public DrawImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPainter();
    }

    private void setPainter() {
        Log.d("Procedure", "DrawImageView created.");
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
    }

    /**
     * Set given image as background,
     * Scale it down by 8 as processing image.
     * @param foodImage Given image as Bitmap.
     */
    public void setFoodImage(Bitmap foodImage) {
        Bitmap bitmap = Bitmap.createScaledBitmap(foodImage,
                foodImage.getWidth()/8, foodImage.getHeight()/8, true);
        Log.v("ImgProc", "Bitmap image config: " + foodImage.getConfig().toString());
        setBackground(new BitmapDrawable(bitmap));
        this.foodImage = new FoodImage(bitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (drawRect) {
            canvas.drawRect(leftTopX, leftTopY, rightBottomX, rightBottomY, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!drawable) return false;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            leftTopX = event.getX();
            leftTopY = event.getY();
            Log.d("Procedure", "Choose top-left point: " + leftTopX + "," + leftTopY);
        } else {
            rightBottomX = event.getX();
            rightBottomY = event.getY();
            drawRect = true;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Log.d("Procedure", "Choose bottom-right: " + rightBottomX + "," + rightBottomY);
                Log.d("Procedure", "Re-draw rect and reload grab-cut background image.");
                // Grab-cut and show result
                grabCutted = grubcut();
                if (grabCutted) {
                    setBackground(new BitmapDrawable(grabCuttedImage));
                }
            }
        }
        // Redraw on this view
        invalidate();
        // Has already handled onTouch
        return true;
    }

    public void setNotDrawble() {
        drawable = false;
    }

    public void setDrawable() {
        drawable = true;
    }

    private boolean grubcut() {
        // Get same scale of rectangular for grab-cut
        double x1 = leftTopX * foodImage.cols() / this.getWidth();
        double y1 = leftTopY * foodImage.rows() / this.getHeight();
        double x2 = rightBottomX * foodImage.cols() / this.getWidth();
        double y2 = rightBottomY * foodImage.rows() / this.getHeight();
        Log.d("ImgProc", "image size: " + foodImage.cols() + "*" + foodImage.rows());
        Log.d("ImgProc", "view size: " + this.getWidth() + "*" + this.getHeight());
        Log.d("ImgProc", "selected region: (" + x1 + "," + y1 + "),(" + x2 + "," + y2 + ")");
        Rect rect = new Rect(new Point(x1, y1), new Point(x2, y2));
        // Extract using grab cut
        Log.v("Procedure", "Grab cutting...");
        mask = foodImage.extractBackgroundMask(rect);
        Log.v("Procedure", "grab-cut done!");
        // save grab-cutted image to bitmap image
        try {
            grabCuttedImage = foodImage.getImageWithMask().toBitmap(Bitmap.Config.ARGB_8888);
        } catch (FoodImage.EmptyContentException e) {
            Log.v("Procedure", "Grab-cut failed");
            return false;
        }
        return true;
    }

    public Bitmap getGrabCuttedImage() throws FoodImage.EmptyContentException {
        if (!grabCutted) {
            throw new FoodImage().new EmptyContentException("Not grab-cutted yet");
        }
        return grabCuttedImage;
    }

    public int learning(Dictionary dictionary, Classifier classifier) {
        // extracting features
        Log.v("Procedure", "extracting features...");
        Mat features = foodImage.extractFeatures(mask,
                FeatureDetector.FAST, DescriptorExtractor.ORB);
        Log.v("Procedure", features.size()+" features are detected");

        // Calculate bag of features.
        Log.v("Procedure", "Calculating bag of features");
        BagOfFeature bagOfFeature = new BagOfFeature(dictionary, features, 1);
        Log.v("Procedure", bagOfFeature.toString());

        // Classify bag of features
        Log.v("Procedure", "Predicting...");
        int label = classifier.predict(bagOfFeature.toMat());
        Log.v("Procedure", "Food ID: " + label);
        return label;
    }
}