package edu.rosehulman.lujasaa.swf;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;

/**
 * Created by sanderkd on 2/19/2016.
 */
public class CompletedStory {

    @JsonIgnore
    private String key;

    private ArrayList<String> members;
    private String owner;
    private boolean completed;
    private int mode;
    private String storyname;
    private String storyTurn;
    private int wordlimit;
    private String story;

    public CompletedStory(Story mStory, String s) {
        this.members = mStory.getMembers();
        this.owner = mStory.getOwner();
        this.completed = mStory.isCompleted();
        this.mode = mStory.getMode();
        this.storyname = mStory.getStoryname();
        this.storyTurn = mStory.getStoryTurn();
        this.wordlimit = mStory.getWordlimit();
        this.story = s;
    }



    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getStoryname() {
        return storyname;
    }

    public void setStoryname(String storyname) {
        this.storyname = storyname;
    }

    public String getStoryTurn() {
        return storyTurn;
    }

    public void setStoryTurn(String storyTurn) {
        this.storyTurn = storyTurn;
    }

    public int getWordlimit() {
        return wordlimit;
    }

    public void setWordlimit(int wordlimit) {
        this.wordlimit = wordlimit;
    }

    public CompletedStory(){

    }


    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }
}

