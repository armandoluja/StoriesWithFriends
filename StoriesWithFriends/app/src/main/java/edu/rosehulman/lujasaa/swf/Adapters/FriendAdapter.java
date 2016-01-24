package edu.rosehulman.lujasaa.swf.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import edu.rosehulman.lujasaa.swf.R;
import edu.rosehulman.lujasaa.swf.User;

/**
 * Created by sanderkd on 1/23/2016.
 */
public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    private ArrayList<User> friendArray;

    public FriendAdapter(){
        friendArray = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_create_story_friend_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mText.setText(friendArray.get(position).getUsername());
        holder.mImage.setImageBitmap(friendArray.get(position).getImage());
    }

    @Override
    public int getItemCount() {
        return friendArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImage;
        private TextView mText;
        public ViewHolder(View itemView) {
            super(itemView);
            mImage = (ImageView) itemView.findViewById(R.id.friend_row_image);
            mText = (TextView) itemView.findViewById(R.id.friend_row_text);
        }
    }
}