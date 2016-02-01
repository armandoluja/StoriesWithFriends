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
import edu.rosehulman.lujasaa.swf.Adapters.StoryAdapter;
import edu.rosehulman.lujasaa.swf.Const;
import edu.rosehulman.lujasaa.swf.Constants;
import edu.rosehulman.lujasaa.swf.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyCurrentStoriesFragment extends Fragment {
    private StoryAdapter mAdapter;
    private Firebase mStoriesRef;

    public MyCurrentStoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStoriesRef = new Firebase(Const.USER_REF + MainActivity.mEmail+"/stories");
        Log.d("firebase", "email: "+ Const.USER_REF + MainActivity.mEmail+"/stories");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_current_stories, container, false);
        RecyclerView storyList = (RecyclerView)view.findViewById(R.id.current_stories_recyler_view);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        storyList.setLayoutManager(manager);
        mAdapter = new StoryAdapter(getActivity(), mStoriesRef);
        storyList.setAdapter(mAdapter);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createStory = new Intent(view.getContext(), CreateStoryActivity.class);
                startActivity(createStory);
            }
        });
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        mStoriesRef.removeEventListener(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.clear();
        mStoriesRef.addChildEventListener(mAdapter);
    }

}
