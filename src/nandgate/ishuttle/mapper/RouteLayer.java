package nandgate.ishuttle.mapper;

import java.util.ArrayList;

import nandgate.ishuttle.listener.MyKSearchListener;
import nandgate.ishuttle.marker.LineCollector;
import nandgate.ishuttle.navigator.MainActivity;
import android.app.Activity;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPlanNode;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.mapapi.search.MKWpNode;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class RouteLayer extends RouteOverlay{

	MyKSearchListener mListener;
	public RouteLayer(Activity arg0, MapView arg1) {
		super(arg0, arg1);
		mListener = MyKSearchListener.getMyInstance();
		mListener.setLayer(this);
	}
	
	void showLayer(LineCollector mPointCollector){
		ArrayList<MKWpNode> nodes=new ArrayList<MKWpNode>();
		
		for(int index=1; index<mPointCollector.pois.size()-1; ++index){
			MKWpNode mMKWpNode=new MKWpNode();
			mMKWpNode.pt=mPointCollector.pois.get(index).pt;
			nodes.add(mMKWpNode);
		    }
		
		p2pRoute(mPointCollector.pois.get(0).pt, mPointCollector.pois.get(mPointCollector.pois.size()-1).pt, nodes);
	}
	
	void p2pRoute(GeoPoint st, GeoPoint en, ArrayList<MKWpNode> nodes){
		MKSearch mSearch = new MKSearch();
		mSearch.init(MainActivity.mBMapManager, mListener);
		MKPlanNode startNode=new MKPlanNode();
		startNode.pt=st;
		MKPlanNode endNode=new MKPlanNode();
		endNode.pt=en;
		mSearch.drivingSearch("北京", startNode, "北京",endNode, nodes);
	}
}
