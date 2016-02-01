package edu.rosehulman.lujasaa.swf.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import edu.rosehulman.lujasaa.swf.User;

/**
 * Created by sanderkd on 1/23/2016.
 */
public class CreateStoryRecyclerAdapter extends RecyclerView.Adapter<CreateStoryRecyclerAdapter.ViewHolder>{

    private ArrayList<User> friendArray;
    private Firebase mFriendRef;
    private Callback mListener;
    private Context mContext;

    public CreateStoryRecyclerAdapter(Context context){
        mContext = context;
        mListener = (Callback) mContext;
        friendArray = new ArrayList<>();
        mFriendRef = new Firebase(Const.FRIEND_REF + MainActivity.mEmail);
        mFriendRef.addChildEventListener(new FriendChildListener());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_create_story_friend_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mText.setText(friendArray.get(position).getDisplayName());
//        holder.mImage.setImageBitmap(friendArray.get(position).getIcon());
        holder.mCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mListener.friendSelected(friendArray.get(position), isChecked);
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

    public interface Callback{
        void friendSelected(User friend, boolean add);
    }

    private class FriendChildListener implements ChildEventListener {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            final String friendID = (String) dataSnapshot.getKey();
            final Firebase displayName = new Firebase(Const.USER_REF + friendID);
            Log.d("db", dataSnapshot.getValue() + "");
            displayName.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("db", ""+dataSnapshot.getValue());
                    User user = dataSnapshot.getValue(User.class);
                    user.setUID(dataSnapshot.getKey());
                    friendArray.add(user);
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
            for(User friend: friendArray){
                if(key.equals(friend.getUID())){
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
}
