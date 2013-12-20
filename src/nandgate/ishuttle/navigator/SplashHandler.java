package nandgate.ishuttle.navigator;

import com.baidu.location.LocationClient;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;

public class SplashHandler extends Handler{
	
	private LocationClient mLocationClient;
	private LinearLayout splash;

	SplashHandler(LocationClient mLocationClient, LinearLayout splash){
		this.mLocationClient=mLocationClient;
		this.splash=splash;
	}
	
	public void handleMessage(Message msg) {
		SystemClock.sleep(3000);
		AlphaAnimation alphaGone=new AlphaAnimation(1.0f, 0f);
		alphaGone.setDuration(500);
		alphaGone.setAnimationListener(new AnimationListener(){
			public void onAnimationEnd(Animation arg0) {
				mLocationClient.start();
				splash.setVisibility(View.GONE);
			}
			public void onAnimationRepeat(Animation arg0) {}
			public void onAnimationStart(Animation arg0) {}
		});
		splash.startAnimation(alphaGone);
		super.handleMessage(msg);
	}
}
