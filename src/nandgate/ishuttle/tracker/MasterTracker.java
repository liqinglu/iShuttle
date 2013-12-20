package nandgate.ishuttle.tracker;

import java.util.Timer;
import java.util.TimerTask;

import nandgate.ishuttle.selector.CSVReader;
import nandgate.ishuttle.settings.SettingView;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.baidu.platform.comapi.basestruct.GeoPoint;

public class MasterTracker extends TimerTask{
	
	public static String locUrl="http://ishuttlebuswx.sinaapp.com/postLocation.php";
	public static GeoPoint gp;
	
	public MasterTracker(){
		Timer mTimer=new Timer();
		mTimer.scheduleAtFixedRate(this, 1000, 5000);
	}

	@Override
	public void run() {
		reportLoc();
	}
	

	String getLine(){
		return SettingView.station.get(CSVReader.CELL_LINE);
	}
	
	void reportLoc(){
		String url=locUrl+"?"+"action=set&"+"line="+getLine()+"&lng="+gp.getLongitudeE6()+"&lat="+gp.getLatitudeE6();
		HttpGet request=new HttpGet(url);
		try {
			HttpResponse response = new DefaultHttpClient().execute(request);
			if(response.getStatusLine().getStatusCode() == 200){
				String result = EntityUtils.toString(response.getEntity());
				if(result.contains("NOK"))
					new DefaultHttpClient().execute(request);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
