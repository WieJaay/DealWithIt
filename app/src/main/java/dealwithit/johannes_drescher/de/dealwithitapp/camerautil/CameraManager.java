package dealwithit.johannes_drescher.de.dealwithitapp.camerautil;

import android.widget.FrameLayout;

import com.commonsware.cwac.camera.CameraFragment;

import dealwithit.johannes_drescher.de.dealwithitapp.activities.CameraActivity;
import dealwithit.johannes_drescher.de.dealwithitapp.task.SwitchCameraTask;

public class CameraManager {

    private FrameLayout mLayout;
    private CameraFragment mFragment;
    private MyCameraHost.Builder mBuilder;
    private CameraActivity mMainInstance;
    private boolean mUsesFrontCamera;
    private boolean mCurrentlyEnabled;

    public CameraManager(CameraActivity mainActivity, FrameLayout replacementFor) {
        mMainInstance = mainActivity;
        mLayout = replacementFor;
        mCurrentlyEnabled = false;
        mUsesFrontCamera = true;
    }

    public void startPreview() {
        mFragment = new CameraFragment();
        mBuilder = new MyCameraHost.Builder(mMainInstance);
        mBuilder.useFrontFacingCamera(mUsesFrontCamera);
        mFragment.setHost(mBuilder.build());
        mMainInstance.getFragmentManager().beginTransaction().replace(mLayout.getId(), mFragment).commit();
        mCurrentlyEnabled = true;
    }

    public void startPreview(boolean useFrontCamera) {
        mFragment = new CameraFragment();
        mBuilder = new MyCameraHost.Builder(mMainInstance);
        mBuilder.useFrontFacingCamera(useFrontCamera);
        mFragment.setHost(mBuilder.build());
        mMainInstance.getFragmentManager().beginTransaction().replace(mLayout.getId(), mFragment).commit();
        mUsesFrontCamera = useFrontCamera;
        mCurrentlyEnabled = true;
    }

    public void stopPreview() {
        mLayout.removeAllViews();
        mCurrentlyEnabled = false;
    }

    public boolean isCurrentlyEnabled() {
        return mCurrentlyEnabled;
    }

    public FrameLayout getReplacedLayout() {
        return mLayout;
    }

    public void setReplacedLayout(FrameLayout layout) {
        this.mLayout = layout;
    }

    public boolean usesFrontCamera() {
        return mUsesFrontCamera;
    }

    public void setmUsesFrontCamera(boolean b) {
        mFragment = new CameraFragment();
        mBuilder = new MyCameraHost.Builder(mMainInstance);
        mBuilder.useFrontFacingCamera(b);
        mFragment.setHost(mBuilder.build());
        mMainInstance.getFragmentManager().beginTransaction().replace(mLayout.getId(), mFragment).commit();
        mUsesFrontCamera = b;
    }

    public void switchCamera() {
        mUsesFrontCamera = !mUsesFrontCamera;
        new SwitchCameraTask(new SwitchCameraTask.onTaskComplete() {
            @Override
            public void cameraSwitched(CameraFragment fragment) {
                mFragment = fragment;
                mMainInstance.getFragmentManager().beginTransaction().replace(mLayout.getId(), mFragment).commit();
            }
        }).execute(new Object[]{mMainInstance, mUsesFrontCamera});

    }

    public CameraFragment getCameraFragment() {
        return mFragment;
    }

    public void takePhoto() {
        try {
            mFragment.takePicture(true, false);
        } catch (IllegalStateException e) {
        }
    }
}
