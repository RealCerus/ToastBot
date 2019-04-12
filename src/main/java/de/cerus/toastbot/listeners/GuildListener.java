/*
 * Copyright (c) 2019 Cerus
 * File created at 11.04.19 11:38
 * Last modification: 11.04.19 11:38
 * All rights reserved.
 */

package de.cerus.toastbot.listeners;

import de.cerus.toastbot.settings.Settings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class GuildListener extends ListenerAdapter {

    private Settings settings;

    public GuildListener(Settings settings) {
        this.settings = settings;
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        Guild guild = event.getGuild();
        System.out.println("[+] Bot joined guild "+guild.getName()+"!");

        User owner = guild.getOwner().getUser();

        MessageEmbed messageEmbed = new EmbedBuilder()
                .setColor(Color.ORANGE)
                .setTitle("Hello!")
                .setImage(guild.getJDA().getSelfUser().getAvatarUrl())
                .setDescription("Hi! I'm Toast Bot. In order to use me you need to set at least one bot channel. " +
                        "To do that, go into a text channel and type `"+settings.getCommandPrefix(guild)+"bot-channel add` " +
                        "(Note: You need the 'Manage channels' permission!). Now you and your members can use my commands! " +
                        ":bread:\nOh, I forgot to tell you something: Did you know you can change my command prefix for your " +
                        "guild? Type `"+settings.getCommandPrefix(guild)+"set-prefix <new prefix>` to change it (Requires 'Manage channels' permission)!")
                .build();
        try {
            owner.openPrivateChannel().complete().sendMessage(messageEmbed).complete();
        } catch (Exception ignored){
            guild.getSystemChannel().sendMessage(messageEmbed).complete();
        }
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        Guild guild = event.getGuild();
        System.out.println("[-] Bot left guild "+guild.getName()+".");
    }
}
