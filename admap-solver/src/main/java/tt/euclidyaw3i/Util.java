package tt.euclidyaw3i;

public class Util {
	public static Point interpolate(Point start, Point end, double alpha) {
		
		int x = (int) Math.round(start.getPos().x + alpha * (end.getPos().x - start.getPos().x));
		int y = (int) Math.round(start.getPos().y + alpha * (end.getPos().y - start.getPos().y));

		double angleDiff = end.getYawInRads() - start.getYawInRads();
		double shortestAngle = ((((angleDiff) % (2*Math.PI)) + (3*Math.PI)) % (2*Math.PI)) - Math.PI;
	    double yaw = start.getYawInRads() + shortestAngle * alpha;
		
		return new Point(x, y, (int) (yaw * 1000));
	}

	public static double angleDiff(double alpha, double beta) {
		double angleDiff = alpha - beta;
		return ((((angleDiff) % (2*Math.PI)) + (3*Math.PI)) % (2*Math.PI)) - Math.PI;
	}
}
