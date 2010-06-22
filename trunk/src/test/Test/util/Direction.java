package test.Test.util;

public class Direction {
	private float degree;
	
	public Direction(float degree) {
		this.degree = changeToPositivRotation(degree);
	}
	
	private float changeToPositivRotation(float degree) {
	  	if (degree>0) degree -= 360;
    	return (degree*-1);	
	}
	
	public float getShortestRotationTo(Direction actDegree) {
		return getShortestRotationTo(actDegree.getDegree());
	}
	public float getShortestRotationTo(float actDegree) {
    	float toRotate = this.degree;
    	if (Math.abs(actDegree-toRotate)>180) {
    		toRotate-=360;
    	}
    	return toRotate;
	}
	
	public float getDegree() {
		return this.degree;
	}
	
	public static String getCompassDirection(Direction degree) {
		return getCompassDirection(degree.getDegree());
	}
	public static String getCompassDirection(float degree) {
    	if (degree>=22.5 && degree<67.5) return "NO";
    	if (degree>=67.5 && degree<112.5) return "O";
    	if (degree>=112.5 && degree<157.5) return "SO";
    	if (degree>=157.5 && degree<202.5) return "S";
    	if (degree>=202.5 && degree<247.5) return "SW";
    	if (degree>=247.5 && degree<292.5) return "W";
    	if (degree>=292.5 && degree<337.5) return "NW";
    	return "N";
    }
	
	public String toString() {
		return this.degree+"°";
	}
}
