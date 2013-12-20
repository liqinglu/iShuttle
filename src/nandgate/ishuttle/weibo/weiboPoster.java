package nandgate.ishuttle.weibo;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;

import nandgate.ishuttle.R;
import nandgate.ishuttle.navigator.MainActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class weiboPoster extends Activity {
	
	public static weiboPoster myInstance;
	private WeiboAuth mWeiboAuth;
	private Oauth2AccessToken mAccessToken;
	private EditText et;
	private InputMethodManager imm;
	
	public static Handler handler;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_weibo);
        myInstance=this;
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
        //Toast.makeText(weiboPoster.this, "当前："+System.currentTimeMillis()+"过期："+AccessTokenKeeper.readAccessToken(this).getExpiresTime(), Toast.LENGTH_SHORT).show();
        if(AccessTokenKeeper.isStored && System.currentTimeMillis()<AccessTokenKeeper.readAccessToken(this).getExpiresTime()){
            if (imm.isActive()) {
            	imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS); 
            }
        }else{
            mWeiboAuth = new WeiboAuth(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
            mWeiboAuth.anthorize(new AuthListener());
            mAccessToken = AccessTokenKeeper.readAccessToken(this);
        }
        et=(EditText)findViewById(R.id.new_weibo_edit);
        
        Button send=(Button)findViewById(R.id.head_new);
        Button set=(Button)findViewById(R.id.head_refresh);
        send.setOnClickListener(new OnClickListener(){
        	
			@Override
			public void onClick(View arg0) {
				if (et.length() > 140) {
					Toast.makeText(weiboPoster.this, "字数超过140，无法发送", 1)
						.show();
					return;
				}else if(et.length()<=0){
					Toast.makeText(weiboPoster.this, "发送内容不能为空，请重新发送", 1)
						.show();
					return;
				}
				
				System.out.println("ready to send");

					Weibo myWeibo=new Weibo(weiboPoster.this);
					Map<String, String> params = new HashMap<String, String>();
					String status = "#朗讯爱班车#";
					params.put("access_token", AccessTokenKeeper.readAccessToken(weiboPoster.this).getToken());
					System.out.println(AccessTokenKeeper.readAccessToken(weiboPoster.this).getToken());
					params.put("status", status + et.getText().toString());
					
					myWeibo.postMethodRequestWithOutFile(Weibo.POST_WEIBO_URL_WITH_CONTENT, params, Weibo.header);
					Toast.makeText(weiboPoster.this, "正在发送", 1).show();
					if (imm.isActive()) {
		            	imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS); 
		            }
					et.setText("");
			}
        });
        
        set.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				System.out.println("I am setting");
			}
        	
        });
        
        handler = new Handler() {
            public void handleMessage(Message msg) {
            	if(200==msg.what)
            		successRep();
            	else
            		failedRep(msg.what);
                super.handleMessage(msg);
            }
        };
        
	}
	   
	    class AuthListener implements WeiboAuthListener {
	        
	        @Override
	        public void onComplete(Bundle values) {
	            // 从 Bundle 中解析 Token
	            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
	            if (mAccessToken.isSessionValid()) {
	                // 保存 Token 到 SharedPreferences
	                AccessTokenKeeper.writeAccessToken(weiboPoster.this, mAccessToken);
	                Toast.makeText(weiboPoster.this, "登录成功", Toast.LENGTH_SHORT).show();
	            } else {
	                // 当您注册的应用程序签名不正确时，就会收到 Code，请确保签名正确
	                String code = values.getString("code");
	                String message = "授权失败";
	                if (!TextUtils.isEmpty(code)) {
	                    message = message + "\nObtained the code: " + code;
	                }
	                Toast.makeText(weiboPoster.this, message, Toast.LENGTH_LONG).show();
	            }
	        }

	        @Override
	        public void onCancel() {
	        	
	        }

	        @Override
	        public void onWeiboException(WeiboException e) {
	            Toast.makeText(weiboPoster.this, 
	                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
	        }
	    }

		public void successRep() {
			Toast.makeText(myInstance, "发送成功", 1).show();

		}

		public void failedRep(int statusCode) {
			Toast.makeText(myInstance, "error: "+statusCode, 1).show();
		}
}
