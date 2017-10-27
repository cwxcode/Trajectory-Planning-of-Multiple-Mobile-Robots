package tt.euclid2i.probleminstance;

import java.util.Collection;

import tt.euclid2i.Region;

public interface Environment {

    public Collection<Region> getObstacles();

    public Region getBoundary();
}
