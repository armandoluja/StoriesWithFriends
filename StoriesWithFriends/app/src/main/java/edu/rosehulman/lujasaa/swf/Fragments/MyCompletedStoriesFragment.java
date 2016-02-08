package edu.rosehulman.lujasaa.swf.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.Firebase;

import edu.rosehulman.lujasaa.swf.Activities.CreateStoryActivity;
import edu.rosehulman.lujasaa.swf.Activities.MainActivity;
import edu.rosehulman.lujasaa.swf.Adapters.CompletedStoriesAdapter;
import edu.rosehulman.lujasaa.swf.Adapters.CurrentStoriesAdapter;
import edu.rosehulman.lujasaa.swf.Const;
import edu.rosehulman.lujasaa.swf.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyCompletedStoriesFragment extends Fragment {


    private CompletedStoriesAdapter mAdapter;
    private Firebase mStoriesRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStoriesRef = new Firebase(Const.USER_REF + MainActivity.mEmail+"/stories/");
        Log.d("firebase", "email: " + Const.USER_REF + MainActivity.mEmail + "/stories/");
    }

    public MyCompletedStoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_completed_stories, container, false);
        RecyclerView storyList = (RecyclerView)view.findViewById(R.id.completed_stories_recyler_view);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        storyList.setLayoutManager(manager);
        mAdapter = new CompletedStoriesAdapter(getActivity(), mStoriesRef);
        storyList.setAdapter(mAdapter);
        return view;
    }

}
