package tt.vis.environentcreator;

import cz.agents.alite.vis.Vis;
import tt.euclid2i.Point;
import tt.euclid2i.region.Polygon;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PolygonCreator {

    private List<Polygon> polygons;
    private Polygon current;

    public PolygonCreator() {
        polygons = new ArrayList<Polygon>();
    }

    public List<Polygon> getPolygons() {
        return polygons;
    }

    @SuppressWarnings("unchecked")
    public List<Polygon> getCurrent() {
        if (current != null) {
            return Collections.singletonList(current);
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    public void addPoint(Point point) {
        if (current != null) {
            current.addPoint(point);
        } else {
            current = new Polygon(new Point[]{point});
        }
    }

    public void savePolygon() {
        if (current != null) {
            polygons.add(current);
            current = null;
        }
    }

    public void clearLast() {
        if (current != null) {
            current = null;
        } else {
            if (!polygons.isEmpty())
                polygons.remove(polygons.size() - 1);

        }
    }

    public void saveList() {
        String name = JOptionPane.showInputDialog(Vis.getInstance(), "Save as (absolute path or relative to the project's root)");

        try {
            if (name == null || name.isEmpty())
                throw new RuntimeException("Empty name");

            BufferedWriter textWriter = new BufferedWriter(new FileWriter(new File(name + ".txt")));

            for (Polygon polygon : polygons) {
                for (Point point : polygon.getPoints()) {
                    textWriter.write(String.format("%d %d%n", point.x, point.y));
                }
                textWriter.newLine();
            }
            textWriter.flush();
            textWriter.close();

            JOptionPane.showMessageDialog(Vis.getInstance(), "List of polygons saved successfully", "Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(Vis.getInstance(), "Could not save", "Unsuccessful", JOptionPane.ERROR_MESSAGE);
        }
    }
}



