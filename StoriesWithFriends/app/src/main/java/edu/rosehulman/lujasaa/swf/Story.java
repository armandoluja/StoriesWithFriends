package edu.rosehulman.lujasaa.swf;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;

/**
 * Created by sanderkd on 1/17/2016.
 */
public class Story {
    @JsonIgnore
    private String key;

    private ArrayList<User> members;
    private User owner;
    private boolean completed;
    private int mode;

    public String getStoryname() {
        return storyname;
    }

    public void setStoryname(String storyname) {
        this.storyname = storyname;
    }

    private String storyname;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ArrayList<User> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<User> members) {
        this.members = members;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setIsCompleted(boolean isCompleted) {
        this.completed = isCompleted;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
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
