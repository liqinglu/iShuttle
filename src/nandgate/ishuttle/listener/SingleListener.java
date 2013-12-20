package nandgate.ishuttle.listener;

import nandgate.ishuttle.navigator.MainActivity;
import android.app.Activity;
import android.graphics.Bitmap;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.PoiOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;

public class SingleListener implements MKMapViewListener, MKSearchListener {
	Activity app=MainActivity.mInstance;
	MapView mMapView=null;
	PoiOverlay poiOverlay=null;
	
	public SingleListener(MapView mMapView){
		this.mMapView=mMapView;
	}
	
	@Override
	public void onClickMapPoi(MapPoi arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetCurrentMap(Bitmap arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMapAnimationFinish() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMapLoadFinish() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMapMoveFinish() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetPoiDetailSearchResult(int type, int error) {
		
	}

	@Override
	public void onGetPoiResult(MKPoiResult res, int type, int error) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

}
