/*
 * Copyright (c) 2019 Cerus
 * File created at 11.04.19 11:38
 * Last modification: 11.04.19 11:38
 * All rights reserved.
 */

package de.cerus.toastbot.tasks;

import de.cerus.toastbot.settings.Settings;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.managers.Presence;

import javax.annotation.Nonnull;
import java.util.Timer;
import java.util.TimerTask;

public class ActivityTimerTask extends TimerTask {

    public static Timer startNew(JDA jda, Settings settings){
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new ActivityTimerTask(jda, settings), 0L, 15*1000L);
        return timer;
    }

    private JDA jda;
    private Settings settings;
    private int step;

    public ActivityTimerTask(@Nonnull JDA jda, Settings settings) {
        this.jda = jda;
        this.settings = settings;
        this.step = 0;
    }

    public void run() {
        Presence presence = jda.getPresence();
        switch (step) {
            default:
            case 0:
                int guilds = jda.getGuilds().size();
                presence.setActivity(Activity.watching(guilds+(guilds == 1 ? " guild" : " guilds")));
                step++;
                break;
            case 1:
                presence.setActivity(Activity.listening(settings.getCommandPrefix()+"<command>"));
                step++;
                break;
            case 2:
                presence.setActivity(Activity.playing("with a toaster"));
                step = 0;
                break;
        }
    }
}
