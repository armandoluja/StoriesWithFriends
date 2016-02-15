package edu.rosehulman.lujasaa.swf.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.batch.android.Batch;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Random;

import edu.rosehulman.lujasaa.swf.Const;
import edu.rosehulman.lujasaa.swf.R;
import edu.rosehulman.lujasaa.swf.Story;
import edu.rosehulman.lujasaa.swf.StoryFragment;
import edu.rosehulman.lujasaa.swf.User;

/**
 * Game room. Stories are written here.
 */
public class WriteStoryActivity extends AppCompatActivity {

    public static final String STORY_KEY = "story_key";
    // The key of the current story to be used/written.
    // It must be used to retrieve all the story fragments.
    private Firebase mUserRef;
    private Firebase mStoryFragmentRef;
    private Firebase mStoryRef;
    private String mStoryKey;
    private Button mAddToStoryButton;
    private EditText mEditText;
    private String mEmail;
    private TextView mTextViewCurrentTurn;
    private TextView mStoryTextView;

    private String mCurrentTurn;//the email of the player who currently has a turn
    private String mCurrentTurnString;

    // Contains every members's contributions to the story
    private ArrayList<StoryFragment> mStoryFragments = new ArrayList<StoryFragment>();
    private int mWordLimit;
    private Story mStory;


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
        setContentView(R.layout.activity_write_story);
        Log.d("batch", "onCreate: ----- WRITE STORY WAS CALLED ----");
        mEmail = MainActivity.mEmail;
        Log.d("TAG", "MainActivity.mEmail: " + MainActivity.mEmail);

        mAddToStoryButton = (Button) findViewById(R.id.button_add_to_story);
        mEditText = (EditText) findViewById(R.id.edit_text_add_to_story);
        mTextViewCurrentTurn = (TextView) findViewById(R.id.write_story_activity_current_turn_textview);
        mStoryTextView = (TextView) findViewById(R.id.story_text_view);

        mAddToStoryButton.setOnClickListener(new AddToStoryOnClickListener());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Capture the story key
        mStoryKey = getIntent().getStringExtra(STORY_KEY);
        mStoryFragmentRef = new Firebase(Const.STORY_FRAGMENTS_REF + mStoryKey + "/");
        Query fragments = mStoryFragmentRef.orderByChild("position");
        fragments.addChildEventListener(new StoryFragmentChildEventListener());

        // Get the story, listen for changes in players' turns
        mStoryRef = new Firebase(Const.STORY_REF + mStoryKey);
        mStoryRef.addValueEventListener(new StoryChildEventListener());

        mUserRef = new Firebase(Const.USER_REF);

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

    private class StoryFragmentChildEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            StoryFragment storyFragment = dataSnapshot.getValue(StoryFragment.class);
            mStoryFragments.add(storyFragment);
            updateStoryTextView();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            // they shouldn't change?
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            //shouldnt happen either
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    }

    /**
     * When a story fragment is added, add it to the story textview
     */
    private void updateStoryTextView() {
        StringBuilder fullStory = new StringBuilder();
        for (int i = 0; i < mStoryFragments.size(); i++) {
            fullStory.append(mStoryFragments.get(i).getText() + " ");
        }
        mStoryTextView.setText(fullStory.toString());
    }

    /**
     * Check if it is their turn, if so, then check that the text is valid length and if everything checks out, submit to firebase.
     */
    private class AddToStoryOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (mCurrentTurn.equals(mEmail)) {
                if (mEditText.getText().toString().equals("")) {
                    Toast.makeText(getBaseContext(), "You must enter at least one word.", Toast.LENGTH_SHORT).show();
                    return;
                }

                int wordCount = mEditText.getText().toString().trim().split(" ").length;
                if (wordCount > mWordLimit) {
                    Toast.makeText(getBaseContext(), "The word limit is: " + mWordLimit, Toast.LENGTH_SHORT).show();
                    return;
                }

                StoryFragment storyFragment = new StoryFragment();
                String text = mEditText.getText().toString();
                int position = 0;
                if (mStoryFragments.size() > 0) {
                    position = mStoryFragments.get(mStoryFragments.size() - 1).getPosition() + 1;
                }
                storyFragment.setPosition(position);
                storyFragment.setSender(mEmail);
                storyFragment.setText(text);

                mStoryFragmentRef.push().setValue(storyFragment);
                ArrayList<String> members = mStory.getMembers();

                String nextTurn = "";
                if(mStory.getMode() == 0) { // gameMode: roundRobin
                    for (int i = 0; i < members.size(); i++) {
                        //get the position of this user, +1 ,
                        // then adjust for wrapping around the array (using mod)
                        if (members.get(i).equals(mEmail)) {
                            nextTurn = members.get((i + 1) % members.size());
                            if (nextTurn.equals(mEmail))
                                break;
                        }
                    }
                    mEditText.setText("");
                    mStoryRef.child("storyTurn").setValue(nextTurn);
                }else{ // gameMode: random
                    Random rn = new Random();
                    while(nextTurn.equals("")){
                        String next = members.get(rn.nextInt(members.size()));
                        if(!next.equals(MainActivity.mEmail)){
                            nextTurn = next;
                            break;
                        };
                    }
                    mEditText.setText("");
                    mStoryRef.child("storyTurn").setValue(nextTurn);
                }

            } else {
                Toast.makeText(getBaseContext(), "It isn't your turn!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Used to capture the story information, including the current turn.
     */
    private class StoryChildEventListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Log.d("firebase", "onChildAdded: " + dataSnapshot.getValue());
            mStory = dataSnapshot.getValue(Story.class);
            mCurrentTurn = mStory.getStoryTurn();


            if (mCurrentTurn.equals(mEmail)) {
                mTextViewCurrentTurn.setText(R.string.write_story_turn_text);
            } else { //get display name
                mUserRef.child(mCurrentTurn).child("displayName").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("writeStory", dataSnapshot.toString());
                        mCurrentTurnString = (String) dataSnapshot.getValue();
                        if (mCurrentTurnString.charAt(mCurrentTurnString.length() - 1) == ('s')) { //take off 's if name ends in s
                            mTextViewCurrentTurn.setText("It is " + mCurrentTurnString + " turn.");
                        } else {
                            mTextViewCurrentTurn.setText("It is " + mCurrentTurnString + "'s turn."); //add
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }
            mWordLimit = mStory.getWordlimit();


            if (mCurrentTurn.equals(mEmail)) {
                mAddToStoryButton.setEnabled(true);
            } else {
                mAddToStoryButton.setEnabled(false);
            }
            setTitle("Writing in: \"" + mStory.getStoryname()+"\"");
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    }
}
