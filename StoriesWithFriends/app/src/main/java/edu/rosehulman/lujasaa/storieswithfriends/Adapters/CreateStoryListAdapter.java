package edu.rosehulman.lujasaa.storieswithfriends.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import edu.rosehulman.lujasaa.storieswithfriends.R;
import edu.rosehulman.lujasaa.storieswithfriends.User;

/**
 * Created by sanderkd on 1/23/2016.
 */

public class CreateStoryListAdapter extends RecyclerView.Adapter<CreateStoryListAdapter.ViewHolder>{

    private ArrayList<User> friendArray;

    public CreateStoryListAdapter(){

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
        holder.mCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //TODO: ADD to gridview
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImage;
        private TextView mText;
        private CheckBox mCheckbox;
        public ViewHolder(View itemView) {
            super(itemView);
            mImage = (ImageView) itemView.findViewById(R.id.friend_row_image);
            mText = (TextView) itemView.findViewById(R.id.friend_row_text);
            mCheckbox = (CheckBox) itemView.findViewById(R.id.friend_row_check_box);
        }
    }
}
