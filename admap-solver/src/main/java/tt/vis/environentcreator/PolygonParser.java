package tt.vis.environentcreator;

import cz.agents.alite.vis.Vis;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.region.Polygon;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PolygonParser {

    public static void save(Collection<Polygon> polygons) {
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

    public static List<Region> load(String file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(file)));
            List<Region> polygons = new ArrayList<Region>();

            String line;
            Polygon polygon = null;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    polygons.add(polygon);
                    polygon = null;
                } else {
                    if (polygon == null)
                        polygon = new Polygon(new Point[]{});

                    String[] split = line.split(" ");
                    int x = Integer.parseInt(split[0]);
                    int y = Integer.parseInt(split[1]);
                    polygon.addPoint(new Point(x, y));
                }
            }

            if (polygon != null)
                polygons.add(polygon);

            return polygons;

        } catch (IOException e) {
            e.printStackTrace();
        }

        throw new RuntimeException("Obstacles were not succesfully loaded");
    }
}
