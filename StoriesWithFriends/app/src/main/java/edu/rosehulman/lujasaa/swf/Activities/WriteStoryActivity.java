package edu.rosehulman.lujasaa.swf.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import edu.rosehulman.lujasaa.swf.CompleteStory;
import edu.rosehulman.lujasaa.swf.Const;
import edu.rosehulman.lujasaa.swf.Notification;
import edu.rosehulman.lujasaa.swf.R;
import edu.rosehulman.lujasaa.swf.Story;
import edu.rosehulman.lujasaa.swf.StoryFragment;

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
    private Menu mMenu;
    private Menu memberSubMenu;
    private Menu kickSubMenu;

    private String mCurrentTurn;//the email of the player who currently has a turn
    private String mCurrentTurnString;

    // Contains every members's contributions to the story
    private ArrayList<StoryFragment> mStoryFragments = new ArrayList<StoryFragment>();
    private int mWordLimit;
    private Story mStory;
    private ArrayList<String> mMembers;
    private HashMap<String, String> mUsernameMap;



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

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mAddToStoryButton = (Button) findViewById(R.id.button_add_to_story);
        mEditText = (EditText) findViewById(R.id.edit_text_add_to_story);
        mTextViewCurrentTurn = (TextView) findViewById(R.id.write_story_activity_current_turn_textview);
        mStoryTextView = (TextView) findViewById(R.id.story_text_view);
        mStoryTextView.setMovementMethod(new ScrollingMovementMethod());
        mMembers = new ArrayList<>();
        mUsernameMap = new HashMap<>();

        mAddToStoryButton.setOnClickListener(new AddToStoryOnClickListener());

        // Capture the story key
        mStoryKey = getIntent().getStringExtra(STORY_KEY);
        mStoryFragmentRef = new Firebase(Const.STORY_FRAGMENTS_REF + mStoryKey + "/");
        Query fragments = mStoryFragmentRef.orderByChild("position");
        fragments.addChildEventListener(new StoryFragmentChildEventListener());

        // Get the story, listen for changes in players' turns
        mStoryRef = new Firebase(Const.STORY_REF + mStoryKey);

        mUserRef = new Firebase(Const.USER_REF);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.write_story_menu, menu);
        mMenu = menu;
        memberSubMenu = mMenu.findItem(R.id.menu_members).getSubMenu();
        kickSubMenu = mMenu.findItem(R.id.menu_kick).getSubMenu();
        mStoryRef.addValueEventListener(new StoryChildEventListener());
        return true;
    }

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu){
//        menu.findItem(R.id.menu_kick).getSubMenu().clear();
//        menu.findItem(R.id.menu_members).getSubMenu().clear();
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        } else if(id == R.id.menu_finish_story){
            completeStory();
            return true;
        } else if(id == R.id.menu_leave){
            leaveStory();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void kickPlayer(final String uName, CharSequence displayName) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        if(mStory.getMembers().size() <= 2){
            dialog.setMessage(R.string.kick_with_2_confirmation);
            dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onBackPressed();
                    for (String member : mMembers) {
                        mUserRef.child(member).child("stories").child(mStoryKey).removeValue();
                    }
                    mStoryRef.removeValue();
                    mStoryFragmentRef.removeValue();
                }
            });
        } else {
            dialog.setMessage(getString(R.string.kick_confirmation, displayName));
            dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int index = mMembers.indexOf(uName);
                    if (mStory.getStoryTurn().equals(uName)) { //change turn
                        changeTurn();
                    }
                    mMembers.remove(index);
                    mStoryRef.child("members").setValue(mMembers);
                    mUserRef.child(uName).child("stories").child(mStoryKey).removeValue();
                }
            });
        }
        dialog.setNegativeButton(android.R.string.cancel, null);
        dialog.create();
        dialog.show();
    }

    private void leaveStory() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        if(mStory.getMembers().size() <= 2){
            dialog.setMessage(R.string.leave_with_2_confirmation);
            dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onBackPressed();
                    for(String member: mMembers) {
                        mUserRef.child(member).child("stories").child(mStoryKey).removeValue();
                    }
                    mStoryFragmentRef.removeValue();
                    mStoryRef.removeValue();

                }
            });
        }
        else {
            dialog.setMessage(R.string.leave_story_confirmation);
            dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int index = mMembers.indexOf(MainActivity.mEmail);
                    if (mStory.getOwner().equals(MainActivity.mEmail)) { //change owner
                        if (index != 0) {
                            mStoryRef.child("storyTurn").setValue(mMembers.get(index - 1));
                        } else {
                            mStoryRef.child("storyTurn").setValue(mMembers.get(index + 1));
                        }

                    }
                    if (mStory.getStoryTurn().equals(MainActivity.mEmail)) { //change turn
                        changeTurn();
                    }
                    mMembers.remove(index);
                    onBackPressed();
                    mStoryRef.child("members").setValue(mMembers);
                    mUserRef.child(MainActivity.mEmail).child("stories").child(mStoryKey).removeValue();

                }
            });
        }
        dialog.setNegativeButton(android.R.string.cancel, null);
        dialog.create();
        dialog.show();

    }

    private void completeStory() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(R.string.complete_story_confirmation);
        dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mStoryRef.child("completed").setValue(true);
                Log.d("db", "here");
                Log.d("db - map ", mUsernameMap.toString());
                new CompleteStory(mStory, mStoryFragments, getBaseContext(), mUsernameMap);
                Intent viewStoryIntent = new Intent(getBaseContext(), ViewStoryActivity.class);
                viewStoryIntent.putExtra(ViewStoryActivity.STORY_KEY, mStoryKey);//pass the story key to get story fragments
                viewStoryIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                getBaseContext().startActivity(viewStoryIntent);
            }
        });
        dialog.setNegativeButton(android.R.string.cancel, null);
        dialog.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
    }

    private class StoryFragmentChildEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            if(dataSnapshot.getValue() != null) {
                StoryFragment storyFragment = dataSnapshot.getValue(StoryFragment.class);
                mStoryFragments.add(storyFragment);
                updateStoryTextView();
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            // they shouldn't change?
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) { //member leaves or is kicked

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
            if(i != 0 && Const.PUNCTUATION.indexOf(mStoryFragments.get(i).getText().charAt(0)) == -1){
                fullStory.append(" " +mStoryFragments.get(i).getText());
            }else{
                fullStory.append(mStoryFragments.get(i).getText());
            }
        }
        if(fullStory.charAt(fullStory.length() - 1) == '.'){
            mEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        }
        else{
            mEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
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
                String[] arr = mEditText.getText().toString().trim().split(" ");
                int wordCount = arr.length;
                if(arr[0].length() == 1 && Const.PUNCTUATION.indexOf(arr[0]) != -1){
                    wordCount--;
                }
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
                storyFragment.setText(text.trim());

                mStoryFragmentRef.push().setValue(storyFragment);

                changeTurn();
            } else {
                Toast.makeText(getBaseContext(), "It isn't your turn!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void changeTurn(){
        String nextTurn = "";
        if(mStory.getMode() == 0) { // gameMode: roundRobin
            for (int i = 0; i < mMembers.size(); i++) {
                //get the position of this user, +1 ,
                // then adjust for wrapping around the array (using mod)
                if (mMembers.get(i).equals(mEmail)) {
                    nextTurn = mMembers.get((i + 1) % mMembers.size());
                    if (nextTurn.equals(mEmail))
                        break;
                }
            }
            mEditText.setText("");
            mStoryRef.child("storyTurn").setValue(nextTurn);
            createTurnNotification(nextTurn);
        }else{ // gameMode: random
            Random rn = new Random();
            while(nextTurn.equals("")){
                String next = mMembers.get(rn.nextInt(mMembers.size()));
                if(!next.equals(mStory.getStoryTurn())){
                    nextTurn = next;
                    break;
                };
            }
            mEditText.setText("");
            mStoryRef.child("storyTurn").setValue(nextTurn);
            createTurnNotification(nextTurn);
        }
    }

    /**
     * Used to capture the story information, including the current turn.
     */
    private class StoryChildEventListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Log.d("firebase", "onChildAdded: " + dataSnapshot.getValue());
            if(dataSnapshot.getValue() != null) {
                mStory = dataSnapshot.getValue(Story.class);
                mStory.setKey(dataSnapshot.getKey());
                mEditText.setHint("Word limit: "+ mStory.getWordlimit());
                mMembers = mStory.getMembers();
                if (!mStory.getOwner().equals(MainActivity.mEmail)) {
                    mMenu.findItem(R.id.menu_finish_story).setVisible(false);
                    mMenu.findItem(R.id.menu_kick).setVisible(false);
                }
                //add members to member/kick player menu item
                for (final String member : mMembers) {
                    mUserRef.child(member).child("displayName").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d("db -snap", dataSnapshot.toString());
                            if (memberSubMenu.size() != mMembers.size()) {
                                mUsernameMap.put(member, (String)dataSnapshot.getValue());
                                memberSubMenu.add((String) dataSnapshot.getValue());
                                if (mStory.getOwner().equals(MainActivity.mEmail) && !member.equals(MainActivity.mEmail)) {
                                    MenuItem item = kickSubMenu.add((String) dataSnapshot.getValue());
                                    item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                        @Override
                                        public boolean onMenuItemClick(MenuItem item) {
                                            kickPlayer(member, item.getTitle());
                                            return true;
                                        }
                                    });
                                }
                                mUserRef.removeEventListener(this);
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });

                }
                mCurrentTurn = mStory.getStoryTurn();


                if (mCurrentTurn.equals(mEmail)) {
                    mTextViewCurrentTurn.setText(R.string.write_story_turn_text);
                } else { //get display name
                    mUserRef.child(mCurrentTurn).child("displayName").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
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
                setTitle("Writing in: \"" + mStory.getStoryname() + "\"");
            }
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    }







    private void createTurnNotification(String nextTurn) {
//        Firebase getUser = new Firebase(Const.REPO_REF);
        ArrayList<String> recipients = new ArrayList<>();
        recipients.add(nextTurn+MainActivity.randomSalt);
        Notification n = new Notification();
        n.setRecipientEmails(recipients);
        n.setType(0);
        Firebase fbNotifications = new Firebase(Const.NOTIFICATIONS_REF);
        fbNotifications.push().setValue(n);
//        Query user = getUser.orderByValue().equalTo(nextTurn);
//        user.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                DataSnapshot user = dataSnapshot.getChildren().iterator().next();
//                ArrayList<String> recipients = new ArrayList<>();
//                recipients.add(user.getKey().toString());
//                Notification n = new Notification();
//                n.setRecipientEmails(recipients);
//                n.setType(0);// your turn!
//                Firebase fbNotifications = new Firebase(Const.NOTIFICATIONS_REF);
//                fbNotifications.push().setValue(n);
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });

    }
}
