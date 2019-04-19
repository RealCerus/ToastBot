/*
 * Copyright (c) 2019 Cerus
 * File created at 11.04.19 11:38
 * Last modification: 11.04.19 11:38
 * All rights reserved.
 */

package de.cerus.toastbot.settings;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.SelfUser;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;

public class Settings {

    private CommentedFileConfig commentedFileConfig;
    private CommentedFileConfig guildSettings;

    private String discordToken;
    private String dropboxToken;
    private String commandPrefix;
    private String giphyToken;
    private String dblToken;
    private String dblVoteAuth;
    private boolean setPresence;
    private boolean voteNeededForGifCommand;
    private boolean commandLog;

    public Settings(@Nonnull CommentedFileConfig commentedFileConfig) {
        this.commentedFileConfig = commentedFileConfig;

        File file = new File("GuildSettings.toml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.guildSettings = CommentedFileConfig.of(file);
        this.guildSettings.load();

        load();
    }

    private void load() {
        if (commentedFileConfig.isEmpty())
            throw new NullPointerException("Config is empty!");
        setDiscordToken(commentedFileConfig.get("discord-bot-token"));
        setDropboxToken(commentedFileConfig.get("dropbox-api-token"));
        setSetPresence(commentedFileConfig.get("set-presence"));
        setCommandPrefix(commentedFileConfig.get("command-prefix"));
        setGiphyToken(commentedFileConfig.get("giphy-api-token"));
        setDblToken(commentedFileConfig.get("discord-bot-list-api-token"));
        setVoteNeededForGifCommand(commentedFileConfig.get("vote-needed-for-cat-gif"));
        setCommandLog(commentedFileConfig.get("command-log"));
        setDblVoteAuth(commentedFileConfig.get("vote-token"));
    }

    public String getDiscordToken() {
        return discordToken;
    }

    public void setDiscordToken(String discordToken) {
        this.discordToken = discordToken;
    }

    public boolean isSetPresence() {
        return setPresence;
    }

    public void setSetPresence(boolean setPresence) {
        this.setPresence = setPresence;
    }

    public String getDropboxToken() {
        return dropboxToken;
    }

    public void setDropboxToken(String dropboxToken) {
        this.dropboxToken = dropboxToken;
    }

    public String getCommandPrefix() {
        return commandPrefix;
    }

    public String getCommandPrefix(Guild guild) {
        if (guildSettings.contains(guild.getId() + ".command-prefix"))
            return guildSettings.get(guild.getId() + ".command-prefix");
        return commandPrefix;
    }

    public void setCommandPrefix(String commandPrefix) {
        this.commandPrefix = commandPrefix;
    }

    public void setCommandPrefix(Guild guild, String commandPrefix) {
        if (guildSettings.contains(guild.getId() + ".command-prefix"))
            guildSettings.remove(guild.getId() + ".command-prefix");
        guildSettings.set(guild.getId() + ".command-prefix", commandPrefix);
        guildSettings.save();
    }

    public boolean startsWithCommandPrefix(String contentRaw) {
        if (contentRaw.startsWith(commandPrefix)) return true;
        for (CommentedConfig.Entry entry : guildSettings.entrySet()) {
            if (!(entry.getValue() instanceof CommentedConfig)) continue;
            CommentedConfig commentedConfig = entry.getValue();
            if (!commentedConfig.contains("command-prefix")) continue;
            String value = commentedConfig.get("command-prefix");
            if (contentRaw.startsWith(value))
                return true;
        }
        return false;
    }

    public String removeCommandPrefix(String contentRaw) {
        if (contentRaw.startsWith(commandPrefix)) return contentRaw.substring(commandPrefix.length());
        for (CommentedConfig.Entry entry : guildSettings.entrySet()) {
            if (!(entry.getValue() instanceof CommentedConfig)) continue;
            CommentedConfig commentedConfig = entry.getValue();
            if (!commentedConfig.contains("command-prefix")) continue;
            String value = commentedConfig.get("command-prefix");
            if (contentRaw.startsWith(value))
                return contentRaw.substring(value.length());
        }
        return contentRaw;
    }

    public String getDblToken() {
        return dblToken;
    }

    public void setDblToken(String dblToken) {
        this.dblToken = dblToken;
    }

    public String getGiphyToken() {
        return giphyToken;
    }

    public void setGiphyToken(String giphyToken) {
        this.giphyToken = giphyToken;
    }

    public boolean isVoteNeededForGifCommand() {
        return voteNeededForGifCommand;
    }

    public void setVoteNeededForGifCommand(boolean voteNeededForGifCommand) {
        this.voteNeededForGifCommand = voteNeededForGifCommand;
    }

    public boolean isCommandLog() {
        return commandLog;
    }

    public void setCommandLog(boolean commandLog) {
        this.commandLog = commandLog;
    }

    public String getDblVoteAuth() {
        return dblVoteAuth;
    }

    public void setDblVoteAuth(String dblVoteAuth) {
        this.dblVoteAuth = dblVoteAuth;
    }
}
