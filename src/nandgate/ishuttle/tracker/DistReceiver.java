package nandgate.ishuttle.tracker;

import nandgate.ishuttle.settings.SystemConfig;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

	public class DistReceiver extends BroadcastReceiver{
		  @Override
		  public void onReceive(Context context, Intent intent)
		  {
			  if(SystemConfig.checkTimeArrange()){
			    	showAlarm(context);
			    }
			  new Thread(){

					@Override
					public void run() {
						SystemClock.sleep(1000);
						SystemConfig.getInstance().resetDistAlarm();
						SystemConfig.getInstance().detectTime();
						super.run();
					}
			    	
			    }.start();
			  
		  }
		  
		  
		  private void showAlarm(Context context){
			    
			    Intent i = new Intent(context, AlarmSurface.class);
			    Bundle bundleRet = new Bundle();    
			    bundleRet.putString("STR_CALLER", "");
			    i.putExtras(bundleRet);
			    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			    context.startActivity(i);
		  }
	}
