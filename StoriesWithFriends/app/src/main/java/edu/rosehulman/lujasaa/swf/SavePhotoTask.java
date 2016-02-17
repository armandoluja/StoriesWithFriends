package edu.rosehulman.lujasaa.swf;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.SyncStateContract;
import android.util.Log;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by sanderkd on 2/16/2016.
 */
public class SavePhotoTask extends AsyncTask<Bitmap, Integer, Boolean> {

    private Context mContext;

    public SavePhotoTask(Context context) {
        mContext = context;
    }

    @Override
    protected Boolean doInBackground(Bitmap... bitmaps) {
        Uri photoUri = PhotoUtils.getOutputMediaUri(mContext.getString(R.string.app_name));

        if (photoUri == null) {
            Log.d(Const.TAG, "Error creating media file, check storage permissions");
            return false;
        }

        try {
            FileOutputStream fos = new FileOutputStream(photoUri.getPath());
            bitmaps[0].compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    photoUri));
        } catch (FileNotFoundException e) {
            Log.d(Const.TAG, "File not found: " + e.getMessage());
            return false;
        } catch (IOException e) {
            Log.d(Const.TAG, "Error accessing file: " + e.getMessage());
            return false;
        }
        return true;
    }

    protected void onPostExecute(Boolean result) {
        if (result) {
            // Log.d(TAG, "saved picture successfully");
            Toast.makeText(mContext, "Saved", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "Save Failed", Toast.LENGTH_SHORT).show();
            Log.d(Const.TAG, "saving picture failed");
        }
    }
}

