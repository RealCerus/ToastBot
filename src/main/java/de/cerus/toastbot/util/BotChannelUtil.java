/*
 * Copyright (c) 2019 Cerus
 * File created at 12.04.19 18:33
 * Last modification: 12.04.19 18:33
 * All rights reserved.
 */

package de.cerus.toastbot.util;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BotChannelUtil {

    private static List<Long> botChannels;
    private static File file;
    private static CommentedFileConfig commentedFileConfig;

    public static void initialize() {
        file = new File("./BotChannels.toml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        commentedFileConfig = CommentedFileConfig.of(file);
        commentedFileConfig.load();
        if (commentedFileConfig.isEmpty()) {
            commentedFileConfig.set("bot-channels", new ArrayList<Long>());
            commentedFileConfig.save();
        }
        botChannels = commentedFileConfig.get("bot-channels");
    }

    public static void addBotChannel(Long id) {
        botChannels.add(id);
    }

    public static void removeBotChannel(Long id) {
        botChannels.remove(id);
    }

    public static boolean isBotChannel(Long id) {
        return botChannels.contains(id);
    }

    public static boolean isBotChannel(TextChannel channel) {
        if(channel.getTopic().toLowerCase().contains("[bot channel]"))
            return true;
        return isBotChannel(channel.getIdLong());
    }

    public static void shutdown(){
        saveChannels();
    }

    public static void saveChannels() {
        if (file == null) return;
        commentedFileConfig.set("bot-channels", botChannels);
        commentedFileConfig.save();
    }

    public static List<Long> getBotChannels() {
        return botChannels;
    }
}
