package com.kennethexercise.login.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.kennethexercise.login.BuildConfig;
import com.kennethexercise.login.R;
import com.kennethexercise.login.util.prefs.LoginDataUtil;

import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by kennethyeh on 2016/10/26.
 */

public class LoginFBActivity  extends Activity {

    private static final String TAG = "fbLoginActivity";

    private CallbackManager callbackManager;
    private Context mContext;

    private LoginButton mLoginButton;
    private AccessToken accessToken;

    private Button loginButton;

    private Button logout;

    private AccessTokenTracker accessTokenTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(this.getApplicationContext());
        if (BuildConfig.DEBUG) {//開發模式 顯示Access Token
            FacebookSdk.setIsDebugEnabled(true);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        }
        setContentView(R.layout.login_fb_layout);
        mContext = this;


        //宣告callback Manager

        callbackManager = CallbackManager.Factory.create();

        //找到login button
//	    setDefautLoginBTN();
        setCustomLoginBTN();

        logout = (Button) findViewById(R.id.fbLogout);
        logout.setOnClickListener(logoutListener);

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // App code

//	        		Log.d(TAG,"----accessTokenTracker");
//	        		Log.d(TAG,"oldAccessToken:"+oldAccessToken.toString());
//	        		Log.d(TAG,"currentAccessToken:"+currentAccessToken.toString());
            }
        };

        doLogin();
    }

    private Button.OnClickListener logoutListener = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {
            Log.d(TAG,"logOut");
            LoginManager.getInstance().logOut();
            leaveActivity();
        }
    };

    private void setCustomLoginBTN(){
        loginButton = (Button) findViewById(R.id.fbLogin);
        loginButton.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                doLogin();

            }
        });
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            //登入成功
            @Override
            public void onSuccess(LoginResult loginResult) {

                //accessToken之後或許還會用到 先存起來
                accessToken = loginResult.getAccessToken();
                String token = accessToken.toString();

                Log.d(TAG, "access token got:"+token);

                String tokenString= loginResult.getAccessToken().getToken();
                Log.d(TAG, "tokenString:"+tokenString);

                //send request and call graph api
                GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {

                    //當RESPONSE回來的時候
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        //讀出姓名 ID FB個人頁面連結
                        Log.d(TAG, "complete");
                        String account = object.optString("email");
                        Log.d(TAG, "email account:"+account);
//                                if(account.trim().equalsIgnoreCase("")){
//                                    Toast.makeText(mContext, "無法取得您facebook e-mail!\n請確認您facebook帳號狀態!", Toast.LENGTH_LONG).show();
//                                    LoginManager.getInstance().logOut();
//                                    return;
//                                }
                        String id = object.optString("id");
                        Log.d(TAG, "id:"+id);
                        String userPhoto = getFacebookProfilePhotoURL(id);
                        Log.d(TAG, "id:"+id);
                        String name =object.optString("name");
                        Log.d(TAG, "userPhoto:"+userPhoto);
                        Log.d(TAG, "name:"+object.optString("name"));
                        Log.d(TAG, "email:"+object.optString("email"));
                        Log.d(TAG, "first_name:"+object.optString("first_name"));
                        Log.d(TAG, "last_name:"+object.optString("last_name"));
                        Log.d(TAG, "link:"+object.optString("link"));
                        Log.d(TAG, "gender:"+object.optString("gender"));
                        Log.d(TAG, "locale:"+object.optString("locale"));
                        Log.d(TAG, "timezone:"+object.optString("timezone"));
                        Log.d(TAG, "updated_time:"+object.optString("updated_time"));
                        Log.d(TAG, "verified:"+object.optString("verified"));

//                        Log.d(TAG, "object.toString:"+object.toString());

                        LoginDataUtil.LoginInfo mLoginInfo = new LoginDataUtil.LoginInfo();
                        mLoginInfo.loginPlatform    = LoginDataUtil.Login_Platform_FB;
                        mLoginInfo.id               = id;
                        mLoginInfo.name             = name;
                        mLoginInfo.photoUrl         = userPhoto;
                        LoginDataUtil mLoginDataUtil = LoginDataUtil.getInstance(mContext);
                        mLoginDataUtil.setLoginInfo(mLoginInfo);
                        Toast.makeText(mContext,"已登入",Toast.LENGTH_SHORT).show();
                        leaveActivity();
                    }
                });

                //包入你想要得到的資料 送出request
                Bundle parameters = new Bundle();
//	            parameters.putString("fields", "id,name,link");
                parameters.putString("fields", "id,name,email,first_name,last_name,link,gender,locale,timezone,updated_time,verified");
                request.setParameters(parameters);
                request.executeAsync();
            }

            /**
             * https://developers.facebook.com/docs/graph-api/reference/user/picture/
             * **/
            private String getFacebookProfilePhotoURL(String id){
                String URL = String.format("https://graph.facebook.com/%s/picture?type=large", id);
                return URL;
            }

            //登入取消

            @Override
            public void onCancel() {
                // App code
                Toast.makeText(mContext,"取消登入",Toast.LENGTH_SHORT).show();
                Log.d(TAG, "CANCEL");
                leaveActivity();
            }

            //登入失敗
            @Override
            public void onError(FacebookException exception) {
                // App code
                Toast.makeText(mContext,"登入失敗",Toast.LENGTH_SHORT).show();
                Log.d(TAG, exception.toString());
                leaveActivity();
            }
        });
    }

    private void doLogin(){
        LoginManager.getInstance().setLoginBehavior(LoginBehavior.WEB_ONLY);//disable single sign on
        LoginManager.getInstance().logInWithReadPermissions(LoginFBActivity.this,  Arrays.asList("public_profile", "email", "user_friends"));
    }

    private void leaveActivity(){
        LoginFBActivity.this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
