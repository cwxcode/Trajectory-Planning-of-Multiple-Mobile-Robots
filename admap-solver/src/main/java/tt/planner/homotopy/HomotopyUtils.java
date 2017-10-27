package tt.planner.homotopy;

import org.jscience.mathematics.number.Complex;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.region.Rectangle;
import tt.euclid2i.util.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class HomotopyUtils {

    private static final int MAX_TRIALS = 20000;

    private HomotopyUtils() {
    }

    public static List<Complex> sampleObstacles(Collection<? extends Region> obstacles, Random random) {
        List<Complex> complexes = new ArrayList<Complex>();

        for (Region obstacle : obstacles) {
            Point point = Util.sampleObstacle(obstacle, random, MAX_TRIALS);
            complexes.add(Complex.valueOf(point.x, point.y));
        }

        return complexes;
    }

    public static List<Complex> sampleFreeSpace(Collection<? extends Region> obstacles, Rectangle problemBounds, Random random) {
        List<Complex> complexes = new ArrayList<Complex>();

        int freeSpaceSamples = obstacles.size() - 1;
        for (int i = 0; i < freeSpaceSamples; i++) {
            Point point = Util.sampleFreeSpace(problemBounds, obstacles, random);
            complexes.add(Complex.valueOf(point.x, point.y));
        }

        return complexes;
    }

}
