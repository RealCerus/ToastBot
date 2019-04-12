/*
 * Copyright (c) 2019 Cerus
 * File created at 11.04.19 11:38
 * Last modification: 11.04.19 11:38
 * All rights reserved.
 */

package de.cerus.toastbot;

import de.cerus.toastbot.command.TerminalCommandReader;
import de.cerus.toastbot.command.UserCommandReader;
import de.cerus.toastbot.commands.terminal.GuildsTCommand;
import de.cerus.toastbot.commands.terminal.HelpTCommand;
import de.cerus.toastbot.commands.terminal.ShutdownTCommand;
import de.cerus.toastbot.commands.user.*;
import de.cerus.toastbot.listeners.GuildListener;
import de.cerus.toastbot.settings.Settings;
import de.cerus.toastbot.tasks.ActivityTimerTask;
import de.cerus.toastbot.util.BotChannelUtil;
import de.cerus.toastbot.util.EmbedUtil;
import de.cerus.toastbot.util.ImageUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.discordbots.api.client.DiscordBotListAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Timer;

public class ToastBot {

    private JDA jda;
    private Settings settings;

    private Logger logger;
    private Timer timer;
    private TerminalCommandReader terminalCommandReader;
    private UserCommandReader userCommandReader;
    private DiscordBotListAPI botListAPI;

    public ToastBot(@Nonnull JDA jda, @Nonnull Settings settings) {
        this.jda = jda;
        this.settings = settings;
        this.logger = LoggerFactory.getLogger(getClass());
        this.terminalCommandReader = new TerminalCommandReader();
        this.userCommandReader = new UserCommandReader(settings);
        //this.botListAPI = new DiscordBotListAPI.Builder().token(settings.getDblToken()).botId(jda.getSelfUser().getId()).build();
    }

    public void launch() {
        // Load everything
        if (settings.isSetPresence())
            this.timer = ActivityTimerTask.startNew(jda, settings);
        ImageUtil.load();
        EmbedUtil.initialize(settings);
        BotChannelUtil.initialize();

        // Register all commands that can be executed from console
        terminalCommandReader.registerCommands(
                new HelpTCommand(terminalCommandReader),
                new GuildsTCommand(jda),
                new ShutdownTCommand(this)
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
                new CatGifUCommand(botListAPI)
        );
        userCommandReader.start(jda);

        // Register listener adapters
        registerEventListeners(
                new GuildListener(settings)
        );

        logger.info("The launch of Toast Bot is now complete.");
    }

    private void registerEventListeners(ListenerAdapter... adapters) {
        for (ListenerAdapter adapter : adapters) {
            jda.addEventListener(adapter);
        }
    }

    public void shutdown() {
        if (timer != null)
            timer.cancel();
        terminalCommandReader.shutdown();
        userCommandReader.shutdown();
        BotChannelUtil.shutdown();

        jda.shutdown();
    }
}
