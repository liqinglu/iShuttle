package nandgate.ishuttle.settings;

import nandgate.ishuttle.R;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingView extends PreferenceActivity implements OnSharedPreferenceChangeListener{
	
	private static SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragement()).commit(); 
		sp= PreferenceManager.getDefaultSharedPreferences(this); 
		sp.registerOnSharedPreferenceChangeListener(this);
	}
	
	public static class PrefsFragement extends PreferenceFragment{  
        @Override  
        public void onCreate(Bundle savedInstanceState) {  
            super.onCreate(savedInstanceState);  
            addPreferencesFromResource(R.xml.setting);  
        }
    }
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sp, String set) {
		SystemConfig.getInstance().setUserPref(""+sp.getBoolean("set_01", false), 
				sp.getString("set_02", "0"), 
				""+sp.getBoolean("set_03", false),
				sp.getString("set_04", "0"));
		
		SystemConfig.getInstance().setGenPref(""+sp.getBoolean("set_11", false), sp.getString("set_12", "null"));
		SystemConfig.getInstance().initSysPref();
	}  
}
