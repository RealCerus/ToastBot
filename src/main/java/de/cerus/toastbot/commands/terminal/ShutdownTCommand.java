/*
 * Copyright (c) 2019 Cerus
 * File created at 12.04.19 18:40
 * Last modification: 12.04.19 18:40
 * All rights reserved.
 */

package de.cerus.toastbot.commands.terminal;

import de.cerus.toastbot.ToastBot;
import de.cerus.toastbot.command.TerminalCommand;

public class ShutdownTCommand extends TerminalCommand {
    private ToastBot toastBot;

    public ShutdownTCommand(ToastBot toastBot) {
        super("shutdown");
        this.toastBot = toastBot;
    }

    @Override
    public void execute(String usedCommand, String[] args) {
        System.out.println("Shutting down...");
        toastBot.shutdown();
        System.out.println("Have a nice day! :)");
        System.exit(0);
    }
}
