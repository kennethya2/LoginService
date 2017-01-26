package com.kennethexercise.login.util.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * Created by kennethyeh on 2016/10/26.
 */

public class LoginDataUtil {

    private String TAG = "LoginDataUtil";

    private Context mContext = null;

    private LoginInfo mLoginInfo = null;
    private static final Type TYPE = new TypeToken<LoginInfo>() {}.getType();

    private SharedPreferences prefs                     = null;
    private static String PREFS_NAME 	                = "LoginDataInfo";
    private static final String PREFS_KEY_Login_Data    = "KeyLoginDataInfo";

    public static final String Login_Platform_Default  = "Login_None";
    public static final String Login_Platform_FB       = "Login_FB";
    public static final String Login_Platform_Google   = "Login_Google";

    public static synchronized LoginDataUtil getInstance(Context context){
        return new LoginDataUtil(context);
    }

    private LoginDataUtil(Context context){
        mContext    = context;
        prefs       = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public synchronized void setLoginInfo(LoginInfo loginInfo){
        Log.i(TAG,"setLoginInfo");
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREFS_KEY_Login_Data, new Gson().toJson(loginInfo));
        editor.commit();
        this.mLoginInfo = loginInfo;
    }

    public synchronized LoginInfo getLoginInfo(){
        Log.i(TAG,"getLoginInfo");
        if(mLoginInfo == null){
            LoginInfo loginInfo = new Gson().fromJson(prefs.getString(PREFS_KEY_Login_Data, null), TYPE);
            if(loginInfo == null){
                loginInfo = new LoginInfo();
                setLoginInfo(loginInfo);
            }
            this.mLoginInfo = loginInfo;
        }
        return mLoginInfo;
    }

    public synchronized void removeLoginInfo(){
        Log.i(TAG,"removeLoginInfo");
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(PREFS_KEY_Login_Data);
        editor.commit();
    }

    public static class LoginInfo implements Serializable {
        public String loginPlatform = Login_Platform_Default;
        public String id ;
        public String name ;
        public String photoUrl ;
    }


}
