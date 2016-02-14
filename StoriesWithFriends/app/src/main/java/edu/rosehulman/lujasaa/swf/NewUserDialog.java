package edu.rosehulman.lujasaa.swf;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getContext();
        currentlySelected = null;
        mUser = getArguments().getParcelable("user");
        mFirebase = new Firebase(Const.USER_REF + MainActivity.mEmail);
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_new_user, null);
        mGridView = (GridView) view.findViewById(R.id.gridView);
        mAdapter = new IconAdapter(getContext());
        mGridView.setAdapter(mAdapter);
        mGridView.setColumnWidth(150);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView mImage = (ImageView) view;
                mImage.getDrawable();
                if(currentlySelected!= null){
                    currentlySelected.setBackgroundColor(0x00000000);
                }
                currentlySelected = mImage;
                imageResource = mAdapter.getResource(position);
                mImage.setBackgroundColor(ContextCompat.getColor(mContext, android.R.color.holo_blue_bright));
            }
        });

        final EditText displayName = (EditText) view.findViewById(R.id.display_name_edit);

        ImageView lblPic = new ImageView(getContext());
        lblPic.setImageResource(R.drawable.teeth);


        builder.setView(view);
        builder.setPositiveButton("Get Started!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mUser.setIcon(imageResource+"");
                mUser.setDisplayName(displayName.getText().toString());
                mFirebase.setValue(mUser);

            }
        });
        builder.setCancelable(false);
        final AlertDialog d = builder.create();

        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                displayName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() == 0 || currentlySelected.equals(null)) {
                            d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        } else {
                            d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
        });


        return d;
    }

}
