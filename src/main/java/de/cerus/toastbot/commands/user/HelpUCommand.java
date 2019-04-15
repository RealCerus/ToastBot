/*
 * Copyright (c) 2019 Cerus
 * File created at 12.04.19 02:52
 * Last modification: 12.04.19 02:52
 * All rights reserved.
 */

package de.cerus.toastbot.commands.user;

import de.cerus.toastbot.command.UserCommand;
import de.cerus.toastbot.command.UserCommandReader;
import de.cerus.toastbot.util.BotChannelUtil;
import de.cerus.toastbot.util.HelpPagination;
import de.cerus.toastbot.util.Pagination;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HelpUCommand extends UserCommand {
    private UserCommandReader commandReader;

    public HelpUCommand(UserCommandReader commandReader) {
        super("help");
        this.commandReader = commandReader;
        setUsage(getCommand()+" [page]");
        setDescription("Displays all commands.");
    }

    @Override
    public void execute(String usedCommand, Member invoker, Message message, TextChannel channel, String[] args) {
        if(!BotChannelUtil.isBotChannel(channel.getIdLong())) return;

        int page;
        if(args.length == 0){
            page = 1;
        } else {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored){
                sendFailure(channel, invoker.getUser(), "Please use `"+getSettings().getCommandPrefix(channel.getGuild())+"help [page]`");
                return;
            }
        }
        page--;
        if(page >= commandReader.getHelpPagination().totalPages()){
            page = 0;
        }

        EmbedBuilder builder = new EmbedBuilder()
                .setColor(COLOR_BLUE)
                .setTitle("**Help | Page "+(page+1)+" of "+commandReader.getHelpPagination().totalPages()+"**")
                .setAuthor(channel.getJDA().getSelfUser().getName(), null, channel.getJDA().getSelfUser().getAvatarUrl())
                .setDescription("All available commands are listed below.")
                .setFooter("\u200BToast Bot help", invoker.getUser().getAvatarUrl());
        String commandPrefix = getSettings().getCommandPrefix(channel.getGuild());
        commandReader.getHelpPagination().getPage(page, commandPrefix).forEach(builder::addField);
        Message helpMessage = channel.sendMessage(builder.build()).complete();
        helpMessage.addReaction("U+25c0").complete();
        helpMessage.addReaction("U+25b6").complete();
    }
}
