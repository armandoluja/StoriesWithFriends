package edu.rosehulman.lujasaa.swf.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.util.ArrayList;

import edu.rosehulman.lujasaa.swf.Const;
import edu.rosehulman.lujasaa.swf.R;
import edu.rosehulman.lujasaa.swf.User;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_CODE_GOOGLE_SIGN_IN = 1;

    private EditText mPasswordView;
    private EditText mEmailView;
    private View mLoginForm;
    private View mProgressSpinner;
    private boolean mLoggingIn;
    private SignInButton mGoogleSignInButton;
    private GoogleApiClient mGoogleApiClient;
    private String mEmailAddress;
    private String mDisplayName;
    private User mUser;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_GOOGLE_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();
                mEmailAddress = account.getEmail();
                mDisplayName = account.getDisplayName();
                //you can get a lot of intereseting things from account (dot) trick
                getGoogleOAuthToken(mEmailAddress);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getGoogleOAuthToken(final String emailAddress) {
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            String errorMessage = null;

            @Override
            protected String doInBackground(Void... params) {
                String token = null;
                try {
                    String scope = "oauth2:profile email";
                    token = GoogleAuthUtil.getToken(getApplication(), emailAddress, scope);
                } catch (IOException transientEx) {
                /* Network or server error */
                    errorMessage = "Network error: " + transientEx.getMessage();
                } catch (UserRecoverableAuthException e) {
                /* We probably need to ask for permissions, so start the intent if there is none pending */
                    Intent recover = e.getIntent();
                    startActivityForResult(recover, REQUEST_CODE_GOOGLE_SIGN_IN);
                } catch (GoogleAuthException authEx) {
                    errorMessage = "Error authenticating with Google: " + authEx.getMessage();
                }
                return token;
            }
            @Override
            protected void onPostExecute(String token) {
                Log.d("FPK", "onPostExecute");
                if (token == null) {
                    onLoginError(errorMessage);
                }else {
                    onGoogleLoginWithToken(token);
                }
            }
        };
        task.execute();
    }

    private void onGoogleLoginWithToken(String token) {
        Firebase firebase = new Firebase(Const.FIREBASE);
        //must write the auth result handler
        firebase.authWithOAuthToken("google", token, new MyAuthResultHandler());
    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
        setContentView(R.layout.activity_login);
        mLoggingIn = false;
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mLoginForm = findViewById(R.id.login_form);
        mProgressSpinner = findViewById(R.id.login_progress);
        View loginButton = findViewById(R.id.email_sign_in_button);
        mGoogleSignInButton = (SignInButton) findViewById(R.id.google_sign_in_button);
        mEmailView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_NEXT) {
                    mPasswordView.requestFocus();
                    return true;
                }
                return false;
            }
        });
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_NULL) {
                    login();
                    return true;
                }
                return false;
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        mGoogleSignInButton.setColorScheme(SignInButton.COLOR_LIGHT);
        mGoogleSignInButton.setSize(SignInButton.SIZE_WIDE);
        mGoogleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginWithGoogle();
            }
        });

        Firebase firebase = new Firebase(Const.FIREBASE);
    }

    private void loginWithGoogle() {
        if (mLoggingIn) {
            return;
        }
        mEmailView.setError(null);
        mPasswordView.setError(null);

        showProgress(true);
        mLoggingIn = true;
        onGoogleLogin();
        hideKeyboard();
    }

    public void onGoogleLogin() {
        //TODO: Log user in with Google Account
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(intent, REQUEST_CODE_GOOGLE_SIGN_IN);
    }


    public void login() {
        if (mLoggingIn) {
            return;
        }

        mEmailView.setError(null);
        mPasswordView.setError(null);

        mEmailAddress = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancelLogin = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_email));
            focusView = mPasswordView;
            cancelLogin = true;
        }

        if (TextUtils.isEmpty(mEmailAddress)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancelLogin = true;
        } else if (!isEmailValid(mEmailAddress)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancelLogin = true;
        }

        if (cancelLogin) {
            // error in login
            focusView.requestFocus();
        } else {
            // show progress spinner, and start background task to login
            showProgress(true);
            mLoggingIn = true;
            onLogin(mEmailAddress, password);
            hideKeyboard();
        }
    }


    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEmailView.getWindowToken(), 0);
    }
    private void showProgress(boolean show) {
        mProgressSpinner.setVisibility(show ? View.VISIBLE : View.GONE);
        mLoginForm.setVisibility(show ? View.GONE : View.VISIBLE);
        mGoogleSignInButton.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    public void onLogin(String email, String password) {
        //TODO: Log user in with username & password
        //stuff that happens when someone logs in
        Firebase firebase = new Firebase(Const.FIREBASE);
        //must write the auth result handler
        mDisplayName = "Default Username";
        firebase.authWithPassword(email, password, new MyAuthResultHandler());
    }

    class MyAuthResultHandler implements Firebase.AuthResultHandler {
        @Override
        public void onAuthenticated(AuthData authData) {
            Intent returnIntent = new Intent();
            mEmailAddress = mEmailAddress.replace('.','%');
            Log.d("firebase", "Login Activity My authResult handler : onAuthenticated: " + mEmailAddress);
            returnIntent.putExtra(MainActivity.AUTH_UID, authData.getUid());
            returnIntent.putExtra(MainActivity.AUTH_EMAIL, mEmailAddress);

            /**
             * Check if the user is already registered, if not, register them
             * with the default registration settings.
             */
            Firebase checkUserNameAndIcon = new Firebase(Const.USER_REF);
            if(checkUserNameAndIcon.child(mEmailAddress) == null){
                /**
                 * Create a default user.
                 */
                mUser = new User();
                mUser.setDisplayName(mDisplayName);
                mUser.setIcon("defaultIcon");
                checkUserNameAndIcon.child(mEmailAddress).setValue(mUser);
            }
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }

        @Override
        public void onAuthenticationError(FirebaseError firebaseError) {
            onLoginError(firebaseError.getMessage());
        }
    }

    public void onLoginError(String message) {
        new AlertDialog.Builder(this)
                .setTitle(this.getString(R.string.login_error))
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .create()
                .show();

        showProgress(false);
        mLoggingIn = false;
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("TAG", "onConnectionFailed: ");
    }


}
