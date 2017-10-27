package cz.agents.alite.vis.element.aggregation;

import cz.agents.alite.vis.element.FilledStyledCircle;

public interface FilledStyledCircleElements extends Elements, FilledStyledElements {

    public Iterable<? extends FilledStyledCircle> getCircles();
}
