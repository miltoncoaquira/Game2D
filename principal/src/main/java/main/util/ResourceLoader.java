package main.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public final class ResourceLoader {

    private ResourceLoader() {
    }

    public static BufferedImage loadImage(String resourcePath) throws IOException {
        try (InputStream inputStream = openStream(resourcePath)) {
            return ImageIO.read(inputStream);
        }
    }

    public static InputStream openStream(String resourcePath) {
        InputStream inputStream = ResourceLoader.class.getResourceAsStream(resourcePath);

        if(inputStream == null) {
            throw new IllegalArgumentException("No se encontro el recurso: " + resourcePath);
        }

        return inputStream;
    }
}
