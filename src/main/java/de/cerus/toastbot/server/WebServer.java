/*
 * Copyright (c) 2019 Cerus
 * File created at 19.04.19 13:01
 * Last modification: 19.04.19 13:01
 * All rights reserved.
 */

package de.cerus.toastbot.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.cerus.toastbot.event.VoteEventCaller;
import de.cerus.toastbot.settings.Settings;
import io.undertow.Undertow;
import io.undertow.util.HeaderMap;
import net.dv8tion.jda.api.JDA;

import java.util.concurrent.atomic.AtomicBoolean;

@Deprecated
public class WebServer {

    private Undertow undertow;
    private VoteEventCaller voteEventCaller;
    private Settings settings;
    private JDA jda;

    public WebServer(VoteEventCaller voteEventCaller, Settings settings, JDA jda) {
        this.voteEventCaller = voteEventCaller;
        this.settings = settings;
        this.jda = jda;
    }

    public void start() {
        undertow = Undertow.builder()
                .addHttpListener(8065, settings.isDevEnv() ? "localhost" : "lukassp.de")
                .setHandler(exchange -> {
                    System.out.println("[Webserver] Handling request: " + exchange.getRequestURL());
                    HeaderMap headerValues = exchange.getRequestHeaders();
                    try {
                        AtomicBoolean allowed = new AtomicBoolean(false);
                        headerValues.forEach(value -> value.forEach(s -> {
                            if (value.getHeaderName().toString().equals("Authorization") && s.equals(settings.getDblVoteAuth()))
                                allowed.set(true);
                        }));
                        if (!allowed.get()) {
                            System.out.println("[Webserver] Request failed");
                            return;
                        }
                        exchange.getRequestReceiver().receiveFullString((httpServerExchange, s) -> {
                            System.out.println("[Webserver] Parsing request...");
                            JsonElement element = new JsonParser().parse(s);
                            JsonObject object = element.getAsJsonObject();
                            String userId = object.get("user").getAsString();
                            boolean weekend = object.get("isWeekend").getAsBoolean();
                            voteEventCaller.call(jda.getUserById(userId), weekend);
                            System.out.println("[Webserver] Request parsed, vote listeners were called");
                        });
                    } catch (Exception ignored) {
                        System.out.println("[Webserver] Could not handle request");
                    }
                }).build();
        undertow.start();
    }

    public void shutdown() {
        try {
            undertow.getWorker().shutdown();
            undertow.stop();
        } catch (Exception ignored) {
        }
    }
}
