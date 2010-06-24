package test.Test.util;

/**
 * Direction stores an float angle between points
 * @author Benjamin Rosenberger
 *
 */
public class Direction {
	private float degree;
	private float originalDegree;
	private static float sectorSize=22.5f;
	
	public Direction(float degree) {
		originalDegree = degree;
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
	public float getOriginalDegree() {
		return this.originalDegree;
	}
	
	public static String getCompassDirection(Direction degree) {
		return getCompassDirectionOld(degree.getDegree());
	}
	public static String getCompassDirectionOld(float degree) {
    	if (degree>=22.5 && degree<67.5) return "NO";
    	if (degree>=67.5 && degree<112.5) return "O";
    	if (degree>=112.5 && degree<157.5) return "SO";
    	if (degree>=157.5 && degree<202.5) return "S";
    	if (degree>=202.5 && degree<247.5) return "SW";
    	if (degree>=247.5 && degree<292.5) return "W";
    	if (degree>=292.5 && degree<337.5) return "NW";
    	return "N";
    }
	public static String getCompassDirection(float degree) {
    	if (degree>=1*sectorSize && degree<2*sectorSize) return "NO";
    	if (degree>=2*sectorSize && degree<3*sectorSize) return "O";
    	if (degree>=3*sectorSize && degree<4*sectorSize) return "SO";
    	if (degree>=-4*sectorSize && degree<-3*sectorSize) return "SW";
    	if (degree>=-3*sectorSize && degree<-2*sectorSize) return "W";
    	if (degree>=-2*sectorSize && degree<-1*sectorSize) return "NW";
    	if (degree>=-1*sectorSize && degree<sectorSize) return "N";
    	return "S";
    }
	
	public String toString() {
		return String.format("%.2f°", this.degree);
	}
}
