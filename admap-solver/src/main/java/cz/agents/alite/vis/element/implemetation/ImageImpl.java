package cz.agents.alite.vis.element.implemetation;

import java.awt.image.BufferedImage;

import cz.agents.alite.vis.element.Image;

public class ImageImpl implements Image {

    private final BufferedImage image;

    public ImageImpl(BufferedImage image) {
        this.image = image;
    }

    @Override
    public BufferedImage getImage() {
        return image;
    }

}
