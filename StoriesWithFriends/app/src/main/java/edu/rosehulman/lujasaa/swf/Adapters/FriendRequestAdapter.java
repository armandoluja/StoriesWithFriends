package edu.rosehulman.lujasaa.swf.Adapters;

import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import edu.rosehulman.lujasaa.swf.Fragments.FriendRequestFragment;
import edu.rosehulman.lujasaa.swf.Notification;
import edu.rosehulman.lujasaa.swf.R;
import edu.rosehulman.lujasaa.swf.User;


/**
 * Created by sanderkd on 1/23/2016.
 */
public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder> {
    private ArrayList<User> friendArray;
    private Firebase friendRef;
    private Firebase friendRequestRef;
    private FriendRequestFragment mFragment;
    private boolean showNoFriendReq;


    public FriendRequestAdapter(FriendRequestFragment frag) {
        friendArray = new ArrayList<>();
        mFragment = frag;
        friendRef = new Firebase(Const.FRIEND_REF);
        friendRequestRef = new Firebase(Const.FRIEND_REQUEST_REF);
        friendRequestRef.child(MainActivity.mEmail).addChildEventListener(new FrendRequestChldListener());
        showNoFriendReq = true;
        mFragment.toggleNoFriendRequest(showNoFriendReq);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_request_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mText.setText(friendArray.get(position).getDisplayName());
//        holder.mImage.setImageBitmap(friendArray.get(position).icon);
        holder.mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAddFriend(friendArray.get(position).getEmail());
            }
        });
        holder.mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseRemoveFriendRequest(friendArray.get(position).getEmail());
            }
        });
        holder.mImage.setImageDrawable(ContextCompat.getDrawable(mFragment.getContext(), Integer.parseInt(friendArray.get(position).getIcon())));
    }

    @Override
    public int getItemCount() {
        return friendArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImage;
        private TextView mText;
        private Button mAddButton;
        private Button mDeleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            mImage = (ImageView) itemView.findViewById(R.id.friend_row_image);
            mText = (TextView) itemView.findViewById(R.id.friend_row_text);
            mAddButton = (Button) itemView.findViewById(R.id.friend_request_row_add);
            mDeleteButton = (Button) itemView.findViewById(R.id.friend_request_row_delete);
        }
    }

    //Friend Request firebase
    private class FrendRequestChldListener implements ChildEventListener {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            final String friendID = (String) dataSnapshot.getKey();
            final Firebase displayName = new Firebase(Const.USER_REF + friendID);
            displayName.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    user.setEmail(dataSnapshot.getKey());
                    friendArray.add(user);
                    if(showNoFriendReq){
                        showNoFriendReq = false;
                        mFragment.toggleNoFriendRequest(showNoFriendReq);
                    }
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
            for (User friend : friendArray) {
                if (key.equals(friend.getEmail())) {
                    friendArray.remove(friend);
                    if(friendArray.isEmpty()){
                        showNoFriendReq = true;
                        mFragment.toggleNoFriendRequest(showNoFriendReq);
                    }
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

    public void firebaseRemoveFriend(String friend) {
        friendRef.child(MainActivity.mEmail).child(friend).removeValue();
    }

    public void firebaseAddFriend(String friend) {
        friendRequestRef.child(MainActivity.mEmail).child(friend).removeValue();
        friendRef.child(MainActivity.mEmail).child(friend).setValue(true);
        friendRef.child(friend).child(MainActivity.mEmail).setValue(true);

        Notification n = new Notification();
        ArrayList<String> re = new ArrayList<>();
        re.add(friend+MainActivity.randomSalt);
        n.setRecipientEmails(re);
        n.setType(5);
        Firebase fb = new Firebase(Const.NOTIFICATIONS_REF);
        fb.push().setValue(n);
    }

    public void firebaseSendFriendRequest(String friend) {
        if (!friend.isEmpty()) {
            String add = friend.replace(".", "%");
            friendRequestRef.child(add).child(MainActivity.mEmail).setValue(true);
        }
        Notification n = new Notification();
        ArrayList<String> re = new ArrayList<>();
        re.add(friend+MainActivity.randomSalt);
        n.setRecipientEmails(re);
        n.setType(4);
        Firebase fb = new Firebase(Const.NOTIFICATIONS_REF);
        fb.push().setValue(n);
    }
    private void firebaseRemoveFriendRequest(String friend) {
        friendRequestRef.child(MainActivity.mEmail).child(friend).removeValue();
    }
}