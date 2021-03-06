/*
 * Copyright (c) 2019 Cerus
 * File created at 11.04.19 23:24
 * Last modification: 11.04.19 23:24
 * All rights reserved.
 */

package de.cerus.toastbot.commands.user;

import de.cerus.toastbot.command.UserCommand;
import de.cerus.toastbot.util.BotChannelUtil;
import de.cerus.toastbot.util.EmbedUtil;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ToastifyUCommand extends UserCommand {
    private static final int COOLDOWN = 30 * 1000;

    private List<Long> cooldown;

    public ToastifyUCommand() {
        super("toastify");
        cooldown = new ArrayList<>();
        setDescription("Shows the percentage of a user of being a toast.");
        setUsage(getCommand() + " @<user>");
    }

    @Override
    public void execute(String usedCommand, Member invoker, Message message, TextChannel channel, String[] args) {
        if (!BotChannelUtil.isBotChannel(channel)) {
            sendNoCommandChannelFailure(channel, invoker.getUser());
            return;
        }

        if (args.length != 1) {
            sendMessage(channel, invoker.getUser(), COLOR_GREEN, "Command '" + usedCommand + "'",
                    "Correct usage:\n`" + getSettings().getCommandPrefix(channel.getGuild()) + "toastify @<user>`");
            return;
        }
        if (message.getMentionedMembers().size() != 1) {
            sendMessage(channel, invoker.getUser(), COLOR_GREEN, "Command '" + usedCommand + "'",
                    "Correct usage:\n`" + getSettings().getCommandPrefix(channel.getGuild()) + "toastify @<user>`\nYou'll need to mention one member.");
            return;
        }
        if (cooldown.contains(invoker.getIdLong())) {
            sendFailure(channel, invoker.getUser(), "Please wait before you execute this command again!");
            return;
        }
        cooldown.add(invoker.getIdLong());

        Member member = message.getMentionedMembers().get(0);
        EmbedUtil.sendToastify(member.getUser(), channel);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                cooldown.remove(invoker.getIdLong());
            }
        }, COOLDOWN);
    }
}
