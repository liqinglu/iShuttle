package nandgate.ishuttle.selector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import nandgate.ishuttle.navigator.MainActivity;
import nandgate.ishuttle.R;
import android.view.View;


public class WheelMain {

	private View view;
	private WheelView wv_line;
	private WheelView wv_bias;
	private WheelView wv_station;
	public int screenheight;
	private static int START_LINE = 1, END_LINE = 15;
	HashMap<String, List<String>> busLines = new HashMap<String, List<String>>();
	public static String key="1A";
	public static Integer index=0;
	private Boolean isMoving;
	
	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public static int getSTART_LINE() {
		return START_LINE;
	}

	public static void setSTART_LINE(int sTART_LINE) {
		START_LINE = sTART_LINE;
	}

	public static int getEND_LINE() {
		return END_LINE;
	}

	public static void setEND_LINE(int eND_LINE) {
		END_LINE = eND_LINE;
	}
	
	public WheelMain(View view) {
		super();
		key="1A";
		index=0;
		
		String[] line_single={"-"};
		String[] line_double={"A", "B"};
		String[] line_trible={"A", "B", "N"};
		String[] line_five={"A", "B", "N", "S", "T"};
		String[] line_three={"A", "B", "C"};
		
		for(int i=1; i<=15; ++i){
			switch(i){
			case 1: 
			case 3:
			case 7:
			case 15:
				busLines.put(""+i, Arrays.asList(line_double));
				break;
			case 8:
				busLines.put(""+i, Arrays.asList(line_trible));
				break;
			case 9:
				busLines.put(""+i, Arrays.asList(line_five));
				break;
			case 10:
				busLines.put(""+i, Arrays.asList(line_three));
				break;
			default:
				busLines.put(""+i, Arrays.asList(line_single));
				break;
			}

		}
		this.view = view;
		setView(view);
	}
	
	/**
	 * @Description: TODO 弹出选择器
	 */
	public void initBusStationPicker() {
		// line
		wv_line = (WheelView) view.findViewById(R.id.year);
		wv_line.setAdapter(new NumericWheelAdapter(START_LINE, END_LINE));
		wv_line.setCyclic(true);
		wv_line.setLabel("号");
		wv_line.setCurrentItem(0);

		// bias
		wv_bias = (WheelView) view.findViewById(R.id.month);
		wv_bias.setAdapter(new AlphaWheelAdapter(busLines.get(""+START_LINE)));
		wv_bias.setCyclic(false);
		wv_bias.setLabel("线");
		wv_bias.setCurrentItem(0);

		// station
		wv_station = (WheelView) view.findViewById(R.id.day);
		wv_station.setAdapter(new AlphaWheelAdapter(getStationName(key)).setLongString());
		wv_station.setCyclic(true);
		wv_station.setLabel("站");
		wv_station.setCurrentItem(0);
		
		WheelView wv_hours = (WheelView)view.findViewById(R.id.hour);
		WheelView wv_mins = (WheelView)view.findViewById(R.id.min);
		wv_hours.setVisibility(View.GONE);
		wv_mins.setVisibility(View.GONE);
		
		// 添加"line"监听
		OnWheelChangedListener wheelListener_line = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int line_num = newValue + START_LINE;
				synchronized(index){
					index=0;
				}
				wv_bias.setAdapter(new AlphaWheelAdapter(busLines.get(""+line_num)));
				key=""+line_num;
				if(!busLines.get(""+line_num).get(wv_bias.getCurrentItem()).contains("-")){
					key+=busLines.get(""+line_num).get(wv_bias.getCurrentItem());
				}
				wv_station.setAdapter(new AlphaWheelAdapter(getStationName(key)).setLongString());
			}
		};
		// 添加"bias"监听
		OnWheelChangedListener wheelListener_bias = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int line_num=wv_line.getCurrentItem()+START_LINE;
				key=""+line_num;
				if(!busLines.get(""+line_num).get(wv_bias.getCurrentItem()).contains("-")){
					key+=busLines.get(""+line_num).get(wv_bias.getCurrentItem());
				}
				wv_station.setAdapter(new AlphaWheelAdapter(getStationName(key)).setLongString());
			}
		};
		
		//添加"station"监听
		OnWheelChangedListener wheelListener_station = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				synchronized(index){
					index=newValue;
				}	
			}
		};
		wv_line.addChangingListener(wheelListener_line);
		wv_bias.addChangingListener(wheelListener_bias);
		wv_station.addChangingListener(wheelListener_station);

		// 根据屏幕密度来指定选择器字体的大小(不同屏幕可能不同)
		int textSize = (screenheight / 100) * 4;
		wv_station.TEXT_SIZE = textSize;
		wv_bias.TEXT_SIZE = textSize;
		wv_line.TEXT_SIZE = textSize;
	}

	public List<String> getStationName(String line){
		List<String> tmp=new ArrayList<String>();
		Map<Integer, List<String>> stationInfo=MainActivity.busInfo.get(line);
		tmp.add("----");
		for(int i=1; i<stationInfo.size(); ++i){
			try{
			tmp.add(stationInfo.get(i*5).get(CSVReader.CELL_NAME));
			}catch(Exception e){
				System.out.println("overload");
			}
		}
		return tmp;
	}
	
}
