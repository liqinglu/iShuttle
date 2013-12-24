package nandgate.ishuttle.settings;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import nandgate.ishuttle.navigator.MainActivity;
import nandgate.ishuttle.tracker.MasterTracker;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class SystemConfig {
	private LocationClient mLocationClient;
	private BDLocationListener mBDLocationListener;   //用来为报警提供位置信息
	private MasterTracker mMasterTracker;	//用来上报当前位置信息
	
	private AlarmManager am_Time;	   			//用来触发时钟报警
	private AlarmManager am_Dist;  				//用来触发距离报警
	private DstScheduleTask mDstTask;    	//距离上报定时器
	private RepScheduleTask mRepTask;    	//位置上报定时器
	private Boolean isDetecting;     				//距离探测使能
	private Boolean isReporting;						//位置上报使能
	
	private static SystemConfig mySelf;
	
	private SystemConfig(){
		mMasterTracker=new MasterTracker();
		mDstTask=new DstScheduleTask();
		mRepTask=new RepScheduleTask();
		
    	mLocationClient = new LocationClient(MainActivity.mInstance);
    	LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setAddrType("null");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(5000);// 设置发起定位请求的间隔时间为5000ms
		option.disableCache(true);// 禁止启用缓存定位
		option.setPoiNumber(0); // 最多返回POI个数
		option.setPoiDistance(0); // poi查询距离
		option.setPoiExtraInfo(false); // 是否需要POI的电话和地址等详细信息
		
		mLocationClient.setLocOption(option);
		mBDLocationListener=new BDLocationListener(){
			@Override
			public void onReceiveLocation(BDLocation location) {
				GeoPoint currentLoc=new GeoPoint((int)(location.getLatitude()*1e6), (int)(location.getLongitude()* 1e6));
				if(isDetecting)
					updateDistAlarm(currentLoc);  //下班班车定位
				if(isReporting)
					updateLocRep(currentLoc); //上班班车位置上报
			}
			@Override
			public void onReceivePoi(BDLocation arg0) {}
        };
	        	
		mLocationClient.registerLocationListener(mBDLocationListener);
	}
	
	public static SystemConfig getInstance(){
		if(mySelf==null)
			mySelf=new SystemConfig();
		
		return mySelf;
	}
	
	public void initSysPref(){
        updateTimeAlarm();
        detectTime();
        reportTime();
	}
	
	public void updateTimeAlarm(){
		Long alarm_timer=Long.valueOf(getTimePref());
		if(alarm_timer==0)
			return;
		
		long day=(System.currentTimeMillis()+28800000)/86400000;
		long time=(System.currentTimeMillis()+28800000)-day*86400000;

		if(time<alarm_timer)
			time=day*86400000-28800000+alarm_timer;
		else
			time=(day+1)*86400000-28800000+alarm_timer;
		boolean check_timer = Boolean.valueOf(getAlarmPref()); 
		if(check_timer){
			am_Time = (AlarmManager)MainActivity.mInstance.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent("nandgate.ishuttle.timeup");
			PendingIntent sender = PendingIntent.getBroadcast(
					MainActivity.mInstance, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			am_Time.set(AlarmManager.RTC_WAKEUP, time, sender);
		}else if(am_Time!=null){
			Intent intent = new Intent("nandgate.ishuttle.timeup");
			PendingIntent sender = PendingIntent.getBroadcast(
					MainActivity.mInstance, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			am_Time.cancel(sender);
		}
	}
	
	private void updateDistAlarm(GeoPoint gp){
		Integer alarm_distance=Integer.valueOf(getDistPref());
		if(alarm_distance==0)
			return;
		
		Boolean alarm_en=Boolean.valueOf(getDtcPref());
		Float mLng=Float.valueOf(getLngPref());
		Float mLat=Float.valueOf(getLatPref());
		
		GeoPoint mPoint=new GeoPoint((int) (mLat*1E6), (int) (mLng*1E6));
		double distance=DistanceUtil.getDistance(mPoint, gp);
		
		if(!alarm_en && am_Dist!=null){
			Intent intent = new Intent("nandgate.ishuttle.timeup");
			PendingIntent sender = PendingIntent.getBroadcast(
					MainActivity.mInstance, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			am_Dist.cancel(sender);
		}else if(alarm_en && distance<alarm_distance){
			am_Dist = (AlarmManager)MainActivity.mInstance.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent("nandgate.ishuttle.arrive");
			PendingIntent sender = PendingIntent.getBroadcast(
					MainActivity.mInstance, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			am_Dist.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+1000, sender);
		}
	}
	
	private void updateLocRep(GeoPoint gp){
		if(Boolean.valueOf(getRepPref()))
			MasterTracker.gp=gp;
	}

	public void resetDistAlarm() {
		isDetecting=false;
		mLocationClient.stop();
	}
	
	private void setDistAlarm(){
		isDetecting=true;
		mLocationClient.start();
	}
	
	private void resetRepAlarm(){
		isReporting=false;
		mMasterTracker.cancel();
		mLocationClient.stop();
		reportTime();
	}

	private void setRepAlarm(){
		isReporting=true;
		mMasterTracker=new MasterTracker();
		mLocationClient.start();
	}
	
	public void detectTime(){
		if(!Boolean.valueOf(getDtcPref()) || Long.valueOf(getDistPref())==0){
			mDstTask.setTask(0);
			return;
		}
		long day=(System.currentTimeMillis()+28800000)/86400000;
		long time_begin=(System.currentTimeMillis()+28800000)-day*86400000;
		if(time_begin<63000000)
			time_begin=day*86400000+34200000;
		else
			time_begin=(day+1)*86400000+34200000;
		mDstTask.setTask(time_begin);
	}
	
	public void nextDay(){
		resetDistAlarm();
		long day=(System.currentTimeMillis()+28800000)/86400000;
		long time_begin=(day+1)*86400000+34200000;
		mDstTask.setTask(time_begin);
	}
	
	private void reportTime(){
		if(!Boolean.valueOf(getRepPref())){
			mRepTask.setTask(0);
			return;
		}
		long day=(System.currentTimeMillis()+28800000)/86400000;
		long time_begin=(System.currentTimeMillis()+28800000)-day*86400000;
		if(time_begin<21600000)
			time_begin=day*86400000-7200000;
		else
			time_begin=(day+1)*86400000-7200000;
		mRepTask.setTask(time_begin);
	}
	
	
	public void setLinePref(String line, String lng, String lat){
		SharedPreferences pref = MainActivity.mInstance.getSharedPreferences("lineInfo", Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.putString("myLine", line);
		editor.putString("myLng", lng);
		editor.putString("myLat", lat);
        editor.commit();
	}
	
	public void setUserPref(String isAlarm, String time, String isDetect, String distance){
		SharedPreferences pref = MainActivity.mInstance.getSharedPreferences("lineInfo", Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.putString("isAlarm", isAlarm);
		editor.putString("time", time);
		editor.putString("isDetect", isDetect);
		editor.putString("distance", distance);
        editor.commit();
	}
	
	public void setGenPref(String isReport, String ring){
		SharedPreferences pref = MainActivity.mInstance.getSharedPreferences("lineInfo", Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.putString("isReport", isReport);
		editor.putString("ring", ring);
        editor.commit();
	}
	
	public String getLinePref(){
		SharedPreferences pref = MainActivity.mInstance.getSharedPreferences("lineInfo", Context.MODE_APPEND);
		return pref.getString("myLine", "0");
	}
	
	private String getLngPref(){
		SharedPreferences pref = MainActivity.mInstance.getSharedPreferences("lineInfo", Context.MODE_APPEND);
		return pref.getString("myLng", "0");
	}
	
	private String getLatPref(){
		SharedPreferences pref = MainActivity.mInstance.getSharedPreferences("lineInfo", Context.MODE_APPEND);
		return pref.getString("myLat", "0");
	}
	
	private String getTimePref(){
		SharedPreferences pref = MainActivity.mInstance.getSharedPreferences("lineInfo", Context.MODE_APPEND);
		return pref.getString("time", "0");
	}
	
	public String getDtcPref(){
		SharedPreferences pref = MainActivity.mInstance.getSharedPreferences("lineInfo", Context.MODE_APPEND);
		return pref.getString("isDetect", "false");
	}
	
	
	public String getDistPref(){
		SharedPreferences pref = MainActivity.mInstance.getSharedPreferences("lineInfo", Context.MODE_APPEND);
		return pref.getString("distance", "0");
	}
	
	
	private String getRepPref(){
		SharedPreferences pref = MainActivity.mInstance.getSharedPreferences("lineInfo", Context.MODE_APPEND);
		return pref.getString("isReport", "false");
	}
	
	
	private String getRingPref(){
		SharedPreferences pref = MainActivity.mInstance.getSharedPreferences("lineInfo", Context.MODE_APPEND);
		return pref.getString("ring", "null");
	}
	
	
	private String getAlarmPref(){
		SharedPreferences pref = MainActivity.mInstance.getSharedPreferences("lineInfo", Context.MODE_APPEND);
		return pref.getString("isAlarm", "false");
	}

	
	public static Boolean checkTimeArrange(){
		  long day=(System.currentTimeMillis()+28800000)/86400000;
		  switch((int)(day+4)%7){
		  case 1:
		  case 2:
		  case 3:
		  case 4:
		  case 5:
			  return true;
		  case 6:
		  case 0:
			  return false;
		  }
		return false;
	  }
	
	class RepScheduleTask{
		Timer start;
		TimerTask startTask;
		Timer end;
		TimerTask endTask;
		
		void setTask(long time_begin){
			if(start!=null && end!=null){
				start.cancel();
				end.cancel();
			}
			
			if(time_begin==0)
				return;
			
			startTask=new TimerTask(){
				public void run() {
					SystemConfig.getInstance().setRepAlarm();
				}
			};
			start=new Timer();
			start.schedule(startTask, new Date(time_begin));
			
			endTask=new TimerTask(){
				public void run() {
					SystemConfig.getInstance().resetRepAlarm();
				}
			};
			end=new Timer();
			end.schedule(endTask, new Date(time_begin+10800000));
		}	
	}
	
	class DstScheduleTask{
		Timer start;
		TimerTask startTask;
		Timer end;
		TimerTask endTask;
		
		void setTask(long time_begin){
			if(start!=null && end!=null){
				start.cancel();
				end.cancel();
			}
			
			if(time_begin==0)
				return;
			
			startTask=new TimerTask(){
				public void run() {
					SystemConfig.getInstance().setDistAlarm();
				}
			};
			start=new Timer();
			start.schedule(startTask, new Date(time_begin));
			
			endTask=new TimerTask(){
				public void run() {
					if(SystemConfig.getInstance().isDetecting)
						SystemConfig.getInstance().resetDistAlarm();
					SystemConfig.getInstance().detectTime();
				}
			};
			end=new Timer();
			end.schedule(endTask, new Date(time_begin+10800000));
		}
		
	}
}
