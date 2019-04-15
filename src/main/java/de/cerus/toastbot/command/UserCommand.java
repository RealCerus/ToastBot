/*
 * Copyright (c) 2019 Cerus
 * File created at 11.04.19 13:37
 * Last modification: 11.04.19 13:37
 * All rights reserved.
 */

package de.cerus.toastbot.command;

import de.cerus.toastbot.settings.Settings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

public abstract class UserCommand {

    protected static int COLOR_GREEN = 8311585;
    protected static int COLOR_RED = 13632027;
    protected static int COLOR_BLUE = 2127320;

    private String command;

    private String description = "";
    private String usage = "";
    private Settings settings;

    public UserCommand(String command) {
        this.command = command;
    }

    public abstract void execute(String usedCommand, Member invoker, Message message, TextChannel channel, String[] args);

    public void onRegistration() {
    }

    public Message sendFailure(TextChannel channel, User user, String message) {
        return channel.sendMessage(
                buildFailure(user, message)
        ).complete();
    }

    public MessageEmbed buildFailure(User user, String message){
        return new EmbedBuilder()
                .setColor(COLOR_RED)
                .setTitle("Error")
                .setDescription(message)
                .setFooter(user.getAsTag(), user.getAvatarUrl())
                .build();
    }

    public Message sendSuccess(TextChannel channel, User user, String message) {
        return channel.sendMessage(
                buildSuccess(user, message)
        ).complete();
    }

    public MessageEmbed buildSuccess(User user, String message){
        return new EmbedBuilder()
                .setColor(COLOR_GREEN)
                .setTitle("Success")
                .setDescription(message)
                .setFooter(user.getAsTag(), user.getAvatarUrl())
                .build();
    }

    public Message sendMessage(TextChannel channel, User user, int color, String title, String message) {
        return channel.sendMessage(
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

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }
}
