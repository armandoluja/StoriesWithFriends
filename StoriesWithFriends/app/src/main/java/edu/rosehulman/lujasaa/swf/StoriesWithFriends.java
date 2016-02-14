package edu.rosehulman.lujasaa.swf;

import android.app.Application;
import android.util.Log;

import com.batch.android.Batch;
import com.batch.android.Config;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

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
        Firebase firebase = new Firebase(Const.FIREBASE);

        if(firebase.getAuth() == null){
            Log.d("batch", "IN APP: firebase NOT authenticated");
        }else{
            Log.d("batch", "IN APP: firebase authenticated: "+ firebase.getAuth().getUid());
            Batch.User.getEditor().setIdentifier(firebase.getAuth().getUid());
        }
        //TODO:
        //Batch.Push.setSmallIconResourceId(R.drawable.push_icon);
        //Batch.Push.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.large_push_icon));
        //Batch.Push.setNotificationsColor(0xFF00FF00);
    }
}
