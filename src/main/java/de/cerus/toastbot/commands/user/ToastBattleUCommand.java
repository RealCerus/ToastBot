/*
 * Copyright (c) 2019 Cerus
 * File created at 17.04.19 22:41
 * Last modification: 17.04.19 22:41
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

public class ToastBattleUCommand extends UserCommand {
    private static final int COOLDOWN = 30 * 1000;

    private List<Long> cooldown = new ArrayList<>();

    public ToastBattleUCommand() {
        super("toastbattle");
        setUsage(getCommand() + " @<user> [@<user 2>]");
        setDescription("Starts a battle between you / someone else and another user.");
    }

    @Override
    public void execute(String usedCommand, Member invoker, Message message, TextChannel channel, String[] args) {
        if (!BotChannelUtil.isBotChannel(channel.getIdLong())) return;

        if (args.length == 0) {
            sendMessage(channel, invoker.getUser(), COLOR_GREEN, "Command '" + usedCommand + "'",
                    "Correct usage:\n" + getSettings().getCommandPrefix(channel.getGuild()) + "`toastbattle @<user> [@<user 2>]`");
            return;
        }
        if (message.getMentionedMembers().size() != 1 && message.getMentionedMembers().size() != 2) {
            sendMessage(channel, invoker.getUser(), COLOR_GREEN, "Command '" + usedCommand + "'",
                    "Correct usage:\n" + getSettings().getCommandPrefix(channel.getGuild()) + "`toastbattle @<user> [@<user 2>]`\nYou'll need to mention one member.");
            return;
        }
        if (cooldown.contains(invoker.getIdLong())) {
            sendFailure(channel, invoker.getUser(), "Please wait before you execute this command again!");
            return;
        }
        cooldown.add(invoker.getIdLong());

        Member member = message.getMentionedMembers().get(0);

        if (message.getMentionedMembers().size() == 2) {
            EmbedUtil.sendToastBattle(member.getUser(), message.getMentionedMembers().get(1).getUser(), channel);
        } else
            EmbedUtil.sendToastBattle(invoker.getUser(), member.getUser(), channel);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                cooldown.remove(invoker.getIdLong());
            }
        }, COOLDOWN);
    }
}
