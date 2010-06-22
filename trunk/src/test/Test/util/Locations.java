package test.Test.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import android.location.Location;

/**
 * The Locationsclass stores last locations to determine directions, distances
 * @author Benjamin Rosenberger
 *
 */
public class Locations {
	private static float minDistance=1;
	private Location destination=null;
	private LinkedList<Location> locations = new LinkedList<Location>();

    //*****************************************************************************************
    //***  Getters & Setters                                                                ***
    //*****************************************************************************************    
	public void setMyLocation(Location myLocation) {
		if (myLocation!=null) {
			locations.addFirst(myLocation);
		}
	}
	public void setDestination(Location destination) {
		this.destination = destination;
	}
	/**
	 * getMyLocation returns the actual set location, if no location is stored an empty one is returned
	 * @return actual position or empty
	 */
	public Location getMyLocation() {
		try {
			return locations.getFirst();
		} catch(NoSuchElementException e) {
			return new Location("gps");
		}
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
		try {
			return locations.get(1);
		} catch (NoSuchElementException e) {
			return new Location("gps");
		}
	}
	private Location getWalkingFrom(float minDistance) {
		Iterator<Location> it = locations.iterator(); 
		Location l=new Location("gps");
		Location actPos = getMyLocation();
		while (it.hasNext()){
			l=it.next();
			if (getDistanceBetween(l, actPos).getDistance()>minDistance) return l;
		}
		return new Location("gps");
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
		return getDirectionBetween(getMyLocation(), getWalkingFrom(Locations.minDistance));
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
