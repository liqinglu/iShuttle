package nandgate.ishuttle.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import nandgate.ishuttle.R;
import nandgate.ishuttle.weibo.AccessTokenKeeper;
import nandgate.ishuttle.weibo.weiboPoster;

public class ConfirmView extends Activity{
	private static ConfirmView mConfirmView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.my_dialog);
		mConfirmView=this;
		new AlertDialog.Builder(this). 
                setTitle("确定重置微博账号？"). 
                setIcon(R.drawable.ic_launcher). 
                setPositiveButton("是", new OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						AccessTokenKeeper.clear(weiboPoster.myInstance);
						Intent mIntent=new Intent();
						mIntent.setClass(ConfirmView.this, weiboPoster.class);
						mConfirmView.startActivity(mIntent);
						finish();
					}
                
                }).
                setNegativeButton("否", new OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
                	
                }).show();
	}

}
