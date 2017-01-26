package com.kennethexercise.login;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kennethexercise.login.login.LoginController;
import com.kennethexercise.login.util.prefs.LoginDataUtil;

public class MainActivity extends AppCompatActivity {

    private String TAG = "LoginMain";
    private Context mContext = null;

    private RelativeLayout notLoginRL;
    private Button fbLoginBTN;
    private Button googleLoginBTN;

    private RelativeLayout loginRL;
    private TextView platformTV;
    private TextView nameTV;
    private TextView idTV;
    private ImageView profileIV;
    private Button logoutBTN;


    private LoginController mLoginController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        notLoginRL      = (RelativeLayout) findViewById(R.id.notLoginRL);
        fbLoginBTN      = (Button) findViewById(R.id.fbLoginBTN);
        googleLoginBTN  = (Button) findViewById(R.id.googleLoginBTN);
        fbLoginBTN.setOnClickListener(login);
        googleLoginBTN.setOnClickListener(login);


        loginRL     = (RelativeLayout) findViewById(R.id.loginRL);
        platformTV  = (TextView) findViewById(R.id.platformTV);
        nameTV      = (TextView) findViewById(R.id.nameTV);
        idTV        = (TextView) findViewById(R.id.idTV);
        profileIV   = (ImageView) findViewById(R.id.profileIV);
        logoutBTN   = (Button) findViewById(R.id.logoutBTN);
        logoutBTN.setOnClickListener(login);


        mLoginController = LoginController.getInstance((MainActivity) mContext);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"onResume");
        LoginDataUtil.LoginInfo mLoginInfo = LoginDataUtil.getInstance(mContext).getLoginInfo();
        setLoginUI(mLoginInfo);
    }

    private void setLoginUI(LoginDataUtil.LoginInfo mLoginInfo){
        Log.d(TAG,"setLoginUI");
        String loginPlatform = mLoginInfo.loginPlatform;
        String name = mLoginInfo.name;
        String id   = mLoginInfo.id;
        String photoUrl = mLoginInfo.photoUrl;

        if(loginPlatform.contentEquals(LoginDataUtil.Login_Platform_Default)){ // 尚未登入
            Log.d(TAG,"尚未登入");
            notLoginRL.setVisibility(View.VISIBLE);
            loginRL.setVisibility(View.GONE);
        }else{
            Log.d(TAG,"已登入");
            notLoginRL.setVisibility(View.GONE);
            loginRL.setVisibility(View.VISIBLE);
            platformTV.setText(loginPlatform);
            nameTV.setText(name);
            idTV.setText(id);
            MyApplication.imageLoader.displayImage(photoUrl,
                    profileIV,
                    MyApplication.options,
                    MyApplication.animateFirstListener);
        }
    }

    private void resetNotLoginUI(){
        Log.d(TAG,"resetNotLoginUI");
        notLoginRL.setVisibility(View.VISIBLE);
        loginRL.setVisibility(View.GONE);

        platformTV.setText("");
        nameTV.setText("");
        idTV.setText("");
    }

    private View.OnClickListener login = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            int id = view.getId();

            switch(id){
                case R.id.fbLoginBTN:
                    mLoginController.doLogin(LoginDataUtil.Login_Platform_FB);
                    break;
                case R.id.googleLoginBTN:
                    mLoginController.doLogin(LoginDataUtil.Login_Platform_Google);
                    break;
                case R.id.logoutBTN:
                    mLoginController.doLoout();
                    resetNotLoginUI();
                    break;
            }

        }
    };
}
