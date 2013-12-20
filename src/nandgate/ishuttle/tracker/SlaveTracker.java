package nandgate.ishuttle.tracker;

import java.util.Timer;
import java.util.TimerTask;

import nandgate.ishuttle.navigator.MainActivity;
import nandgate.ishuttle.selector.CSVReader;
import nandgate.ishuttle.settings.SettingView;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.baidu.platform.comapi.basestruct.GeoPoint;

public class SlaveTracker extends TimerTask{
	public static String locUrl="http://ishuttlebuswx.sinaapp.com/postLocation.php";
	public SlaveTracker(){
		Timer mTimer=new Timer();
		mTimer.scheduleAtFixedRate(this, 5000, 10000);
	}

	@Override
	public void run() {
		syncLoc();
	}
	

	String getLine(){
		return SettingView.station.get(CSVReader.CELL_LINE);
	}


	
	void syncLoc(){
		String url=locUrl+"?"+"action=get&"+"line="+getLine();
		HttpGet request=new HttpGet(url);
		try {
			HttpResponse response = new DefaultHttpClient().execute(request);
			if(response.getStatusLine().getStatusCode() == 200){
				String result = EntityUtils.toString(response.getEntity());
				String[] data=result.split(" ");
				if(data.length==2)
					MainActivity.postOnMap(new GeoPoint(Integer.valueOf(data[0]), Integer.valueOf(data[1])));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
