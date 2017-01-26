package com.kennethexercise.login.login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.kennethexercise.login.util.prefs.LoginDataUtil;


/**
 * Created by kennethyeh on 2016/10/26.
 */

public class LoginController {

    private String TAG = "LoginController";

    private AppCompatActivity mContext;
    private GoogleApiClient mGoogleApiClient;


    public static LoginController getInstance(AppCompatActivity context){
        return new LoginController(context);
    }

    private LoginController(AppCompatActivity context){
        this.mContext=context;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .enableAutoManage(mContext /* FragmentActivity */,
                        new GoogleApiClient.OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                            }
                        })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public synchronized void doLogin(String loginPlatform){
        Log.i(TAG,"doLogin");

        switch(loginPlatform){
            case LoginDataUtil.Login_Platform_FB:
                Intent intent = new Intent();
//                    intent.setClass(mContext, LoginSampleFBActivity.class);
                intent.setClass(mContext, LoginFBActivity.class);
                mContext.startActivity(intent);
                Log.d(TAG,"loginFB");
                break;
            case LoginDataUtil.Login_Platform_Google:
                Intent intent2 = new Intent();
//                    intent2.setClass(mContext, LoginSampleGoogleActivity.class);
                intent2.setClass(mContext, LoginGoogleActivity.class);
                mContext.startActivity(intent2);
                Log.d(TAG,"loginGoogle");
                break;
        }

    }

    public synchronized void doLoout(){
        Log.i(TAG,"doLoout");
        LoginDataUtil.LoginInfo mLoginInfo = LoginDataUtil.getInstance(mContext).getLoginInfo();
        switch(mLoginInfo.loginPlatform){
            case LoginDataUtil.Login_Platform_FB:
                logoutFBAccount();
                break;
            case LoginDataUtil.Login_Platform_Google:
                logoutGoogleAccouunt();
                break;
        }
    }

    private void logoutFBAccount(){
        Log.d(TAG,"logoutFBAccount");
        FacebookSdk.sdkInitialize(mContext.getApplicationContext());

        LoginManager.getInstance().logOut();
        LoginDataUtil mLoginDataUtil = LoginDataUtil.getInstance(mContext);
        mLoginDataUtil.removeLoginInfo();
    }

    private void logoutGoogleAccouunt(){
        Log.d(TAG,"logoutGoogleAccouunt");

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        int statusCode = status.getStatusCode();
                        String statusMessage = status.getStatusMessage();
                        if(status.isSuccess()){
                            LoginDataUtil mLoginDataUtil = LoginDataUtil.getInstance(mContext);
                            mLoginDataUtil.removeLoginInfo();
                        }
                        Log.d(TAG,"statusCode:"+statusCode);
                        Log.d(TAG,"statusMessage:"+statusMessage);
                    }
                });
    }
}
