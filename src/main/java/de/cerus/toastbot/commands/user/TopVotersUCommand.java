/*
 * Copyright (c) 2019 Cerus
 * File created at 19.04.19 20:54
 * Last modification: 19.04.19 20:54
 * All rights reserved.
 */

package de.cerus.toastbot.commands.user;

import de.cerus.toastbot.command.UserCommand;
import de.cerus.toastbot.util.BotChannelUtil;
import de.cerus.toastbot.util.TopVoterUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Map;

public class TopVotersUCommand extends UserCommand {
    public TopVotersUCommand() {
        super("top-voters");
        setDescription("Shows the users with the most votes.");
    }

    @Override
    public void execute(String usedCommand, Member invoker, Message message, TextChannel channel, String[] args) {
        if (!BotChannelUtil.isBotChannel(channel)) {
            sendNoCommandChannelFailure(channel, invoker.getUser());
            return;
        }

        Map<Long, Long> topFive = TopVoterUtil.getTopFive();
        if (topFive.isEmpty()) {
            sendMessage(channel, invoker.getUser(), COLOR_GREEN, "No users found", "No users were found.");
            return;
        }
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("Top 5 voters")
                .setColor(COLOR_GREEN)
                .setFooter("Requested by " + invoker.getUser().getAsTag(), invoker.getUser().getAvatarUrl());
        topFive.forEach((id, votes) -> builder.addField(channel.getJDA().getUserById(id).getAsTag(), votes + " votes", false));
        channel.sendMessage(builder.build()).complete();
    }
}
