package nandgate.ishuttle.tracker;

import nandgate.ishuttle.R;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class AlarmSurface extends Activity{
	static MediaPlayer mMediaPlayer = new MediaPlayer();    
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.alert_view);
       
        mMediaPlayer.reset();
        mMediaPlayer.setLooping(true);
		try{
			 mMediaPlayer.setDataSource(this, 
		     RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
		     mMediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
		     mMediaPlayer.prepare();
		     mMediaPlayer.start();
		}catch(Exception e){
		     Log.e("Timedown", e.toString());
		}
		
		ImageView clock=(ImageView)findViewById(R.id.alarmImage);
		clock.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				mMediaPlayer.stop();
				AlarmSurface.this.finish();
			}
			 
		 });
    }

}
