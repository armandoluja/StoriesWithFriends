package edu.rosehulman.lujasaa.swf;

import com.firebase.client.Firebase;

import edu.rosehulman.lujasaa.swf.Activities.MainActivity;

/**
 * Created by sanderkd on 1/30/2016.
 */
public class Const {
    public static String FIREBASE = "https://lujasaa-stories-with-friends.firebaseio.com/";
    public static String USER_REF = FIREBASE + "user/";
    public static String FRIEND_REF = FIREBASE + "friend/";
    public static String FRIEND_REQUEST_REF = FIREBASE + "friend-request/";
    public static String REPO_REF = FIREBASE + "/repo/";
    public static String STORY_REF = FIREBASE + "/story/";
    public static String STORY_FRAGMENTS_REF = FIREBASE + "/fragments/";
//    public static String BATCH_PUSH_NOTIFICATIONS_API_KEY = "DEV56BE5E9744714DB835827E8FE9C";
    public static String BATCH_PUSH_NOTIFICATIONS_API_KEY = "56BE5E9742F765CF3F5EA8C37904F0";
    public static String GOOGLE_STORIES_WITH_FRIENDS_PROJECT_NUMBER = "658592650920";

    public static String[] CSS_COLOR_CHOICES = {"red", "yellow", "purple", "green", "blue", "magenta", "black", "brown"};

    public static String NOTIFICATIONS_REF = FIREBASE + "/notification/";

    public static final String SETTING_PREFERENCES = "SETTING_PREFERENCES";

    public static final String PUNCTUATION = ".,-!?:;";

    public static final String TAG = "PHOTOMSG";

}
