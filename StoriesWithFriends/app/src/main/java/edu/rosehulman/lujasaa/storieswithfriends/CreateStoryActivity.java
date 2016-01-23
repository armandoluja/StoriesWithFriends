package edu.rosehulman.lujasaa.storieswithfriends;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Spinner;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class CreateStoryActivity extends AppCompatActivity {
    private Button mCreateBtn;
    private Button mCancelBtn;
    private Context mContext;
    private Spinner mWordSpinner;
    private Spinner mTimeSpinner;
    private GridView mGridView;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_story);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mContext = getBaseContext();
        mCreateBtn = (Button)findViewById(R.id.create_story_button);
        mCancelBtn = (Button)findViewById(R.id.cancel_create_button);
        mWordSpinner = (Spinner)findViewById(R.id.create_story_word_limit_spinner);
        mTimeSpinner = (Spinner)findViewById(R.id.create_story_time_limit_spinner);
        mGridView = (GridView)findViewById(R.id.create_story_grid_view);
        mListView = (ListView)findViewById(R.id.create_story_list_view);

        ArrayAdapter<CharSequence> wordLimitAdapter = ArrayAdapter.createFromResource(this,
                R.array.word_limit_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> timeLimitAdapter = ArrayAdapter.createFromResource(this,
                R.array.time_limit_array, android.R.layout.simple_spinner_item);
        wordLimitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeLimitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mWordSpinner.setAdapter(wordLimitAdapter);
        mTimeSpinner.setAdapter(timeLimitAdapter);


        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent writeStory = new Intent(mContext, WriteStoryActivity.class);
                startActivity(writeStory);
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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

}
