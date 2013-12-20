package nandgate.ishuttle.mapper;

import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;

public class LocLayer extends MyLocationOverlay{
	
	private static LocationMode currentMode=LocationMode.NORMAL;
	public LocLayer(MapView arg0) {
		super(arg0);
		
	}

	public void setMode(LocationMode mode){
		currentMode=mode;
	}
	
	public void showlayer(LocationData locationData) {
		super.setData(locationData);
		setLocationMode(currentMode);
	}

}
