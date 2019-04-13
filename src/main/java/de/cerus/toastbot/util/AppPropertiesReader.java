/*
 * Copyright (c) 2019 Cerus
 * File created at 13.04.19 12:14
 * Last modification: 13.04.19 12:14
 * All rights reserved.
 */

package de.cerus.toastbot.util;

import de.cerus.toastbot.launcher.BotLauncher;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppPropertiesReader {

    private static String version = "Undefined";

    public static void initialize() {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = BotLauncher.class.getResourceAsStream("app.properties");
            if (inputStream == null)
                inputStream = BotLauncher.class.getResourceAsStream("/app.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            System.err.println("Failed to load app.properties");
            return;
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        version = properties.getProperty("toastbot.version");
    }

    public static String getVersion() {
        return version;
    }
}
