package test.Test.util;

import java.util.Iterator;
import java.util.LinkedList;

import android.location.Location;

/**
 * The Locationsclass stores last locations to determine directions, distances
 * @author Benjamin Rosenberger
 *
 */
public class Locations {
	private static double MIN_DISTANCE=2;
	private static float FACTOR=0.0f;
	private static Locations instance = new Locations();
	private Location destination=null;
	private LinkedList<Location> lastLocations= new LinkedList<Location>();
	private Location myLocation=null;

	public static Locations getInstance() {
		return instance;
	}
	
    //*****************************************************************************************
    //***  Getters & Setters                                                                ***
    //*****************************************************************************************    
	public void setMyLocation(Location myLocation) {
		if (myLocation!=null) {
			setLastLocation(this.myLocation);
			this.myLocation=myLocation;
		}
	}
	public void setDestination(Location destination) {
		this.destination = destination;
	}
	/**
	 * setting the last position, flattened with x% of old value
	 * @param newLocation
	 */
	public void setLastLocation(Location newLocation) {
		if (newLocation==null) return;	
		if (lastLocations.size()==0) lastLocations.addFirst(newLocation);
		if (getDistanceBetween(lastLocations.getFirst(),newLocation).getDistance()>MIN_DISTANCE) 
			lastLocations.addFirst(newLocation); 
/*		else {
			if (getLastWalkingDistance().getDistance()>newLocation.getAccuracy()) lastLocations.addFirst(newLocation);
			else {
				Location last= lastLocations.getFirst();
				Location newLast = new Location("gps");
				newLast.setLatitude(last.getLatitude()*FACTOR+newLocation.getLatitude()*(1-FACTOR));
				newLast.setLongitude(last.getLongitude()*FACTOR+newLocation.getLongitude()*(1-FACTOR));
				lastLocations.addFirst(newLast);				
			}
		}
*/	
	}
	/**
	 * getMyLocation returns the actual set location, if no location is stored an empty one is returned
	 * @return actual position or empty
	 */
	public Location getMyLocation() {
		if (myLocation!=null) return myLocation;
		else return new Location("gps");
	}
	/**
	 * returns the destination location
	 * @return destination or current location if none set
	 */
	public Location getDestination() {
		return this.destination==null?getMyLocation():this.destination;
	}
	/**
	 * returns the accuracy of the last location
	 * @return accuracy as distance
	 */
	public Distance getAccuracy() {
		return new Distance(getMyLocation().getAccuracy());
	}
	private Location getWalkingFrom() {
		if (lastLocations.size()==0) return getMyLocation();
		Location last = lastLocations.getFirst();
		if (getDistanceBetween(last, getMyLocation()).getDistance()>MIN_DISTANCE)
			return lastLocations.getFirst();
		return lastLocations.size()>1?lastLocations.get(2):last;
	}
	private Location getWalkingFrom(float minDistance) {
		Iterator<Location> it = lastLocations.iterator(); 
		Location l=new Location("gps");
		Location actPos = getMyLocation();
		while (it.hasNext()){
			l=it.next();
			if (getDistanceBetween(l, actPos).getDistance()>minDistance) return l;
		}
		return l;
	}
	//*****************************************************************************************
    //***  Distance functionality                                                           ***
    //*****************************************************************************************    
	/**
	 * @return distance between current location and destination
	 */
	public Distance getDistanceToDestination() {
		return getDistanceBetween(getMyLocation(), getDestination());
	}
	/**
	 * 
	 * @return returns distance between current location and the location before
	 */
	public Distance getLastWalkingDistance() {
		return getDistanceBetween(getWalkingFrom(), getMyLocation());
	}
	/**
	 * returns the distance between two given locations
	 * @param from	location
	 * @param to	location
	 * @return	distance
	 */
	public static Distance getDistanceBetween(Location from, Location to) {
		return new Distance(from.distanceTo(to));
	}
	
    //*****************************************************************************************
    //***  Direction functionality                                                          ***
    //*****************************************************************************************    	
	private Direction correctWithCompass(Direction d) {
		return new Direction(d.getDegree()+getWalkingDirection().getOriginalDegree());
	}
	/**
	 * @return Direction between actual location and destination
	 */
	public Direction getDirectionToDestination() {
		return getDirectionBetween(getMyLocation(), getDestination());
	}
	/**
	 * 
	 * @return direction from the last position to the destination
	 */
	public Direction getLastDirectionToDestination() {
		return getDirectionBetween(getWalkingFrom(), getDestination());
	}
	/**
	 * 
	 * @return direction between last two locations for electronic compass
	 */
	public Direction getWalkingDirection() {
/*		float d=0;
		int i;
		if (lastLocations.size()==0) return new Direction(d);
		Object[] lArr= lastLocations.toArray();
		Location to = getMyLocation();
		for (i=0;i<5 && i<lArr.length;i++) {
			d+=getDirectionBetween((Location) lArr[i],to).getOriginalDegree();
			to = (Location)lArr[i];
		}
		
		return new Direction(d/i);*/
		return getDirectionBetween(getWalkingFrom(), getMyLocation());
	}
	/**
	 * 
	 * @param from	location
	 * @param to	location
	 * @return	direction between from and to
	 */
	public static Direction getDirectionBetween(Location from, Location to) {
		return new Direction(from.bearingTo(to));
	}
}
