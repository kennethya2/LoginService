#LoginService


### MainActivity
----
LoginService 入口

#### 1. Facebook Login 

#### 2. Google Login

<img src="https://s3-ap-northeast-1.amazonaws.com/marktdown/LoginService/login-MainActivity.jpg"  width="216" height="384"">

<pre><code>
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
 </code></pre>
 
### LoginController
----


#### 1. doLogin
<pre><code>
public synchronized void doLogin(String loginPlatform){
        Log.i(TAG,"doLogin");

        switch(loginPlatform){
            case LoginDataUtil.Login_Platform_FB:
                Intent intent = new Intent();
                intent.setClass(mContext, LoginFBActivity.class);
                mContext.startActivity(intent);
                Log.d(TAG,"loginFB");
                break;
            case LoginDataUtil.Login_Platform_Google:
                Intent intent2 = new Intent();
                intent2.setClass(mContext, LoginGoogleActivity.class);
                mContext.startActivity(intent2);
                Log.d(TAG,"loginGoogle");
                break;
        }
    }
</code></pre>

#### 2. doLoout
<pre><code>
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
</code></pre>



### Facebook Login 
----
LoginFBActivity

<img src="https://s3-ap-northeast-1.amazonaws.com/marktdown/LoginService/login-LoginFBActivity.jpg"  width="216" height="384"">
<img src="https://s3-ap-northeast-1.amazonaws.com/marktdown/LoginService/login-LoginFBActivity-done.png"  width="216" height="384"">

Facebook Login Success

<img src="https://s3-ap-northeast-1.amazonaws.com/marktdown/LoginService/login-LoginFBActivity-success.png"  width="216" height="384"">


### Google Login
----
LoginGoogleActivity

<img src="https://s3-ap-northeast-1.amazonaws.com/marktdown/LoginService/login-LoginGoogleActivity.png"  width="216" height="384"">

Google Login Success

<img src="https://s3-ap-northeast-1.amazonaws.com/marktdown/LoginService/login-LoginGoogleActivity-success.png"  width="216" height="384"">

