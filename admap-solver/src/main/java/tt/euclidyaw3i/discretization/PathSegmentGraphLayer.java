package tt.euclidyaw3i.discretization;

import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.layer.AbstractLayer;
import cz.agents.alite.vis.layer.VisLayer;
import org.jgrapht.DirectedGraph;

import java.awt.*;

public class PathSegmentGraphLayer extends AbstractLayer {

	public static <V, E> VisLayer create(
			final DirectedGraph<tt.euclidyaw3i.Point, PathSegment> graph, final boolean drawPathOrientations, final Color color) {

		return new AbstractLayer() {

			@Override
			public void paint(Graphics2D canvas) {

				canvas.setStroke(new BasicStroke(1));
				
				for (PathSegment roadSegment : graph.edgeSet()) {
					tt.euclidyaw3i.Point[] waypoints = roadSegment.getWaypoints();
					
					for (int i = 0; i < waypoints.length-1; i++) {

						canvas.setColor(color);
						canvas.drawLine(
								Vis.transX(waypoints[i].getPos().x),
								Vis.transY(waypoints[i].getPos().y),
								Vis.transX(waypoints[i + 1].getPos().x),
								Vis.transY(waypoints[i + 1].getPos().y));
					}

					if (drawPathOrientations) {
						for (int i = 0; i < waypoints.length; i++) {
							canvas.setColor(color);
							canvas.drawOval(Vis.transX(waypoints[i].getPos().x) - 1, Vis.transY(waypoints[i].getPos().y) - 1, 2, 2);

							canvas.setColor(color);
							float tipLength = 30;
							canvas.drawLine(
									Vis.transX(waypoints[i].getPos().x),
									Vis.transY(waypoints[i].getPos().y),
									Vis.transX(waypoints[i].getPos().x + tipLength * Math.cos(waypoints[i].getYawInRads())),
									Vis.transY(waypoints[i].getPos().y + tipLength * Math.sin(waypoints[i].getYawInRads())));
						}
					}

				}
				
				for (tt.euclidyaw3i.Point vertex : graph.vertexSet()) {
					canvas.setColor(color.darker());
					canvas.fillOval(Vis.transX(vertex.getPos().x)-2, Vis.transY(vertex.getPos().y)-2, 4, 4);
					
					float tipLength = 100;
					canvas.drawLine(
							Vis.transX(vertex.getPos().x),
							Vis.transY(vertex.getPos().y),
							Vis.transX(vertex.getPos().x + tipLength*Math.cos(vertex.getYawInRads())),
							Vis.transY(vertex.getPos().y + tipLength*Math.sin(vertex.getYawInRads())));
				}
			}

		};

	}

}
