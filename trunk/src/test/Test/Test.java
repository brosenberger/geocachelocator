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
import android.view.animation.*;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

//distances: http://www.anddev.org/the_friend_finder_-_mapactivity_using_gps_-_part_i_-_ii-t93.html
//compass and so on: http://developer.android.com/resources/samples/ApiDemos/src/com/example/android/apis/graphics/Compass.html
public class Test extends Activity {
	private static final String TAG = "GeoCacheLocator";
	private static final int GREEN_ARROW = 0;
	private static final int YELLOW_ARROW = 100;
	private static final int RED_ARROW = 200;
	private static final int FLAG = 300;
	private boolean[] visibleArrows={false,false,false};
	private boolean[] lastVisibleArrows={false,false,false};
	private LocationManager lm;
	private static float RED_BORDER=1000;
	private static float YELLOW_BORDER=300;
	private Locations locationService = Locations.getInstance();
	
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
        
        /*updateAnimations();
        updateForms();
        */
        
        //testDirections();
        
        updateAll();
        updateForms();
        
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
    	TextView tv = (TextView) findViewById(R.id.distance);
    	tv.setText(distance+" ("+(char)177+locationService.getAccuracy()+")");
    }
    private void updateArrowVisibility() {
    	float newD = locationService.getDistanceToDestination().getDistance();
    	boolean first=false, sec=false, third=false;

    	lastVisibleArrows = visibleArrows;
    	if (newD<=YELLOW_BORDER && (newD>locationService.getAccuracy().getDistance()||newD>1)) { //--> new is green
    		first=true;
    	} else if (newD>YELLOW_BORDER && newD<=RED_BORDER) { //-->new is yellow
    		sec=true;
    	} else if (newD>RED_BORDER) {
    		third=true;
    	}
    	//else not needed due to standard settings --> new is flag
    	visibleArrows = new boolean[]{first,sec,third};
    }
    private void updateActPos() {
    	Location myLocation = locationService.getMyLocation();
		TextView tv = (TextView) findViewById(R.id.latitude);
		tv.setText((myLocation.getLatitude()>0?"N":"S")+" "+myLocation.getLatitude()+"");
		tv = (TextView) findViewById(R.id.longitude);
		tv.setText((myLocation.getLongitude()>0?"E":"W")+" "+myLocation.getLongitude()+"");
    }
    private void updateDirection() {
    	TextView tv = (TextView) findViewById(R.id.orientation);
    	Direction compass = locationService.getWalkingDirection();
    	tv.setText("Direction: " +compass+" ("+Direction.getCompassDirection(compass)+")");
    }
    private void updateAnimations() {
    	((ImageView)findViewById(R.id.image_arrow_green)).startAnimation(generateAnimationSet(Test.GREEN_ARROW));
    	((ImageView)findViewById(R.id.image_arrow_yellow)).startAnimation(generateAnimationSet(Test.YELLOW_ARROW));
    	((ImageView)findViewById(R.id.image_arrow_red)).startAnimation(generateAnimationSet(Test.RED_ARROW));   
    	((ImageView)findViewById(R.id.image_flag)).startAnimation(generateAnimationSet(Test.FLAG));
    }
    private void updateAll() {
    	updateDistance();
    	updateDirection();
    	updateActPos();
    	updateArrowVisibility();
    	updateAnimations();
    }
    private void updateForms() {
    	Location destination = locationService.getDestination();
    	((EditText)findViewById(R.id.inLatitude)).setText(Math.abs(destination.getLatitude())+"");
    	((EditText)findViewById(R.id.inLongitude)).setText(Math.abs(destination.getLongitude())+"");
    	((Spinner)findViewById(R.id.SpinnerSN)).setSelection(destination.getLatitude()<0?1:0);
    	((Spinner)findViewById(R.id.SpinnerEW)).setSelection(destination.getLongitude()<0?1:0);
    }
    //*****************************************************************************************
    //***  Image Animations                                                                 ***
    //*****************************************************************************************        

    private AnimationSet generateAnimationSet(int img) {
    	AnimationSet a = new AnimationSet(false);
    	a.addAnimation(generateRotationAnimation(img));
    	a.addAnimation(generateTransparencyAnimation(img));
    	a.setFillAfter(true);
    	return a;
    }
    private Animation generateRotationAnimation(int img) {
    	Animation rA=null;
    	if (img == Test.GREEN_ARROW) {
    		rA = generateRotationAnimationForImage((ImageView)findViewById(R.id.image_arrow_green));  		
    	} else if (img == Test.YELLOW_ARROW) {
    		rA = generateRotationAnimationForImage((ImageView)findViewById(R.id.image_arrow_yellow));  		
    	} else if (img == Test.RED_ARROW){
    		rA = generateRotationAnimationForImage((ImageView)findViewById(R.id.image_arrow_red));  		    		
    	} else if (img == Test.FLAG) {
    		rA = generateScalationAnimation((ImageView)findViewById(R.id.image_flag));
    	}
    	return rA;
    }
    private Animation generateRotationAnimationForImage(ImageView img) {
    	long duration=1000;
    	int from = (int) locationService.getLastDirectionToDestination().getDegree();
    	int to = (int) locationService.getDirectionToDestination().getShortestRotationTo(locationService.getDirectionToDestination());
    	float xOff = img.getWidth()/2.0f;
    	float yOff = img.getHeight()/2.0f;
    	Rotate3dAnimation a = new Rotate3dAnimation(from, to, xOff, yOff, 0, false);
    	a.setFillAfter(true);
    	a.setDuration(duration);
		return a;
    }
    private Animation generateScalationAnimation(ImageView img) {
    	ScaleAnimation sA = new ScaleAnimation(1,0.75f,1,0.75f,Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF,0.5f);
    	sA.setRepeatMode(Animation.REVERSE);
    	sA.setRepeatCount(Animation.INFINITE);
    	sA.setDuration(3000);
    	sA.setFillAfter(true);
    	return sA;
    }
    private Animation generateTransparencyAnimation(int img) {
    	float from=0, to=0;
    	if (img == Test.GREEN_ARROW) {
    	  	if (visibleArrows[0]) to=1;
    	  	if (lastVisibleArrows[0]) from=1;
    	} else if (img == Test.YELLOW_ARROW) {
    		if (visibleArrows[1]) to=1;
    		if (lastVisibleArrows[1]) from=1;
    	} else if (img == Test.RED_ARROW){
    		if (visibleArrows[2]) to=1;
    		if (lastVisibleArrows[2]) from=1;
    	} else if (img == Test.FLAG) {
    		if (!visibleArrows[0] && !visibleArrows[1] && !visibleArrows[2]) to=1;
    		if (!lastVisibleArrows[0] && !lastVisibleArrows[1] && !lastVisibleArrows[2]) from=1;
    	}
    	return generateTransparencyAnimation(1000, from, to);    	
    }
    private Animation generateTransparencyAnimation(long duration, float from, float to) {
    	AlphaAnimation a = new AlphaAnimation(from, to);
    	a.setFillAfter(true);
    	a.setInterpolator(new LinearInterpolator());
    	a.setDuration(duration);
    	return a;
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
			locationService.setMyLocation(roundToComma(location,6));
			updateAll();
		}
		
		private Location roundToComma(Location l, int comma) {
			l.setLatitude(roundDouble(l.getLatitude(),comma));
			l.setLongitude(roundDouble(l.getLongitude(),comma));
			return l;
		}
		
		private double roundDouble(double d, int c) {
			double f = Math.pow(10, c);
			d*=f;
			d=Math.round(d)/f;
			return d;
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
    
    private Location generateLocation(double lat, double lon) {
    	Location l = new Location("gps");
    	l.setLatitude(lat);
    	l.setLongitude(lon);
    	return l;
    }
    private void testDirections() {
    	testNorth();
    	testSouth();
    	testWest();
    	testEast();
    }
    private void testNorth() {
    	Locations l = new Locations();
    	l.setMyLocation(generateLocation(48, 14));
    	l.setMyLocation(generateLocation(49, 14));
    	Direction d = l.getWalkingDirection();
    	Log.d(TAG, "Direction north: "+d.getOriginalDegree());    	
    }
    private void testSouth() {
    	Locations l = new Locations();
    	l.setMyLocation(generateLocation(49, 14));
    	l.setMyLocation(generateLocation(48, 14));
    	Direction d = l.getWalkingDirection();
    	Log.d(TAG, "Direction south: "+d.getOriginalDegree());
    }
    private void testWest() {
    	Locations l = new Locations();
    	l.setMyLocation(generateLocation(48, 14));
    	l.setMyLocation(generateLocation(48, 13));
    	Direction d = l.getWalkingDirection();
    	Log.d(TAG, "Direction west: "+d.getOriginalDegree());
    }
    private void testEast() {
    	Locations l = new Locations();
    	l.setMyLocation(generateLocation(48, 13));
    	l.setMyLocation(generateLocation(48, 14));
    	Direction d = l.getWalkingDirection();
    	Log.d(TAG, "Direction east: "+d.getOriginalDegree());
    }
}