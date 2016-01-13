package dealwithit.johannes_drescher.de.dealwithitapp.util;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import dealwithit.johannes_drescher.de.dealwithitapp.view.PhotoView;

/**
 * Created by Johannes on 11.01.16.
 */
public class DragPinchRotateTouchListener implements View.OnTouchListener {

    private Context mContext;
    private PhotoView mPhotoView;
    private float mScale;
    private float mRotation;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private RotationGestureDetector rotationGestureDetector;

    public DragPinchRotateTouchListener(Context context, PhotoView photoView) {
        mContext = context;
        mPhotoView = photoView;
        mScale = 1.0f;
        mRotation = 0.0f;
        gestureDetector = new GestureDetector(mContext, onGestureListener);
        scaleGestureDetector = new ScaleGestureDetector(mContext, onScaleGestureListener);
        rotationGestureDetector = new RotationGestureDetector(onRotationGestureListener);
    }

    public PhotoView.OnDataChangeListener getOnDataChangeListener() {
        return new PhotoView.OnDataChangeListener() {
            @Override
            public void onScaleChange(float scale) {
                mScale = scale;
            }
        };
    }

    GestureDetector.SimpleOnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mPhotoView.scroll(-distanceX, -distanceY);
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    };
    ScaleGestureDetector.SimpleOnScaleGestureListener onScaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mPhotoView.setScale(mScale * detector.getScaleFactor());
            return super.onScale(detector);
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            mScale *= detector.getScaleFactor();
            super.onScaleEnd(detector);
        }
    };
    RotationGestureDetector.OnRotationGestureListener onRotationGestureListener = new RotationGestureDetector.OnRotationGestureListener() {
        @Override
        public void onRotation(RotationGestureDetector rotationDetector) {
            mPhotoView.setRotate(mRotation - rotationDetector.getAngle());
        }

        @Override
        public void onRotationEnd(RotationGestureDetector rotationDetector) {
            mRotation = mRotation - rotationDetector.getAngle();
        }
    };

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        scaleGestureDetector.onTouchEvent(event);
        rotationGestureDetector.onTouchEvent(event);
        return true;
    }

}
