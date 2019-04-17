/*
 * Copyright (c) 2019 Cerus
 * File created at 12.04.19 02:26
 * Last modification: 12.04.19 02:26
 * All rights reserved.
 */

package de.cerus.toastbot.commands.user;

import de.cerus.toastbot.command.UserCommand;
import de.cerus.toastbot.util.BotChannelUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

public class CreditsUCommand extends UserCommand {
    public CreditsUCommand() {
        super("credits");
        setDescription("Lists the credits for this bot.");
    }

    @Override
    public void execute(String usedCommand, Member invoker, Message message, TextChannel channel, String[] args) {
        if(!BotChannelUtil.isBotChannel(channel.getIdLong())) return;

        MessageEmbed builder = new EmbedBuilder()
                .setColor(COLOR_BLUE)
                .setTitle("**Credits of Toast Bot**")
                .setAuthor(channel.getJDA().getSelfUser().getName(), null, channel.getJDA().getSelfUser().getAvatarUrl())
                .setDescription("All credits are listed below. :bread:")
                .setThumbnail(channel.getJDA().getSelfUser().getAvatarUrl())
                .addField("Developer & Maintainer", channel.getJDA().getUserById(347018538713874444L).getAsMention(), false)
                .addField("'Toastify' background", "Photo by [Polina Rytova](https://unsplash.com/photos/1dGMs4hhcVA?utm_source=unsplash&utm_medium=referral&utm_content=creditCopyText) on [Unsplash](https://unsplash.com/?utm_source=unsplash&utm_medium=referral&utm_content=creditCopyText)", false)
                .addField("Discord Library", "[Java Discord API (JDA)](https://github.com/DV8FromTheWorld/JDA)", false)
                .addField("Toast Bot Avatar", "[Clipart by Kiss PNG](https://de.kisspng.com/png-voe087/)", false)
                .addField("File host", "Using [DropBox](https://dropbox.com/) to host the generated images", false)
                .addField("Gifs", "Using the gifs available at [Giphy](https://giphy.com/)", false)
                .addField("Tester & Helper", channel.getJDA().getUserById(325957450807115776L).getAsMention()+", "+channel.getJDA().getUserById(332142165402714113L).getAsMention(), false)
                .addField("Server provider", channel.getJDA().getUserById(332142165402714113L).getAsMention(), false)
                .build();
        channel.sendMessage(builder).complete();
    }
}
