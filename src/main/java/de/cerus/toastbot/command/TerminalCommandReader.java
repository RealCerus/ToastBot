/*
 * Copyright (c) 2019 Cerus
 * File created at 11.04.19 11:38
 * Last modification: 11.04.19 11:38
 * All rights reserved.
 */

package de.cerus.toastbot.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class TerminalCommandReader {

    private List<TerminalCommand> commands;
    private Scanner scanner;
    private Thread scannerThread;
    private Logger logger;

    public TerminalCommandReader() {
        commands = new ArrayList<>();
        logger = LoggerFactory.getLogger(getClass());
    }

    public void registerCommands(TerminalCommand... commands) {
        this.commands.addAll(Arrays.asList(commands));
    }

    public void start() {
        logger.info("Terminal command listener started");

        // Listening for new lines
        scanner = new Scanner(System.in);
        scannerThread = new Thread(() -> {
            while (scanner.hasNext()) {
                String message = scanner.nextLine();
                if (message.equals("")) return;
                if (!execute(message))
                    System.out.println("Command not found. Type help to list all commands.");
            }
        });
        scannerThread.start();
    }

    public boolean execute(@Nonnull String message) {
        if (message.matches("\\s+")) return false;
        String[] splittedMessage = message.split("\\s+");
        String command = splittedMessage[0];
        String[] args = Arrays.copyOfRange(splittedMessage, 1, splittedMessage.length);

        List<TerminalCommand> commands = this.commands.stream().filter(terminalCommand -> terminalCommand.getCommand().equalsIgnoreCase(command)).collect(Collectors.toList());
        commands.forEach(terminalCommand -> terminalCommand.execute(command, args));

        return !commands.isEmpty();
    }

    public List<TerminalCommand> getCommands() {
        return commands;
    }

    public void shutdown() {
        if (scannerThread != null && scannerThread.isAlive() && !scannerThread.isInterrupted())
            scannerThread.interrupt();
        if (scanner != null)
            scanner.close();
    }
}
