/*
 * Copyright (c) 2019 Cerus
 * File created at 11.04.19 13:37
 * Last modification: 11.04.19 13:37
 * All rights reserved.
 */

package de.cerus.toastbot.command;

import de.cerus.toastbot.settings.Settings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public abstract class UserCommand {

    protected static int COLOR_GREEN = 8311585;
    protected static int COLOR_RED = 13632027;
    protected static int COLOR_BLUE = 2127320;

    private String command;

    private String description = "";
    private Settings settings;

    public UserCommand(String command) {
        this.command = command;
    }

    public abstract void execute(String usedCommand, Member invoker, Message message, TextChannel channel, String[] args);

    public void onRegistration() {
    }

    public void sendFailure(TextChannel channel, User user, String message) {
        channel.sendMessage(
                new EmbedBuilder()
                        .setColor(COLOR_RED)
                        .setTitle("Error")
                        .setDescription(message)
                        .setFooter(user.getAsTag(), user.getAvatarUrl())
                        .build()
        ).complete();
    }

    public void sendSuccess(TextChannel channel, User user, String message) {
        channel.sendMessage(
                new EmbedBuilder()
                        .setColor(COLOR_GREEN)
                        .setTitle("Success")
                        .setDescription(message)
                        .setFooter(user.getAsTag(), user.getAvatarUrl())
                        .build()
        ).complete();
    }

    public void sendMessage(TextChannel channel, User user, int color, String title, String message) {
        channel.sendMessage(
                new EmbedBuilder()
                        .setColor(color)
                        .setTitle(title)
                        .setDescription(message)
                        .setFooter(user.getAsTag(), user.getAvatarUrl())
                        .build()
        ).complete();
    }

    public String getCommand() {
        return command;
    }

    public Settings getSettings() {
        return settings;
    }

    void setSettings(Settings settings) {
        this.settings = settings;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
