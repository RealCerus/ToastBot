/*
 * Copyright (c) 2019 Cerus
 * File created at 12.04.19 02:52
 * Last modification: 12.04.19 02:52
 * All rights reserved.
 */

package de.cerus.toastbot.commands.user;

import de.cerus.toastbot.command.UserCommand;
import de.cerus.toastbot.command.UserCommandReader;
import de.cerus.toastbot.util.BotChannelUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class HelpUCommand extends UserCommand {
    private UserCommandReader commandReader;

    public HelpUCommand(UserCommandReader commandReader) {
        super("help");
        this.commandReader = commandReader;
        setDescription("Displays all commands.");
    }

    @Override
    public void execute(String usedCommand, Member invoker, Message message, TextChannel channel, String[] args) {
        if(!BotChannelUtil.isBotChannel(channel.getIdLong())) return;

        EmbedBuilder builder = new EmbedBuilder()
                .setColor(COLOR_BLUE)
                .setTitle("**Help**")
                .setAuthor(channel.getJDA().getSelfUser().getName(), null, channel.getJDA().getSelfUser().getAvatarUrl())
                .setDescription("All available commands are listed below.")
                .setFooter("Requested by " + invoker.getUser().getAsTag(), invoker.getUser().getAvatarUrl());
        String commandPrefix = getSettings().getCommandPrefix(channel.getGuild());
        commandReader.getCommands().forEach(userCommand -> builder.addField(commandPrefix + (userCommand.getUsage().equals("") ? userCommand.getCommand() : userCommand.getUsage()), userCommand.getDescription(), false));
        channel.sendMessage(builder.build()).complete();
    }
}
