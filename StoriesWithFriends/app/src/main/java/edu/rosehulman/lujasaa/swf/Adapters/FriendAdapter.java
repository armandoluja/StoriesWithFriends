package edu.rosehulman.lujasaa.swf.Adapters;

import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import edu.rosehulman.lujasaa.swf.Fragments.FriendsFragment;
import edu.rosehulman.lujasaa.swf.R;
import edu.rosehulman.lujasaa.swf.User;


/**
 * Created by sanderkd on 1/23/2016.
 */
public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    private ArrayList<User> friendArray;
    private Firebase friendRef;
    private Firebase friendRequestRef;
    private FriendsFragment mFragment;

    public FriendAdapter(FriendsFragment frag) {
        friendArray = new ArrayList<>();
        mFragment = frag;
        friendRef = new Firebase(Const.FRIEND_REF);
        friendRequestRef = new Firebase(Const.FRIEND_REQUEST_REF);
        friendRef.child(MainActivity.mEmail).addChildEventListener(new FriendChildListener());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mText.setText(friendArray.get(position).getDisplayName());
//        holder.mImage.setImageBitmap(friendArray.get(position).icon);
        holder.mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFriendDialog(v, position);
            }
        });
        holder.mImage.setImageDrawable(ContextCompat.getDrawable(mFragment.getContext(), Integer.parseInt(friendArray.get(position).getIcon())));
    }

    private void removeFriendDialog(final View v, final int position) {
        DialogFragment df = new DialogFragment() {
            public Dialog onCreateDialog(Bundle savedInstanceState) {
//                View view = getActivity().getLayoutInflater().inflate(android.l, null, false);
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage(getString(R.string.delete_friend_dialog, friendArray.get(position).getDisplayName()));
                builder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        firebaseRemoveFriend(friendArray.get(position).getEmail());
                    }
                });
                builder.setNegativeButton(getString(android.R.string.cancel), null);
                return builder.create();
            }
        };
        df.show(((MainActivity) mFragment.getActivity()).getmFragmentManager(), "dialog");

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
        }
    }

    private class FriendChildListener implements ChildEventListener {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            final String friendID = (String) dataSnapshot.getKey();
            final Firebase displayName = new Firebase(Const.USER_REF + friendID);
            Log.d("db", dataSnapshot.getValue()+"");
            displayName.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("db", ""+dataSnapshot.getValue());
                    User user = dataSnapshot.getValue(User.class);
                    user.setEmail(dataSnapshot.getKey());
                    friendArray.add(user);
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

            //handle calling
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            //shouldn't happen
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            String key = dataSnapshot.getKey();
            for(User friend: friendArray){
                if(key.equals(friend.getEmail())){
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

    public void firebaseRemoveFriend(String friend){
        friendRef.child(MainActivity.mEmail).child(friend).removeValue();
    }
    public void firebaseAddFriend(String friend){
        friendRequestRef.child(MainActivity.mEmail).child(friend).removeValue();
        friendRef.child(MainActivity.mEmail).child(friend).setValue(true);
    }
    public void firebaseSendFriendRequest(String friend){
        if(!friend.isEmpty()) {
            String add = friend.replace(".", "%");
            friendRequestRef.child(add).child(MainActivity.mEmail).setValue(true);
        }
    }



}