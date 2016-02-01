package edu.rosehulman.lujasaa.swf.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import edu.rosehulman.lujasaa.swf.R;
import edu.rosehulman.lujasaa.swf.User;

/**
 * Created by sanderkd on 1/31/2016.
 */
public class CreateStoryGridviewAdapter extends BaseAdapter{
    private ArrayList<User> mAddedArray;
    private Context mContext;

    public CreateStoryGridviewAdapter(Context context){
        mContext = context;
        mAddedArray = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mAddedArray.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_create_story_add_friend_view, parent, false);
//        if(convertView != null){
//            View view = LayoutInflater.from(mContext).inflate(R.layout.activity_create_story_add_friend_view, parent, false);
//        }
//        else{
//            view = convertView;
//        }
        ImageView mImage = (ImageView) view.findViewById(R.id.create_story_add_friend_image);
        TextView mText = (TextView) view.findViewById(R.id.create_story_add_friend_text);
        mText.setText(mAddedArray.get(position).getDisplayName());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }

    public void addFriend(User user){
        mAddedArray.add(user);
        notifyDataSetChanged();
    }

    public void removeFriend(User user){

    }
}
