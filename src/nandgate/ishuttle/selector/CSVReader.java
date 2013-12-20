package nandgate.ishuttle.selector;

import android.annotation.SuppressLint;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CSVReader {  

	public static final int CELL_LINE = 0;
	public static final int CELL_INDEX = 1;
	public static final int CELL_SEQUENCE = 2;
	public static final int CELL_TIME = 3;
	public static final int CELL_NAME = 4;
	public static final int CELL_DESC = 5;
	public static final int CELL_LNG = 6;
	public static final int CELL_LAT = 7;
	public static final int CELL_VIEW = 8;
	public static final int CELL_VISIBLE = 9;
	
    private InputStreamReader fr = null;  
    private BufferedReader br = null;  
  
    public CSVReader(InputStream inputStream) throws IOException {
        fr = new InputStreamReader(inputStream);  
    }
  
    /** 
     * 解析csv文件 到一个list中 每个单元个为一个String类型记录，每一行为一个list。 再将所有的行放到一个总list中 
     */  
    @SuppressLint("UseSparseArrays")
	public Map<String, Map<Integer, List<String>>> readCSVFile() throws IOException {  
        br = new BufferedReader(fr);  
        String rec = null;// 一行  
        String str;// 一个单元格  
        
        String line="0";
        Map<Integer, List<String>> stationInfo=new HashMap<Integer, List<String>>();
        Map<String, Map<Integer, List<String>>> lineInfo = new HashMap<String, Map<Integer, List<String>>>();  
        
        try {
            while ((rec = br.readLine()) != null) {
                Pattern pCells = Pattern.compile("(\"[^\"]*(\"{2})*[^\"]*\")*[^,]*,");
                Matcher mCells = pCells.matcher(rec);
                List<String> cells = new ArrayList<String>();
                
                while (mCells.find()) {  
                    str = mCells.group();
                    str = str.replaceAll("(?sm)\"?([^\"]*(\"{2})*[^\"]*)\"?.*,", "$1");
                    str = str.replaceAll("(?sm)(\"(\"))", "$2");
                    cells.add(str);
                }

                if(!cells.get(0).contains(line)){
                	lineInfo.put(line, stationInfo);
            		stationInfo=new HashMap<Integer, List<String>>();
            		line=cells.get(0);
                }
                stationInfo.put(Integer.decode(cells.get(1)), cells);
            }
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            if (fr != null) {  
                fr.close();  
            }  
            if (br != null) {  
                br.close();  
            }  
        }  
        return lineInfo;  
    } 
}