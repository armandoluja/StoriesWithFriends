package edu.rosehulman.lujasaa.swf.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

import edu.rosehulman.lujasaa.swf.Constants;
import edu.rosehulman.lujasaa.swf.Fragments.FriendsFragment;
import edu.rosehulman.lujasaa.swf.Fragments.MyCompletedStoriesFragment;
import edu.rosehulman.lujasaa.swf.Fragments.MyCurrentStoriesFragment;
import edu.rosehulman.lujasaa.swf.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int LOGIN_REQUEST_CODE = 1;

    public static final String AUTH_UID = "AUTH_UID";
    private Firebase mLoginRef;
    private String mFirebaseURLAfterAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Firebase.setAndroidContext(this);

        Firebase firebase = new Firebase(Constants.FIREBASE_URL);
        if (firebase.getAuth() == null || isExpired(firebase.getAuth())) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivityForResult(loginIntent, LOGIN_REQUEST_CODE);
        } else {
            //stay here
            mFirebaseURLAfterAuth = Constants.FIREBASE_URL + "/users/" + firebase.getAuth().getUid();
            mLoginRef = new Firebase(mFirebaseURLAfterAuth);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createStory = new Intent(view.getContext(), CreateStoryActivity.class);
                createStory.putExtra(Constants.FIREBASE, mFirebaseURLAfterAuth);
                startActivity(createStory);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == LOGIN_REQUEST_CODE){
            mFirebaseURLAfterAuth = Constants.FIREBASE_URL + "/users/" + data.getStringExtra(AUTH_UID);
            mLoginRef = new Firebase(mFirebaseURLAfterAuth);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onLogout() {
        //TODO: Log the user out.
        Firebase firebase = new Firebase(Constants.FIREBASE_URL);
        firebase.unauth();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

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
            switchTo = new MyCurrentStoriesFragment();
        } else if (id == R.id.nav_completed_stories) {
            switchTo = new MyCompletedStoriesFragment();
        } else if (id == R.id.nav_friends) {
            switchTo = new FriendsFragment();
        } else if (id == R.id.nav_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        } else if (id == R.id.nav_logout) {
//            Intent loginIntent = new Intent(this, LoginActivity.class);
//            startActivity(loginIntent);
            onLogout();
        }

        if (switchTo != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, switchTo);
            //clear the backstack so that pressing the back button will exit the application
            int nEntries = getSupportFragmentManager().getBackStackEntryCount();
            for(int i = 0 ; i < nEntries ; i ++){
                getSupportFragmentManager().popBackStackImmediate();
            }
            ft.commit();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
