package edu.rosehulman.lujasaa.swf.Adapters;

import android.content.Context;
import android.graphics.drawable.Icon;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.fasterxml.jackson.databind.deser.Deserializers;

import java.util.ArrayList;

import edu.rosehulman.lujasaa.swf.Activities.MainActivity;
import edu.rosehulman.lujasaa.swf.R;

/**
 * Created by sanderkd on 2/13/2016.
 */
public class IconAdapter extends BaseAdapter {
    private String[] mIcons;
    private Context mContext;
    private ImageView[] mItems;
    private int[] mResources;

    public IconAdapter(Context context){
        mIcons = context.getResources().getStringArray(R.array.icons);
        mContext = context;
        mItems = new ImageView[mIcons.length];
        mResources = new int[mIcons.length];
    }

    @Override
    public int getCount() {
        return mIcons.length;
    }

    @Override
    public ImageView getItem(int position) {
        return mItems[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView iv;
        if(convertView == null) {
            iv = new ImageView(mContext);
            iv.setLayoutParams(new GridView.LayoutParams(150, 150));
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setPadding(5, 5, 5, 5);

        }
        else{
            iv = (ImageView) convertView;
        }
        int resource = mContext.getResources().getIdentifier(mIcons[position], "drawable", MainActivity.packageName);
        mResources[position] = resource;
        iv.setImageDrawable(ContextCompat.getDrawable(mContext, resource));
        mItems[position] = iv;
        return iv;
    }

    public int getResource(int position){
        return mResources[position];
    }
}
