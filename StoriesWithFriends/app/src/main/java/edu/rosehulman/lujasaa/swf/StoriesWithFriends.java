package edu.rosehulman.lujasaa.swf;

import android.app.Application;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.batch.android.Batch;
import com.batch.android.BatchPushReceiver;
import com.batch.android.BatchPushService;
import com.batch.android.BatchUserDataEditor;
import com.batch.android.Config;
import com.batch.android.PushNotificationType;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import edu.rosehulman.lujasaa.swf.Activities.MainActivity;

/**
 * Created by lujasaa on 2/12/2016.
 */
public class StoriesWithFriends extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("batch", "onCreate: ----- APP WAS CALLED ----");
        Firebase.setAndroidContext(this);
        Batch.setConfig(new Config(Const.BATCH_PUSH_NOTIFICATIONS_API_KEY));
        Batch.Push.setGCMSenderId(Const.GOOGLE_STORIES_WITH_FRIENDS_PROJECT_NUMBER);
        Batch.User.getEditor().setIdentifier(null);
        Firebase firebase = new Firebase(Const.FIREBASE);

        if(firebase.getAuth() == null){
            Log.d("batch", "IN APP: firebase NOT authenticated");
        }else{
            Log.d("batch", "IN APP: firebase authenticated uid : " + firebase.getAuth().getUid());
        }
        Batch.Push.setSmallIconResourceId(R.drawable.ic_stat_notification);
        Batch.Push.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.notification_large));
    }
}
