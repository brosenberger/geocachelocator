package test.Test;

public class Distance {
	private float distance;
	
	public Distance(float distance) {
		this.distance = distance;
	}
	
	public float getDistance() {
		return distance;
	}
	
	public String toString() {
		float d=distance;
		if (d/1000>0.8) {
			d /= 1000;
			return String.format("%.2f km", d);
		} else {
			return String.format("%.1f m", d);
		}
		
	}
}
