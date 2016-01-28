package edu.rosehulman.lujasaa.swf.Adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import edu.rosehulman.lujasaa.swf.R;
import edu.rosehulman.lujasaa.swf.User;


/**
 * Created by sanderkd on 1/23/2016.
 */
public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    private ArrayList<Friend> friendArray;
    private Firebase mFriendRef;
    private String FRIEND_PATH = "https://lujasaa-stories-with-friends.firebaseio.com/friend/sanderkd@rose-hulman%edu";

    public FriendAdapter() {
        friendArray = new ArrayList<>();
        mFriendRef = new Firebase(FRIEND_PATH);
        mFriendRef.addChildEventListener(new FriendChildListener());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d("db", "friend array 0: " +friendArray.get(0).displayName);
        holder.mText.setText(friendArray.get(position).displayName);
//        holder.mImage.setImageBitmap(friendArray.get(position).icon);
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

    private class FriendChildListener implements ChildEventListener {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            final String friendID = (String) dataSnapshot.getValue();
            Log.d("db", "friendID: " + friendID);
            final Firebase displayName = new Firebase("https://lujasaa-stories-with-friends.firebaseio.com/user/" + friendID + "/displayName");
            displayName.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("db", "displayName: " + dataSnapshot.getValue());
                    Friend f = new Friend(friendID, (String) dataSnapshot.getValue());
                    Log.d("db", "friend id: "+ f.friendID + " displayName: " + f.displayName);
                    friendArray.add(f);
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            //shouldn't happen
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            //shouldn't happen
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            //shouldn't happen
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
            //shouldn't happen
        }
    }

    private class Friend{
        public String friendID;
        public String displayName;
        Friend(String friendID, String  displayName){
            this.friendID = friendID;
            this.displayName = displayName;
        }
    }
}