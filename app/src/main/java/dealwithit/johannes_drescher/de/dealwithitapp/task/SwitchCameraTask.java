package dealwithit.johannes_drescher.de.dealwithitapp.task;

import android.content.Context;
import android.os.AsyncTask;

import com.commonsware.cwac.camera.CameraFragment;

import dealwithit.johannes_drescher.de.dealwithitapp.activities.CameraActivity;
import dealwithit.johannes_drescher.de.dealwithitapp.camerautil.MyCameraHost;

/**
 * Created by Johannes on 08.01.16.
 */

public class SwitchCameraTask extends AsyncTask<Object,Void,CameraFragment> {

    private onTaskComplete taskComplete;
    private Object o;

    public SwitchCameraTask(onTaskComplete onTaskComplete) {
        taskComplete = onTaskComplete;
    }

    @Override
    protected CameraFragment doInBackground(Object... params) {
        CameraFragment mFragment = new CameraFragment();
        CameraActivity context = (CameraActivity)params[0];
        MyCameraHost.Builder mBuilder = new MyCameraHost.Builder(context);
        mBuilder.useFrontFacingCamera((boolean) params[1]);
        mFragment.setHost(mBuilder.build());
        return mFragment;
    }

    @Override
    protected void onPostExecute(CameraFragment fragment) {
        super.onPostExecute(fragment);
        taskComplete.cameraSwitched(fragment);
    }

    public interface onTaskComplete{void cameraSwitched(CameraFragment fragment);}

}
