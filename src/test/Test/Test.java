package test.Test;

import test.Test.util.Direction;
import test.Test.util.Distance;
import test.Test.util.Locations;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

//distances: http://www.anddev.org/the_friend_finder_-_mapactivity_using_gps_-_part_i_-_ii-t93.html
//compass and so on: http://developer.android.com/resources/samples/ApiDemos/src/com/example/android/apis/graphics/Compass.html
public class Test extends Activity {
	private static final String TAG = "GeoCacheLocator";
	private LocationManager lm;
	private Direction actDegree=new Direction(0);
	private static float RED_BORDER=1000;
	private static float YELLOW_BORDER=300;
	private Locations locationService = new Locations();
	
    /** Called when the activity is first created. */
    @SuppressWarnings("unchecked")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationUpdateHandler());

        locationService.setMyLocation(lm.getLastKnownLocation("gps"));
                
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
        
        Animation a = generateTransparencyAnimation(0, 100, 0);
        ((ImageView) findViewById(R.id.ImageArrow)).startAnimation(a);
        ((ImageView) findViewById(R.id.image_arrow_yellow)).startAnimation(a);
        ((ImageView) findViewById(R.id.image_arrow_red)).startAnimation(a);
        
        Log.i(TAG, "GPS Locator loaded");
    }
    
    //*****************************************************************************************
    //***  Menu                                                                             ***
    //*****************************************************************************************    
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

    //*****************************************************************************************
    //***  Updates                                                                          ***
    //*****************************************************************************************    
    private void updateDistance() {
    	Distance distance = locationService.getDistanceToDestination();
    	Distance oldDistance = locationService.getLastWalkingDistance();
    	TextView tv = (TextView) findViewById(R.id.distance);
    	tv.setText(distance+" ("+(char)177+locationService.getMyLocation().getAccuracy()+"m)");
    	//tv.setText(getDistance(actPos)+" ("+(char)177+actPos.getAccuracy()+"m)");
    	updateArrowColor(oldDistance.getDistance(), distance.getDistance());
    }
    private void updateArrowColor(float oldD, float newD) {
    	Animation fadeOut = generateTransparencyAnimation(100, 100, 0);
    	Animation fadeIn = generateTransparencyAnimation(100, 0, 100);
    	ImageView out, in;
    	if (oldD>RED_BORDER && newD>YELLOW_BORDER) { //-->red to yellow
    		in = (ImageView) findViewById(R.id.image_arrow_yellow);
    		out = (ImageView) findViewById(R.id.image_arrow_red);
    	} else if (oldD>YELLOW_BORDER && oldD<=RED_BORDER && newD<=YELLOW_BORDER) { //-->yellow to green
    		in = (ImageView) findViewById(R.id.ImageArrow);
    		out = (ImageView) findViewById(R.id.image_arrow_yellow);
    	} else if (oldD<=YELLOW_BORDER && newD>YELLOW_BORDER && newD<=RED_BORDER) { //-->green to yellow
    		in = (ImageView) findViewById(R.id.image_arrow_yellow);
    		out = (ImageView) findViewById(R.id.ImageArrow);
    	} else if (oldD>RED_BORDER && newD<=YELLOW_BORDER) { //-->red to green
    		in = (ImageView) findViewById(R.id.ImageArrow);
    		out = (ImageView) findViewById(R.id.image_arrow_red);
    	} else if (oldD<=YELLOW_BORDER && newD>RED_BORDER) { //-->green to red
    		in = (ImageView) findViewById(R.id.image_arrow_red);
    		out = (ImageView) findViewById(R.id.ImageArrow);
    	} else { //-->yellow to red
    		in = (ImageView) findViewById(R.id.image_arrow_red);
    		out = (ImageView) findViewById(R.id.image_arrow_yellow);
    	}
    	in.startAnimation(fadeIn);
    	out.startAnimation(fadeOut);
    }
    private void updateActPos() {
    	Location myLocation = locationService.getMyLocation();
		TextView tv = (TextView) findViewById(R.id.latitude);
		tv.setText((myLocation.getLatitude()>0?"N":"S")+" "+myLocation.getLatitude()+"");
		tv = (TextView) findViewById(R.id.longitude);
		tv.setText((myLocation.getLongitude()>0?"E":"W")+" "+myLocation.getLongitude()+"");
		TableLayout tl = (TableLayout) findViewById(R.id.table_actposition);
		tl.requestLayout();
    }
    private void updateDirection() {
    	TextView tv = (TextView) findViewById(R.id.orientation);
    	Direction compass = locationService.getWalkingDirection();
    	tv.setText("Direction:" +compass+" ("+Direction.getCompassDirection(compass)+")");
    }
    private void updateAll() {
    	updateDistance();
    	updateDirection();
    	updateActPos();
    	rotateImage();
    }

    //*****************************************************************************************
    //***  Image Animations                                                                 ***
    //*****************************************************************************************        
    private Animation generateRotationAnimation(long duration, int from, int to, float xOff, float yOff) {
    	Rotate3dAnimation r = new Rotate3dAnimation(from, to, xOff, yOff, 0, false);
    	r.setFillAfter(true);
    	r.setDuration(duration);
    	return r;
    }
    private Animation generateTransparencyAnimation(long duration, float from, float to) {
    	AlphaAnimation a = new AlphaAnimation(from, to);
    	a.setFillAfter(true);
    	a.setDuration(duration);
    	return a;
    }
    private void rotateImage() {
		float to = locationService.getDirectionToDestination().getShortestRotationTo(actDegree);
		startRotationAnimationForImage((ImageView) findViewById(R.id.ImageArrow), to);
		startRotationAnimationForImage((ImageView) findViewById(R.id.image_arrow_yellow), to);
		startRotationAnimationForImage((ImageView) findViewById(R.id.image_arrow_red), to);
		actDegree= locationService.getDirectionToDestination();
    }
    private void startRotationAnimationForImage(ImageView img,float to) {
		Animation a = generateRotationAnimation(1000,(int) actDegree.getDegree(), (int) to, img.getWidth()/2.0f, img.getHeight()/2.0f);
		img.startAnimation(a); 
    }

    //*****************************************************************************************
    //***  Setters & Getters                                                                ***
    //*****************************************************************************************    
    private void getNewDestinationFromForm() {
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
			locationService.setDestination(dest);
			updateAll();
		} catch (Exception e) {}
    }

    //*****************************************************************************************
    //***  Listener                                                                         ***
    //*****************************************************************************************    
    private class LocationUpdateHandler implements LocationListener {
		public void onLocationChanged(Location location) {
			locationService.setMyLocation(location);
			updateAll();
		}

		public void onProviderDisabled(String provider) {}
		public void onProviderEnabled(String provider) {}
		public void onStatusChanged(String provider, int status, Bundle extras) {}
    } 
    private class ButtonListener implements View.OnClickListener {
		public void onClick(View v) {
			getNewDestinationFromForm();
		}    
	}
}