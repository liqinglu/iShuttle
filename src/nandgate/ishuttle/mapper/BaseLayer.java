package nandgate.ishuttle.mapper;

import java.util.ArrayList;

import nandgate.ishuttle.marker.DetailView;
import nandgate.ishuttle.navigator.MainActivity;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class BaseLayer extends ItemizedOverlay<OverlayItem>{
	
	LayerManager mManager=MainActivity.mLayerManager;
	Boolean step=false;
	
	public static final int STEP_ONE=1;
	public static final int STEP_TWO=2;
	
	public BaseLayer(Drawable arg0, MapView arg1) {
		super(arg0, arg1);
	}
	
	public void setFlag(Boolean flag){
		step=flag;
	}
	
	protected boolean onTap(int index){
		mManager.selection=index;
		if(mManager.searchType==mManager.POI_SEARCH && step==false){
			MainActivity.mMapView.getOverlays().clear();
			GeoPoint gp=mManager.mMKPois.get(index).pt;
			mManager.applyBaseLayer(mManager.mMKPois);
			mManager.applyPopLayer(gp);
			mManager.showPop(true);
			MainActivity.mMapView.refresh();
		}else{
			Intent mIntent=new Intent(MainActivity.mInstance, DetailView.class);
			MainActivity.mInstance.startActivity(mIntent);
		}
		
		return true;
	}
	
	@Override
	public boolean onTap(GeoPoint arg0, MapView arg1) {
		try{
			mManager.showPop(false);
		}catch(Exception e){
			System.out.println("can not close this pop");
		};
		return super.onTap(arg0, arg1);
	}

	public void showLayer(ArrayList<MKPoiInfo> arrayList){
		for(int i=0; i<arrayList.size(); ++i){
			MKPoiInfo info=arrayList.get(i);
			OverlayItem tmp=new OverlayItem(info.pt, info.name, info.address);
			tmp.setMarker(MainActivity.marker);
			addItem(tmp);
		}
	}
}
