package com.kennethexercise.login.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.kennethexercise.login.R;
import com.kennethexercise.login.util.prefs.LoginDataUtil;

/**
 * Created by kennethyeh on 2016/10/26.
 */

public class LoginGoogleActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final String TAG = "googleLoginActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
    private TextView mStatusTextView;
    private ProgressDialog mProgressDialog;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_google_layout);
        mContext = this;

        // Views
        mStatusTextView = (TextView) findViewById(R.id.status);

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.disconnect_button).setOnClickListener(this);
//        String web_client_id = getString(R.string.web_client_id);
//        Log.d(TAG,"web_client_id:"+web_client_id);
        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail() // Specifies that email info is requested by your application
                /* Specifies that an ID token for authenticated users is requested. Requesting an ID token requires that the server client ID be specified. */
//                .requestIdToken(web_client_id)  //serverClientId:The client ID of the server that will verify the integrity of the token.
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_client]

        // [START customize_button]
        // Customize sign-in button. The sign-in button can be displayed in
        // multiple sizes and color schemes. It can also be contextually
        // rendered based on the requested scopes. For example. a red button may
        // be displayed when Google+ scopes are requested, but a white button
        // may be displayed when only basic profile is requested. Try adding the
        // Scopes.PLUS_LOGIN scope to the GoogleSignInOptions to see the
        // difference.
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setScopes(gso.getScopeArray());
        // [END customize_button]

        signIn();
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG,"resultCode:"+resultCode);
        if(resultCode == Activity.RESULT_CANCELED){
            Toast.makeText(mContext,"取消登入",Toast.LENGTH_SHORT).show();
            leaveActivity();
            return;
        }
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    // [END onActivityResult]

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        String status = result.getStatus().toString();
        Log.d(TAG, "handleSignIn getStatus:" + status);
        int statusCode = result.getStatus().getStatusCode();
        Log.d(TAG, "statusCode:" + statusCode);
        Log.d(TAG, "result toString:" + result.toString());
        Log.d(TAG, "-----");
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            /*
            * new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            * */
            String displayName      = acct.getDisplayName();            // Returns the display name of the signed in user
            String familyName       = acct.getFamilyName();             // Returns the family name of the signed in user
            String id               = acct.getId();                     // Returns the unique ID for the Google account
            String photoUrl         = acct.getPhotoUrl().toString();    // Returns the photo url of the signed in user
            String givenName        = acct.getGivenName();              // Returns the given name of the signed in user
            /*
            * if requestEmail() was configured
            * */
            String email            = acct.getEmail();                  // Returns the email address of the signed in user

            /**
             * You must configure Google Sign-In with the requestIdToken method to successfully call getIdToken.
             * If you do not configure Google Sign-In to request ID tokens, the getIdToken method returns null.
             * **/
            String idToken          = acct.getIdToken(); // Returns an ID token that you can send to your server if requestIdToken(String) was configured;
            String serverAuthCode   = acct.getServerAuthCode();
//            Set<Scope> scope = acct.getGrantedScopes();
//            for(Scope s: scope){
//                String scopeStr = s.toString();
//                Log.d(TAG, "scopeStr:"+scopeStr);
//            }
//            Log.d(TAG, "-------");
            Log.d(TAG, "displayName:"+displayName);
            Log.d(TAG, "email:"+email);
            Log.d(TAG, "familyName:"+familyName);
            Log.d(TAG, "id:"+id);
            Log.d(TAG, "idToken:"+idToken);
            Log.d(TAG, "photoUrl:"+photoUrl);
            Log.d(TAG, "givenName:"+givenName);
            Log.d(TAG, "serverAuthCode:"+serverAuthCode);

            Log.d(TAG, "========");

            LoginDataUtil.LoginInfo mLoginInfo = new LoginDataUtil.LoginInfo();
            mLoginInfo.loginPlatform    = LoginDataUtil.Login_Platform_Google;
            mLoginInfo.id               = id;
            mLoginInfo.name             = displayName;
            mLoginInfo.photoUrl         = photoUrl;
            LoginDataUtil mLoginDataUtil = LoginDataUtil.getInstance(mContext);
            mLoginDataUtil.setLoginInfo(mLoginInfo);


            mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            Toast.makeText(mContext,"已登入",Toast.LENGTH_SHORT).show();
            leaveActivity();
//            updateUI(true);
        } else {

            // force signOut
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                        }
                    });
            // Signed out, show unauthenticated UI.
            updateUI(false);
            if(statusCode == CommonStatusCodes.SIGN_IN_REQUIRED){
                // do nothing
            } else{
                Toast.makeText(mContext, "登入失敗",Toast.LENGTH_SHORT).show();
                leaveActivity();
            }
        }
    }
    // [END handleSignInResult]

    // [START signIn]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]

    // [START signOut]
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END signOut]

    // [START revokeAccess]
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
//            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
//            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else {
            mStatusTextView.setText(R.string.signed_out);

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
            case R.id.disconnect_button:
                revokeAccess();
                break;
        }
    }

    private void leaveActivity(){
        LoginGoogleActivity.this.finish();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG,"onBackPressed");
        Toast.makeText(mContext,"取消登入",Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }
}