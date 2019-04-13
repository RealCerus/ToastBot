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
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class EconomyUCommand extends UserCommand {
    private EconomyController economyController;
    private Boolean isInGuild;

    public EconomyUCommand(EconomyController economyController) {
        super("economy");
        setDescription("Shows your breadcrumbs. This command is currently useless and you currently cant gain breadcrumbs.");
        this.economyController = economyController;
    }

    @Override
    public void execute(String usedCommand, Member invoker, Message message, TextChannel channel, String[] args) {
        if(!BotChannelUtil.isBotChannel(channel.getIdLong())) return;

        if(isInGuild == null)
            isInGuild = channel.getJDA().getGuilds().stream().filter(guild -> guild.getId().equals("565825337108463626")).iterator().hasNext();

        if(args.length == 1 && message.getMentionedMembers().size() == 1){
            Member member = message.getMentionedMembers().get(0);
            sendMessage(channel, invoker.getUser(), COLOR_GREEN, member.getUser().getAsTag()+"'s breadcrumbs", member.getAsMention()+"'s breadcrumbs: **"+economyController.getBreadcrumbs(member)+"** "+getEmoji(channel.getJDA()));
            return;
        }

        sendMessage(channel, invoker.getUser(), COLOR_GREEN, "Your breadcrumbs", "Your breadcrumbs: **"+economyController.getBreadcrumbs(invoker)+"** "+getEmoji(channel.getJDA()));
    }

    private String getEmoji(JDA jda) {
        if(isInGuild)
            return "<:breadcrumb:566584331112415235>";
        else return ":bread:";
    }
}
