package tt.euclid2i.probleminstance;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.region.Polygon;
import tt.euclid2i.region.Rectangle;

public class RandomEnvironment implements Environment {

    protected int seed;
    protected Rectangle bounds;
    protected Collection<Region> obstacles = new LinkedList<Region>();

    public RandomEnvironment(int dimX, int dimY, int obstacneCount, int maxSize, int seed) {
        Random random = new Random(seed);
        bounds = new Rectangle(new Point(0, 0), new Point(dimX, dimY));
        this.seed = seed;
        createObstacles(obstacneCount, maxSize, random);
    }

    private void createObstacles(int n, int maxSize, Random random) {
        for (int i = 0; i < n; i++) {
            int size = 1+random.nextInt(maxSize-1);
            int x = bounds.getCorner1().x + random.nextInt(bounds.getCorner2().x - bounds.getCorner1().x);
            int y = bounds.getCorner1().y + random.nextInt(bounds.getCorner2().y - bounds.getCorner1().y);
            Rectangle obstacle = new Rectangle(new Point(x, y), new Point(x + size, y + size));
            Polygon polygon = obstacle.toPolygon();
            obstacles.add(polygon);
        }
    }

    @Override
    public Collection<Region> getObstacles() {
        return obstacles;
    }
    public int getSeed() {
        return seed;
    }

	@Override
	public Region getBoundary() {
		return bounds.toOutFilledPolygon();
	}

}
