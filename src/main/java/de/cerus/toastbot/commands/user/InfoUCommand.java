/*
 * Copyright (c) 2019 Cerus
 * File created at 12.04.19 18:07
 * Last modification: 12.04.19 18:07
 * All rights reserved.
 */

package de.cerus.toastbot.commands.user;

import de.cerus.toastbot.command.UserCommand;
import de.cerus.toastbot.util.AppPropertiesReader;
import de.cerus.toastbot.util.BotChannelUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

public class InfoUCommand extends UserCommand {
    public InfoUCommand() {
        super("info");
        setDescription("Show some information about me.");
    }

    @Override
    public void execute(String usedCommand, Member invoker, Message message, TextChannel channel, String[] args) {
        if (!BotChannelUtil.isBotChannel(channel.getIdLong())) return;

        int guilds = channel.getJDA().getGuilds().size();
        MessageEmbed messageEmbed = new EmbedBuilder()
                .setColor(COLOR_BLUE)
                .setImage(channel.getJDA().getSelfUser().getAvatarUrl())
                .setTitle("**Toast Bot v" + AppPropertiesReader.getVersion() + " info**", "https://discord.gg/ddX3eSf")
                .addField("Bot ID", channel.getJDA().getSelfUser().getId(), false)
                .addField("Guilds using me", guilds + " guild" + (guilds == 0 || guilds > 1 ? "s" : ""), false)
                .addField("Creator", channel.getJDA().getUserById(347018538713874444L).getAsMention(), false)
                .addField("Support discord", "https://discord.gg/ddX3eSf", false)
                .addField("Help command", "`" + getSettings().getCommandPrefix(channel.getGuild()) + "help`", false)
                .addField("More info", "[Click here](https://discordbots.org/bot/565579372128501776)", false)
                .build();
        channel.sendMessage(messageEmbed).complete();
    }
}
