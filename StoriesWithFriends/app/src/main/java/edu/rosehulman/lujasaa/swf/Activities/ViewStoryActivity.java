package edu.rosehulman.lujasaa.swf.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;

import com.batch.android.Batch;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import edu.rosehulman.lujasaa.swf.Const;
import edu.rosehulman.lujasaa.swf.R;
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

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mWebView = (WebView) findViewById(R.id.web_view_story);

        mStoryKey = getIntent().getStringExtra(STORY_KEY);
        mColors = Const.CSS_COLOR_CHOICES;
        mUsedColors = new ArrayList<>();
        mUserColors = new HashMap<>();
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
                for(String user: mMembers){
                    int resource = getResources().getIdentifier(mUserColors.get(user), "string", getPackageName());
                    String color = getString(resource);
                    mCustomHtml.append("<p><span class='box' style='background:"+color+"'></span>"+ user+"<p>");
                }
                mCustomHtml.append("</body></html>");
                mWebView.loadDataWithBaseURL("file:///android_asset/", mCustomHtml.toString() , "text/html", "UTF-8", "");
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


}
