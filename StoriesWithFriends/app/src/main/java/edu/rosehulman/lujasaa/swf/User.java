package edu.rosehulman.lujasaa.swf;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;

/**
 * Created by sanderkd on 1/17/2016.
 */
public class User {

    private String displayName;
    private String icon;


    @JsonIgnore
    private ArrayList<String> stories;
    @JsonIgnore
    private String email;

    public User(){
        //required empty constructor
    }

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

    public void setValues(User vals){
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
}
