package cz.agents.alite.vis.layer.terminal;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import cz.agents.alite.vis.element.aggregation.ImageElements;

public abstract class ImageLayer extends TerminalLayer {

    private final ImageElements imageElements;

    protected ImageLayer(ImageElements imageElements) {
        this.imageElements = imageElements;
    }

    protected ImageElements getImageElements() {
        return imageElements;
    }

    public static BufferedImage loadImage(File file) {
        BufferedImage img = null;
        try {
            img = loadImage(file.toURI().toURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return img;
    }

    public static BufferedImage loadImage(URL file) {
        BufferedImage img = null;

        try {
            img = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return img;
    }

}
