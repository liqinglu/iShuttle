package nandgate.ishuttle.listener;

import com.baidu.mapapi.search.MKSearch;

import android.text.Editable;
import android.text.TextWatcher;

public class TextListener implements TextWatcher{
	MKSearch mkSearch;
	
		public TextListener(MKSearch mkSearch) {
		this.mkSearch=mkSearch;
	}

		@Override
		public void afterTextChanged(Editable arg0) {
			
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1,
				int arg2, int arg3) {
			
		}
		@Override
		public void onTextChanged(CharSequence cs, int arg1, int arg2,
				int arg3) {
			 if ( cs.length() <=0 ){
				 return ;
			 }
			 System.out.println("textchange");
			 mkSearch.suggestionSearch(cs.toString(), "北京");		
			 //mkSearch.poiSearchInCity("北京", "北京");
		}
 
}
