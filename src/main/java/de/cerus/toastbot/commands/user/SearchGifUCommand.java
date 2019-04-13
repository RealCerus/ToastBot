/*
 * Copyright (c) 2019 Cerus
 * File created at 13.04.19 16:28
 * Last modification: 13.04.19 16:28
 * All rights reserved.
 */

package de.cerus.toastbot.commands.user;

import at.mukprojects.giphy4j.Giphy;
import at.mukprojects.giphy4j.entity.search.SearchFeed;
import at.mukprojects.giphy4j.exception.GiphyException;
import de.cerus.toastbot.command.UserCommand;
import de.cerus.toastbot.util.BotChannelUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SearchGifUCommand extends UserCommand {
    private static final int COOLDOWN = 15 * 1000;

    private List<Long> cooldown;
    private Giphy giphy;

    public SearchGifUCommand() {
        super("search-gif");
        setUsage(getCommand()+" <query>");
        setDescription("Shows you 5 gifs based on your search query.");
        cooldown = new ArrayList<>();
    }

    @Override
    public void onRegistration() {
        this.giphy = new Giphy(getSettings().getGiphyToken());
    }

    @Override
    public void execute(String usedCommand, Member invoker, Message message, TextChannel channel, String[] args) {
        if (!BotChannelUtil.isBotChannel(channel.getIdLong())) return;
        if (cooldown.contains(invoker.getIdLong())) {
            sendFailure(channel, invoker.getUser(), "Please wait before you execute this command again!");
            return;
        }

        if(args.length == 0){
            sendMessage(channel, invoker.getUser(), COLOR_GREEN, "Command '"+usedCommand+"'", "Correct usage:\n`"+getSettings().getCommandPrefix(channel.getGuild())+"search-gif <query>`");
            return;
        }
        cooldown.add(invoker.getIdLong());

        StringBuilder search = new StringBuilder();
        for(int i = 0; i < args.length; i++)
            search.append(" ").append(args[i]);

        try {
            SearchFeed searchFeed = giphy.search(search.toString().trim(), 5, 0);
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setColor(COLOR_GREEN)
                    .setTitle("Search results")
                    .setFooter("Powered by Giphy", "https://img.cerus-dev.de/giphy.gif");
            searchFeed.getDataList().forEach(giphyData -> embedBuilder.addField(giphyData.getSlug(), "[Click here]("+giphyData.getUrl()+")", false));
            channel.sendMessage(embedBuilder.build()).complete();
        } catch (GiphyException e) {
            e.printStackTrace();
            sendFailure(channel, invoker.getUser(), "Failed to search for gifs matching "+search.toString().trim()+".");
        }

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                cooldown.remove(invoker.getIdLong());
            }
        }, COOLDOWN);
    }
}
