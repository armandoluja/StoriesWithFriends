package edu.rosehulman.lujasaa.storieswithfriends;

import java.util.ArrayList;

/**
 * Created by sanderkd on 1/17/2016.
 */
public class Story {
    private int mStoryId;
    private ArrayList<Integer> mStoryUsers;
    private int mStoryOwner;
    private boolean isCompleted;
    private int mMode;
    private int mWordLimit;
    private int mCurrentTurn;
    private ArrayList<StoryFragment> mStory;

    public Story(){

    }


}
