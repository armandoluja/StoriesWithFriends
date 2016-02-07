package edu.rosehulman.lujasaa.swf.Fragments;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filterable;

import edu.rosehulman.lujasaa.swf.User;

/**
 * Created by sanderkd on 2/7/2016.
 */
public class FilterableAdapter extends ArrayAdapter<User> implements Filterable {
    public FilterableAdapter(Context context, int resource) {
        super(context, resource);
    }
}
