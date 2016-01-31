package edu.rosehulman.lujasaa.swf.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.rosehulman.lujasaa.swf.Activities.CreateStoryActivity;
import edu.rosehulman.lujasaa.swf.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyCurrentStoriesFragment extends Fragment {




    public MyCurrentStoriesFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_current_stories, container, false);
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

}
