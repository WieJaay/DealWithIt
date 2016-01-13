package dealwithit.johannes_drescher.de.dealwithitapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import dealwithit.johannes_drescher.de.dealwithitapp.R;
import dealwithit.johannes_drescher.de.dealwithitapp.task.ReadPhotoTask;
import dealwithit.johannes_drescher.de.dealwithitapp.task.SavePhotoTask;
import dealwithit.johannes_drescher.de.dealwithitapp.util.DragPinchRotateTouchListener;
import dealwithit.johannes_drescher.de.dealwithitapp.util.ImageUtil;
import dealwithit.johannes_drescher.de.dealwithitapp.view.PhotoView;

public class EditPhotoActivity extends AppCompatActivity {

    private Bitmap photo;
    private final String TAG = "EditPhotoActivity";
    private PhotoView mPhotoViewGlasses;
    private PhotoView mPhotoViewPicture;
    private ImageButton mCheckORSaveButton;
    private ImageButton mCancelButton;
    private boolean mCheck;
    private boolean mShare;
    private Uri muriToImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_photo);
        mCheck = true;
        mShare = false;

        mCheckORSaveButton = (ImageButton) findViewById(R.id.checkORsave_EDIT_PHOTO);
        mCancelButton = (ImageButton) findViewById(R.id.cancel_EDIT_PHOTO);

        mPhotoViewGlasses = (PhotoView) findViewById(R.id.glasses_EDIT_PHOTO);
        DragPinchRotateTouchListener l1 = new DragPinchRotateTouchListener(this, mPhotoViewGlasses);
        mPhotoViewGlasses.setOnTouchListener(l1);
        mPhotoViewGlasses.setOnDataChangeListener(l1.getOnDataChangeListener());
        mPhotoViewGlasses.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.glasses));
        mPhotoViewGlasses.setVisibility(View.INVISIBLE);

        mPhotoViewPicture = (PhotoView) findViewById(R.id.picture);
        DragPinchRotateTouchListener l2 = new DragPinchRotateTouchListener(this, mPhotoViewPicture);
        mPhotoViewPicture.setBackgroundColor(getResources().getColor(R.color.black_overlay));
        mPhotoViewPicture.setOnTouchListener(l2);
        mPhotoViewPicture.setOnDataChangeListener(l2.getOnDataChangeListener());

        initListeners();
        readPhoto();
    }

    private void readPhoto() {

        final ProgressDialog pg = new ProgressDialog(this);
        pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pg.setTitle(getResources().getString(R.string.loading_image));
        pg.show();

        File sd = Environment.getExternalStorageDirectory();
        String path = getIntent().getStringExtra("filepath");
        File dest = new File(sd, path + ".png");

        new ReadPhotoTask(new ReadPhotoTask.onTaskComplete() {
            @Override
            public void fileRead(Bitmap bm) {
                photo = bm;
                mPhotoViewPicture.setBitmap(ImageUtil.rotateAndRenderBitmap(bm,270f));
                pg.cancel();
                RelativeLayout image_wrapper = (RelativeLayout) findViewById(R.id.image_wrapper_EDIT_PHOTO);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, photo.getWidth());
                params.addRule(RelativeLayout.ABOVE, R.id.buttonPanel);
                image_wrapper.setLayoutParams(params);
                image_wrapper.requestLayout();

                RelativeLayout squareMaker = (RelativeLayout) findViewById(R.id.squareMaker);
                params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_TOP, R.id.image_wrapper_EDIT_PHOTO);
                squareMaker.setLayoutParams(params);
                squareMaker.setBackgroundColor(getResources().getColor(R.color.lightBlue));
                squareMaker.requestLayout();

                Toast.makeText(EditPhotoActivity.this, getResources().getString(R.string.position_image), Toast.LENGTH_LONG).show();
            }
        }).execute(dest);
    }

    private void initListeners() {
        mCheckORSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mShare) {
                    if (mCheck) {
                        EditPhotoActivity.this.mPhotoViewGlasses.setVisibility(View.VISIBLE);
                        ((ImageView) EditPhotoActivity.this.findViewById(R.id.DealWithIt_EDIT_PHOTO)).setVisibility(View.VISIBLE);
                        Toast.makeText(EditPhotoActivity.this, getResources().getString(R.string.position_glasses), Toast.LENGTH_LONG).show();
                        mCheckORSaveButton.setBackground(getResources().getDrawable(R.drawable.save));
                        mCheck = false;
                    } else {
                        final ProgressDialog progressDialog = new ProgressDialog(EditPhotoActivity.this);
                        progressDialog.setTitle(getResources().getString(R.string.saveing_image));
                        progressDialog.setMessage("");
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.show();
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                        Bitmap image = ImageUtil.takeScreenshot((RelativeLayout) findViewById(R.id.image_wrapper_EDIT_PHOTO));
                        File sd = Environment.getExternalStorageDirectory();
                        File dest = new File(sd, "Deal_With_It " + dateFormat.format(new Date()) + ".png");
                        muriToImage = Uri.fromFile(dest);//Test
                        new SavePhotoTask(new SavePhotoTask.onTaskComplete() {
                            @Override
                            public void fileSaved() {
                                progressDialog.cancel();
                                mCheckORSaveButton.setBackground(getResources().getDrawable(R.drawable.send));
                                mCheckORSaveButton.getLayoutParams().width = (int) (mCheckORSaveButton.getHeight()*0.715);
                                mCheckORSaveButton.requestLayout();
                                mShare = true;
                            }
                        }).execute(new Object[]{image, dest});
                    }
                } else {
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, muriToImage);
                    shareIntent.setType("image/*");
                    startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.share)));
                }
            }
        });


        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditPhotoActivity.this.finish();
            }
        });
    }
}
