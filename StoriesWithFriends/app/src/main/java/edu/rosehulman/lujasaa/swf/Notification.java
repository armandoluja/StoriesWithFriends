package edu.rosehulman.lujasaa.swf;

import java.util.ArrayList;

/**
 * Created by lujasaa on 2/14/2016.
 */
public class Notification {
    private ArrayList<String> recipientEmails;
    private int type;

    public String getMessage(){
        switch (type){
            case 0:
            {
                return "Is is your turn!";
            }
            case 1:
            {
                return "A story you are in was updated!";
            }
            case 2:
            {
                return "A story you are in was completed!";
            }
            case 3:
            {
                return "You have been added to a new story!";
            }
            case 4:
            {
                return "You have a new friend request!";
            }
            default:
                return "Error";
        }
    }

    public ArrayList<String> getRecipientEmails() {
        return recipientEmails;
    }

    public void setRecipientEmails(ArrayList<String> recipientEmails) {
        this.recipientEmails = recipientEmails;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
