package dealwithit.johannes_drescher.de.dealwithitapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import dealwithit.johannes_drescher.de.dealwithitapp.R;
import dealwithit.johannes_drescher.de.dealwithitapp.view.PhotoView;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private final int CAMERA_ACTIVITY_REQUEST_CODE = 21;
    private final int EDIT_PHOTO_REQUEST_CODE = 22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this,CameraActivity.class);
        startActivityForResult(intent, CAMERA_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CAMERA_ACTIVITY_REQUEST_CODE) {
            Intent intent = new Intent(this,EditPhotoActivity.class);
            intent.putExtra("bitmapfilename", data.getExtras().getString("bitmapfilename"));
            startActivityForResult(intent, EDIT_PHOTO_REQUEST_CODE);
        }
        if(requestCode==EDIT_PHOTO_REQUEST_CODE) {
            Intent intent = new Intent(this,CameraActivity.class);
            startActivityForResult(intent, CAMERA_ACTIVITY_REQUEST_CODE);
        }
    }
}
