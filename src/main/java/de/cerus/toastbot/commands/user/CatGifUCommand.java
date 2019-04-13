/*
 * Copyright (c) 2019 Cerus
 * File created at 13.04.19 00:23
 * Last modification: 13.04.19 00:23
 * All rights reserved.
 */

package de.cerus.toastbot.commands.user;

import at.mukprojects.giphy4j.Giphy;
import at.mukprojects.giphy4j.entity.search.SearchRandom;
import at.mukprojects.giphy4j.exception.GiphyException;
import de.cerus.toastbot.command.UserCommand;
import de.cerus.toastbot.util.BotChannelUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.discordbots.api.client.DiscordBotListAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CatGifUCommand extends UserCommand {
    private static final int COOLDOWN = 15 * 1000;

    private List<Long> cooldown;
    private DiscordBotListAPI botListAPI;
    private Giphy giphy;

    public CatGifUCommand(DiscordBotListAPI botListAPI) {
        super("cat-gif");
        setDescription("Sends a random cat gif from Giphy.");
        this.cooldown = new ArrayList<>();
        this.botListAPI = botListAPI;
    }

    @Override
    public void onRegistration() {
        this.giphy = new Giphy(getSettings().getGiphyToken());
    }

    @Override
    public void execute(String usedCommand, Member invoker, Message message, TextChannel channel, String[] args) {
        if (!BotChannelUtil.isBotChannel(channel.getIdLong())) return;
        if (args.length != 0) {
            sendMessage(channel, invoker.getUser(), COLOR_GREEN, "Command '" + usedCommand + "'", "Correct usage:\n`" + getSettings().getCommandPrefix(channel.getGuild()) + "cat-gif`");
            return;
        }
        if (cooldown.contains(invoker.getIdLong())) {
            sendFailure(channel, invoker.getUser(), "Please wait before you execute this command again!");
            return;
        }
        cooldown.add(invoker.getIdLong());

        botListAPI.hasVoted(invoker.getId()).whenComplete((hasVoted, e) -> {
            if(getSettings().isVoteNeededForGifCommand()){
                if(e != null) {
                    sendFailure(channel, invoker.getUser(), "Failed to verify your vote. Please try again later.");
                    return;
                }
                if(!hasVoted){
                    sendFailure(channel, invoker.getUser(), "This command is for voters only! Vote [here](https://discordbots.org/bot/565579372128501776)");
                    return;
                }
            }

            try {
                SearchRandom data = giphy.searchRandom("cat");
                channel.sendMessage(
                        new EmbedBuilder()
                                .setColor(COLOR_GREEN)
                                .setTitle("Cat GIF", data.getData().getUrl())
                                .setDescription("Here is your cat gif!")
                                .setImage(data.getData().getImageUrl())
                                .setFooter("Powered by Giphy", "https://img.cerus-dev.de/giphy.gif")
                                .build()
                ).complete();
            } catch (GiphyException ex) {
                ex.printStackTrace();
                sendFailure(channel, invoker.getUser(), "Failed to load a gif");
            }
        });

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                cooldown.remove(invoker.getIdLong());
            }
        }, COOLDOWN);
    }
}
