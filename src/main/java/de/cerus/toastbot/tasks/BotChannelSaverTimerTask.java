/*
 * Copyright (c) 2019 Cerus
 * File created at 13.04.19 12:06
 * Last modification: 13.04.19 12:06
 * All rights reserved.
 */

package de.cerus.toastbot.tasks;

import de.cerus.toastbot.util.BotChannelUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BotChannelSaverTimerTask extends TimerTask {

    public static Timer startNew() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new BotChannelSaverTimerTask(), (30 * 60) * 1000L, (60 * 60) * 1000L);
        return timer;
    }

    @Override
    public void run() {
        System.out.println("Trying to save bot channels...");
        BotChannelUtil.saveChannels();
        System.out.println("Saved bot channels");
    }
}
