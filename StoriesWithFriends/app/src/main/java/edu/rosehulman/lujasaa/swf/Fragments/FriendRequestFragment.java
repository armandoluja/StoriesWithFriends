package edu.rosehulman.lujasaa.swf.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.rosehulman.lujasaa.swf.Adapters.FriendAdapter;
import edu.rosehulman.lujasaa.swf.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendRequestFragment extends Fragment {

    public FriendAdapter mAdapter;

    public FriendRequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend_request, container, false);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.friend_request_recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.hasFixedSize();
        mAdapter = new FriendAdapter(false);
        rv.setAdapter(mAdapter);
        return view;
    }

}
