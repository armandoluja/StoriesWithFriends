package edu.rosehulman.lujasaa.swf.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import edu.rosehulman.lujasaa.swf.Activities.MainActivity;
import edu.rosehulman.lujasaa.swf.Const;
import edu.rosehulman.lujasaa.swf.Constants;
import edu.rosehulman.lujasaa.swf.R;
import edu.rosehulman.lujasaa.swf.Story;

/**
 * Created by sanderkd on 1/17/2016.
 */
public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> implements ChildEventListener{
    private final LayoutInflater mInflator;
    private Firebase mFirebase;
    private ArrayList<Story> mStories;

    public StoryAdapter(Context context, Firebase firebaseRef) {
        // you need the email to get their stories
        mInflator = LayoutInflater.from(context);
        mStories = new ArrayList<>();
        mFirebase = firebaseRef;

        //testing what a story looks like in firebase as a json object
//        Firebase fb = new Firebase(Const.STORY_REF);
//        ArrayList<String> mems = new ArrayList<>();
//        mems.add("jonesfh@rose-hulman%edu");
//        mems.add("lujasaa@rose-hulman%edu");
//        Story s = new Story(mems,"lujasaa@rose-hulman%edu",false,0,"Test","lujasaa@rose-hulman%edu",10);
//        fb.push().setValue(s);
    }

    // this needs to go in the create story activity, since we cant view story objects here
//    public void firebasePush(Story story) {
//        mFirebase.push().setValue(story);
//    }
//
//    public void firebaseUpdate(Story story) {
//        mFirebase.child(story.getKey()).setValue(story);
//    }
//
//    public void firebaseRemove(Story story) {
//        mFirebase.child(story.getKey()).removeValue();
//    }

    @Override
    public StoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflator.inflate(R.layout.recycler_view_story,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(StoryAdapter.ViewHolder holder, int position) {
        holder.bindToView(mStories.get(position));
    }

    @Override
    public int getItemCount() {
        return mStories.size();
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Log.d("firebase", "onChildAdded: " + dataSnapshot.getKey());
        Firebase getstoryRef = new Firebase(Const.STORY_REF + dataSnapshot.getKey());
        getstoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Story s = dataSnapshot.getValue(Story.class);
                s.setKey(dataSnapshot.getKey());
                mStories.add(s);
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }

    public void clear(){
        mStories.clear();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mStoryName;
        TextView mStoryTurn;
        TextView mStoryTime;

        public ViewHolder(View itemView){
            super(itemView);
            mStoryName = (TextView) itemView.findViewById(R.id.storyName);
            mStoryTurn = (TextView) itemView.findViewById(R.id.storyTurn);
            mStoryTime = (TextView) itemView.findViewById(R.id.storyTime);
        }

        public void bindToView(Story story) {
            mStoryName.setText(story.getStoryname());
            // the current turn is stored as a users email address, since it is unique
            if(MainActivity.mEmail.equals(story.getStoryTurn())){
                mStoryTurn.setText("Your Turn!");
            }else{
                mStoryTurn.setText("Not your turn.");
            }
            mStoryTime.setText("XX:XXPM/AM");
        }
    }
}
