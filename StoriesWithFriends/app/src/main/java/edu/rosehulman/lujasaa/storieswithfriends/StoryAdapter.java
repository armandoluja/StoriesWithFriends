package edu.rosehulman.lujasaa.storieswithfriends;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.rosehulman.lujasaa.storieswithfriends.Fragments.MyCurrentStoriesFragment;

/**
 * Created by sanderkd on 1/17/2016.
 */
public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder>{
    private MyCurrentStoriesFragment.Callback mCallback;

    public StoryAdapter(Context context, MyCurrentStoriesFragment.Callback callback) {
        mCallback = callback;
    }

    @Override
    public StoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(StoryAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
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
    }
}
