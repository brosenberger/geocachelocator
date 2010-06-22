package test.Test.util;

import java.util.LinkedList;
import java.util.NoSuchElementException;

import android.location.Location;

public class Locations {
	private Location destination=null;
	private LinkedList<Location> oldLocations = new LinkedList<Location>();

    //*****************************************************************************************
    //***  Getters & Setters                                                                ***
    //*****************************************************************************************    
	public void setMyLocation(Location myLocation) {
		if (myLocation!=null) {
			oldLocations.addFirst(myLocation);
			if (destination==null) destination=myLocation;
		}
	}
	public void setDestination(Location destination) {
		if (destination!=null) this.destination = destination;
	}
	public Location getMyLocation() {
		try {
			return oldLocations.getFirst();
		} catch(NoSuchElementException e) {
			return new Location("gps");
		}
	}
	private Location getWalkingFrom() {
		try {
			return oldLocations.get(1);
		} catch (NoSuchElementException e) {
			return new Location("gps");
		}
	}	

	//*****************************************************************************************
    //***  Distance functionality                                                           ***
    //*****************************************************************************************    
	public Distance getDistanceToDestination() {
		return getDistanceBetween(getMyLocation(), destination);
	}
	public Distance getLastWalkingDistance() {
		return getDistanceBetween(getWalkingFrom(), getMyLocation());
	}
	public static Distance getDistanceBetween(Location from, Location to) {
		return new Distance(from.distanceTo(to));
	}
	
    //*****************************************************************************************
    //***  Direction functionality                                                          ***
    //*****************************************************************************************    	
	public Direction getDirectionToDestination() {
		return getDirectionBetween(getMyLocation(), destination);
	}
	public Direction getWalkingDirection() {
		return getDirectionBetween(getMyLocation(), getWalkingFrom());
	}
	public static Direction getDirectionBetween(Location from, Location to) {
		return new Direction(from.bearingTo(to));
	}
}
