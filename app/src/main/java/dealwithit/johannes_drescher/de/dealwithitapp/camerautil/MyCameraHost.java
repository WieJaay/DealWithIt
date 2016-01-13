package dealwithit.johannes_drescher.de.dealwithitapp.camerautil;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaActionSound;
import android.media.MediaRecorder;
import android.os.Build.VERSION;
import android.os.Environment;
import android.util.Log;

import com.commonsware.cwac.camera.CameraHost;
import com.commonsware.cwac.camera.CameraUtils;
import com.commonsware.cwac.camera.DeviceProfile;
import com.commonsware.cwac.camera.PictureTransaction;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import dealwithit.johannes_drescher.de.dealwithitapp.activities.CameraActivity;

public class MyCameraHost implements CameraHost {
    private static final String[] SCAN_TYPES = new String[]{"image/jpeg"};
    private Context ctxt = null;
    private int cameraId = -1;
    private DeviceProfile profile = null;
    private File photoDirectory = null;
    private File videoDirectory = null;
    private RecordingHint recordingHint = null;
    private boolean mirrorFFC = false;
    private boolean useFrontFacingCamera = false;
    private boolean scanSavedImage = true;
    private boolean useFullBleedPreview = true;
    private boolean useSingleShotMode = false;
    private CameraActivity instance;


    public MyCameraHost(Context _ctxt) {
        this.ctxt = _ctxt.getApplicationContext();
        instance = (CameraActivity) _ctxt;
    }

    public Parameters adjustPictureParameters(PictureTransaction xact, Parameters parameters) {
        return parameters;
    }

    public Parameters adjustPreviewParameters(Parameters parameters) {
        return parameters;
    }

    public void configureRecorderAudio(int cameraId, MediaRecorder recorder) {
        recorder.setAudioSource(5);
    }

    public void configureRecorderOutput(int cameraId, MediaRecorder recorder) {
        recorder.setOutputFile(this.getVideoPath().getAbsolutePath());
    }

    @TargetApi(11)
    public void configureRecorderProfile(int cameraId, MediaRecorder recorder) {
        if(VERSION.SDK_INT >= 11 && !CamcorderProfile.hasProfile(cameraId, 1)) {
            if(VERSION.SDK_INT < 11 || !CamcorderProfile.hasProfile(cameraId, 0)) {
                throw new IllegalStateException("cannot find valid CamcorderProfile");
            }

            recorder.setProfile(CamcorderProfile.get(cameraId, 0));
        } else {
            recorder.setProfile(CamcorderProfile.get(cameraId, 1));
        }

    }

    public int getCameraId() {
        if(this.cameraId == -1) {
            this.initCameraId();
        }

        return this.cameraId;
    }

    private void initCameraId() {
        int count = Camera.getNumberOfCameras();
        int result = -1;
        if(count > 0) {
            result = 0;
            CameraInfo info = new CameraInfo();

            for(int i = 0; i < count; ++i) {
                Camera.getCameraInfo(i, info);
                if(info.facing == 0 && !this.useFrontFacingCamera()) {
                    result = i;
                    break;
                }

                if(info.facing == 1 && this.useFrontFacingCamera()) {
                    result = i;
                    break;
                }
            }
        }

        this.cameraId = result;
    }

    public DeviceProfile getDeviceProfile() {
        if(this.profile == null) {
            this.initDeviceProfile(this.ctxt);
        }

        return this.profile;
    }

    private void initDeviceProfile(Context ctxt) {
        this.profile = DeviceProfile.getInstance(ctxt);
    }

    public Size getPictureSize(PictureTransaction xact, Parameters parameters) {
        return CameraUtils.getLargestPictureSize(this, parameters);
    }

    public Size getPreviewSize(int displayOrientation, int width, int height, Parameters parameters) {
        return CameraUtils.getBestAspectPreviewSize(displayOrientation, width, height, parameters);
    }

    @TargetApi(11)
    public Size getPreferredPreviewSizeForVideo(int displayOrientation, int width, int height, Parameters parameters, Size deviceHint) {
        return deviceHint != null?deviceHint:(VERSION.SDK_INT >= 11?parameters.getPreferredPreviewSizeForVideo():null);
    }

    public ShutterCallback getShutterCallback() {
        return null;
    }

    public void handleException(Exception e) {
        Log.e(this.getClass().getSimpleName(), "Exception in setPreviewDisplay()", e);
    }

