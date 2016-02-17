package edu.rosehulman.lujasaa.swf.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.batch.android.Batch;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import edu.rosehulman.lujasaa.swf.Const;
import edu.rosehulman.lujasaa.swf.R;
import edu.rosehulman.lujasaa.swf.SavePhotoTask;
import edu.rosehulman.lujasaa.swf.Story;
import edu.rosehulman.lujasaa.swf.StoryFragment;

public class ViewStoryActivity extends AppCompatActivity {

    public static final String STORY_KEY = "story_key";
    public WebView mWebView;
    public String mStoryKey;
    private Firebase mStoryRef;
    private Firebase mStoryFragmentRef;
    ArrayList<StoryFragment> mStoryFragments;
    private Story mStory;
    private HashMap<String, String> mUserColors;
    private String[] mColors;
    private ArrayList<String> mUsedColors;
    private StringBuilder mCustomHtml;
    private ArrayList<String> mMembers;
    private Context mContext;
    private Firebase mUserRef;
    private HashMap<String, String> mUNameDisplayName;


    @Override
    protected void onStop() {
        Batch.onStop(this);
        super.onStop();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Batch.onNewIntent(this, intent);
    }

    @Override
    protected void onDestroy() {
        Batch.onDestroy(this);
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Batch.onStart(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_story);
        mContext = this;

        mUserRef = new Firebase(Const.USER_REF);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mWebView = (WebView) findViewById(R.id.web_view_story);
        mStoryKey = getIntent().getStringExtra(STORY_KEY);
        mColors = Const.CSS_COLOR_CHOICES;
        mUsedColors = new ArrayList<>();
        mUserColors = new HashMap<>();
        mUNameDisplayName = new HashMap<>();
        mStoryFragments = new ArrayList<>();
        mMembers = new ArrayList<>();

        mCustomHtml = new StringBuilder();
        mCustomHtml.append("<html>");
        mCustomHtml.append("<head>");
        mCustomHtml.append("<link rel=stylesheet href='css/style.css'>");

        mStoryRef = new Firebase(Const.STORY_REF + mStoryKey);
        mStoryRef.addValueEventListener(new StoryChildEventListener());

        mStoryRef.addListenerForSingleValueEvent(new ValueEventListener() { //once data is initialized
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCustomHtml.append("</head>");
                mCustomHtml.append("<body>");
                mCustomHtml.append("<h1 class='centered'>" + mStory.getStoryname() + "</h1>");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });



        mStoryFragmentRef = new Firebase(Const.STORY_FRAGMENTS_REF + mStoryKey + "/");
        Query fragments = mStoryFragmentRef.orderByChild("position");





        fragments.addChildEventListener(new StoryFragmentChildEventListener());


        fragments.addListenerForSingleValueEvent(new ValueEventListener() { //runs once initial data is done
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCustomHtml.append("</p>");
                for (final String user : mMembers) {
                        int resource = getResources().getIdentifier(mUserColors.get(user), "string", getPackageName());
                        String color = getString(resource);
                        Log.d("db", mUNameDisplayName.toString());
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                        while(mUNameDisplayName.size() != mMembers.size()){
                            Log.d("db","mUN: "+ mUNameDisplayName.size());
                            try {
                                thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        mCustomHtml.append("<p><span class='box' style='background:" + color + "'></span>" + mUNameDisplayName.get(user) + "<p>");
                        mUserRef.child(user).child("displayName").removeEventListener(this);
                }
                mCustomHtml.append("</body></html>");
                mWebView.loadDataWithBaseURL("file:///android_asset/", mCustomHtml.toString(), "text/html", "UTF-8", "");


            }

            public void onCancelled(FirebaseError firebaseError) {
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateStoryTextView(StoryFragment storyFragment) {
        mCustomHtml.append("<span class='" + mUserColors.get(storyFragment.getSender()) + "'>" + storyFragment.getText() + " </span>");
    }


    private class StoryChildEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mStory = dataSnapshot.getValue(Story.class);
            mMembers = mStory.getMembers();
            for(final String user: mMembers){
                mUserRef.child(user).child("displayName").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("db", "here");
                        mUNameDisplayName.put(user, (String) dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }
            while(mUsedColors.size() != mMembers.size()){
                int rnd = new Random().nextInt(mColors.length);
                if(mUsedColors.indexOf(mColors[rnd]) == -1){
                    mUserColors.put(mMembers.get(mUsedColors.size()), mColors[rnd]);
                    mUsedColors.add(mColors[rnd]);
                }


            }
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    }

    private class StoryFragmentChildEventListener implements ChildEventListener {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            StoryFragment storyFragment = dataSnapshot.getValue(StoryFragment.class);
            mStoryFragments.add(storyFragment);
            updateStoryTextView(storyFragment);
        }



        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.write_story_menu, menu);
        menu.clear();
        MenuItem saveAsPhoto = menu.add("Save as Photo");
        saveAsPhoto.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mWebView.measure(View.MeasureSpec.makeMeasureSpec(
                                View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                mWebView.layout(0, 0, mWebView.getMeasuredWidth(),
                        mWebView.getMeasuredHeight());
                mWebView.setDrawingCacheEnabled(true);
                mWebView.buildDrawingCache();
                Bitmap bm = Bitmap.createBitmap(mWebView.getMeasuredWidth(),
                        mWebView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

                Canvas bigcanvas = new Canvas(bm);
                Paint paint = new Paint();
                int iHeight = bm.getHeight();
                bigcanvas.drawBitmap(bm, 0, iHeight, paint);
                mWebView.draw(bigcanvas);
                savePhoto(bm);
                return true;
            }
        });
        return true;
    }

    private void savePhoto(Bitmap bm) {
        SavePhotoTask task = new SavePhotoTask(this);
        task.execute(bm);
    }


}
