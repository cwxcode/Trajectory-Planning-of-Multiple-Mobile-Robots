package cz.agents.alite.vis.layer.terminal;

import java.awt.Graphics2D;

import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.element.Line;
import cz.agents.alite.vis.element.aggregation.LineElements;

public class ArrowLayer extends LineLayer {
// TODO: should be an extender of the LineLayer (inheritance is bad here!)

    protected ArrowLayer(LineElements lineElements) {
        super(lineElements);
    }

    @Override
    protected void onEachLine(Graphics2D canvas, Line line) {
        int x1 = Vis.transX(line.getFrom().x);
        int y1 = Vis.transY(line.getFrom().y);
        int x2 = Vis.transX(line.getTo().x);
        int y2 = Vis.transY(line.getTo().y);

        Vector3d arrowPart1 = new Vector3d(x2 - x1, y2 - y1, 0);
        Vector3d arrowPart2 = new Vector3d(arrowPart1);
        Matrix4d transf1 = new Matrix4d();
        transf1.rotZ(5 * Math.PI / 6);
        transf1.transform(arrowPart1);
        transf1.rotZ(7 * Math.PI / 6);
        transf1.transform(arrowPart2);
        arrowPart1.normalize();
        arrowPart1.scale(10);

        arrowPart2.normalize();
        arrowPart2.scale(10);

        canvas.drawLine(x2, y2, x2 + (int) arrowPart1.x, y2 + (int) arrowPart1.y);
        canvas.drawLine(x2, y2, x2 + (int) arrowPart2.x, y2 + (int) arrowPart2.y);
    }

    public static ArrowLayer create(LineElements lineElements) {
        return new ArrowLayer(lineElements);
    }

}
