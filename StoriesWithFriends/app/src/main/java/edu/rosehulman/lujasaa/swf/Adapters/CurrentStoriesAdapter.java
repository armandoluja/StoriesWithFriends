package edu.rosehulman.lujasaa.swf.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerTitleStrip;
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

import edu.rosehulman.lujasaa.swf.Activities.MainActivity;
import edu.rosehulman.lujasaa.swf.Activities.WriteStoryActivity;
import edu.rosehulman.lujasaa.swf.Const;
import edu.rosehulman.lujasaa.swf.R;
import edu.rosehulman.lujasaa.swf.Story;

/**
 * Gets all of the current stories for this user and displays them in a cardview.
 */
public class CurrentStoriesAdapter extends RecyclerView.Adapter<CurrentStoriesAdapter.ViewHolder>{
    private final LayoutInflater mInflator;
    private Firebase mFirebase;
    private ArrayList<Story> mStories;
    private Firebase mStoryRef;

    private Context mContext;//need this to start activities

    public CurrentStoriesAdapter(Context context, Firebase firebaseRef) {
        // you need the email to get their stories
        mInflator = LayoutInflater.from(context);
        mStories = new ArrayList<>();
        mFirebase = firebaseRef;
        mContext = context;
        mFirebase.addChildEventListener(new CurrentStoryChildEventListener());
        mStoryRef = new Firebase(Const.STORY_REF);
    }

    @Override
    public CurrentStoriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflator.inflate(R.layout.item_view_current_stories,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(CurrentStoriesAdapter.ViewHolder holder, int position) {
        holder.bindToView(mStories.get(position));
    }

    @Override
    public int getItemCount() {
        return mStories.size();
    }


    public void clear(){
        mStories.clear();
    }

    /**
     * When a story is clicked, it starts a new WriteStoryActivity.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView mStoryName;
        TextView mStoryTurn;
        TextView mStoryTime;
        String mStoryKey;

        public ViewHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            mStoryName = (TextView) itemView.findViewById(R.id.storyName);
            mStoryTurn = (TextView) itemView.findViewById(R.id.storyTurn);
            mStoryTime = (TextView) itemView.findViewById(R.id.storyTime);
        }

        public void bindToView(Story story) {
            mStoryName.setText(story.getStoryname());
            mStoryKey = story.getKey();
            // the current turn is stored as a users email address, since it is unique
            if(MainActivity.mEmail.equals(story.getStoryTurn())){
                mStoryTurn.setText("Your Turn!");
            }else{
                mStoryTurn.setText("Not your turn.");
            }
            mStoryTime.setText(" ");//not using this yet
        }

        @Override
        public void onClick(View v) {
            Intent writeStoryIntent = new Intent(mContext,WriteStoryActivity.class);
            writeStoryIntent.putExtra(WriteStoryActivity.STORY_KEY,mStoryKey);//pass the story key to get story fragments
            mContext.startActivity(writeStoryIntent);
        }
    }

    private class CurrentStoryChildEventListener implements ChildEventListener {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            mStoryRef.child(dataSnapshot.getKey()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() == null){
                        for(Story story: mStories){
                            if(story.getKey().equals(dataSnapshot.getKey())){
                                mStories.remove(story);
                                notifyDataSetChanged();
                                mStoryRef.removeEventListener(this);
                                return;
                            }
                        }
                    }
                    Log.d("datatype", " is : " + dataSnapshot);
                    String key = dataSnapshot.getKey();
                    Story newStory = dataSnapshot.getValue(Story.class);
                    newStory.setKey(key);
                    for(int i = 0 ; i < mStories.size(); i ++){
                        if(mStories.get(i).getKey().equals(key)){
                            mStories.set(i, newStory);
                            notifyDataSetChanged();
                            return;
                        }
                    }
                    if (!newStory.isCompleted()) {
                        // get only current stories
                        mStories.add(newStory);
                        notifyDataSetChanged();
                    }
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
    }
}
