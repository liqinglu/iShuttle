package nandgate.ishuttle.marker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nandgate.ishuttle.navigator.MainActivity;
import nandgate.ishuttle.selector.CSVReader;

import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class PoiCollector {
	public ArrayList<MKPoiInfo> pois=new ArrayList<MKPoiInfo>();
	private MKPoiInfo tmp;
	
	public PoiCollector(GeoPoint center){
		Set<String> key=MainActivity.busInfo.keySet();
		for(Iterator<String> it=key.iterator(); it.hasNext(); ){
			Map<Integer, List<String>> line=MainActivity.busInfo.get(it.next());
			for(int i=5; i<99; ++i){
				try{
					List<String> station=line.get(i);
					Double cLat=Double.valueOf(station.get(CSVReader.CELL_LAT));
					Double cLng=Double.valueOf(station.get(CSVReader.CELL_LNG));
					GeoPoint mGP=new GeoPoint((int)(cLat * 1E6), (int)(cLng * 1E6));
					if(getDistance(mGP, center)<3000){
						newPoi().setGeo(mGP)
							.setAddr(station.get(CSVReader.CELL_LINE))
								.setName(station.get(CSVReader.CELL_INDEX))
									.InsertIt();
					}
				}catch(Exception e){
					
				}
			}
			
		}
	}
	
	PoiCollector newPoi(){
		tmp=new MKPoiInfo();
		return this;
	}
	
	PoiCollector setGeo(GeoPoint gp){
		tmp.pt=gp;
		return this;
	}
	
	PoiCollector setAddr(String address){
		tmp.address=address;
		return this;
	}
	
	PoiCollector setName(String name){
		tmp.name=name;
		return this;
	}
	
	void InsertIt(){
		pois.add(tmp);
	}
	
	public static Double getDistance(GeoPoint gp1, GeoPoint gp2){
		return DistanceUtil.getDistance(gp1, gp2);
	}
}
