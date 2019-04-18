/*
 * Copyright (c) 2019 Cerus
 * File created at 18.04.19 13:28
 * Last modification: 18.04.19 13:28
 * All rights reserved.
 */

package de.cerus.toastbot.listeners;

import de.cerus.toastbot.command.TerminalCommandReader;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PrivateChannelListener extends ListenerAdapter {

    private TerminalCommandReader reader;

    public PrivateChannelListener(TerminalCommandReader reader) {
        this.reader = reader;
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        if(!event.getAuthor().getId().equals("347018538713874444")) return;
        event.getChannel().sendMessage("Invoked shutdown TCommand").complete();
        boolean success = reader.execute("shutdown");
    }
}
