/*
 * Copyright (c) 2019 Cerus
 * File created at 11.04.19 11:38
 * Last modification: 11.04.19 11:38
 * All rights reserved.
 */

package de.cerus.toastbot.launcher;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import de.cerus.toastbot.ToastBot;
import de.cerus.toastbot.settings.Settings;
import de.cerus.toastbot.util.AppPropertiesReader;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;

public class BotLauncher {

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(BotLauncher.class);

        File settingsFile = new File("./Settings.toml");
        if (!settingsFile.exists()) {
            try {
                boolean created = settingsFile.createNewFile();
                logger.info(created ? "Created the file for the settings." : "Failed to create the file for the settings.");
            } catch (IOException e) {
                logger.error("Failed to create a file for the settings (fatal)");
                System.exit(-1);
                return;
            }
        }

        CommentedFileConfig config = CommentedFileConfig.of(settingsFile);
        config.load();

        if (config.isEmpty()) {
            fillDefaults(config);
            logger.info("The default settings were set. Please edit them and start this program again.");
            System.exit(0);
            return;
        }

        Settings settings = new Settings(config);

        JDA jda;
        try {
            jda = new JDABuilder(settings.getDiscordToken()).build();
        } catch (LoginException e) {
            logger.error("Failed to create JDA object. Program will exit. (" + e.getMessage() + ")");
            System.exit(-1);
            return;
        }

        logger.info("Waiting for JDA to be ready...");
        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            logger.error("Failed to wait for the JDA to get ready. Program will exit.");
            System.exit(-1);
            return;
        }
        logger.info("JDA is ready!");

        AppPropertiesReader.initialize();

        logger.info("Starting Toast Bot v" + AppPropertiesReader.getVersion());
        ToastBot bot = new ToastBot(jda, settings);
        bot.launch();

        //Runtime.getRuntime().addShutdownHook(new Thread(bot::saveChannels));
    }

    private static void fillDefaults(CommentedFileConfig settings) {
        settings.set("discord-bot-token", "<place token here>");
        settings.set("set-presence", true);
        settings.set("dropbox-api-token", "<place token here>");
        settings.set("giphy-api-token", "<place token here>");
        settings.set("discord-bot-list-api-token", "<place token here>");
        settings.set("command-prefix", "+");
        settings.set("vote-needed-for-cat-gif", true);
        settings.set("command-log", true);
        settings.setComment("giphy-api-token", "You can create a Giphy API token here: https://developers.giphy.com/dashboard/?create=true");
        settings.setComment("discord-bot-token", "You can create a Discord bot token here: https://discordapp.com/developers/applications/");
        settings.setComment("dropbox-api-token", "You can create a DropBox token here: https://www.dropbox.com/developers/apps");
        settings.save();
    }

}
