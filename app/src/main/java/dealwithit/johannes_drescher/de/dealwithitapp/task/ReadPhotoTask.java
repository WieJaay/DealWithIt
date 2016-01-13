package dealwithit.johannes_drescher.de.dealwithitapp.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import dealwithit.johannes_drescher.de.dealwithitapp.R;

/**
 * Created by Johannes on 07.01.16.
 */
public class ReadPhotoTask extends AsyncTask<File,Void,Bitmap>{

    private onTaskComplete taskComplete;

    public ReadPhotoTask(onTaskComplete onTaskComplete) {
        taskComplete = onTaskComplete;
    }

    @Override
    protected Bitmap doInBackground(File... file) {
        Bitmap photo = null;
        FileInputStream streamIn = null;
        try {
            streamIn = new FileInputStream(file[0]);
            photo = BitmapFactory.decodeStream(streamIn);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        file[0].delete();
        return photo;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        taskComplete.fileRead(bitmap);
    }

    public interface onTaskComplete{void fileRead(Bitmap bm);}
}
