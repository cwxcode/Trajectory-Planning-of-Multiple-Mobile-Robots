package tt.euclid2i.probleminstance;

import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.region.Rectangle;
import tt.euclid2i.util.Util;

import java.util.Collection;
import java.util.Random;

public class ShortestPathProblem {

    final protected int targetRegionSide;
    protected int seed;
    protected Environment environment;
    protected Point start;
    protected Region targetRegion;
    protected Point targetPoint;

    public ShortestPathProblem(int dimX, int dimY, int obstacles, int obstacleMaxSize, int targetSize, int seed) {
        this.targetRegionSide = targetSize;
        this.seed = seed;
        this.environment = new RandomEnvironment(dimX, dimY, obstacles, obstacleMaxSize, seed);

        Random random = new Random(seed);

        do {
            start = Util.sampleFreeSpace(environment.getBoundary().getBoundingBox(), environment.getObstacles(), random);
        } while (start.getX() > dimX / 4 || start.getY() > dimY / 4);

        do {
            targetPoint = Util.sampleFreeSpace(environment.getBoundary().getBoundingBox(), environment.getObstacles(), random);
        } while (targetPoint.getX() < dimX * 3 / 4 || targetPoint.getY() < dimY * 3 / 4);

        targetRegion = new Rectangle(
                new Point(targetPoint.x - targetRegionSide / 2, targetPoint.y - targetRegionSide / 2),
                new Point(targetPoint.x + targetRegionSide / 2, targetPoint.y + targetRegionSide / 2));

    }

    public Collection<Region> getObstacles() {
        return environment.getObstacles();
    }

    public Region getBoundary() {
    	return environment.getBoundary();
    }

    public Point getStart() {
        return start;
    }

    public Point getTargetPoint() {
        return targetPoint;
    }

    public Region getTargetRegion() {
        return targetRegion;
    }

    public int getSeed() {
        return seed;
    }

    public int getTargetRegionRadius() {
        return targetRegionSide / 2;
    }
}
