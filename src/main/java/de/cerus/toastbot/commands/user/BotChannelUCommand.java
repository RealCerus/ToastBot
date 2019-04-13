/*
 * Copyright (c) 2019 Cerus
 * File created at 12.04.19 18:45
 * Last modification: 12.04.19 18:45
 * All rights reserved.
 */

package de.cerus.toastbot.commands.user;

import de.cerus.toastbot.command.UserCommand;
import de.cerus.toastbot.util.BotChannelUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class BotChannelUCommand extends UserCommand {
    public BotChannelUCommand() {
        super("bot-channel");
        setDescription("Allows admins to allow / deny the use of my commands in certain channels.");
        setUsage(getCommand() + " [add, remove]");
    }

    @Override
    public void execute(String usedCommand, Member invoker, Message message, TextChannel channel, String[] args) {
        if (!invoker.hasPermission(Permission.MANAGE_CHANNEL)) return;

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("add")) {
                if (!BotChannelUtil.isBotChannel(channel.getIdLong())) {
                    BotChannelUtil.addBotChannel(channel.getIdLong());
                    sendSuccess(channel, invoker.getUser(), "My commands can now be executed in this channel!");
                    return;
                } else sendFailure(channel, invoker.getUser(), "This channel is already added as a bot channel.");
                return;
            }

            if (args[0].equalsIgnoreCase("remove")) {
                if (BotChannelUtil.isBotChannel(channel.getIdLong())) {
                    BotChannelUtil.removeBotChannel(channel.getIdLong());
                    sendSuccess(channel, invoker.getUser(), "My commands can no longer be executed in this channel!");
                    return;
                } else sendFailure(channel, invoker.getUser(), "This channel is not added as a bot channel.");
                return;
            }
        }

        sendMessage(channel, invoker.getUser(), COLOR_GREEN, "Command '" + usedCommand + "'", "Correct usage:\n`" + getSettings().getCommandPrefix(channel.getGuild()) + "bot-channel [add, remove]`");
    }
}
