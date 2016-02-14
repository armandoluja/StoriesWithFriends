package edu.rosehulman.lujasaa.swf;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import edu.rosehulman.lujasaa.swf.Activities.MainActivity;
import edu.rosehulman.lujasaa.swf.Adapters.IconAdapter;

/**
 * Created by sanderkd on 2/13/2016.
 */
public class NewUserDialog extends DialogFragment{
    private Context mContext;
    private User mUser;
    private Firebase mFirebase;
    private GridView mGridView;
    private IconAdapter mAdapter;
    private ImageView currentlySelected;
    private int imageResource;
    private Callback mListener;
    private boolean isNewUser;
    private EditText displayName;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Callback) {
            mListener = (Callback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getContext();
        currentlySelected = null;
        mUser = new User();
        mFirebase = new Firebase(Const.USER_REF + MainActivity.mEmail);
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_new_user, null);
        displayName = (EditText) view.findViewById(R.id.display_name_edit);
        mGridView = (GridView) view.findViewById(R.id.gridView);
        mAdapter = new IconAdapter(getContext());
        mGridView.setAdapter(mAdapter);
        mGridView.setColumnWidth(150);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView mImage = (ImageView) view;
                mImage.getDrawable();
                if (currentlySelected != null) {
                    currentlySelected.setBackgroundColor(0x00000000);
                }
                currentlySelected = mImage;
                imageResource = mAdapter.getResource(position);
                mImage.setBackgroundColor(ContextCompat.getColor(mContext, android.R.color.holo_blue_bright));
            }
        });

        Bundle args = getArguments();
        isNewUser = args.getBoolean("isNewUser"); //true if called from mainActivity, false if called from settingsActivity
        if(!isNewUser){
            String name = args.getString("displayName");
            String icon = args.getString("icon");
            ((TextView) view.findViewById(R.id.welcome_title)).setText(R.string.change_settings);
            ((TextView) view.findViewById(R.id.welcome_message)).setVisibility(View.INVISIBLE);
            displayName.setText(name);
        }


        builder.setView(view);
        builder.setPositiveButton("Get Started!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // do nothing here, override it below
            }
        });
        builder.setCancelable(false);
        final AlertDialog d = builder.create();

        //override the normal button actions
        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (displayName.getText().toString().length() == 0) {
                            Toast.makeText(getContext(), "Display name cannot be empty!", Toast.LENGTH_SHORT).show();
                        } else if (displayName.getText().toString().length() > 12){
                            Toast.makeText(getContext(), "Display name must be less than 13 characters!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            if (currentlySelected == null) {
                                Toast.makeText(getContext(), "Please select an icon.", Toast.LENGTH_SHORT).show();
                            } else {
                                mUser.setIcon(imageResource + "");
                                mUser.setDisplayName(displayName.getText().toString());
                                mFirebase.setValue(mUser);
                                mListener.onSet(displayName.getText().toString(), imageResource + "");
                                d.dismiss();
                            }
                        }
                    }
                });
            }
        });
        return d;
    }


    //used to set nav drawer top icon and displayName
    public interface Callback{
        // TODO: Update argument type and name
        void onSet(String displayName, String icon);
    }

}
