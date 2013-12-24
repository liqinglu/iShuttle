package nandgate.ishuttle.tracker;

import nandgate.ishuttle.navigator.MainActivity;
import nandgate.ishuttle.settings.SystemConfig;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;

import com.baidu.platform.comapi.basestruct.GeoPoint;

public class SlaveTracker{
	private static String locUrl="http://ishuttlebuswx.sinaapp.com/postLocation.php";
	private static SlaveTracker mySelf;
	
	private SlaveTracker(){
		
	}
	
	public static SlaveTracker getTracker(MainActivity mActivity){
		if(mySelf==null)
			mySelf=new SlaveTracker();
		
		return mySelf;
	}
	

	String getLine(){
		return SystemConfig.getInstance().getLinePref();
	}


	
	public void syncLoc(Activity mActivity){
		if(Integer.valueOf(getLine())==0){
			new AlertDialog.Builder(MainActivity.mInstance).setTitle("请先设置常用站点^_^").show();
			return;
		}
		new Thread(){

			@Override
			public void run() {
				String url=locUrl+"?"+"action=get&"+"line="+getLine();
				HttpGet request=new HttpGet(url);
				try {
					HttpResponse response = new DefaultHttpClient().execute(request);
					if(response.getStatusLine().getStatusCode() == 200){
						String result = EntityUtils.toString(response.getEntity());
						String[] data=result.split(" ");
						if(data.length==2)
							MainActivity.postOnMap(new GeoPoint(Integer.valueOf(data[1]), Integer.valueOf(data[0])));
					}
					System.out.println("slave get resp:" +response);
				} catch (Exception e) {
					e.printStackTrace();
				}
				super.run();
			}
			
			
		}.start();
		
	}
}
