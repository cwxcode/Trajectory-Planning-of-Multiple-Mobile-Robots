/**
 *
 */
package cz.agents.alite.vis.element.aggregation;

import cz.agents.alite.vis.element.Point;

public interface PointElements extends StyledElements {

    Iterable<? extends Point> getPoints();

}
