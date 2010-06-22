package test.Test.util;

/**
 * Distance saves a float distance and formated printing
 * @author Benjamin Rosenberger
 *
 */
public class Distance {
	private float distance;
	
	public Distance(float distance) {
		this.distance = distance;
	}
	
	public float getDistance() {
		return distance;
	}
	/**
	 * differing between m and km
	 */
	public String toString() {
		float d=distance;
		if (d/1000>0.8) {
			d /= 1000;
			return String.format("%.2fkm", d);
		} else {
			return String.format("%.1fm", d);
		}
		
	}
}
