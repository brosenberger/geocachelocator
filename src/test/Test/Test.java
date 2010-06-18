package test.Test;

import android.app.Activity;
import android.content.Context;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

//distances: http://www.anddev.org/the_friend_finder_-_mapactivity_using_gps_-_part_i_-_ii-t93.html
//compass and so on: http://developer.android.com/resources/samples/ApiDemos/src/com/example/android/apis/graphics/Compass.html
@SuppressWarnings("deprecation")
public class Test extends Activity {
	private static final String TAG = "TEST";
	private LocationManager lm;
	private SensorManager sm;
	private Location destination,myLocation;
	private int actDegree=0;
	protected ArrayAdapter<CharSequence> mAdapter;

    /** Called when the activity is first created. */
    @SuppressWarnings("unchecked")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationUpdateHandler());
        setMyLocation(lm.getLastKnownLocation("gps"));
        
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sm.registerListener(new SensorChangedListener(), SensorManager.SENSOR_ORIENTATION);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        final TextView dist = (TextView) findViewById(R.id.distance);
        dist.setText("-.- m");
        
        final Spinner spinnerSN = (Spinner) findViewById(R.id.SpinnerSN);
        ArrayAdapter ad = ArrayAdapter.createFromResource(this, R.array.SN, android.R.layout.simple_spinner_item);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSN.setAdapter(ad);
        
        final Spinner spinnerEW = (Spinner) findViewById(R.id.SpinnerEW);
        ArrayAdapter ad2 = ArrayAdapter.createFromResource(this, R.array.EW, android.R.layout.simple_spinner_item);
        ad2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEW.setAdapter(ad2);

        final Button button = (Button) findViewById(R.id.Button01);
        button.setOnClickListener(new ButtonListener());
    }
    
    private void updateDistance() {
    	Location actPos = myLocation==null?destination:myLocation;
    	TextView tv = (TextView) findViewById(R.id.distance);
    	tv.setText(getDistance(actPos)+" ("+(char)177+actPos.getAccuracy()+")");
    }
    private void updateActPos() {
		TextView tv = (TextView) findViewById(R.id.latitude);
		tv.setText((myLocation.getLatitude()>0?"N":"S")+" "+myLocation.getLatitude()+"");
		tv = (TextView) findViewById(R.id.longitude);
		tv.setText((myLocation.getLongitude()>0?"E":"W")+" "+myLocation.getLongitude()+"");
		TableLayout tl = (TableLayout) findViewById(R.id.table_actposition);
		tl.requestLayout();
    }
    private String getDistance(Location actPos) {
    	return new Distance(actPos.distanceTo(destination)).toString();
    }
    
    private void rotateImage() {
		ImageView img = (ImageView) findViewById(R.id.ImageArrow);
		float to = myLocation.bearingTo(destination);
		Log.e(TAG, "direction to="+to);
		Rotate3dAnimation r = new Rotate3dAnimation(actDegree,(int) to,img.getWidth()/2.0f,img.getHeight()/2.0f,0,false);
		r.setDuration(1000);
		r.setFillAfter(true);
		img.startAnimation(r);
		
		actDegree=(int) to;
    }
    
    private void setDestination(Location destination) {
    	this.destination = new Location(destination);
   		Log.i(TAG, "destination changed to: "+destination.toString());
    }
    private void setMyLocation(Location myLocation) {
    	this.myLocation = myLocation;
    	if (myLocation!=null){ 
    		Log.i(TAG, "my location changed to: "+myLocation.toString());
    	}
    }
    
    private void updateAll() {
    	updateDistance();
    	updateActPos();
    	rotateImage();
    }
        
    private class LocationUpdateHandler implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			if (destination==null) setDestination(location);
			setMyLocation(location);
			updateAll();
		}

		@Override public void onProviderDisabled(String provider) {}
		@Override public void onProviderEnabled(String provider) {}
		@Override public void onStatusChanged(String provider, int status, Bundle extras) {}
    }
    
    @SuppressWarnings("deprecation")
	private class SensorChangedListener implements SensorListener {
		@Override public void onAccuracyChanged(int sensor, int accuracy) {}

		@Override
		public void onSensorChanged(int sensor, float[] values) {
			// TODO Auto-generated method stub
			//										yaw					pitch				roll
			Log.d("ORIENTATIOn", "sensorChanged (" + values[0] + ", " + values[1] + ", " + values[2] + ")");
		}
    	
    }
    
    private class ButtonListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			Location dest = new Location("gps");
			float longitude, latitude;
			try {
				longitude = Float.parseFloat(((EditText) findViewById(R.id.inLongitude)).getText().toString());
				latitude = Float.parseFloat(((EditText) findViewById(R.id.inLatitude)).getText().toString());
				Spinner spinnerSN = (Spinner) findViewById(R.id.SpinnerSN);
				Spinner spinnerEW = (Spinner) findViewById(R.id.SpinnerEW);
				if (spinnerSN.getSelectedItem().toString().equalsIgnoreCase("S")) latitude *= -1;
				if (spinnerEW.getSelectedItem().toString().equalsIgnoreCase("W")) longitude *= -1;
				dest.setLatitude(latitude);
				dest.setLongitude(longitude);
				setDestination(dest);
				updateAll();
			} catch (Exception e) {}
		}    }
}