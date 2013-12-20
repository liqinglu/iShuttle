package nandgate.ishuttle.mapper;

import nandgate.ishuttle.R;
import nandgate.ishuttle.navigator.MainActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class PopLayer extends PopupOverlay{

	public static GeoPoint gp;
	static PopLayer myInst;

	public PopLayer(MapView mMapView) {
		super(mMapView, new PopupClickListener(){
			public void onClickedPopup(int arg0) {
				myInst.showPopup();
			}
		});
		myInst=this;
	}
	
	public void showLayer(GeoPoint gp){
		PopLayer.gp=gp;
	}
	
	public void showPopup(){
		View election=View.inflate(MainActivity.mInstance, R.layout.elect_home, null);
		TextView mTextView=(TextView)election.findViewById(R.id.sethome);
		Button mButton=(Button)election.findViewById(R.id.ensurence);
		election.setClickable(false);
		mTextView.setClickable(false);
		mButton.setOnClickListener(MainActivity.mInstance);
		super.showPopup(election, gp, 32);
	}
}
