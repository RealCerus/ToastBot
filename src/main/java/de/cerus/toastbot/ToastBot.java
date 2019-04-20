/*
 * Copyright (c) 2019 Cerus
 * File created at 11.04.19 11:38
 * Last modification: 11.04.19 11:38
 * All rights reserved.
 */

package de.cerus.toastbot;

import at.mukprojects.giphy4j.Giphy;
import de.cerus.dblwebhookapi.WebhookServer;
import de.cerus.toastbot.command.SendThanksTCommand;
import de.cerus.toastbot.command.TerminalCommandReader;
import de.cerus.toastbot.command.UserCommandReader;
import de.cerus.toastbot.commands.terminal.GuildsTCommand;
import de.cerus.toastbot.commands.terminal.HelpTCommand;
import de.cerus.toastbot.commands.terminal.ShutdownTCommand;
import de.cerus.toastbot.commands.user.*;
import de.cerus.toastbot.economy.EconomyController;
import de.cerus.toastbot.listeners.GuildListener;
import de.cerus.toastbot.listeners.PrivateChannelListener;
import de.cerus.toastbot.listeners.ReactionListener;
import de.cerus.toastbot.settings.Settings;
import de.cerus.toastbot.tasks.ActivityTimerTask;
import de.cerus.toastbot.tasks.BotChannelSaverTimerTask;
import de.cerus.toastbot.tasks.StatsTimerTask;
import de.cerus.toastbot.user.ToastBotUser;
import de.cerus.toastbot.user.items.*;
import de.cerus.toastbot.util.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.discordbots.api.client.DiscordBotListAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.File;
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
    /*    private Thread voteChecker;
        private VoteCheckerRunnable voteCheckerRunnable;
        private VoteEventCaller voteEventCaller;*/
    private EconomyController economyController;
    private WebhookServer webhookServer;

    public ToastBot(@Nonnull JDA jda, @Nonnull Settings settings) {
        this.jda = jda;
        this.settings = settings;
        this.logger = LoggerFactory.getLogger(getClass());
        this.terminalCommandReader = new TerminalCommandReader();
        this.userCommandReader = new UserCommandReader(settings);
        this.botListAPI = new DiscordBotListAPI.Builder().token(settings.getDblToken()).botId(jda.getSelfUser().getId()).build();
        //this.voteEventCaller = new VoteEventCaller();
        this.webhookServer = new WebhookServer.Builder().setAuthorization(settings.getDblVoteAuth())
                .setBotId(jda.getSelfUser().getId()).setHost(settings.isDevEnv() ? "localhost" : "lukassp.de").setPort(8065).setSilent(false).build();
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
        TopVoterUtil.initialize(jda);
        startVoteCheck();
        registerItems();

        // Register the only vote listener
        webhookServer.registerListener((userId, botId, isTest, isWeekend) -> {
            User user = jda.getUserById(userId);
            if (user == null) return;
            System.out.print("[Vote] " + user.getAsTag() + " voted");
            TopVoterUtil.addVote(user);
            long votes = TopVoterUtil.getVotes(user);
            ToastBotUser toastBotUser = new ToastBotUser(user);
            if(votes == 10)
                toastBotUser.getInventory().addItem(new BronzeVoteAwardItem(1));
            if(votes == 25)
                toastBotUser.getInventory().addItem(new SilverVoteAwardItem(1));
            if(votes == 50)
                toastBotUser.getInventory().addItem(new GoldenVoteAwardItem(1));
            if(votes == 100)
                toastBotUser.getInventory().addItem(new PlatinumVoteAwardItem(1));
            toastBotUser.save();
            int multiplier = TopVoterUtil.getVotes(user) == 5 ? 5 : TopVoterUtil.getVotes(user) == 15 ?
                    10 : TopVoterUtil.getVotes(user) == 30 ? 20 : TopVoterUtil.getVotes(user) >= 60 ? 50 : 0;
            economyController.addBreadcrumbs(user, isWeekend ? 20 + multiplier : 10 + multiplier);
            try {
                user.openPrivateChannel().complete().sendMessage(
                        VoteUtil.getThankYouMessage(user, isWeekend, multiplier)
                ).complete();
                System.out.println();
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
                new CatGifUCommand(botListAPI, giphy, economyController),
                new EconomyUCommand(economyController),
                new SearchGifUCommand(giphy),
                new VoteUCommand(),
                new ToastBattleUCommand(),
                new TopVotersUCommand(),
                new UpgradeHpUCommand(economyController),
                new UserInfoUCommand(economyController),
                new ItemInfoUCommand(),
                new UseItemUCommand(),
                new SellItemUCommand(economyController)
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

    private void registerItems() {
        GlobalItemRegistry.registerItems(
                new BronzeVoteAwardItem(1),
                new SilverVoteAwardItem(1),
                new GoldenVoteAwardItem(1),
                new PlatinumVoteAwardItem(1)
        );
    }

    private void registerEventListeners(ListenerAdapter... adapters) {
        for (ListenerAdapter adapter : adapters) {
            jda.addEventListener(adapter);
        }
    }

    public void startVoteCheck() {
        /*voteCheckerRunnable = new VoteCheckerRunnable(jda, botListAPI, voteEventCaller);
        voteChecker = new Thread(voteCheckerRunnable);
        voteChecker.start();*/
        webhookServer.start();
    }

    public void shutdown() {
        if (presenceTimer != null)
            presenceTimer.cancel();
        botChannelSaver.cancel();
        statsTimer.cancel();
/*        if (voteCheckerRunnable != null) {
            voteCheckerRunnable.setInterrupted(true);
            voteCheckerRunnable.shutdown();
        }
        if (voteChecker != null)
            voteChecker.interrupt();*/
        terminalCommandReader.shutdown();
        userCommandReader.shutdown();
        BotChannelUtil.shutdown();
        try {
            webhookServer.stop();
        } catch (Exception ignored) {
        }
        economyController.shutdown();

        jda.shutdown();
    }
}
