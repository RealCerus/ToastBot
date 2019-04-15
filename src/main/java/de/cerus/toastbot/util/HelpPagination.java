/*
 * Copyright (c) 2019 Cerus
 * File created at 14.04.19 12:14
 * Last modification: 14.04.19 12:14
 * All rights reserved.
 */

package de.cerus.toastbot.util;

import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.List;
import java.util.stream.Collectors;

public class HelpPagination extends Pagination<MessageEmbed.Field> {
    public HelpPagination(int pageSize) {
        super(pageSize);
    }

    public List<MessageEmbed.Field> getPage(int page, String commandPrefix) {
        return super.getPage(page).stream().map(field -> changeField(field, commandPrefix)).collect(Collectors.toList());
    }

    private MessageEmbed.Field changeField(MessageEmbed.Field field, String commandPrefix) {
        return new MessageEmbed.Field(commandPrefix+field.getName(), field.getValue(), field.isInline());
    }
}
