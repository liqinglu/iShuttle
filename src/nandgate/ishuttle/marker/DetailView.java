package nandgate.ishuttle.marker;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import nandgate.ishuttle.R;
import nandgate.ishuttle.mapper.LayerManager;
import nandgate.ishuttle.navigator.MainActivity;
import nandgate.ishuttle.selector.CSVReader;
import nandgate.ishuttle.settings.SettingView;
import nandgate.ishuttle.settings.SystemConfig;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailView extends Activity{
	static List<String> station;
	static DetailView myInstance;
	@SuppressLint("UseValueOf")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		myInstance=this;
		LayerManager mLayerManager=MainActivity.mLayerManager;
		
		if(mLayerManager.searchType==LayerManager.STA_SEARCH){
			DetailView.station=mLayerManager.mCollector.station;
		}else if(mLayerManager.searchType==LayerManager.LIN_SEARCH){
			if(mLayerManager.selection<mLayerManager.mCollector.line.size())
				DetailView.station=mLayerManager.mCollector.line.get((mLayerManager.selection+1)*5);
			else
				DetailView.station=mLayerManager.mCollector.station;
		}else{
			String line=mLayerManager.mMKPois.get(mLayerManager.selection).address;
			String idx=mLayerManager.mMKPois.get(mLayerManager.selection).name;
			DetailView.station=MainActivity.busInfo.get(line).get(new Integer(idx));
		}
		
		TextView title=(TextView)findViewById(R.id.tvitem1);
		TextView line=(TextView)findViewById(R.id.tvitem2);
		TextView desc=(TextView)findViewById(R.id.tvitem3);
		TextView time=(TextView)findViewById(R.id.tvitem4);
		ImageView pic=(ImageView)findViewById(R.id.imgitem);
		
		Button add=(Button)findViewById(R.id.add);
		
		Typeface mTypeface=Typeface.createFromAsset(getAssets(), "fonts/my_type.ttf");
		title.setText(station.get(CSVReader.CELL_NAME));
		line.setTypeface(mTypeface);
		line.setText(station.get(CSVReader.CELL_NAME)+" 站是 "+
							station.get(CSVReader.CELL_LINE)+" 号线的第 "+
							station.get(CSVReader.CELL_SEQUENCE)+" 站。");
		desc.setTypeface(mTypeface);
		desc.setText(station.get(CSVReader.CELL_NAME)+" 站的停靠点是 "+
							station.get(CSVReader.CELL_DESC)+" 请参考街景图所示。");
		time.setTypeface(mTypeface);
		time.setText(station.get(CSVReader.CELL_NAME)+" 站的到站时间是 "+station.get(CSVReader.CELL_TIME));
		
		String number="";
		if(station.get(CSVReader.CELL_SEQUENCE).length()==1)
			number="0"+station.get(CSVReader.CELL_SEQUENCE);
		try{
			InputStream is=getAssets().open("pics/"+station.get(CSVReader.CELL_LINE)+number+".JPG");
			pic.setImageBitmap(BitmapFactory.decodeStream(is));
			is.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		add.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				SystemConfig.getInstance().setLinePref(station.get(CSVReader.CELL_LINE), station.get(CSVReader.CELL_LNG), station.get(CSVReader.CELL_LAT));
				Intent mIntent=new Intent(myInstance, SettingView.class);
				mIntent.putExtra("set_station", true);
				myInstance.startActivity(mIntent);
				finish();
			}
			
		});

	}

	@Override
	public boolean onTouchEvent(MotionEvent event){
		finish();
		return true;
	}
}
