package dealwithit.johannes_drescher.de.dealwithitapp.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.io.Serializable;

import dealwithit.johannes_drescher.de.dealwithitapp.util.RotationGestureDetector;

public class PhotoView extends View {

    private static final int BACKGROUND_COLOR = 0xffffffff;

    private boolean mFirstDraw;
    private int mWidth;
    private int mHeight;

    private float mOffsetX;
    private float mOffsetY;
    private float mScale;
    private float mRotation;

    private Paint mPaint;
    private Paint mClearPaint;
    private Bitmap mBitmap;

    private Matrix mMatrix;
    private OnDataChangeListener mOnDataChangeListener;

    public PhotoView(Context context) {
        super(context);
        init();
    }

    public PhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PhotoView(Context context, AttributeSet attrs, int r) {
        super(context, attrs, r);
        init();
    }

    public void setData(DataHolder dh, Bitmap bm)  {
        this.mWidth = dh.mWidth;
        this.mHeight = dh.mHeight;
        this.mOffsetX = dh.mOffsetX;
        this.mOffsetY = dh.mOffsetY;
        this.mScale = dh.mScale;
        this.mRotation = dh.mRotation;
        this.mBitmap = bm;

        invalidate();
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mBitmap != null && mFirstDraw) {
            centerImage();
            mFirstDraw = false;
        }

        mMatrix.reset();
        mMatrix.preTranslate(mOffsetX, mOffsetY);
        mMatrix.preRotate(mRotation);
        mMatrix.preScale(mScale, mScale);

        if (mBitmap != null) {
            canvas.drawPaint(mClearPaint);
            canvas.drawBitmap(mBitmap, mMatrix, mPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(mWidth, mHeight);
    }

    public void setOnDataChangeListener(OnDataChangeListener onDataChangeListener) {
        mOnDataChangeListener = onDataChangeListener;
    }

    public Bitmap getSnapShot() {

        Bitmap snapShotBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(snapShotBitmap);

        Paint paint = new Paint();
        paint.setColor(BACKGROUND_COLOR);
        canvas.drawPaint(paint);
        canvas.drawBitmap(mBitmap, mMatrix, paint);

        return snapShotBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        mFirstDraw = true;

        invalidate();
        requestLayout();
    }

    public void scroll(float deltaX, float deltaY) {
        mOffsetX += deltaX;
        mOffsetY += deltaY;

        invalidate();
        requestLayout();
    }

    public void setScale(float scale) {
        mScale = scale;

        invalidate();
        requestLayout();
    }

    public void setRotate(float degree) {
        mRotation = degree;

        invalidate();
        requestLayout();
    }

    private void centerImage() {
        int targetSize = Math.min(mWidth, mHeight);

        float scale = targetSize / (float) mBitmap.getWidth();
        if (scale < 1.0) {
            mScale = scale;
            if (mOnDataChangeListener != null) {
                mOnDataChangeListener.onScaleChange(mScale);
            }
        }else{
            scale = mScale;
        }

        mOffsetX = (mWidth / 2.0f) - ((mBitmap.getWidth() * scale) / 2.0f);
        mOffsetY = (mHeight / 2.0f) - ((mBitmap.getHeight() * scale) / 2.0f);
    }

    private void init() {
        mOffsetX = 0.0f;
        mOffsetY = 0.0f;
        mScale = 1.0f;
        mRotation = 0.0f;
        mMatrix = new Matrix();

        mClearPaint = new Paint();
        mClearPaint.setColor(0xff00ff);
        mClearPaint.setStyle(Paint.Style.FILL);

        mPaint = new Paint();
        mPaint.setAntiAlias(false);
    }

    public interface OnDataChangeListener {
        void onScaleChange(float scale);
    }

    public static class DataHolder implements Serializable {
        private int mWidth;
        private int mHeight;
        private float mOffsetX;
        private float mOffsetY;
        private float mScale;
        private float mRotation;

        public DataHolder(int width, int height, float offsetX, float offsetY, float scale, float rotation) {
            mWidth = width;
            mHeight = height;
            mOffsetX = offsetX;
            mOffsetY = offsetY;
            mScale = scale;
            mRotation = rotation;
        }

        @Override
        public String toString() {
            String s = mWidth + "|" + mHeight + "|" + mOffsetX + "|" + mOffsetY + "|" + mScale + "|" + mRotation;
            return s;
        }

        public static DataHolder fromString(String s) {
            String[] a = s.split("|");
            return new DataHolder(Integer.valueOf(a[0]),Integer.valueOf(a[1]),Float.valueOf(a[2]),Float.valueOf(a[3]),Float.valueOf(a[4]),Float.valueOf(a[5]));
        }
    }

    public DataHolder generateDataHolder() {
        return new DataHolder(mWidth,mHeight,mOffsetX,mOffsetY,mScale,mRotation);
    }
}
