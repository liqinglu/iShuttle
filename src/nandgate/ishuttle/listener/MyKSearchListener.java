package nandgate.ishuttle.listener;

import nandgate.ishuttle.mapper.BaseLayer;
import nandgate.ishuttle.mapper.LayerManager;
import nandgate.ishuttle.mapper.RouteLayer;
import nandgate.ishuttle.navigator.MainActivity;
import android.widget.ArrayAdapter;

import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.PoiOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionInfo;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;

public class MyKSearchListener implements MKSearchListener{
	
	private ArrayAdapter<String> sugAdapter = null;
	private MapView mMapView=null;
	private LayerManager mManager=null;
	private RouteLayer mLayer=null;
	
	private static MyKSearchListener myInstance=null;
	
	public static MyKSearchListener getMyInstance(ArrayAdapter<String> sugAdapter, MapView mMapView){
		if(myInstance==null)
			myInstance=new MyKSearchListener(sugAdapter, mMapView);
		
		return myInstance;
	}
	
	public static MyKSearchListener getMyInstance(){
		return myInstance;
	}
	
	public void setLayer(RouteLayer layerIns){
		mLayer=layerIns;
	}
	
	private MyKSearchListener(ArrayAdapter<String> sugAdapter, MapView mMapView){
		this.sugAdapter=sugAdapter;
		this.mMapView=mMapView;
		this.mManager=MainActivity.mLayerManager;
	}
	
	@Override
	public void onGetAddrResult(MKAddrInfo arg0, int arg1) {}

	@Override
	public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {}

	@Override
	public void onGetDrivingRouteResult(MKDrivingRouteResult res, int arg1) {
		mLayer.setData(res.getPlan(0).getRoute(0));
		mMapView.refresh();
	}

	@Override
	public void onGetPoiDetailSearchResult(int arg0, int arg1) {}

	@Override
	public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1, int arg2) {}

	@Override
	public void onGetSuggestionResult(MKSuggestionResult res, int arg1) {
		System.out.println("suggestionArrived");
		if ( res == null || res.getAllSuggestions() == null){
    		return ;
    	}
    	
    	sugAdapter.clear();
    	for ( MKSuggestionInfo info : res.getAllSuggestions()){
    		if ( info.key != null)
    		    sugAdapter.add(info.key);
    	}
    	sugAdapter.notifyDataSetChanged();
	}

	@Override
	public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {}

	@Override
	public void onGetTransitRouteResult(MKTransitRouteResult res, int error) {}

	@Override
	public void onGetPoiResult(MKPoiResult res, int type, int error) {
		System.out.println("getPoiArrived");
		if(res==null)
			return;
		mManager.initial();
		mManager.switchType(null, -1);
		mManager.applyBaseLayer(res.getAllPoi());
		mMapView.refresh();
		for (MKPoiInfo info : res.getAllPoi()) {
			if (info.pt != null) {
				mMapView.getController().animateTo(info.pt);
				break;
			}
		}
	}

}
