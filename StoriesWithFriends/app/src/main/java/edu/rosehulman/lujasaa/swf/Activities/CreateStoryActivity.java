package edu.rosehulman.lujasaa.swf.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import com.batch.android.Batch;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import edu.rosehulman.lujasaa.swf.Adapters.CreateStoryGridviewAdapter;
import edu.rosehulman.lujasaa.swf.Adapters.CreateStoryRecyclerAdapter;
import edu.rosehulman.lujasaa.swf.Const;
import edu.rosehulman.lujasaa.swf.Notification;
import edu.rosehulman.lujasaa.swf.R;
import edu.rosehulman.lujasaa.swf.Story;
import edu.rosehulman.lujasaa.swf.User;

/**
 * Story creation, can select friends to join the story, and word limit, as well as time limits.
 */
public class CreateStoryActivity extends AppCompatActivity implements CreateStoryRecyclerAdapter.Callback{
    private Button mCreateBtn;
    private Button mCancelBtn;
    private Context mContext;
    private EditText mStoryNameEditText;
    private Spinner mWordSpinner;// word limit per turn

    private Spinner mStoryModeSpinner;

    // Used for selecting friends to join the story.
    private GridView mGridView;
    private CreateStoryRecyclerAdapter mRecyclerAdapter;
    private CreateStoryGridviewAdapter mGridviewAdapter;

    private Firebase mStoryRef;

    //The key belonging to the newly created story.
    private String mStoryKey;

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
        Log.d("batch", "onCreate: ----- CREATE STORY WAS CALLED ----");
        setTitle("Create a story:");
        if(savedInstanceState != null){
            mStoryRef = new Firebase(savedInstanceState.getString(Const.FIREBASE));
        }

        setContentView(R.layout.activity_create_story);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mContext = getBaseContext();
        mCreateBtn = (Button)findViewById(R.id.create_story_button);
        mCancelBtn = (Button)findViewById(R.id.cancel_create_button);
        mStoryNameEditText = (EditText)findViewById(R.id.story_name_edit_text);
        mWordSpinner = (Spinner)findViewById(R.id.create_story_word_limit_spinner);
        mStoryModeSpinner = (Spinner) findViewById(R.id.story_mode_spinner);

        mGridView = (GridView)findViewById(R.id.create_story_grid_view);
        mGridviewAdapter = new CreateStoryGridviewAdapter(this);
        mGridView.setAdapter(mGridviewAdapter);

        //setup friend recyclerView
        RecyclerView rV = (RecyclerView) findViewById(R.id.create_story_recycler_view);
        LinearLayoutManager recyclerLayout = new LinearLayoutManager(this);
        rV.setLayoutManager(recyclerLayout);
        rV.setHasFixedSize(true);
        mRecyclerAdapter = new CreateStoryRecyclerAdapter(this);
        rV.setAdapter(mRecyclerAdapter);


        ArrayAdapter<CharSequence> wordLimitAdapter = ArrayAdapter.createFromResource(this,
                R.array.word_limit_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> timeLimitAdapter = ArrayAdapter.createFromResource(this,
                R.array.time_limit_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> storyModeAdapter = ArrayAdapter.createFromResource(this,
                R.array.story_mode_array, android.R.layout.simple_spinner_item);
        wordLimitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeLimitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        storyModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mWordSpinner.setAdapter(wordLimitAdapter);
        mStoryModeSpinner.setAdapter(storyModeAdapter);



        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(createStory()){
                    Intent writeStory = new Intent(mContext, WriteStoryActivity.class);
                    writeStory.putExtra(WriteStoryActivity.STORY_KEY,mStoryKey);
                    startActivity(writeStory);
                }
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    /**
     * Captures the current settings that the user has specified and creates a story object,
     * then it pushes that story object to the story path in firebase.
     * It also adds that story's key to every user that was added to the story.
     * Returns true if a story was created.
     * ie. (/user/<Storykey: true>)
     */
    private boolean createStory() {
        ArrayList<String> members = mGridviewAdapter.getMembersArray();// An array of EMAILS of OTHER USERS
        if(members.size() < 1){
            Toast.makeText(CreateStoryActivity.this, "You must invite at least one friend.", Toast.LENGTH_SHORT).show();
            return false;// must have at least one other person
        }
        // MUST ADD THE OWNER AS WELL, BECAUSE THE ADAPTER SHOULDN'T.
        String owner = MainActivity.mEmail;
        members.add(owner);

        String storyName = mStoryNameEditText.getText().toString();
        if(storyName.equals("") || storyName == null){
            Toast.makeText(CreateStoryActivity.this, "Enter a name for your masterpiece!", Toast.LENGTH_SHORT).show();
            return false;
        }

        int wordLimit = Integer.parseInt(mWordSpinner.getSelectedItem().toString());
        //I know the variables below are redundant, but i wanted to specify what each parameter means.
        String currentTurn = MainActivity.mEmail;
        boolean isCompleted = false;
        int mode = mStoryModeSpinner.getSelectedItemPosition();

        Story newStory = new Story(members,owner,isCompleted,mode,storyName,currentTurn,wordLimit);

        Firebase storyRef = new Firebase(Const.STORY_REF);
        Firebase storyPush = storyRef.push();// create a key
        mStoryKey = storyPush.getKey();
        newStory.setKey(mStoryKey);// IMPORTANT: STORY THE KEY, use it later(below)
        storyPush.setValue(newStory);// create the story at this key's path


        // For each user, need to push the story's existence,
        // Firebase path: Const.USERS_REF/ email /stories/ <storykey>/
        // set value (true)
        for(int i = 0 ; i < members.size(); i ++){
            Firebase userStoriesRef = new Firebase(Const.USER_REF + members.get(i) + "/stories/"+mStoryKey+"/");
            userStoriesRef.setValue(true);
        }

        ArrayList<String> keys = new ArrayList<>();
        for(int i = 0 ; i < mGridviewAdapter.getMembersArray().size(); i ++){
            keys.add(mGridviewAdapter.getMembersArray().get(i) + MainActivity.randomSalt);
        }
        createNewStoryNotification(keys);//send notification to all the OTHER users
        return true;
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
    public void friendSelected(User friend, boolean add) {
        if(add){
            mGridviewAdapter.addFriend(friend);
        }else{
            mGridviewAdapter.removeFriend(friend);
        }
    }


    private void createNewStoryNotification(ArrayList<String> recipients) {

//        for(int i = 0 ; i < recipients.size(); i ++ ){
//            String email = recipients.get(i);
//            Firebase getUser = new Firebase(Const.REPO_REF);
//            Query user = getUser.orderByValue().equalTo(email);
//            user.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    DataSnapshot user = dataSnapshot.getChildren().iterator().next();
//                    recipients.add(user.getKey().toString());
//                }
//
//                @Override
//                public void onCancelled(FirebaseError firebaseError) {
//
//                }
//            });
//        }


        Notification n = new Notification();
        n.setRecipientEmails(recipients);
        n.setType(3);
        Firebase fbNotifications = new Firebase(Const.NOTIFICATIONS_REF);
        fbNotifications.push().setValue(n);
    }
}
