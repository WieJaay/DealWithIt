package dealwithit.johannes_drescher.de.dealwithitapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import java.io.File;
import java.util.Date;

import dealwithit.johannes_drescher.de.dealwithitapp.R;
import dealwithit.johannes_drescher.de.dealwithitapp.camerautil.CameraManager;
import dealwithit.johannes_drescher.de.dealwithitapp.task.SavePhotoTask;

public class CameraActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ImageButton mFlipButton;
    private Animation animRotate;
    private CameraManager mCameraManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mFlipButton = (ImageButton) findViewById(R.id.flip_button);
        animRotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        initListeners();
        mCameraManager = new CameraManager(this,(FrameLayout)findViewById(R.id.camera_preview_layout));
        mCameraManager.startPreview();
    }

    private void initListeners() {
        mFlipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFlipButton.startAnimation(animRotate);
                mCameraManager.switchCamera();
            }
        });

        final ImageButton rec = (ImageButton) findViewById(R.id.rec_button);
        rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraManager.takePhoto();
            }
        });
    }

    public void setImage(Bitmap bitmap) {
        String filename = generateTimeStampedFileName();
        saveBitmap(bitmap, filename);

        Intent intent = new Intent();
        intent.putExtra("bitmapfilename", filename);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void saveBitmap(Bitmap bm, final String filename) {

        Looper.prepare();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.loading_image));
        progressDialog.setMessage("");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        File sd = Environment.getExternalStorageDirectory();
        File dest = new File(sd, filename + ".png");

        Object[] objects = new Object[2];
        objects[0] = bm;
        objects[1] = dest;
        new SavePhotoTask(new SavePhotoTask.onTaskComplete() {
            @Override
            public void fileSaved() {
                Intent intent = new Intent();
                intent.putExtra("bitmapfilename", filename);
                setResult(RESULT_OK, intent);
                finish();
                progressDialog.cancel();
            }
        }).execute(objects);
    }

    private String generateTimeStampedFileName() {
        Date d = new Date();
        return "DealWithIt-" + d.getDay() + "-" + d.getMonth() + "-" + d.getYear() + "-" + d.getHours() + ":" + d.getMinutes() + ":" + d.getSeconds() + ":";
    }
}