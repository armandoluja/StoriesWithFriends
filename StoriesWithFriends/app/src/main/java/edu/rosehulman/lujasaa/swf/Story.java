package edu.rosehulman.lujasaa.swf;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;

/**
 * Created by sanderkd on 1/17/2016.
 */
public class Story {
    @JsonIgnore
    private String key;
    private int mStoryId;
    private ArrayList<User> mStoryUsers;
    private User mStoryOwner;
    private boolean isCompleted;
    private int mMode;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getmStoryId() {
        return mStoryId;
    }

    public void setmStoryId(int mStoryId) {
        this.mStoryId = mStoryId;
    }

    public ArrayList<User> getmStoryUsers() {
        return mStoryUsers;
    }

    public void setmStoryUsers(ArrayList<User> mStoryUsers) {
        this.mStoryUsers = mStoryUsers;
    }

    public User getmStoryOwner() {
        return mStoryOwner;
    }

    public void setmStoryOwner(User mStoryOwner) {
        this.mStoryOwner = mStoryOwner;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public int getmMode() {
        return mMode;
    }

    public void setmMode(int mMode) {
        this.mMode = mMode;
    }

    public int getmWordLimit() {
        return mWordLimit;
    }

    public void setmWordLimit(int mWordLimit) {
        this.mWordLimit = mWordLimit;
    }

    public int getmCurrentTurn() {
        return mCurrentTurn;
    }

    public void setmCurrentTurn(int mCurrentTurn) {
        this.mCurrentTurn = mCurrentTurn;
    }

    public ArrayList<StoryFragment> getmStory() {
        return mStory;
    }

    public void setmStory(ArrayList<StoryFragment> mStory) {
        this.mStory = mStory;
    }

    private int mWordLimit;
    private int mCurrentTurn;
    private ArrayList<StoryFragment> mStory;

    public Story(){

    }


}
