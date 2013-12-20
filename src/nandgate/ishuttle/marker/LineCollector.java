package nandgate.ishuttle.marker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nandgate.ishuttle.selector.CSVReader;

import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class LineCollector{
	public ArrayList<MKPoiInfo> pois=new ArrayList<MKPoiInfo>();
	MKPoiInfo tmp=null;
	
	public Map<Integer, List<String>> line=null;
	public List<String> station=null;
	
	public LineCollector(List<String> station){
		singleStation(station);
		this.station=station;
		this.line=null;
	}
	
	public LineCollector(Map<Integer, List<String>> line, List<String> station){
		for(int i=5; i<99; ++i){
			try{
				singleStation(line.get(i));
			}catch(Exception e){
				continue;
			}
		}
		singleStation(station);
		this.line=line;
		this.station=station;
	}
	
	void singleStation(List<String> station){
		int lat=(int)(Double.valueOf(station.get(CSVReader.CELL_LAT))*1E6);
		int lng=(int)(Double.valueOf(station.get(CSVReader.CELL_LNG))*1E6);
		newPoi().setGeo(new GeoPoint(lat, lng))
			.setAddr(station.get(CSVReader.CELL_DESC))
				.setName(station.get(CSVReader.CELL_NAME))
					.InsertIt();
	}
	
	LineCollector newPoi(){
		tmp=new MKPoiInfo();
		return this;
	}
	
	LineCollector setGeo(GeoPoint gp){
		tmp.pt=gp;
		return this;
	}
	
	LineCollector setAddr(String address){
		tmp.address=address;
		return this;
	}
	
	LineCollector setName(String name){
		tmp.name=name;
		return this;
	}
	
	void InsertIt(){
		pois.add(tmp);
	}
	
	ArrayList<MKPoiInfo> visiblePoi(){
		ArrayList<MKPoiInfo> visibleList=new ArrayList<MKPoiInfo>();
		for(int i=0; i<pois.size(); ++i){
			if(!pois.get(i).name.contains("null"))
				visibleList.add(pois.get(i));
		}
		return visibleList;
	}
	
	
}
