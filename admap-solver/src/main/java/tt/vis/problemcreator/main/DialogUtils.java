package tt.vis.problemcreator.main;

import cz.agents.alite.vis.Vis;
import tt.euclid2i.Point;
import tt.euclid2i.region.Rectangle;

import javax.swing.*;

public class DialogUtils {

    private DialogUtils() {
    }

    public static int getNumber(String text, int defaultValue) {
        do {
            String string = JOptionPane.showInputDialog(Vis.getInstance(), text, defaultValue);
            try {
                return Integer.parseInt(string);
            } catch (Exception ex) {
                String errorMessage = String.format("Trouble occurred while parsing \"%s\", try it again", string);
                JOptionPane.showMessageDialog(Vis.getInstance(), errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } while (true);

    }

    public static Rectangle getRectangle(String text, String defaultValue) {
        do {
            String string = JOptionPane.showInputDialog(Vis.getInstance(), text, defaultValue);
            try {

                String[] arr = string.split("x");
                return new Rectangle(new Point(0, 0), new Point(Integer.parseInt(arr[0]), Integer.parseInt(arr[1])));

            } catch (Exception ex) {
                String errorMessage = String.format("Trouble occurred while parsing \"%s\", try it again", string);
                JOptionPane.showMessageDialog(Vis.getInstance(), errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } while (true);

    }
}
