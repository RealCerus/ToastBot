/*
 * Copyright (c) 2019 Cerus
 * File created at 13.04.19 13:42
 * Last modification: 13.04.19 13:42
 * All rights reserved.
 */

package de.cerus.toastbot.tasks;

import net.dv8tion.jda.api.JDA;
import org.discordbots.api.client.DiscordBotListAPI;

import java.util.Timer;
import java.util.TimerTask;

public class StatsTimerTask extends TimerTask {

    public static Timer createNew(JDA jda, DiscordBotListAPI discordBotListAPI){
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new StatsTimerTask(discordBotListAPI, jda), 0, (2*60)*1000);
        return timer;
    }

    private DiscordBotListAPI discordBotListAPI;
    private JDA jda;

    public StatsTimerTask(DiscordBotListAPI discordBotListAPI, JDA jda) {
        this.discordBotListAPI = discordBotListAPI;
        this.jda = jda;
    }

    @Override
    public void run() {
        try {
            //discordBotListAPI.setStats(jda.getGuilds().size());
        } catch (Exception ignored){
        }
    }
}
