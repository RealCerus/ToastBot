/*
 * Copyright (c) 2019 Cerus
 * File created at 11.04.19 11:38
 * Last modification: 11.04.19 11:38
 * All rights reserved.
 */

package de.cerus.toastbot;

import at.mukprojects.giphy4j.Giphy;
import de.cerus.toastbot.command.SendThanksTCommand;
import de.cerus.toastbot.command.TerminalCommandReader;
import de.cerus.toastbot.command.UserCommandReader;
import de.cerus.toastbot.commands.terminal.GuildsTCommand;
import de.cerus.toastbot.commands.terminal.HelpTCommand;
import de.cerus.toastbot.commands.terminal.ShutdownTCommand;
import de.cerus.toastbot.commands.user.*;
import de.cerus.toastbot.economy.EconomyController;
import de.cerus.toastbot.event.VoteEventCaller;
import de.cerus.toastbot.listeners.GuildListener;
import de.cerus.toastbot.listeners.PrivateChannelListener;
import de.cerus.toastbot.listeners.ReactionListener;
import de.cerus.toastbot.settings.Settings;
import de.cerus.toastbot.tasks.ActivityTimerTask;
import de.cerus.toastbot.tasks.BotChannelSaverTimerTask;
import de.cerus.toastbot.tasks.StatsTimerTask;
import de.cerus.toastbot.tasks.VoteCheckerRunnable;
import de.cerus.toastbot.util.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;
import org.discordbots.api.client.DiscordBotListAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;

public class ToastBot {

    private JDA jda;
    private Settings settings;

    private Logger logger;
    private Timer presenceTimer;
    private Timer botChannelSaver;
    private Timer statsTimer;
    private TerminalCommandReader terminalCommandReader;
    private UserCommandReader userCommandReader;
    private DiscordBotListAPI botListAPI;
    private Thread voteChecker;
    private VoteCheckerRunnable voteCheckerRunnable;
    private VoteEventCaller voteEventCaller;
    private EconomyController economyController;

    public ToastBot(@Nonnull JDA jda, @Nonnull Settings settings) {
        this.jda = jda;
        this.settings = settings;
        this.logger = LoggerFactory.getLogger(getClass());
        this.terminalCommandReader = new TerminalCommandReader();
        this.userCommandReader = new UserCommandReader(settings);
        this.botListAPI = new DiscordBotListAPI.Builder().token(settings.getDblToken()).botId(jda.getSelfUser().getId()).build();
        this.voteEventCaller = new VoteEventCaller();
    }

    public void launch() {
        // Load everything
        if (settings.isSetPresence())
            this.presenceTimer = ActivityTimerTask.startNew(jda, settings);
        ImageUtil.load();
        EmbedUtil.initialize(settings);
        BotChannelUtil.initialize();
        this.botChannelSaver = BotChannelSaverTimerTask.startNew();
        this.statsTimer = StatsTimerTask.createNew(jda, botListAPI);
        economyController = new EconomyController(new File("./Economy.toml"));
        Giphy giphy = new Giphy(settings.getGiphyToken());
        EmoteUtil.initialize(jda);
        VoteUtil.initialize(economyController, giphy);
        startVoteCheck();

        // Register the only vote listener
        voteEventCaller.registerListener((member, guild, isWeekend) -> {
            System.out.print("[Vote] " + member.getUser().getAsTag() + " voted");
            economyController.addBreadcrumbs(member, isWeekend ? 10 : 5);
            try {
                member.getUser().openPrivateChannel().complete().sendMessage(
                        VoteUtil.getThankYouMessage(member.getUser(), isWeekend)
                ).complete();
                System.out.println("\n");
            } catch (Exception ignored) {
                System.out.print(" | failed to send DM\n");
            }
        });

        // Register all commands that can be executed from console
        terminalCommandReader.registerCommands(
                new HelpTCommand(terminalCommandReader),
                new GuildsTCommand(jda),
                new ShutdownTCommand(this),
                new SendThanksTCommand(jda)/* <- This command should be used for testing only */
        );
        terminalCommandReader.start();

        // Register all commands Discord users can use
        userCommandReader.registerCommands(
                new HelpUCommand(userCommandReader),
                new ToastUCommand(),
                new ToastifyUCommand(),
                new CreditsUCommand(),
                new InfoUCommand(),
                new BotChannelUCommand(),
                new SetPrefixUCommand(),
                new CatGifUCommand(botListAPI, giphy),
                new EconomyUCommand(economyController),
                new SearchGifUCommand(giphy),
                new VoteUCommand(),
                new ToastBattleUCommand()
        );
        userCommandReader.start(jda);

        // Register listener adapters
        registerEventListeners(
                new GuildListener(settings),
                new ReactionListener(userCommandReader, settings),
                new PrivateChannelListener(terminalCommandReader)
        );

        logger.info("The launch of Toast Bot is now complete.");
    }

    private void registerEventListeners(ListenerAdapter... adapters) {
        for (ListenerAdapter adapter : adapters) {
            jda.addEventListener(adapter);
        }
    }

    public void startVoteCheck() {
        voteCheckerRunnable = new VoteCheckerRunnable(jda, botListAPI, voteEventCaller);
        voteChecker = new Thread(voteCheckerRunnable);
        voteChecker.start();
    }

    public void shutdown() {
        if (presenceTimer != null)
            presenceTimer.cancel();
        botChannelSaver.cancel();
        statsTimer.cancel();
        if (voteCheckerRunnable != null) {
            voteCheckerRunnable.setInterrupted(true);
            voteCheckerRunnable.shutdown();
        }
        if (voteChecker != null)
            voteChecker.interrupt();
        terminalCommandReader.shutdown();
        userCommandReader.shutdown();
        BotChannelUtil.shutdown();
        economyController.shutdown();

        jda.shutdown();
    }
}
