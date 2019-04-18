/*
 * Copyright (c) 2019 Cerus
 * File created at 12.04.19 19:40
 * Last modification: 12.04.19 19:40
 * All rights reserved.
 */

package de.cerus.toastbot.commands.user;

import de.cerus.toastbot.command.UserCommand;
import de.cerus.toastbot.util.BotChannelUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class SetPrefixUCommand extends UserCommand {
    public SetPrefixUCommand() {
        super("set-prefix");
        setDescription("Allows admins to change my command prefix for this guild.");
        setUsage(getCommand() + " <new prefix>");
    }

    @Override
    public void execute(String usedCommand, Member invoker, Message message, TextChannel channel, String[] args) {
        if (!BotChannelUtil.isBotChannel(channel.getIdLong())) {
            sendNoCommandChannelFailure(channel, invoker.getUser());
            return;
        }

        if(!invoker.hasPermission(Permission.MANAGE_CHANNEL)){
            sendFailure(channel, invoker.getUser(), "Sorry, it seems like you don't have the required permissions (Manage channels) for this action.");
            return;
        }
        if(args.length >= 1){
            StringBuilder prefix = new StringBuilder();
            for(int i = 0; i < args.length; i++)
                prefix.append(" ").append(args[i]);
            getSettings().setCommandPrefix(channel.getGuild(), prefix.toString().substring(1));
            sendSuccess(channel, invoker.getUser(), "My command prefix for this guild was changed to `"+getSettings().getCommandPrefix(channel.getGuild())+"`!");
            return;
        }

        sendMessage(channel, invoker.getUser(), COLOR_GREEN, "Command '"+usedCommand+"'",
                "Correct usage:\n`"+getSettings().getCommandPrefix(channel.getGuild())+"set-prefix <new prefix>`");
    }
}
