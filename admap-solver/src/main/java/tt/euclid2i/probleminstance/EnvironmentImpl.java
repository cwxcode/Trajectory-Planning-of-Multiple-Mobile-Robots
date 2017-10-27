package tt.euclid2i.probleminstance;

import tt.euclid2i.Region;

import java.util.Collection;

/**
 * Created by Vojtech Letal on 2/26/14.
 */
public class EnvironmentImpl implements Environment {

    private Collection<Region> obstacles;
    private Region boundary;

    public EnvironmentImpl(Collection<Region> obstacles, Region boundary) {
        this.obstacles = obstacles;
        this.boundary = boundary;
    }

    public Collection<Region> getObstacles() {
        return obstacles;
    }

    public void setObstacles(Collection<Region> obstacles) {
        this.obstacles = obstacles;
    }

    public void setBoundary(Region boundary) {
        this.boundary = boundary;
    }

    @Override
    public Region getBoundary() {
    	return boundary;
    }
}
