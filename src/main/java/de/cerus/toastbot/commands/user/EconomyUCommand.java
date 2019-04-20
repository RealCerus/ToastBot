/*
 * Copyright (c) 2019 Cerus
 * File created at 13.04.19 13:06
 * Last modification: 13.04.19 13:06
 * All rights reserved.
 */

package de.cerus.toastbot.commands.user;

import de.cerus.toastbot.command.UserCommand;
import de.cerus.toastbot.economy.EconomyController;
import de.cerus.toastbot.util.BotChannelUtil;
import de.cerus.toastbot.util.EmoteUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Map;

public class EconomyUCommand extends UserCommand {
    private EconomyController economyController;

    public EconomyUCommand(EconomyController economyController) {
        super("economy");
        setDescription("Shows your breadcrumbs.");
        setUsage(getCommand()+" [@<user>, top]");
        this.economyController = economyController;
    }

    @Override
    public void execute(String usedCommand, Member invoker, Message message, TextChannel channel, String[] args) {
        if (!BotChannelUtil.isBotChannel(channel)) {
            sendNoCommandChannelFailure(channel, invoker.getUser());
            return;
        }

        if (args.length == 1) {
            if(message.getMentionedMembers().size() == 1){
                Member member = message.getMentionedMembers().get(0);
                sendMessage(channel, invoker.getUser(), COLOR_GREEN, member.getUser().getAsTag() + "'s breadcrumbs", member.getAsMention() + "'s breadcrumbs: **" + economyController.getBreadcrumbs(member) + "** " + EmoteUtil.getBreadcrumbEmote());
                return;
            }
            if(args[0].equalsIgnoreCase("top")){
                Map<Long, Long> topFive = economyController.getTopFive(channel.getGuild());
                if(topFive.isEmpty()){
                    sendMessage(channel, invoker.getUser(), COLOR_GREEN, "No members found", "No members were found.");
                    return;
                }
                EmbedBuilder builder = new EmbedBuilder()
                        .setTitle("Top 5 members of guild "+channel.getGuild().getName())
                        .setColor(COLOR_GREEN)
                        .setFooter("Requested by "+invoker.getUser().getAsTag(), invoker.getUser().getAvatarUrl());
                topFive.forEach((id, breadcrumbs) -> builder.addField(channel.getJDA().getUserById(id).getAsTag(), breadcrumbs+" "+EmoteUtil.getBreadcrumbEmote(), false));
                channel.sendMessage(builder.build()).complete();
            }
            return;
        }

        sendMessage(channel, invoker.getUser(), COLOR_GREEN, "Your breadcrumbs", "Your breadcrumbs: **" + economyController.getBreadcrumbs(invoker) + "** " + EmoteUtil.getBreadcrumbEmote());
    }
}
