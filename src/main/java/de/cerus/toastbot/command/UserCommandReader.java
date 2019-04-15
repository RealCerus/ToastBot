/*
 * Copyright (c) 2019 Cerus
 * File created at 11.04.19 13:37
 * Last modification: 11.04.19 13:37
 * All rights reserved.
 */

package de.cerus.toastbot.command;

import de.cerus.toastbot.settings.Settings;
import de.cerus.toastbot.util.BotChannelUtil;
import de.cerus.toastbot.util.HelpPagination;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UserCommandReader {

    private List<UserCommand> commands;
    private Logger logger;

    private JDA jda;
    private ListenerAdapter listenerAdapter;
    private Settings settings;
    private HelpPagination helpPagination;

    public UserCommandReader(Settings settings) {
        this.settings = settings;
        commands = new ArrayList<>();
        logger = LoggerFactory.getLogger(getClass());
        this.helpPagination = new HelpPagination(4);
    }

    public void start(JDA jda) {
        this.jda = jda;
        listenerAdapter = new ListenerAdapter() {
            @Override
            public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
                if(event.getMember() == null) return;
                if(event.getMember().getUser() == null) return;
                if(event.getMember().getUser().isBot()) return;
                String contentRaw = event.getMessage().getContentRaw();
                if(!settings.startsWithCommandPrefix(contentRaw) && !contentRaw.startsWith(event.getJDA().getSelfUser().getAsMention())) return;
                if(contentRaw.trim().equals(event.getJDA().getSelfUser().getAsMention()) && BotChannelUtil.isBotChannel(event.getChannel().getIdLong())){
                    execute("info", event.getMember(), event.getMessage());
                    return;
                }
                contentRaw = settings.removeCommandPrefix(contentRaw);
                if(contentRaw.startsWith(event.getJDA().getSelfUser().getAsMention()))
                    contentRaw = contentRaw.substring(event.getJDA().getSelfUser().getAsMention().length()).trim();
                boolean success = execute(contentRaw, event.getMember(), event.getMessage());
                if(!success && BotChannelUtil.isBotChannel(event.getChannel().getIdLong()))
                    event.getChannel().sendMessage(
                            new EmbedBuilder()
                                    .setColor(16312092)
                                    .setTitle("Unknown command")
                                    .setDescription("Maybe try `"+settings.getCommandPrefix(event.getGuild())+"help`?")
                                    .setFooter(event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl())
                                    .build()
                    ).complete();
            }
        };
        jda.addEventListener(listenerAdapter);
    }

    public void shutdown() {
        if (jda != null && listenerAdapter != null)
            jda.removeEventListener(listenerAdapter);
    }

    public void registerCommands(UserCommand... commands) {
        for (UserCommand command : commands) {
            command.setSettings(settings);
            this.commands.add(command);
            helpPagination.add(new MessageEmbed.Field((command.getUsage().equals("") ? command.getCommand() : command.getUsage()), command.getDescription(), false));
            command.onRegistration();
        }
    }

    public List<UserCommand> getCommands() {
        return commands;
    }

    public boolean execute(@Nonnull String stringMessage, @Nonnull Member member, @Nonnull Message message) {
        if (stringMessage.matches("\\s+")) return false;
        String[] splittedMessage = stringMessage.split("\\s+");
        String command = splittedMessage[0];
        String[] args = Arrays.copyOfRange(splittedMessage, 1, splittedMessage.length);

        List<UserCommand> commands = this.commands.stream().filter(userCommand -> userCommand.getCommand().equalsIgnoreCase(command)).collect(Collectors.toList());
        commands.forEach(userCommand -> userCommand.execute(command, member, message, message.getTextChannel(), args));

        return !commands.isEmpty();
    }

    public HelpPagination getHelpPagination() {
        return helpPagination;
    }
}
