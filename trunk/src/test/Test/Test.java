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
import android.view.*;
import android.view.animation.Animation;
import android.widget.*;

//distances: http://www.anddev.org/the_friend_finder_-_mapactivity_using_gps_-_part_i_-_ii-t93.html
//compass and so on: http://developer.android.com/resources/samples/ApiDemos/src/com/example/android/apis/graphics/Compass.html
@SuppressWarnings("deprecation")
public class Test extends Activity {
	private static final String TAG = "GeoCacheLocator";
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
    
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inf = getMenuInflater();
    	inf.inflate(R.menu.options_menu, menu);
    	return true;
    }
    
    public boolean onContextItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    		case R.id.close:
    			closeProgram();
    			return true;
    		default: return super.onContextItemSelected(item);
    	}
    }
    
    private void closeProgram() {
    	
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
    private float[] getRotationTo(int actDegree, Location destination) {
    	float to = myLocation.bearingTo(destination);
    	float toRotate;
    	if (to>0) to -= 360;
    	toRotate = to *= -1;
    	if (Math.abs(actDegree-to)>180) {
    		toRotate-=360;
    	}
    	float[] ret = {to,toRotate};
    	return ret;
    }
    private Animation generateRotationAnimation(int from, int to, float xOff, float yOff) {
    	Rotate3dAnimation r = new Rotate3dAnimation(from, to, xOff, yOff, 0, false);
    	r.setFillAfter(true);
    	r.setDuration(1000);
    	return r;
    }
    private void rotateImage() {
		ImageView img = (ImageView) findViewById(R.id.ImageArrow);
		float to[] = getRotationTo(actDegree,destination);
	//	Log.e(TAG, "rotating from: "+actDegree+"\tto:"+to[0]);
		img.startAnimation(generateRotationAnimation(actDegree, (int) to[1], img.getWidth()/2.0f, img.getHeight()/2.0f));
		actDegree=(int) to[0];
    }
    private void setDestination(Location destination) {
    	this.destination = new Location(destination);
   	//	Log.i(TAG, "destination changed to: "+destination.toString());
    }
    private void setMyLocation(Location myLocation) {
    	this.myLocation = myLocation;
    	if (myLocation!=null){ 
    //		Log.i(TAG, "my location changed to: "+myLocation.toString());
    	}
    }
    private void getNewDestination() {
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
    }
    
    private void updateAll() {
    	updateDistance();
    	updateActPos();
    	rotateImage();
    }
        
    private class LocationUpdateHandler implements LocationListener {
		public void onLocationChanged(Location location) {
			if (destination==null) setDestination(location);
			setMyLocation(location);
			updateAll();
		}

		public void onProviderDisabled(String provider) {}
		public void onProviderEnabled(String provider) {}
		public void onStatusChanged(String provider, int status, Bundle extras) {}
    } 
    private class SensorChangedListener implements SensorListener {
		public void onAccuracyChanged(int sensor, int accuracy) {}

		public void onSensorChanged(int sensor, float[] values) {
			// TODO Auto-generated method stub
			//										yaw					pitch				roll
			Log.d(TAG, "sensorChanged (" + values[0] + ", " + values[1] + ", " + values[2] + ")");
		}
    	
    }
    private class ButtonListener implements View.OnClickListener {
		public void onClick(View v) {
			getNewDestination();
		}    
	}
}