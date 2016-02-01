package edu.rosehulman.lujasaa.swf;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;

/**
 * Created by sanderkd on 1/17/2016.
 */
public class Story {
    @JsonIgnore
    private String key;

    private ArrayList<String> members;
    private String owner;
    private boolean completed;
    private int mode;
    private String storyname;
    private String storyTurn;
    private int wordlimit;

    public Story(ArrayList<String> members, String owner, boolean completed, int mode, String storyname, String storyTurn, int wordlimit) {
        this.members = members;
        this.owner = owner;
        this.completed = completed;
        this.mode = mode;
        this.storyname = storyname;
        this.storyTurn = storyTurn;
        this.wordlimit = wordlimit;
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

    public Story(){

    }


}
