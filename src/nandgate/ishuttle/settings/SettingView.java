package nandgate.ishuttle.settings;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import nandgate.ishuttle.R;
import nandgate.ishuttle.marker.DetailView;
import nandgate.ishuttle.selector.CSVReader;
import nandgate.ishuttle.tracker.MasterTracker;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingView extends PreferenceActivity implements OnSharedPreferenceChangeListener{
	
	private static LocationClient mLocationClient=null;
	private static BDLocationListener mBDLocationListener=null;
	private static MasterTracker mMasterTracker=null;
	private static AlarmManager am_Time=null;
	private static AlarmManager am_Dist=null;
	private static SettingView myInstance;
	private static SharedPreferences sp;
	private static Boolean isDetected=false;
	private static Boolean isReported=false;
	public static List<String> station;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myInstance=this;
		if(getIntent().getBooleanExtra("set_station", false)){
			station=DetailView.station;
		}
		getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragement()).commit(); 
		sp= PreferenceManager.getDefaultSharedPreferences(this); 
		sp.registerOnSharedPreferenceChangeListener(this);

        if(mLocationClient==null){
        	mLocationClient = new LocationClient(getApplicationContext());
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
        }
        
        if(mBDLocationListener==null){
	        	mBDLocationListener=new BDLocationListener(){
	
				@Override
				public void onReceiveLocation(BDLocation location) {
					GeoPoint currentLoc=new GeoPoint((int)(location.getLatitude()*1e6), (int)(location.getLongitude()* 1e6));
					if(isDetected)
						updateDistAlarm(currentLoc);
					if(isReported)
						updateLocRep(currentLoc);
						
				}

				@Override
				public void onReceivePoi(BDLocation arg0) {
					
				}
	        	
	        };
			mLocationClient.registerLocationListener(mBDLocationListener);
		}
        
        if(sp.getBoolean("set_11", false)){
        	mMasterTracker=new MasterTracker();
        }
	}
	
	
	public static class PrefsFragement extends PreferenceFragment{  
        @Override  
        public void onCreate(Bundle savedInstanceState) {  
            super.onCreate(savedInstanceState);  
            addPreferencesFromResource(R.xml.setting);  
        }  
    }

	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sp, String set) {

		if(set.contains("set_01") || set.contains("set_02") || set.contains("set_13")){
			updateTimeAlarm();
		}
		if(set.contains("set_03") || set.contains("set_04") || set.contains("set_13")){
			detectTime();
		}
		if(set.contains("set_11")){
			reportTime();
		}
	}  
	
	public static void updateTimeAlarm(){
		Long alarm_timer=Long.valueOf(sp.getString("set_02", "0"));
		if(alarm_timer==0)
			return;
		
		long day=(System.currentTimeMillis()+28800000)/86400000;
		long time=(System.currentTimeMillis()+28800000)-day*86400000;

		if(time<alarm_timer)
			time=day*86400000-28800000+alarm_timer;
		else
			time=(day+1)*86400000-28800000+alarm_timer;
		boolean check_timer = sp.getBoolean("set_01", false); 
		if(check_timer){
			am_Time = (AlarmManager)myInstance.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent("nandgate.ishuttle.timeup");
			PendingIntent sender = PendingIntent.getBroadcast(
	        		myInstance, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			am_Time.set(AlarmManager.RTC_WAKEUP, time, sender);
		}else if(am_Time!=null){
			Intent intent = new Intent("nandgate.ishuttle.timeup");
			PendingIntent sender = PendingIntent.getBroadcast(
	        		myInstance, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			am_Time.cancel(sender);
		}
	}
	
	public static void updateDistAlarm(GeoPoint gp){
		Integer alarm_distance=Integer.valueOf(sp.getString("set_04", "0"));
		if(alarm_distance==0)
			return;
		
		Boolean alarm_en=Boolean.valueOf(sp.getBoolean("set_03", false));
			
		Float mLng=Float.valueOf(DetailView.station.get(CSVReader.CELL_LNG));
		Float mLat=Float.valueOf(DetailView.station.get(CSVReader.CELL_LAT));
		
		GeoPoint mPoint=new GeoPoint((int) (mLat*1E6), (int) (mLng*1E6));
		double distance=DistanceUtil.getDistance(mPoint, gp);
		
		if(!alarm_en && am_Dist!=null){
			Intent intent = new Intent("nandgate.ishuttle.timeup");
			PendingIntent sender = PendingIntent.getBroadcast(
	        		myInstance, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			am_Dist.cancel(sender);
		}else if(distance<alarm_distance){
			am_Dist = (AlarmManager)myInstance.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent("nandgate.ishuttle.arrive");
			PendingIntent sender = PendingIntent.getBroadcast(
	        		myInstance, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			am_Dist.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+1000, sender);
		}
	}
	
	void updateLocRep(GeoPoint gp){
		boolean check_rep = sp.getBoolean("set_11", false); 
		if(!check_rep)
			return;
		
		mMasterTracker.gp=gp;
	}

	public static void resetDistAlarm() {
		isDetected=false;
		mLocationClient.stop();
		detectTime();
	}
	
	public static void setDistAlarm(){
		isDetected=true;
		mLocationClient.start();
	}
	
	public static void resetRepAlarm(){
		isReported=false;
		mMasterTracker.cancel();
		mLocationClient.stop();
		reportTime();
	}

	public static void setRepAlarm(){
		isReported=true;
		mMasterTracker=new MasterTracker();
		mLocationClient.start();
	}
	
private static void detectTime(){
		
		long day=(System.currentTimeMillis()+28800000)/86400000;
		long time_begin=(System.currentTimeMillis()+28800000)-day*86400000;
	
		if(time_begin<61200000)
			time_begin=day*86400000+32400000;
		else
			time_begin=(day+1)*86400000+32400000;
		
		long time_end=time_begin+10800000;
		
		TimerTask detector=new TimerTask(){
			public void run() {
				setDistAlarm();
			}
		};
		Timer start=new Timer();
		start.schedule(detector, new Date(time_begin));
	
		TimerTask shutdown=new TimerTask(){
			public void run() {
				if(isDetected)
					resetDistAlarm();
			}
		};
		Timer end=new Timer();
		end.schedule(shutdown, new Date(time_end));
	}
	
	private static void reportTime(){
		
		long day=(System.currentTimeMillis()+28800000)/86400000;
		long time_begin=(System.currentTimeMillis()+28800000)-day*86400000;
	
		if(time_begin<21600000)
			time_begin=day*86400000-7200000;
		else
			time_begin=(day+1)*86400000-7200000;
		
		long time_end=time_begin+10800000;
		
		TimerTask reporter=new TimerTask(){
			public void run() {
				setRepAlarm();
			}
		};
		Timer start=new Timer();
		start.schedule(reporter, new Date(time_begin));
	
		TimerTask shutdown=new TimerTask(){
			public void run() {
				if(isReported)
					resetRepAlarm();
			}
		};
		Timer end=new Timer();
		end.schedule(shutdown, new Date(time_end));
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
}
