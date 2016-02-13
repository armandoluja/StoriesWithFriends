package edu.rosehulman.lujasaa.swf.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;

import edu.rosehulman.lujasaa.swf.Adapters.FriendAdapter;
import edu.rosehulman.lujasaa.swf.Adapters.FriendRequestAdapter;
import edu.rosehulman.lujasaa.swf.Const;
import edu.rosehulman.lujasaa.swf.R;
import edu.rosehulman.lujasaa.swf.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendRequestFragment extends Fragment {

    public FriendRequestAdapter mAdapter;
    private TextView noFRtext;
    private ArrayList<User> searchValues;
    private ArrayList<String> mValues;
    private ListView mListView;
    private ArrayAdapter mSearchAdapter;
    private Firebase firebase;
    private ArrayList<User> mUsers;
    private boolean searchFocus;
    private SearchView addFriendSearch;

    public FriendRequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend_request, container, false);
        noFRtext = (TextView) view.findViewById(R.id.no_friends_text);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.friend_request_recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.hasFixedSize();
        mAdapter = new FriendRequestAdapter(this);
        rv.setAdapter(mAdapter);
        firebase = new Firebase(Const.USER_REF);
        firebase.addChildEventListener(new MyListener(view));
        mValues = new ArrayList<>();
        mUsers = new ArrayList<>();
        searchValues = new ArrayList<>();

        addFriendSearch = (SearchView) view.findViewById(R.id.add_friend_search);
        addFriendSearch.setQueryHint(getString(R.string.add_a_friend));

        mListView = (ListView) view.findViewById(R.id.friend_search_list);


        addFriendSearch.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                changeFocus(hasFocus);
            }

        });


        addFriendSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.equals("")){
                    changeFocus(false);
                    return true;
                }
                else{
                    if(!searchFocus){
                        changeFocus(true);
                    }
                }
                searchValues.clear();
                mValues.clear();
                mSearchAdapter.notifyDataSetChanged();
                if(searchValues.size() < 6){
                    for(User user: mUsers){
                        if(user.getDisplayName().toLowerCase().contains(newText) || user.getEmail().toLowerCase().contains(newText)){
                            searchValues.add(user);
                            mValues.add(user.getDisplayName());
                            mSearchAdapter.notifyDataSetChanged();
                        }
                    }
                }
                return true;
            }
        });

        if(mAdapter.getItemCount() != 0){
            noFRtext.setEnabled(false);
        }
        return view;
    }


    public void toggleNoFriendRequest(boolean show){
        if(show){
            noFRtext.setText(getString(R.string.no_friends));
        } else{
            noFRtext.setText(getString(R.string.friend_requests));
        }
    }

    class MyListener implements ChildEventListener {

        private View mView;
        MyListener(View view){
            mView = view;
        }
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            User user = dataSnapshot.getValue(User.class);
            user.setEmail(dataSnapshot.getKey());
            mUsers.add(user);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    }

    private void changeFocus(boolean hasFocus){
        searchFocus = hasFocus;
        if (hasFocus) { //set Listview
            mSearchAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, mValues);
            mListView.setAdapter(mSearchAdapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mAdapter.firebaseSendFriendRequest(searchValues.get(position).getEmail());
                    Toast toast = Toast.makeText(getContext(), getString(R.string.friend_request_sent), Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        } else {
            mListView.setAdapter(null);
            mListView.setClickable(false);
        }
    }

}
