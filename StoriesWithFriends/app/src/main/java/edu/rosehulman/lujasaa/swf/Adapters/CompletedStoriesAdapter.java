package edu.rosehulman.lujasaa.swf.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
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

import edu.rosehulman.lujasaa.swf.Activities.ViewStoryActivity;
import edu.rosehulman.lujasaa.swf.Const;
import edu.rosehulman.lujasaa.swf.R;
import edu.rosehulman.lujasaa.swf.Story;

/**
 * Similar to the IncompletedStoriesAdapter, with a different child event listener, and a different layout/viewholder
 */
public class CompletedStoriesAdapter extends RecyclerView.Adapter<CompletedStoriesAdapter.ViewHolder> {
    private final LayoutInflater mInflator;
    private Firebase mFirebase;
    private ArrayList<Story> mStories;

    private Context mContext;//need this to start activities

    public CompletedStoriesAdapter(Context context, Firebase firebaseRef) {
        // you need the email to get their stories
        mInflator = LayoutInflater.from(context);
        mStories = new ArrayList<>();
        mFirebase = firebaseRef;
        mContext = context;
        mFirebase.addChildEventListener(new CompletedStoryChildEventListener());
    }

    @Override
    public CompletedStoriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflator.inflate(R.layout.item_view_completed_stories,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(CompletedStoriesAdapter.ViewHolder holder, int position) {
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
     * When a story is clicked, it starts a new ViewStoryActivity.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView mStoryName;
        TextView mCompletionDate;
        String mStoryKey;

        public ViewHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            mStoryName = (TextView) itemView.findViewById(R.id.storyName);
            mCompletionDate = (TextView) itemView.findViewById(R.id.completion_time);
        }

        public void bindToView(Story story) {
            mStoryName.setText(story.getStoryname());
            mStoryKey = story.getKey();
            mCompletionDate.setText(" ");//not using this yet
        }

        @Override
        public void onClick(View v) {
            Intent viewStoryIntent = new Intent(mContext,ViewStoryActivity.class);
            viewStoryIntent.putExtra(ViewStoryActivity.STORY_KEY,mStoryKey);//pass the story key to get story fragments
            mContext.startActivity(viewStoryIntent);
        }
    }

    private class CompletedStoryChildEventListener implements ChildEventListener {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Firebase getStoryRef = new Firebase(Const.STORY_REF + dataSnapshot.getKey());
            getStoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Story s = dataSnapshot.getValue(Story.class);
                    s.setKey(dataSnapshot.getKey());
                    if(s.isCompleted()){
                        // get only completed stories
                        mStories.add(s);
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
