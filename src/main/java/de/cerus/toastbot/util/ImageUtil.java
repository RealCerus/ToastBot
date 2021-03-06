/*
 * Copyright (c) 2019 Cerus
 * File created at 11.04.19 12:38
 * Last modification: 11.04.19 12:38
 * All rights reserved.
 */

package de.cerus.toastbot.util;

import de.cerus.toastbot.launcher.BotLauncher;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.io.FileUtils;
import sun.awt.image.ToolkitImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static java.awt.Toolkit.getDefaultToolkit;

public class ImageUtil {

    private static File toast = new File("./toast.jpg");
    private static File miniToast = new File("./mini_toast.gif");
    private static File wheatField = new File("./wheat_field.jpg");
    private static File fireImage = new File("./fire.jpg");
    private static File lightningImage = new File("./lightning.png");
    private static Font breadFont = new Font("bread", Font.PLAIN, 30);

    public static BufferedImage getAvatarImage(User user, int width) {
        if (user.getAvatarUrl() == null && user.getDefaultAvatarUrl() == null) return null;

        try {
            java.net.URL url = new URL((user.getAvatarUrl() == null ? user.getDefaultAvatarUrl() : user.getAvatarUrl()) + "?size=128");
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
            ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();

            int c;
            while ((c = in.read()) != -1) {
                byteArrayOut.write(c);
            }

            ToolkitImage image = (ToolkitImage) getDefaultToolkit().createImage(byteArrayOut.toByteArray());
            Image temporary = new ImageIcon(image).getImage();

            BufferedImage bufferedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

            Graphics2D graphics = bufferedImage.createGraphics();
            graphics.drawImage(temporary, 0, 0, null);
            graphics.dispose();

            return scale(bufferedImage, width, width);
            //ImageIO.write(bufferedImage, "png", new File("./output.png"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File toastify(User user, int percent) {
        try {
            // Read the background image
            BufferedImage wheatFieldImage = ImageIO.read(wheatField);
            // Get the user's avatar
            BufferedImage avatar = getAvatarImage(user, 128);

            if (avatar == null) return null;

            // Get the background's graphics object to draw all the other stuff
            Graphics graphics = wheatFieldImage.getGraphics();
            // Draw the avatar in the middle of the screen
            graphics.drawImage(avatar, (wheatFieldImage.getWidth() / 2) - avatar.getWidth() / 2, (wheatFieldImage.getHeight() / 2) - avatar.getHeight() / 2, null);

            // Read our little toast icon
            BufferedImage miniToastImage = ImageIO.read(miniToast);

            // Drawing the text
            graphics.setColor(Color.WHITE);
            Font font = new Font("Segoe UI", Font.BOLD, 27);
            graphics.setFont(font);
            String s = user.getAsTag() + ": " + percent + "%";
            int stringWitdh = graphics.getFontMetrics(font).stringWidth(s);
            int newStringWitdh = graphics.getFontMetrics(font).stringWidth(s) + miniToastImage.getWidth();
            graphics.drawString(s, (wheatFieldImage.getWidth() / 2) - newStringWitdh / 2, (wheatFieldImage.getHeight() / 2) + avatar.getHeight() + 5);
            // Drawing our toast icon next to the text
            graphics.drawImage(miniToastImage, ((wheatFieldImage.getWidth() / 2) + (stringWitdh / 2)), (wheatFieldImage.getHeight() / 2) + avatar.getHeight() - 30, null);

            // Drawing the background of the 'progress bar'
            graphics.setColor(Color.GRAY);
            int rectX = 15;
            int rectY = (wheatFieldImage.getHeight() / 5);
            int rectWidth = (wheatFieldImage.getWidth()) - 30;
            int rectHeight = 10;
            graphics.fillRect(rectX, rectY, rectWidth, rectHeight);

            // Drawing the actual content of the 'progress bar'
            graphics.setColor(Color.LIGHT_GRAY);
            rectY += 1;
            rectX += 1;
            rectHeight -= 2;
            rectWidth -= 2;
            rectWidth = (rectWidth / 100) * percent;
            graphics.fillRect(rectX, rectY, rectWidth, rectHeight);

            // Dispose the graphics object because we don't need it anymore
            graphics.dispose();

            // Save the image to work with it later on
            File directory = new File("./toastify-images/");
            directory.mkdirs();
            File savedImage = new File(directory, "toastify-" + user.getId() + "-" + System.currentTimeMillis() + ".png");

            ImageIO.write(wheatFieldImage, "png", savedImage);
            return savedImage;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File toastUser(User user) {
        try {
            // Reading the background image
            BufferedImage toastImage = ImageIO.read(toast);
            // Getting the user's avatar
            BufferedImage avatar = getAvatarImage(user, 128);

            if (avatar == null) return null;

            Graphics graphics = toastImage.getGraphics();
            // Drawing the avatar to the center of the background image
            graphics.drawImage(avatar, (toastImage.getWidth() / 2) - avatar.getWidth() / 2, (toastImage.getHeight() / 2) - avatar.getHeight() / 2, null);
            graphics.dispose();

            File directory = new File("./toast-images/");
            directory.mkdirs();
            File savedImage = new File(directory, "toast-" + user.getId() + "-" + System.currentTimeMillis() + ".png");

            // Saving the generated image to use it later
            ImageIO.write(toastImage, "png", savedImage);
            return savedImage;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File nameToastUser(User user) {
        try {
            // Reading the background image
            BufferedImage toastImage = ImageIO.read(toast);

            // Getting the graphics object and setting all preferences
            Graphics graphics = toastImage.getGraphics();
            graphics.setFont(breadFont);
            graphics.setColor(Color.darkGray);
            int stringWidth = graphics.getFontMetrics(breadFont).stringWidth(user.getName());
            if (stringWidth >= 175) {
                Font tempFont = new Font(breadFont.getName(), Font.PLAIN, 20);
                graphics.setFont(tempFont);
                stringWidth = graphics.getFontMetrics(tempFont).stringWidth(user.getName());
            }
            // Drawing the name
            graphics.drawString(user.getName().toLowerCase().trim(), (toastImage.getWidth() / 2) - stringWidth / 2, toastImage.getHeight() / 2);
            graphics.dispose();

            File directory = new File("./toast-images/");
            directory.mkdirs();
            File savedImage = new File(directory, "toast-" + user.getId() + "-" + System.currentTimeMillis() + ".png");

            // Saving the generated image to use it later
            ImageIO.write(toastImage, "png", savedImage);
            return savedImage;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File toastBatlle(User userOne, User userTwo) {
        try {
            // Reading the background image
            BufferedImage fire = ImageIO.read(fireImage);
            // Reading the lightning image
            BufferedImage lightning = ImageIO.read(lightningImage);

            // Get the avatar's
            BufferedImage userOneAvatar = getAvatarImage(userOne, 256);
            BufferedImage userTwoAvatar = getAvatarImage(userTwo, 256);

            // Getting the graphics object and setting all preferences
            Graphics graphics = fire.getGraphics();

            int oneThird = fire.getWidth()/3;
            int xOne = (fire.getWidth()/2)-oneThird-(userOneAvatar.getWidth()/2);
            int xTwo = (fire.getWidth()/2)+oneThird-(userTwoAvatar.getWidth()/2);
            int y = (fire.getHeight()/2)-(256/2);
            int xLightning = (fire.getWidth()/2)-(lightning.getWidth()/2);
            int yLightning = (fire.getHeight()/2)-(lightning.getHeight()/2);

            // Draw all images
            graphics.drawImage(lightning, xLightning, yLightning, null);
            graphics.drawImage(userOneAvatar, xOne, y, null);
            graphics.drawImage(userTwoAvatar, xTwo, y, null);

            graphics.dispose();

            File directory = new File("./toastbattle-images/");
            directory.mkdirs();
            File savedImage = new File(directory, "toastbattle-" + userOne.getId() + "-" + userTwo.getId() + "-" + System.currentTimeMillis() + ".png");

            // Saving the generated image to use it later
            ImageIO.write(fire, "png", savedImage);
            return savedImage;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static BufferedImage scale(BufferedImage imageToScale, int dWidth, int dHeight) {
        BufferedImage scaledImage = null;
        if (imageToScale != null) {
            scaledImage = new BufferedImage(dWidth, dHeight, imageToScale.getType());
            Graphics2D graphics2D = scaledImage.createGraphics();
            graphics2D.drawImage(imageToScale, 0, 0, dWidth, dHeight, null);
            graphics2D.dispose();
        }
        return scaledImage;
    }

    public static void load() {
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            File fontFile = new File("./yummy_bread.ttf");
            if (!fontFile.exists()) {
                FileUtils.copyURLToFile(new java.net.URL("http://cerus-dev.de/dl/toastbot/yummy_bread.ttf"), fontFile);
            }
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("./yummy_bread.ttf")));
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

        if (!toast.exists()) {
            try {
                InputStream stream = BotLauncher.class.getClassLoader().getResourceAsStream("toast.jpg");
                if (stream == null)
                    stream = BotLauncher.class.getClassLoader().getResourceAsStream("/toast.jpg");
                Files.copy(stream, toast.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!wheatField.exists()) {
            try {
                InputStream stream = BotLauncher.class.getClassLoader().getResourceAsStream("wheat_field.jpg");
                if (stream == null)
                    stream = BotLauncher.class.getClassLoader().getResourceAsStream("/wheat_field.jpg");
                Files.copy(stream, wheatField.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!miniToast.exists()) {
            try {
                InputStream stream = BotLauncher.class.getClassLoader().getResourceAsStream("mini_toast.gif");
                if (stream == null)
                    stream = BotLauncher.class.getClassLoader().getResourceAsStream("/mini_toast.gif");
                Files.copy(stream, miniToast.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!fireImage.exists()) {
            try {
                InputStream stream = BotLauncher.class.getClassLoader().getResourceAsStream("fire.jpg");
                if (stream == null)
                    stream = BotLauncher.class.getClassLoader().getResourceAsStream("/fire.jpg");
                Files.copy(stream, fireImage.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!lightningImage.exists()) {
            try {
                InputStream stream = BotLauncher.class.getClassLoader().getResourceAsStream("lightning.png");
                if (stream == null)
                    stream = BotLauncher.class.getClassLoader().getResourceAsStream("/lightning.png");
                Files.copy(stream, lightningImage.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
