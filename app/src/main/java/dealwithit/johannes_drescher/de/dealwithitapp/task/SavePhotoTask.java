package dealwithit.johannes_drescher.de.dealwithitapp.task;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Johannes on 07.01.16.
 */
public class SavePhotoTask extends AsyncTask<Object[], Void, Void> {

    private onTaskComplete taskComplete;

    public SavePhotoTask(onTaskComplete t) {
        taskComplete = t;
    }

    @Override
    protected Void doInBackground(Object[]... params) {
        Object a = params[0][0];
        Object b = params[0][1];
        Bitmap bitmap = (Bitmap)a;
        File dest = (File)b;

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(dest);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        taskComplete.fileSaved();
    }

    public interface onTaskComplete{void fileSaved();}
}
