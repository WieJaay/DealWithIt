package dealwithit.johannes_drescher.de.dealwithitapp.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import dealwithit.johannes_drescher.de.dealwithitapp.R;

public class CameraActivity extends AppCompatActivity {

    private static final String TAG = "CameraActivity";
    private final int CAMERA_REQUEST_CODE = 11;
    private String mPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        takePhoto();
    }

    private void takePhoto(){
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempFile(this)) );
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    private File getTempFile(Context context){
        File sd = Environment.getExternalStorageDirectory();
        mPath = generateTimestampedFileName("DealWithIt");
        File dest = new File(sd, mPath + ".png");
        return dest;
    }

    private String generateTimestampedFileName(String s) {
        return s + new SimpleDateFormat("yyyy-mm-dd HH:mm:ss").format(new Date());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch(requestCode){
                case CAMERA_REQUEST_CODE:
                    Intent intent = new Intent();
                    intent.putExtra("filepath",mPath);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
            }
        }
    }
}