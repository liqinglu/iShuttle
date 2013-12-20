package nandgate.ishuttle.mapper;

import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Symbol;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class GraphLayer extends GraphicsOverlay{

	GraphLayer myInstance;
	public GraphLayer(MapView arg0) {
		super(arg0);
		myInstance=this;
	}
	
	void showLayer(GeoPoint gp){
		int radius=800;
		Geometry mGeo=new Geometry();
		mGeo.setCircle(gp, radius);
		Symbol mSymbol=new Symbol();
		Symbol.Color mColor=mSymbol.new Color();
		mColor.alpha=100;
		mColor.red=255;
		mColor.blue=255;
		mColor.green=207;
		mSymbol.setSurface(mColor, 1, 0);
		myInstance.setData(new Graphic(mGeo, mSymbol));
	}
}
