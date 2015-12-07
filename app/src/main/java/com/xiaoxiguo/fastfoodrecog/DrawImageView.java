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
import android.widget.Toast;

import org.opencv.core.Point;
import org.opencv.core.Rect;

public class DrawImageView extends View {
    private Bitmap foodImage;
    private Bitmap grabCuttedImage;
    private Paint paint;

    private boolean drawRect = false;
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

    // set given image as background, scale it down by 4
    public void setFoodImage(Bitmap foodImage) {
        this.foodImage = Bitmap.createScaledBitmap(foodImage,
                foodImage.getWidth()/8, foodImage.getHeight()/8, true);
        Log.v("ImgProc", "Bitmap image config: " + foodImage.getConfig().toString());
        setBackground(new BitmapDrawable(this.foodImage));
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
                grubcut();
                setBackground(new BitmapDrawable(grabCuttedImage));
            }
        }
        // Redraw on this view
        invalidate();
        // Has already handled onTouch
        return true;
    }

    private void grubcut() {
        FoodImage image = new FoodImage(foodImage);
        // Get same scale of rectangular for grab-cut
        double x1 = leftTopX * foodImage.getWidth() / this.getWidth();
        double y1 = leftTopY * foodImage.getHeight() / this.getHeight();
        double x2 = rightBottomX * foodImage.getWidth() / this.getWidth();
        double y2 = rightBottomY * foodImage.getHeight() / this.getHeight();
        Log.d("ImgProc", "image size: " + image.size());
        Log.d("ImgProc", "view size: " + this.getWidth() + "*" + this.getHeight());
        Log.d("ImgProc", "selected region: (" + x1 + "," + y1 + "),(" + x2 + "," + y2 + ")");
        Rect rect = new Rect(new Point(x1, y1), new Point(x2, y2));
        // Extract using grab cut
        Log.v("Procedure", "Grab cutting...");
        image.extractBackgroundMask(rect);
        Log.v("Procedure", "grab-cut done!");
        // save grab-cutted image to bitmap image
        try {
            grabCuttedImage = image.getImageWithMask().toBitmap(foodImage.getConfig());
        } catch (FoodImage.EmptyContentException e) {
            grabCuttedImage = foodImage;
            Log.v("Procedure", "Grab-cut failed");
        }
    }
}
