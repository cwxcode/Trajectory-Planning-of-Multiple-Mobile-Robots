package tt.vis;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import cz.agents.alite.vis.element.Line;
import cz.agents.alite.vis.element.aggregation.LineElements;
import cz.agents.alite.vis.element.aggregation.PointElements;
import cz.agents.alite.vis.element.implemetation.LineImpl;
import cz.agents.alite.vis.layer.AbstractLayer;
import cz.agents.alite.vis.layer.GroupLayer;
import cz.agents.alite.vis.layer.VisLayer;
import cz.agents.alite.vis.layer.terminal.LineLayer;
import cz.agents.alite.vis.layer.terminal.PointLayer;

public class GraphPathLayer extends AbstractLayer {

    public interface PathProvider<V, E> {
        GraphPath<V, E> getPath();
    }

    GraphPathLayer() {
    }

    public static <V, E> VisLayer create(final PathProvider<V, E> pathProvider, final ProjectionTo2d<V> projection, final Color edgeColor, final Color vertexColor,
            final int edgeStrokeWidth, final int vertexStrokeWidth) {
        GroupLayer group = GroupLayer.create();

        // edges
        group.addSubLayer(LineLayer.create(new LineElements() {

            @Override
            public Iterable<Line> getLines() {
                GraphPath<V, E> path = pathProvider.getPath();
                Collection<Line> lines = new ArrayList<Line>();
                if (path != null) {
                    Graph<V, E> graph = path.getGraph();
                    for (E edge : path.getEdgeList()) {
                        Point2d start = projection.project(graph.getEdgeSource(edge));
                        Point2d end = projection.project(graph.getEdgeTarget(edge));

                        if (start != null && end != null) {
                        	lines.add(new LineImpl( new Point3d(start.x, start.y, 0), new Point3d(end.x, end.y, 0)));
                        }
                     }
                }
                return lines;
            }

            @Override
            public int getStrokeWidth() {
                return edgeStrokeWidth;
            }

            @Override
            public Color getColor() {
                return edgeColor;
            }

        }));

        // vertices
        group.addSubLayer(PointLayer.create(new PointElements() {

            class VisPoint implements cz.agents.alite.vis.element.Point {
                Point3d point3d;

                public VisPoint(double x, double y, double z) {
                    this.point3d = new Point3d(x,y,z);
                }

                @Override
                public Point3d getPosition() {
                    return point3d;
                }
            }

            @Override
            public Iterable<cz.agents.alite.vis.element.Point> getPoints() {
                Collection<cz.agents.alite.vis.element.Point> points = new ArrayList<cz.agents.alite.vis.element.Point>();
                GraphPath<V, E> path = pathProvider.getPath();
                if (path != null) {
                    Graph<V, E> graph = path.getGraph();
                    for (E edge : path.getEdgeList()) {
                        Point2d source = projection.project(graph.getEdgeSource(edge));
                        if (source != null) {
                        	points.add(new VisPoint(source.x, source.y, 0.0));
                        }
                    }
                    Point2d end = projection.project(path.getEndVertex());
                    if (end != null) {
                    	points.add(new VisPoint(end.x, end.y, 0));
                    }
                }
                return points;
            }

            @Override
            public int getStrokeWidth() {
                return vertexStrokeWidth;
            }

            @Override
            public Color getColor() {
                return vertexColor;
            }

        }));


        return group;
    }

}
