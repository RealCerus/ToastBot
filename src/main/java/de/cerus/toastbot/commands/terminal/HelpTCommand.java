/*
 * Copyright (c) 2019 Cerus
 * File created at 11.04.19 23:01
 * Last modification: 11.04.19 23:01
 * All rights reserved.
 */

package de.cerus.toastbot.commands.terminal;

import de.cerus.toastbot.command.TerminalCommand;
import de.cerus.toastbot.command.TerminalCommandReader;

public class HelpTCommand extends TerminalCommand {
    private TerminalCommandReader reader;

    public HelpTCommand(TerminalCommandReader reader) {
        super("help");
        this.reader = reader;
    }

    @Override
    public void execute(String usedCommand, String[] args) {
        System.out.println("---- Help ----");
        reader.getCommands().forEach(System.out::println);
        System.out.println("---- End ----");
    }
}
