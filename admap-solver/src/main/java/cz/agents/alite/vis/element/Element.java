package cz.agents.alite.vis.element;

import cz.agents.alite.vis.element.aggregation.Elements;

/**
 * The visual elements are abstract descriptions of various objects,
 * which can be drawn.
 *
 * Each element is described only by its interface, and the parameters of the
 * elements defined by the methods in the interface are the only thing needed,
 * so the element can be drawn.
 *
 * Any object implementing the particular element interface, can be consequently
 * used for the visualization. Which means, the data structures (which can be
 * possibly visual elements) of the application do not need to re-instantiated,
 * but can only implement the interface and also be used in the visualization.
 *
 * Each element has its own default implementation in the <code>implementation</code>
 * sub-package. The default implementations can be used, if there is no explicit
 * data structure representing the visual element in the application.
 *
 * Groups of the elements are described by their aggregations
 * (see the {@link Elements} interface).
 *
 *
 * @author Antonin Komenda
 */
public interface Element {

}
