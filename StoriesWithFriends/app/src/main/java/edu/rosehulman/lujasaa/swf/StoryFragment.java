package edu.rosehulman.lujasaa.swf;

/**
 * Created by sanderkd on 1/17/2016.
 */
public class StoryFragment {
    private int position;//key
    private String text;
    private String sender;

    public StoryFragment(){
        
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
