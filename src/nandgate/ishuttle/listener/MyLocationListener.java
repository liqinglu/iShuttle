package nandgate.ishuttle.listener;

import nandgate.ishuttle.marker.PoiCollector;
import nandgate.ishuttle.navigator.MainActivity;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapView;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class MyLocationListener implements BDLocationListener {
	private Boolean flag_new=false;
	private Boolean flag_req=false;
	private Boolean flag_cp=false;
	private MapView mMapView;
	
	public MyLocationListener(MapView mMapView2) {
		this.mMapView=mMapView2;
	}
	public void setFresh(){
		flag_new=true;
	}
	
	public void setReq(){
		flag_req=true;
	}
	
	public void setCompass(){
		flag_cp=true;
	}
	
	@Override
	public void onReceiveLocation(BDLocation location) {
		if (location == null)
			return;
		
		LocationData locationData = new LocationData();
		locationData.latitude = location.getLatitude();
		locationData.longitude = location.getLongitude();
		locationData.direction = 2.0f;
		locationData.accuracy = location.getRadius();
		locationData.direction = location.getDerect();
		GeoPoint mGP=new GeoPoint((int)(locationData.latitude*1e6), (int)(locationData.longitude* 1e6));
		
		if (flag_new && !flag_req && !flag_cp) {
			MainActivity.mLayerManager.initial();
			mMapView.getController().setOverlooking(0);
			MainActivity.mLayerManager.applyLocLayer(locationData, false);
			mMapView.refresh();
			mMapView.getController().animateTo(mGP);
			flag_new = false;
		}
		if(!flag_new && flag_req && !flag_cp){
			MainActivity.mLayerManager.initial();
			mMapView.getController().setOverlooking(0);
			MainActivity.mLayerManager.switchType(null, -1);
			MainActivity.mLayerManager.applyLocLayer(locationData);
			MainActivity.mLayerManager.applyBaseLayer(new PoiCollector(mGP).pois, true);
			mMapView.refresh();
			mMapView.getController().animateTo(mGP);
			flag_req = false;
		}
		if (!flag_new && !flag_req && flag_cp) {
			MainActivity.mLayerManager.initial();
			mMapView.getController().setOverlooking(-45);
			MainActivity.mLayerManager.applyLocLayer(locationData, true);
			mMapView.refresh();
			mMapView.getController().animateTo(mGP);
			
			flag_cp = false;
		}
		else{
			flag_new=false;
			flag_req=false;
			flag_cp=false;
		}
	}

	@Override
	public void onReceivePoi(BDLocation poiLocation) {
	}
}
