package edu.rosehulman.lujasaa.swf.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.batch.android.Batch;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import edu.rosehulman.lujasaa.swf.Const;
import edu.rosehulman.lujasaa.swf.NewUserDialog;
import edu.rosehulman.lujasaa.swf.R;
import edu.rosehulman.lujasaa.swf.User;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SettingsActivity extends AppCompatActivity implements NewUserDialog.Callback{

    private ImageView mIcon;
    private TextView mUsername;
    private User mUser;
    private ImageView editUser;
    private CheckBox vibrateCheck;
    private CheckBox ledLightCheck;
    private CheckBox headsUpCheck;
    private CheckBox wifiCheck;
    private SharedPreferences.Editor mPrefs;


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
        setContentView(R.layout.activity_settings);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        SharedPreferences prefRef = getSharedPreferences(Const.SETTING_PREFERENCES, MODE_PRIVATE);
        mPrefs = prefRef.edit();

        mIcon = (ImageView) findViewById(R.id.settings_profile_icon);
        mUsername = (TextView) findViewById(R.id.settings_username);
        vibrateCheck = (CheckBox) findViewById(R.id.settings_vibrate_checkbox);
        ledLightCheck = (CheckBox) findViewById(R.id.settings_led_light_checkbox);
        headsUpCheck = (CheckBox) findViewById(R.id.settings_heads_up_notifications);
        wifiCheck = (CheckBox) findViewById(R.id.settings_data_checkbox);
        editUser = (ImageView) findViewById(R.id.edit_user_settings);

        //set initial checked
        vibrateCheck.setChecked(prefRef.getBoolean(vibrateCheck.getText().toString(), true));
        ledLightCheck.setChecked(prefRef.getBoolean(ledLightCheck.getText().toString(), true));
        headsUpCheck.setChecked(prefRef.getBoolean(headsUpCheck.getText().toString(), true));
        wifiCheck.setChecked(prefRef.getBoolean(wifiCheck.getText().toString(), false));

        //add to prefs on changed
        vibrateCheck.setOnCheckedChangeListener(new OnCheckChangeListener(vibrateCheck));
        ledLightCheck.setOnCheckedChangeListener(new OnCheckChangeListener(ledLightCheck));
        headsUpCheck.setOnCheckedChangeListener(new OnCheckChangeListener(headsUpCheck));
        wifiCheck.setOnCheckedChangeListener(new OnCheckChangeListener(wifiCheck));

        Firebase mFirebase = new Firebase(Const.USER_REF + MainActivity.mEmail);
        mFirebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(User.class);
                mIcon.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), Integer.parseInt(mUser.getIcon())));
                mUsername.setText(mUser.getDisplayName());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        editUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewUserDialog df = new NewUserDialog();
                Bundle args = new Bundle();
                args.putBoolean("isNewUser", false);
                args.putString("displayName", mUser.getDisplayName());
                args.putString("icon", mUser.getIcon());
                df.setArguments(args);
                df.show(getSupportFragmentManager(), "newUser");
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
    public void onSet(String displayName, String icon) {
        mIcon.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), Integer.parseInt(icon)));
        mUsername.setText(displayName);
    }

    private class OnCheckChangeListener implements CompoundButton.OnCheckedChangeListener {

        private CheckBox mCheckBox;

        private OnCheckChangeListener(CheckBox checkBox){
            mCheckBox = checkBox;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mPrefs.putBoolean(mCheckBox.getText().toString(), isChecked);
            mPrefs.commit();
        }
    }
}

