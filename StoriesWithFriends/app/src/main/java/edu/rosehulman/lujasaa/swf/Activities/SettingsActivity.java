package edu.rosehulman.lujasaa.swf.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.batch.android.Batch;

import edu.rosehulman.lujasaa.swf.R;
import edu.rosehulman.lujasaa.swf.User;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SettingsActivity extends AppCompatActivity {

    private ImageView mIcon;
    private TextView mUsername;
    private User mUser;


    @Override
    protected void onStop() {
        Batch.onStop(this);
        super.onStop();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Batch.onNewIntent(this, intent);
        super.onNewIntent(intent);
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
        setContentView(R.layout.activity_settings);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mUser = getIntent().getParcelableExtra("user");

        mIcon = (ImageView) findViewById(R.id.settings_profile_icon);
        mUsername = (TextView) findViewById(R.id.settings_username);

        if(mUser != null) {
            mIcon.setImageDrawable(ContextCompat.getDrawable(this, Integer.parseInt(mUser.getIcon())));
            mUsername.setText(mUser.getDisplayName());
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
}

