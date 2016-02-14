package edu.rosehulman.lujasaa.swf;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;

/**
 * Created by sanderkd on 1/17/2016.
 */
public class User implements Parcelable {

    private String displayName;
    private String icon;


    @JsonIgnore
    private ArrayList<String> stories;
    @JsonIgnore
    private String email;

    public User() {
        //required empty constructor
    }

    protected User(Parcel in) {
        displayName = in.readString();
        icon = in.readString();
        stories = in.createStringArrayList();
        email = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public ArrayList<String> getStories() {
        return stories;
    }

    public void setStories(ArrayList<String> stories) {
        this.stories = stories;
    }

    public void setValues(User vals) {
        this.displayName = vals.displayName;
        this.icon = vals.icon;
        this.stories = vals.stories;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(displayName);
        dest.writeString(icon);
        dest.writeStringList(stories);
        dest.writeString(email);
    }
}
