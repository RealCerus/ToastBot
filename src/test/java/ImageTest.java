/*
 * Copyright (c) 2019 Cerus
 * File created at 11.04.19 11:49
 * Last modification: 11.04.19 11:49
 * All rights reserved.
 */

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ImageTest {

    public static void main(String[] args) {
        try {
            BufferedImage image2 = ImageIO.read(new URL("file:///C:\\Users\\Maximilian\\Pictures\\Iron_Sword.png"));
            BufferedImage image = ImageIO.read(new URL("file:///C:\\Users\\Maximilian\\Pictures\\mc.png"));

            Graphics graphics = image.getGraphics();
            graphics.drawImage(image2, 0, 0, null);
            graphics.dispose();

            ImageIO.write(image, "png", new File("./my file.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
