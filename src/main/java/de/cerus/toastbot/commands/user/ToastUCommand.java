/*
 * Copyright (c) 2019 Cerus
 * File created at 11.04.19 22:21
 * Last modification: 11.04.19 22:21
 * All rights reserved.
 */

package de.cerus.toastbot.commands.user;

import de.cerus.toastbot.command.UserCommand;
import de.cerus.toastbot.util.BotChannelUtil;
import de.cerus.toastbot.util.EmbedUtil;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ToastUCommand extends UserCommand {
    private static final int COOLDOWN = 30 * 1000;

    private List<Long> cooldown;

    public ToastUCommand() {
        super("toast");
        cooldown = new ArrayList<>();
        setDescription("'Toasts' a user's avatar or his name.");
        setUsage(getCommand() + " @<user> [-name]");
    }

    @Override
    public void execute(String usedCommand, Member invoker, Message message, TextChannel channel, String[] args) {
        if (!BotChannelUtil.isBotChannel(channel)) {
            sendNoCommandChannelFailure(channel, invoker.getUser());
            return;
        }

        if (args.length == 0) {
            sendMessage(channel, invoker.getUser(), COLOR_GREEN, "Command '" + usedCommand + "'",
                    "Correct usage:\n" + getSettings().getCommandPrefix(channel.getGuild()) + "`toast @<user> [-name]`");
            return;
        }
        if (message.getMentionedMembers().size() != 1) {
            sendMessage(channel, invoker.getUser(), COLOR_GREEN, "Command '" + usedCommand + "'",
                    "Correct usage:\n" + getSettings().getCommandPrefix(channel.getGuild()) + "`toast @<user> [-name]`\nYou'll need to mention one member.");
            return;
        }
        if (cooldown.contains(invoker.getIdLong())) {
            sendFailure(channel, invoker.getUser(), "Please wait before you execute this command again!");
            return;
        }
        cooldown.add(invoker.getIdLong());

        Member member = message.getMentionedMembers().get(0);
        EmbedUtil.sendToast(member.getUser(), channel, args.length >= 2 && args[1].equalsIgnoreCase("-name"));

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                cooldown.remove(invoker.getIdLong());
            }
        }, COOLDOWN);
    }
}
