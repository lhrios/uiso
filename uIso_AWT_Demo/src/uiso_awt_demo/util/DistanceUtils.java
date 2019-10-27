package uiso_awt_demo.util;

public class DistanceUtils {
	public static int diagonalDistance(int x1, int y1, int x2, int y2) {
		return Math.max(Math.abs(x1 - x2), Math.abs(y1 - y2));
	}

	public static int euclideanDistance(int x1, int y1, int x2, int y2) {
		return (int) (Math.round(Math.sqrt(Math.pow(x1 - x2, 2) * Math.pow(y1 - y2, 2))));
	}
}
