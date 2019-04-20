/*
 * Copyright (c) 2019 Cerus
 * File created at 14.04.19 12:27
 * Last modification: 14.04.19 12:27
 * All rights reserved.
 */

package de.cerus.toastbot.listeners;

import de.cerus.toastbot.command.UserCommandReader;
import de.cerus.toastbot.settings.Settings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReactionListener extends ListenerAdapter {

    private UserCommandReader userCommandReader;
    private Settings settings;

    public ReactionListener(UserCommandReader userCommandReader, Settings settings) {
        this.userCommandReader = userCommandReader;
        this.settings = settings;
    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        if(event.getMember().getUser().isBot()) return;
        MessageReaction reaction = event.getReaction();
        if(!reaction.getReactionEmote().isEmoji()) return;
        if(!reaction.getReactionEmote().getAsCodepoints().equals("U+25c0") && !reaction.getReactionEmote().getAsCodepoints().equals("U+25b6")) return;
        MessageHistory history;
        try {
            history = event.getChannel().getHistoryAround(event.getMessageId(), 1).complete(true);
        } catch (RateLimitedException e) {
            e.printStackTrace();
            return;
        }
        if(history.isEmpty()) return;
        Message message = history.getMessageById(event.getMessageId());
        if(message.getAuthor().getIdLong() != event.getJDA().getSelfUser().getIdLong()) return;
        if(message.getEmbeds().size() != 1) return;
        MessageEmbed messageEmbed = message.getEmbeds().get(0);
        MessageEmbed.Footer footer = messageEmbed.getFooter();
        if(footer == null) return;
        if(!footer.getText().equals("\u200BToast Bot help")) return;
        int page;
        try {
            page = Integer.parseInt(messageEmbed.getTitle().replace("**Help | Page ", "").replaceAll(" of (.*)\\*\\*", ""));
        } catch (NumberFormatException ignored){
            return;
        }
        boolean nextPage = reaction.getReactionEmote().getAsCodepoints().equals("U+25b6");
        if(nextPage){
            if(userCommandReader.getHelpPagination().totalPages() <= page){
                return;
            }
            EmbedBuilder builder = new EmbedBuilder()
                    .setColor(2127320)
                    .setTitle("**Help | Page "+(page+1)+" of "+userCommandReader.getHelpPagination().totalPages()+"**")
                    .setAuthor(event.getJDA().getSelfUser().getName(), null, event.getJDA().getSelfUser().getAvatarUrl())
                    .setDescription("All available commands are listed below. Total commands: "+userCommandReader.getHelpPagination().size())
                    .setFooter("\u200BToast Bot help", event.getMember().getUser().getAvatarUrl());
            String commandPrefix = settings.getCommandPrefix(event.getGuild());
            userCommandReader.getHelpPagination().getPage(page, commandPrefix).forEach(builder::addField);
            message.editMessage(builder.build()).submit();
        } else {
            if(0 > (page-2)){
                return;
            }
            EmbedBuilder builder = new EmbedBuilder()
                    .setColor(2127320)
                    .setTitle("**Help | Page "+(page-1)+" of "+userCommandReader.getHelpPagination().totalPages()+"**")
                    .setAuthor(event.getJDA().getSelfUser().getName(), null, event.getJDA().getSelfUser().getAvatarUrl())
                    .setDescription("All available commands are listed below. Total commands: "+userCommandReader.getHelpPagination().size())
                    .setFooter("\u200BToast Bot help", event.getMember().getUser().getAvatarUrl());
            String commandPrefix = settings.getCommandPrefix(event.getGuild());
            userCommandReader.getHelpPagination().getPage(page-2, commandPrefix).forEach(builder::addField);
            message.editMessage(builder.build()).submit();
        }
    }
}
