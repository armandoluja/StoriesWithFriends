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

import edu.rosehulman.lujasaa.swf.CompletedStory;
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
    private CompletedStory mStory;
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

        mStoryRef = new Firebase(Const.COMPLETE_STORY_REF + mStoryKey);

        mStoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("dataSnap", dataSnapshot.toString());
                CompletedStory mStory = dataSnapshot.getValue(CompletedStory.class);
                mStory = dataSnapshot.getValue(CompletedStory.class);
                mWebView.loadDataWithBaseURL("file:///android_asset/", mStory.getStory(), "text/html", "UTF-8", "");
            }

            @Override
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
