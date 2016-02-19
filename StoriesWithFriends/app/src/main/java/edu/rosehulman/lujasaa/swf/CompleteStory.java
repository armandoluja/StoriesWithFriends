package edu.rosehulman.lujasaa.swf;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by sanderkd on 2/19/2016.
 */
public class CompleteStory {

    public static final String STORY_KEY = "story_key";
    public WebView mWebView;
    public String mStoryKey;
    private Firebase mStoryRef;
    private Firebase mStoryFragmentRef;
    ArrayList<StoryFragment> mStoryFragments;
    private Story mStory;
    private HashMap<String, String> mUserColors;
    private String[] mColors;
    private ArrayList<String> mUsedColors;
    private StringBuilder mCustomHtml;
    private ArrayList<String> mMembers;
    private Context mContext;
    private Firebase mUserRef;
    private HashMap<String, String> mUNameDisplayName;

    public CompleteStory(Story story, ArrayList<StoryFragment> storyFragments, Context context, HashMap<String, String> usernameMap){

        mStory = story;
        mStoryFragments = storyFragments;
        mMembers = mStory.getMembers();
        mContext = context;
        mUNameDisplayName = usernameMap;
        mUsedColors = new ArrayList<>();
        mUserColors = new HashMap<>();
        mColors = Const.CSS_COLOR_CHOICES;

        while(mUsedColors.size() != mMembers.size()){
            int rnd = new Random().nextInt(mColors.length);
            if(mUsedColors.indexOf(mColors[rnd]) == -1){
                mUserColors.put(mMembers.get(mUsedColors.size()), mColors[rnd]);
                mUsedColors.add(mColors[rnd]);
            }
        }

        mCustomHtml = new StringBuilder();
        mCustomHtml.append("<html>");
        mCustomHtml.append("<head>");
        mCustomHtml.append("<link rel=stylesheet href='css/style.css'>");

        for(StoryFragment frag: mStoryFragments){
            mCustomHtml.append("<span class='" + mUserColors.get(frag.getSender()) + "'>" + frag.getText() + " </span>");
        }

        mCustomHtml.append("</p>");
        for (final String user : mMembers) {
            int resource = mContext.getResources().getIdentifier(mUserColors.get(user), "string", mContext.getPackageName());
            final String color = mContext.getString(resource);
            Log.d("db", mUNameDisplayName.toString());
            mCustomHtml.append("<p><span class='box' style='background:" + color + "'></span>" + mUNameDisplayName.get(user) + "<p>");
        }
        mCustomHtml.append("</body></html>");

        mCustomHtml.append("</body></html>");

        Firebase firebase = new Firebase(Const.COMPLETE_STORY_REF + mStory.getKey());

        firebase.setValue(new CompletedStory(mStory, mCustomHtml.toString()));

    }
}
