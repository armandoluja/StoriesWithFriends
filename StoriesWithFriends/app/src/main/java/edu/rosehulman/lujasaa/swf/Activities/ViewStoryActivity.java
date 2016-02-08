package edu.rosehulman.lujasaa.swf.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import edu.rosehulman.lujasaa.swf.R;

public class ViewStoryActivity extends AppCompatActivity {

    public static final String STORY_KEY = "story_key";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_story);
    }
}
