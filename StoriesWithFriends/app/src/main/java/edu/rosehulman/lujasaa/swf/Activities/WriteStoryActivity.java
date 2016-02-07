package edu.rosehulman.lujasaa.swf.Activities;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.firebase.client.Firebase;

import edu.rosehulman.lujasaa.swf.R;

/**
 * Game room. Stories are written here.
 */
public class WriteStoryActivity extends AppCompatActivity {

    public static final String STORY_KEY = "story_key";
    // The key of the current story to be used/written.
    // It must be used to retrieve all the story fragments.
    private String mStoryKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Capture the story key
        mStoryKey = getIntent().getStringExtra(STORY_KEY);
        setContentView(R.layout.activity_write_story);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
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
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
    }
}
