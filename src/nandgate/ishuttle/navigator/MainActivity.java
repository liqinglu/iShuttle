package nandgate.ishuttle.navigator;

import nandgate.ishuttle.pathbutton.composerLayout;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import nandgate.ishuttle.R;
import nandgate.ishuttle.listener.MyLocationListener;
import nandgate.ishuttle.listener.TextListener;
import nandgate.ishuttle.listener.MyKSearchListener;
import nandgate.ishuttle.mapper.LayerManager;
import nandgate.ishuttle.mapper.PopLayer;
import nandgate.ishuttle.marker.PoiCollector;
import nandgate.ishuttle.selector.*;
import nandgate.ishuttle.settings.SettingView;
import nandgate.ishuttle.tracker.MasterTracker;
import nandgate.ishuttle.tracker.SlaveTracker;
import nandgate.ishuttle.weibo.weiboPoster;

import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;

public class MainActivity extends Activity implements OnClickListener{
	private static final String key="4d2c5892407098dc2541f1fc5d37bfbd";
	public static BMapManager mBMapManager = null;
	public static Map<String, Map<Integer, List<String>>> busInfo=null;
	public static MapView mMapView=null;
	public static MainActivity mInstance;
	public static Drawable marker;
	public static LayerManager mLayerManager;

	private AutoCompleteTextView et_search;
	private Button btn_search;
	private RadioButton radio_btn_srnd;//快查
	private RadioButton radio_btn_post;//社交
	private RadioButton radio_btn_more;//更多
	private RadioButton radio_btn_line;//路线
	private RadioButton radio_btn_track;//模式
	private composerLayout clayout;
    private LinearLayout splash;
	private MKSearch mkSearch;
	private MyKSearchListener mMKSearchListener;
	private LocationClient mLocationClient;
	private MyLocationListener mMyLocationListener;
	private int displayType;
	private Boolean isCompass;

	private static final int NORMAL=0;
	private static final int TRAFFIC=1;
	private static final int SATELLITE=2;
	private static final int TRAF_SATE=3;
	private static final int BIAS=56789;
	private static final int BUTTON1=0;
	private static final int BUTTON2=1;
	private static final int BUTTON3=2;
	private static final int BUTTON4=3;
	 @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mInstance=this;
		mBMapManager = new BMapManager(this);
		mBMapManager.init(key,null);
		setContentView(R.layout.activity_main);
		displayType=NORMAL;
		isCompass=true;
		splash = (LinearLayout) findViewById(R.id.splashscreen);
		mLocationClient = new LocationClient(getApplicationContext());
		new SplashHandler(mLocationClient, splash).sendMessageDelayed(new Message(), 1000);
	    getSource();
	    
		mMapView = (MapView) findViewById(R.id.bmapView);
		mLayerManager=new LayerManager(mMapView);
		mMapView.setBuiltInZoomControls(true);
		MapController mMapController = mMapView.getController();
		GeoPoint point = new GeoPoint((int) (39.915 * 1E6), (int) (116.404 * 1E6));
		mMapController.setCenter(point);
		mMapController.setZoom(14);
		initView();
		initSearch();
		// 发起定位请求
        mMyLocationListener=new MyLocationListener(mMapView);
        mMyLocationListener.setFresh();
		mLocationClient.registerLocationListener(mMyLocationListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setAddrType("null");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(5000);// 设置发起定位请求的间隔时间为5000ms
		option.disableCache(true);// 禁止启用缓存定位
		option.setPoiNumber(0); // 最多返回POI个数
		option.setPoiDistance(0); // poi查询距离
		option.setPoiExtraInfo(false); // 是否需要POI的电话和地址等详细信息
		mLocationClient.setLocOption(option);
		
		clayout.init(new int[] { R.drawable.guid_me,
				R.drawable.locate_me, R.drawable.set_voice,
				R.drawable.change_mode }, R.drawable.surf_home,
				R.drawable.composer_icn_plus, composerLayout.RIGHTCENTER, 120,
				200);
}
	 
		@Override
		public void onClick(View v) {
			System.out.println(v.getId());
			switch (v.getId()) {
			case R.id.radio_btn_line:
				mLocationClient.stop();
				LayoutInflater inflater=LayoutInflater.from(MainActivity.this);
				final View Linepickerview=inflater.inflate(R.layout.timepicker, null);
				ScreenInfo screenInfo = new ScreenInfo(MainActivity.this);
				final WheelMain wheelMain = new WheelMain(Linepickerview);
				wheelMain.screenheight = screenInfo.getHeight();
				wheelMain.initBusStationPicker();
				new AlertDialog.Builder(MainActivity.this)
				.setTitle("选择线路")
				.setView(Linepickerview)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						mLayerManager.initial();
						mLayerManager.switchType(MainActivity.busInfo.get(WheelMain.key), WheelMain.index);
						mLayerManager.applyRouteLayer();
						mLayerManager.applyBaseLayer();
						mInstance.initSearch();
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {}
				}).show();
				break;
			case R.id.radio_btn_srnd:
				mMyLocationListener.setReq();
				mLocationClient.start();
				mLocationClient.requestLocation();
				break;
			 case R.id.btn_search:
				 String content = et_search.getText().toString();
				 mLocationClient.stop();
				 mkSearch.poiSearchInCity("北京",content);
			 break;
			 
