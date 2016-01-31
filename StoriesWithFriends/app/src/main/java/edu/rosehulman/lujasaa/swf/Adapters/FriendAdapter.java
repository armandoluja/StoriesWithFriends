package edu.rosehulman.lujasaa.swf.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import edu.rosehulman.lujasaa.swf.Activities.MainActivity;
import edu.rosehulman.lujasaa.swf.Const;
import edu.rosehulman.lujasaa.swf.R;


/**
 * Created by sanderkd on 1/23/2016.
 */
public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    private ArrayList<Friend> friendArray;
    private Firebase mFriendRef;
    private Firebase mFriendRequestRef;
    private Boolean friendState;

    public FriendAdapter(Boolean friend) {
        friendArray = new ArrayList<>();
        mFriendRef = new Firebase(Const.FRIEND_REF);
        friendState = friend;
        mFriendRequestRef = new Firebase(Const.FRIEND_REQUEST_REF);
        if(friendState) {
            mFriendRef.child(MainActivity.mEmail).addChildEventListener(new FriendChildListener());
        }
        else{
            mFriendRequestRef.child(MainActivity.mEmail).addChildEventListener(new FrendRequestChldListener());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mText.setText(friendArray.get(position).displayName);
//        holder.mImage.setImageBitmap(friendArray.get(position).icon);
        holder.mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(friendState) {
                    firebaseRemoveFriend(friendArray.get(position).friendID);
                }
                else{
                    firebaseAddFriend(friendArray.get(position).friendID);
                }
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
        private Button mButton;

        public ViewHolder(View itemView) {
            super(itemView);
            mImage = (ImageView) itemView.findViewById(R.id.friend_row_image);
            mText = (TextView) itemView.findViewById(R.id.friend_row_text);
            mButton= (Button) itemView.findViewById(R.id.friend_row_button);
            if(!friendState){
                mButton.setBackgroundResource(R.drawable.add);
            }
        }
    }

    private class FriendChildListener implements ChildEventListener {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            final String friendID = (String) dataSnapshot.getKey();
            final Firebase displayName = new Firebase(Const.USER_REF + friendID + "/displayName");
            displayName.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Friend f = new Friend(friendID, (String) dataSnapshot.getValue());
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
            String key = dataSnapshot.getKey();
            for(Friend friend: friendArray){
                if(key.equals(friend.friendID)){
                    friendArray.remove(friend);
                    notifyDataSetChanged();
                    return;
                }
            }
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

    //Friend Request firebase
    private class FrendRequestChldListener implements ChildEventListener {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            final String friendID = (String) dataSnapshot.getKey();
            final Firebase displayName = new Firebase(Const.USER_REF + friendID + "/displayName");
            displayName.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Friend f = new Friend(friendID, (String) dataSnapshot.getValue());
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

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            String key = dataSnapshot.getKey();
            for(Friend friend: friendArray){
                if(key.equals(friend.friendID)){
                    friendArray.remove(friend);
                    notifyDataSetChanged();
                    return;
                }
            }
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    }

    public void firebaseRemoveFriend(String friend){
        mFriendRef.child(MainActivity.mEmail).child(friend).removeValue();
    }
    public void firebaseAddFriend(String friend){
        mFriendRequestRef.child(MainActivity.mEmail).child(friend).removeValue();
        mFriendRef.child(MainActivity.mEmail).child(friend).setValue(true);
    }
    public void firebaseSendFriendRequest(String friend){
        mFriendRequestRef.child(friend).child(MainActivity.mEmail).setValue(true);
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