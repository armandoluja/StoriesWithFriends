package edu.rosehulman.lujasaa.swf.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import edu.rosehulman.lujasaa.swf.Adapters.FriendAdapter;
import edu.rosehulman.lujasaa.swf.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private EditText addFriendText;
    private Button addFriendButton;
    private FriendAdapter mAdapter;

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.friend_recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.hasFixedSize();
        mAdapter = new FriendAdapter(true);
        rv.setAdapter(mAdapter);

        addFriendText = (EditText) view.findViewById(R.id.add_friend_text);
        addFriendButton = (Button) view.findViewById(R.id.add_friend_button);

        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.firebaseSendFriendRequest(addFriendText.getText().toString().toLowerCase());
            }
        });

        return view;
    }

}
