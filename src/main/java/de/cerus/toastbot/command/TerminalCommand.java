/*
 * Copyright (c) 2019 Cerus
 * File created at 11.04.19 11:38
 * Last modification: 11.04.19 11:38
 * All rights reserved.
 */

package de.cerus.toastbot.command;

public abstract class TerminalCommand {

    private String command;

    public TerminalCommand(String command) {
        this.command = command;
    }

    public abstract void execute(String usedCommand, String[] args);

    public String getCommand() {
        return command;
    }

    @Override
    public String toString() {
        return getCommand();
    }
}
