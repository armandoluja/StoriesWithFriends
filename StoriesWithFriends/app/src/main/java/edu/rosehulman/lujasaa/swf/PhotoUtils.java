package edu.rosehulman.lujasaa.swf;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by sanderkd on 2/16/2016.
 */
public class PhotoUtils {
    static Uri getOutputMediaUri(String folder) {
        File storageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                folder);

        Log.d(Const.TAG, "Media to be stored at " + storageDir.getPath());

        // Create the storage directory if it does not exist
        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                Log.d(Const.TAG, "Failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                .format(new Date());
        File mediaFile = new File(storageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");
        // Return the URI of the file.
        return Uri.fromFile(mediaFile);
    }

}