			 case R.id.radio_btn_more:
				 mLocationClient.stop();
				 Intent intent = new Intent();
					intent.setClass(MainActivity.this,SettingView.class);
					startActivity(intent);
				 break;
				 
			 case R.id.radio_btn_track: 
				 mLocationClient.stop();
				 new SlaveTracker();
				 break;
			 case R.id.radio_btn_post: 
				Intent intent1 = new Intent();
				intent1.setClass(MainActivity.this, weiboPoster.class);
				startActivity(intent1);
				 break;
				 
			 case R.id.ensurence:
				 MainActivity.mLayerManager.initial();
				 mLayerManager.applyGraphLayer(PopLayer.gp);
				 mLayerManager.applyBaseLayer(new PoiCollector(PopLayer.gp).pois, true);
				 mLayerManager.showPop(false);
				 mMapView.refresh();
				 mMapView.getController().animateTo(PopLayer.gp);
				 break;
				 
			 case (BIAS+BUTTON1):
				
				 break;
			 
			 case (BIAS+BUTTON2):
					if(isCompass)
						mMyLocationListener.setCompass();
					else
						mMyLocationListener.setFresh();
				 	isCompass=!isCompass;
					mLocationClient.start();
					mLocationClient.requestLocation();
				break;

			 case (BIAS+BUTTON3):
				 AlertDialog mad3=new AlertDialog.Builder(MainActivity.this).setTitle("set voice").create();
			 	mad3.show();
				 break;

			 case (BIAS+BUTTON4):
				 switch(displayType){
				 	case NORMAL:
				 		displayType=TRAFFIC;
				 		mMapView.setTraffic(true);
				 		mMapView.setSatellite(false);
				 		break;
				 	case TRAFFIC:
				 		displayType=SATELLITE;
				 		mMapView.setTraffic(false);
				 		mMapView.setSatellite(true);
				 		break;
				 	case SATELLITE:
				 		displayType=TRAF_SATE;
				 		mMapView.setTraffic(true);
				 		mMapView.setSatellite(true);
				 		break;
				 	case TRAF_SATE:
				 	default:
				 		displayType=NORMAL;
				 		mMapView.setTraffic(false);
				 		mMapView.setSatellite(false);
				 		break;
				 }
				 mMapView.refresh();
				 break;
			}
		}

		@Override
		protected void onDestroy() {
			mMapView.destroy();
			super.onDestroy();
		}

		@Override
		protected void onPause() {
			mMapView.onPause();
			super.onPause();
		}

		@Override
		protected void onResume() {
			mMapView.onResume();
			super.onResume();
		}
		
		//获取班车信息
		private void getSource(){
			try {
				CSVReader mCSVReader = new CSVReader(getClass().getResourceAsStream("/assets/bus_all.csv"));
				busInfo = mCSVReader.readCSVFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//初始化控件
		private void initView() {
			marker=getResources().getDrawable(R.drawable.red);
			btn_search = (Button) findViewById(R.id.btn_search);
			btn_search.setOnClickListener(this);
			et_search = (AutoCompleteTextView) findViewById(R.id.et_search);
			radio_btn_line = (RadioButton) findViewById(R.id.radio_btn_line);
			radio_btn_srnd = (RadioButton) findViewById(R.id.radio_btn_srnd);
			radio_btn_post = (RadioButton) findViewById(R.id.radio_btn_post);
			radio_btn_more = (RadioButton) findViewById(R.id.radio_btn_more);
			radio_btn_track = (RadioButton) findViewById(R.id.radio_btn_track);
			radio_btn_srnd.setOnClickListener(this);
			radio_btn_track.setOnClickListener(this);
			radio_btn_post.setOnClickListener(this);
			radio_btn_more.setOnClickListener(this);
			radio_btn_line.setOnClickListener(this);
			clayout = (composerLayout) findViewById(R.id.surfButton);
		}
		
		//循环更新建议列表
		public void initSearch(){	
			mkSearch = new MKSearch();
			ArrayAdapter<String> sugAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line);
	        et_search.setAdapter(sugAdapter);
	        mMKSearchListener=MyKSearchListener.getMyInstance(sugAdapter, mMapView);
	        mkSearch.init(mBMapManager, mMKSearchListener);
	        et_search.addTextChangedListener(new TextListener(mkSearch));
		}
		
		//slave tracker更新位置
		public static void postOnMap(GeoPoint gp) {
			mLayerManager.initial();
			mLayerManager.applyBaseLayer(gp);
			mMapView.refresh();
			mMapView.getController().animateTo(gp);
		}
}
