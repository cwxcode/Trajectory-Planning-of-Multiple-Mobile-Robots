package tt.euclidyaw3i;

public class Point {
	tt.euclid2i.Point position;
	int yawInMilliRads; // ini milli-radians

	public Point(int x, int y, int yawInMilliRads) {
		super();
		this.position = new tt.euclid2i.Point(x,y);
		this.yawInMilliRads = yawInMilliRads;
	}

	public Point(int x, int y, float yawInRads) {
		super();
		this.position = new tt.euclid2i.Point(x,y);
		this.yawInMilliRads = Math.round(yawInRads * 1000);
	}

	public Point(tt.euclid2i.Point position, int yawInMilliRads) {
		super();
		this.position = position;
		this.yawInMilliRads = yawInMilliRads;
	}
	
	public Point(tt.euclid2i.Point position, float yawInRads) {
		this(position, Math.round(yawInRads*1000));
	}
	
	public float getYawInRads() {
		return (yawInMilliRads / 1000.0f);
	}
	
	public tt.euclid2i.Point getPos() {
		return position;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((position == null) ? 0 : position.hashCode());
		result = prime * result + yawInMilliRads;
		return result;
	}
	
	public double[] toQuaternion() {
		
		double c1 = Math.cos(getYawInRads() / 2);
		double c2 = Math.cos(0); // 1
		double c3 = Math.cos(0); // 1
		
		double s1 = Math.sin(getYawInRads() / 2);
		double s2 = Math.sin(0); // 0
		double s3 = Math.sin(0); // 0
		
		double w = c1 * c2 * c3 - s1 * s2 * s3;
		
		double x = s1 * s2 * c3 + c1 * c2 * s3; // 0 
		double y = s1 * c2 * c3 + c1 * s2 * s3;  
		double z = c1 * s2 * c3 - s1 * c2 * s3; // 0
		
		return new double[] {x, y, z, w};
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		if (yawInMilliRads != other.yawInMilliRads)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "(" + position.x + ", " + position.y + ", " +  yawInMilliRads + ")";
	}
	
	
}
