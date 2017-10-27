package tt.euclid2d;

import javax.vecmath.Tuple2d;
import javax.vecmath.Tuple2f;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector2f;

public class Vector extends Vector2d {
	private static final long serialVersionUID = -4154617178007394413L;

	public Vector() {
		super();
	}

	public Vector(double x, double y) {
		super(x, y);
	}

	public Vector(double[] v) {
		super(v);
	}

	public Vector(Tuple2d t1) {
		super(t1);
	}

	public Vector(Tuple2f t1) {
		super(t1);
	}

	public Vector(Vector2d v1) {
		super(v1);
	}

	public Vector(Vector2f v1) {
		super(v1);
	}
}
