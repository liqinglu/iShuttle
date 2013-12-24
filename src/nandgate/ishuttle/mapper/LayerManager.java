package nandgate.ishuttle.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nandgate.ishuttle.R;
import nandgate.ishuttle.listener.SingleListener;
import nandgate.ishuttle.marker.LineCollector;
import nandgate.ishuttle.navigator.MainActivity;
import nandgate.ishuttle.selector.CSVReader;
import nandgate.ishuttle.selector.WheelMain;

import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay.LocationMode;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class LayerManager {
	private Double cLat = 39.945 ;
	private Double cLon = 116.404;
	private BaseLayer mBase;
	private RouteLayer mRoute;
	private GraphLayer mGraph;
	private PopLayer mPop;
	private LocLayer mLoc;
	
	private MainActivity mActivity;
	private MapView mMapView;
	private MapController mMapController;
	
	public LineCollector mCollector=null;
	public ArrayList<MKPoiInfo> mMKPois=null;
	public int selection=-1;
	public int searchType=-1;
	
	public static final int STA_SEARCH=0;
	public static final int LIN_SEARCH=1;
	public static final int POI_SEARCH=2;
	
	public LayerManager(MapView mMapView){
		this.mActivity=MainActivity.mInstance;
		this.mMapView = mMapView;
		MKMapViewListener mMapListener = new SingleListener(mMapView);
		//TODO: add some operations to make MapView reconfig.
		 mMapController = mMapView.getController();
		mMapView.regMapViewListener(MainActivity.mBMapManager, mMapListener);
	}
	
	public void initial(){
		searchType=-1;
		selection=-1;
		mCollector=null;
		mMKPois=null;
		mMapView.getOverlays().clear();
	}
	
	public void switchType(Map<Integer, List<String>> content, int index){
		if(index>0){
			mMapController.setZoom(15);
			List<String> station=content.get(WheelMain.index*5);
			cLat = Double.valueOf(station.get(CSVReader.CELL_LAT));
			cLon = Double.valueOf(station.get(CSVReader.CELL_LNG));
			mCollector=new LineCollector(station);
			searchType=STA_SEARCH;
		}else if(index==0){
			mMapController.setZoom(13);
			List<String> finalStation=MainActivity.busInfo.get("0").get(0);
			cLat = (Double.valueOf(content.get(5).get(CSVReader.CELL_LAT))+Double.valueOf(finalStation.get(CSVReader.CELL_LAT)))*0.5;
			cLon = (Double.valueOf(content.get(5).get(CSVReader.CELL_LNG))+Double.valueOf(finalStation.get(CSVReader.CELL_LNG)))*0.5;
			mCollector=new LineCollector(content, finalStation);
			searchType=LIN_SEARCH;
		}else{
			mMapController.setZoom(14);
			searchType=POI_SEARCH;
		}
		mMapController.setCenter(new GeoPoint((int)(cLat * 1E6), (int)(cLon * 1E6)));
	}
	
	public void applyLocLayer(LocationData locationData){
		mLoc.setMode(LocationMode.NORMAL);
		mLoc.showlayer(locationData);
		mMapView.getOverlays().add(mLoc);
	}
	
	public void applyLocLayer(LocationData locationData, Boolean cpMod){
		mLoc=new LocLayer(MainActivity.mMapView);
		if(cpMod)
			mLoc.setLocationMode(LocationMode.COMPASS);
		else
			mLoc.setLocationMode(LocationMode.FOLLOWING);
		mLoc.showlayer(locationData);
		mMapView.getOverlays().add(mLoc);
	}
	
	public void applyBaseLayer(){
		mBase=new BaseLayer(mActivity.getResources().getDrawable(R.drawable.red), mMapView);
	   	mBase.showLayer(mCollector.pois, MainActivity.marker);
	   	mMapView.getOverlays().add(mBase);
	}
	
	public void applyBaseLayer(ArrayList<MKPoiInfo> mMKPois){
		this.applyBaseLayer(mMKPois, false);
	}
	
	public void applyBaseLayer(ArrayList<MKPoiInfo> mMKPois, Boolean flag){
		this.mMKPois=mMKPois;
		mBase=new BaseLayer(mActivity.getResources().getDrawable(R.drawable.red), mMapView);
	   	mBase.showLayer(mMKPois, MainActivity.marker);
	   	mBase.setFlag(flag);
	   	mMapView.getOverlays().add(mBase);
	}
	
	public void applyBusLayer(GeoPoint gp){
		mBase=new BaseLayer(mActivity.getResources().getDrawable(R.drawable.bus), mMapView);
		ArrayList<MKPoiInfo> oneList=new ArrayList<MKPoiInfo>();
		MKPoiInfo mMKPoint=new MKPoiInfo();
		mMKPoint.pt=gp;
		oneList.add(mMKPoint);
		mBase.showLayer(oneList, mActivity.getResources().getDrawable(R.drawable.bus));
		mBase.setFlag(false);
	   	mMapView.getOverlays().add(mBase);
	}
	
	public void applyRouteLayer(){
		mRoute=new RouteLayer(mActivity, mMapView);
		mRoute.showLayer(mCollector);
		mMapView.getOverlays().add(mRoute);
	}
	
	public void applyGraphLayer(GeoPoint center){
		mGraph=new GraphLayer(mMapView);
		mGraph.showLayer(center);
		mMapView.getOverlays().add(mGraph);
	}
	
	public void applyPopLayer(GeoPoint gp){
	   	mPop=new PopLayer(mMapView);
	   	mPop.showLayer(gp);
	   	mMapView.getOverlays().add(mPop);
	}
	
	public void showPop(boolean display){
		if(display){
			mPop.showPopup();
		}else{
			mPop.hidePop();
		}
	}
}
