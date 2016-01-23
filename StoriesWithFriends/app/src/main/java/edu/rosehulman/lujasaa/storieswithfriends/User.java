package edu.rosehulman.lujasaa.storieswithfriends;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by sanderkd on 1/17/2016.
 */
public class User {
    private int mId;
    private String username;
    private boolean vibrate;
    private Bitmap image;
    private ArrayList<Integer> mStoryIds;

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ArrayList<Integer> getmStoryIds() {
        return mStoryIds;
    }

    public void setmStoryIds(ArrayList<Integer> mStoryIds) {
        this.mStoryIds = mStoryIds;
    }

    public boolean isVibrate() {
        return vibrate;
    }

    public void setVibrate(boolean vibrate) {
        this.vibrate = vibrate;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