    public boolean mirrorFFC() {
        return this.mirrorFFC;
    }

    public void saveImage(PictureTransaction xact, Bitmap bitmap) {
        instance.setImage(bitmap);
    }

    public void saveImage(PictureTransaction xact, byte[] image) {

    }

    @TargetApi(16)
    public void onAutoFocus(boolean success, Camera camera) {
        if(success && VERSION.SDK_INT >= 16) {
            (new MediaActionSound()).play(1);
        }

    }

    public boolean useSingleShotMode() {
        return this.useSingleShotMode;
    }

    public void autoFocusAvailable() {
    }

    public void autoFocusUnavailable() {
    }

    public RecordingHint getRecordingHint() {
        if(this.recordingHint == null) {
            this.initRecordingHint();
        }

        return this.recordingHint;
    }

    private void initRecordingHint() {
        this.recordingHint = this.profile.getDefaultRecordingHint();
        if(this.recordingHint == RecordingHint.NONE) {
            this.recordingHint = RecordingHint.ANY;
        }

    }

    public void onCameraFail(FailureReason reason) {
        //Log.e("CWAC-Camera", String.format("Camera access failed: %d", new Object[]{Integer.valueOf(reason.value)}));
    }

    public boolean useFullBleedPreview() {
        return this.useFullBleedPreview;
    }

    public float maxPictureCleanupHeapUsage() {
        return 1.0F;
    }

    protected File getPhotoPath() {
        File dir = this.getPhotoDirectory();
        dir.mkdirs();
        return new File(dir, this.getPhotoFilename());
    }

    protected File getPhotoDirectory() {
        if(this.photoDirectory == null) {
            this.initPhotoDirectory();
        }

        return this.photoDirectory;
    }

    private void initPhotoDirectory() {
        this.photoDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
    }

    protected String getPhotoFilename() {
        String ts = (new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)).format(new Date());
        return "Photo_" + ts + ".jpg";
    }

    protected File getVideoPath() {
        File dir = this.getVideoDirectory();
        dir.mkdirs();
        return new File(dir, this.getVideoFilename());
    }

    protected File getVideoDirectory() {
        if(this.videoDirectory == null) {
            this.initVideoDirectory();
        }

        return this.videoDirectory;
    }

    private void initVideoDirectory() {
        this.videoDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
    }

    protected String getVideoFilename() {
        String ts = (new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)).format(new Date());
        return "Video_" + ts + ".mp4";
    }

    protected boolean useFrontFacingCamera() {
        return this.useFrontFacingCamera;
    }

    protected boolean scanSavedImage() {
        return this.scanSavedImage;
    }

    public static class Builder {
        private MyCameraHost host;

        public Builder(Context ctxt) {
            this(new MyCameraHost(ctxt));
        }

        public Builder(MyCameraHost host) {
            this.host = null;
            this.host = host;
        }

        public MyCameraHost build() {
            return this.host;
        }

        public MyCameraHost.Builder cameraId(int cameraId) {
            this.host.cameraId = cameraId;
            return this;
        }

        public MyCameraHost.Builder deviceProfile(DeviceProfile profile) {
            this.host.profile = profile;
            return this;
        }

        public MyCameraHost.Builder mirrorFFC(boolean mirrorFFC) {
            this.host.mirrorFFC = mirrorFFC;
            return this;
        }

        public MyCameraHost.Builder photoDirectory(File photoDirectory) {
            this.host.photoDirectory = photoDirectory;
            return this;
        }

        public MyCameraHost.Builder recordingHint(RecordingHint recordingHint) {
            this.host.recordingHint = recordingHint;
            return this;
        }

        public MyCameraHost.Builder scanSavedImage(boolean scanSavedImage) {
            this.host.scanSavedImage = scanSavedImage;
            return this;
        }

        public MyCameraHost.Builder useFrontFacingCamera(boolean useFrontFacingCamera) {
            this.host.useFrontFacingCamera = useFrontFacingCamera;
            return this;
        }

        public MyCameraHost.Builder useFullBleedPreview(boolean useFullBleedPreview) {
            this.host.useFullBleedPreview = useFullBleedPreview;
            return this;
        }

        public MyCameraHost.Builder useSingleShotMode(boolean useSingleShotMode) {
            this.host.useSingleShotMode = useSingleShotMode;
            return this;
        }

        public MyCameraHost.Builder videoDirectory(File videoDirectory) {
            this.host.videoDirectory = videoDirectory;
            return this;
        }
    }
}
