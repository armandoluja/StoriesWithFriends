package edu.rosehulman.lujasaa.swf.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.batch.android.Batch;
import com.batch.android.Config;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import edu.rosehulman.lujasaa.swf.Const;
import edu.rosehulman.lujasaa.swf.Fragments.FriendRequestFragment;
import edu.rosehulman.lujasaa.swf.Fragments.FriendTopFragment;
import edu.rosehulman.lujasaa.swf.Fragments.FriendsFragment;
import edu.rosehulman.lujasaa.swf.Fragments.MyCompletedStoriesFragment;
import edu.rosehulman.lujasaa.swf.Fragments.MyCurrentStoriesFragment;
import edu.rosehulman.lujasaa.swf.NewUserDialog;
import edu.rosehulman.lujasaa.swf.R;
import edu.rosehulman.lujasaa.swf.User;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FriendTopFragment.Callback {

    private static final int LOGIN_REQUEST_CODE = 1;
    private final static String PREFS = "PREFS";
    public static final String AUTH_UID = "AUTH_UID";
    public static final String AUTH_EMAIL = "AUTH_EMAIL";
    public static String mUID;
    public static String mEmail;
    public static String packageName;
    private FragmentManager mFragmentManager;
    private Firebase mFirebase;
    private User mUser;

    private FriendsFragment friendFragment;
    private FriendRequestFragment friendRequestFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Firebase.setAndroidContext(this);
        Log.d("batch", "onCreate: ----- MAIN WAS CALLED ----");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //load the email and uid from shared prefs
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        mEmail = prefs.getString(AUTH_EMAIL, "");
        mUID = prefs.getString(AUTH_UID, "");

        mFragmentManager = getSupportFragmentManager();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        friendFragment = new FriendsFragment();
        friendRequestFragment = new FriendRequestFragment();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Batch.onStart(this);
        packageName = getPackageName();

        mFirebase = new Firebase(Const.FIREBASE);
        Log.d("batch", "on start was called ------: ");
        if (mFirebase.getAuth() == null || isExpired(mFirebase.getAuth())) {
            Log.d("batch","Main> Firebase NOT authenticated");
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivityForResult(loginIntent, LOGIN_REQUEST_CODE);
        } else {
            if(mEmail == null || mEmail.equals("")){
                Firebase getUserInfo = new Firebase(Const.REPO_REF + mFirebase.getAuth().getUid());
                Log.d("batch", "the current authd user is : " + mFirebase.getAuth().getUid());
                getUserInfo.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mEmail = dataSnapshot.getValue().toString();
                        Log.d("batch", "pos 2" + MainActivity.mEmail);
                        checkUsername();
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }else{
                continueLoadingOnStart();
            }
        }

    }

    public void continueLoadingOnStart(){
        Batch.User.getEditor().setIdentifier(mFirebase.getAuth().getUid());
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.replace(R.id.fragment_container, new MyCurrentStoriesFragment());
        //clear the backstack so that pressing the back button will exit the application
        int nEntries = getSupportFragmentManager().getBackStackEntryCount();
        for (int i = 0; i < nEntries; i++) {
            mFragmentManager.popBackStackImmediate();
        }
        ft.commit();
    }

    public void checkUsername(){
        //if the user doesn't have a displayname
        Log.d("mainmethod", MainActivity.mEmail);
        Firebase firebase = new Firebase(Const.USER_REF + mEmail);
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("login", dataSnapshot.toString());
                User user = dataSnapshot.getValue(User.class);
                mUser = user;
                ImageView navImage = (ImageView) findViewById(R.id.image_nav_drawer);

                TextView navText = (TextView) findViewById(R.id.text_nav_drawer);
                // mUser may be null if the user was just recently registered
                if(mUser != null && !mUser.getDisplayName().equals("Default Username") && !mUser.getIcon().equals("defaultIcon")){
                    //cant parse default username to and int
                    navImage.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), Integer.parseInt(mUser.getIcon())));
                    navText.setText(mUser.getDisplayName());
                }else{
                    NewUserDialog df = new NewUserDialog();
                    df.setCancelable(false);
                    df.show(mFragmentManager, "newUser");
                }

                continueLoadingOnStart();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    // store the email and uid
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("batch", "Main.OnPause was called.");
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(AUTH_UID, mUID);
        editor.putString(AUTH_EMAIL, mEmail);
        // Put the other fields into the editor
        editor.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST_CODE) {
            Log.d("batch", "Main.OnActivity result was called");

            Bundle extras = data.getExtras();
            mUID = extras.getString(AUTH_UID);
            mEmail = extras.getString(AUTH_EMAIL);

            Log.d("batch", "pos 1" + MainActivity.mEmail);
            checkUsername();

//        Firebase.setAndroidContext(this);
            mFragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.replace(R.id.fragment_container, new MyCurrentStoriesFragment());
            //clear the backstack so that pressing the back button will exit the application
            int nEntries = getSupportFragmentManager().getBackStackEntryCount();
            for (int i = 0; i < nEntries; i++) {
                mFragmentManager.popBackStackImmediate();
            }
            ft.commit();
        }
    }

    public void onLogout() {
        //TODO: Log the user out.
        Firebase firebase = new Firebase(Const.FIREBASE);
        firebase.unauth();
        int nEntries = getSupportFragmentManager().getBackStackEntryCount();
        for (int i = 0; i < nEntries; i++) {
            mFragmentManager.popBackStackImmediate();
        }
        Batch.User.getEditor().setIdentifier(null);
        mEmail = null;
        mUID = null;
        this.onStart();
//        Intent loginIntent = new Intent(this, LoginActivity.class);
//        startActivityForResult(loginIntent, LOGIN_REQUEST_CODE);
    }

    private boolean isExpired(AuthData authData) {
        return (System.currentTimeMillis() / 1000) >= authData.getExpires();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment switchTo = null;

        if (id == R.id.nav_current_stories) {
            Log.d("firebase", "printing out email from mainactivity: " + mEmail);
            switchTo = new MyCurrentStoriesFragment();
            setTitle("SWF: My Current Stories");
        } else if (id == R.id.nav_completed_stories) {
            switchTo = new MyCompletedStoriesFragment();
            setTitle("SWF: My Completed Stories");

        } else if (id == R.id.nav_friends) {
            friendFragment(true);
            setTitle("SWF: Friends");
        } else if (id == R.id.nav_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        } else if (id == R.id.nav_logout) {
            setTitle("Stories With Friends");
            onLogout();
        }

        if (switchTo != null) {
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.replace(R.id.fragment_container, switchTo);
            //clear the backstack so that pressing the back button will exit the application
            int nEntries = getSupportFragmentManager().getBackStackEntryCount();
            for (int i = 0; i < nEntries; i++) {
                mFragmentManager.popBackStackImmediate();
            }
            ft.commit();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //friend : true if on friends tab, false if on friend request tab
    private void friendFragment(Boolean friend) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();

        //doesn't replace if already on the friends tab
        if (!(mFragmentManager.findFragmentById(R.id.fragment_container) instanceof FriendTopFragment)) {
            ft.replace(R.id.fragment_container, new FriendTopFragment());
        }
        if (friend && !(mFragmentManager.findFragmentById(R.id.fragment_bottom_container) instanceof FriendsFragment)) {
            ft.replace(R.id.fragment_bottom_container, friendFragment);
        } else if (!friend && !(mFragmentManager.findFragmentById(R.id.fragment_bottom_container) instanceof FriendRequestFragment)) {
            ft.replace(R.id.fragment_bottom_container, friendRequestFragment);
        }
        int nEntries = getSupportFragmentManager().getBackStackEntryCount();
        for (int i = 0; i < nEntries; i++) {
            getSupportFragmentManager().popBackStackImmediate();
        }
        ft.commit();
    }

    public FragmentManager getmFragmentManager(){
        return mFragmentManager;
    }

    @Override
    public void onFragmentInteraction(Boolean friend) {
        friendFragment(friend);
    }


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
}