package edu.rosehulman.lujasaa.swf.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

import edu.rosehulman.lujasaa.swf.Const;
import edu.rosehulman.lujasaa.swf.Fragments.FriendRequestFragment;
import edu.rosehulman.lujasaa.swf.Fragments.FriendTopFragment;
import edu.rosehulman.lujasaa.swf.Fragments.FriendsFragment;
import edu.rosehulman.lujasaa.swf.Fragments.MyCompletedStoriesFragment;
import edu.rosehulman.lujasaa.swf.Fragments.MyCurrentStoriesFragment;
import edu.rosehulman.lujasaa.swf.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FriendTopFragment.Callback {

    private static final int LOGIN_REQUEST_CODE = 1;
    private final static String PREFS = "PREFS";
    public static final String AUTH_UID = "AUTH_UID";
    public static final String AUTH_EMAIL = "AUTH_EMAIL";
    public static String mUID;
    public static String mEmail;
    private FragmentManager mFragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //load the email and uid from shared prefs
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        mEmail = prefs.getString(AUTH_EMAIL, "");
        mUID = prefs.getString(AUTH_UID, "");

        Firebase firebase = new Firebase(Const.FIREBASE);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mFragmentManager = getSupportFragmentManager();
        if (firebase.getAuth() == null || isExpired(firebase.getAuth())) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivityForResult(loginIntent, LOGIN_REQUEST_CODE);
        } else {
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

    // store the email and uid
    @Override
    protected void onPause() {
        super.onPause();
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
            Bundle extras = data.getExtras();
            mUID = extras.getString(AUTH_UID);
            mEmail = extras.getString(AUTH_EMAIL);

            Firebase.setAndroidContext(this);
            mFragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.replace(R.id.fragment_container, new MyCurrentStoriesFragment());
            //clear the backstack so that pressing the back button will exit the application
            int nEntries = getSupportFragmentManager().getBackStackEntryCount();
            for (int i = 0; i < nEntries; i++) {
                mFragmentManager.popBackStackImmediate();
            }
            ft.commit();
//            Log.d("Extras", "onActivityResult 1: " + data.getStringExtra(AUTH_EMAIL) );
//            Log.d("Extras", "onActivityResult 2: " + extras.getString(AUTH_EMAIL) );
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
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivityForResult(loginIntent, LOGIN_REQUEST_CODE);
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
            // Handle the camera action
            Log.d("firebase", "printing out email from mainactivity: " + mEmail);
            switchTo = new MyCurrentStoriesFragment();
        } else if (id == R.id.nav_completed_stories) {
            switchTo = new MyCompletedStoriesFragment();
        } else if (id == R.id.nav_friends) {
            friendFragment(true);
        } else if (id == R.id.nav_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        } else if (id == R.id.nav_logout) {
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
            ft.replace(R.id.fragment_bottom_container, new FriendsFragment());
        } else if (!friend && !(mFragmentManager.findFragmentById(R.id.fragment_bottom_container) instanceof FriendRequestFragment)) {
            ft.replace(R.id.fragment_bottom_container, new FriendRequestFragment());
        }
        int nEntries = getSupportFragmentManager().getBackStackEntryCount();
        for (int i = 0; i < nEntries; i++) {
            getSupportFragmentManager().popBackStackImmediate();
        }
        ft.commit();
    }

    @Override
    public void onFragmentInteraction(Boolean friend) {
        friendFragment(friend);
    }
}