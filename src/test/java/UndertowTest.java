/*
 * Copyright (c) 2019 Cerus
 * File created at 19.04.19 13:05
 * Last modification: 19.04.19 13:05
 * All rights reserved.
 */

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.undertow.Undertow;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class UndertowTest {

    public static void main(String[] args) {
        Undertow undertow = Undertow.builder()
                .addHttpListener(8065, "localhost")
                .setHandler(exchange -> {
                    HeaderMap headerValues = exchange.getRequestHeaders();
                    try {
                        if(!headerValues.contains("content-type")) {
                            exchange.getResponseSender().send("{\"error\": true}");
                            return;
                        }
                        if(!headerValues.contains("Authorization")) {
                            exchange.getResponseSender().send("{\"error\": true}");
                            return;
                        }
                        if(!headerValues.get("Authorization").getHeaderName().toString().equals("")) {
                            exchange.getResponseSender().send("{\"error\": true}");
                            return;
                        }
                        exchange.getRequestReceiver().receiveFullString((httpServerExchange, s) -> {
                            JsonElement element = new JsonParser().parse(s);
                            JsonObject object = element.getAsJsonObject();
                            String userId = object.get("user").getAsString();
                            boolean weekend = object.get("isWeekend").getAsBoolean();
                        });
                    } catch (Exception ignored){
                        ignored.printStackTrace();
                    }
                }).build();
        undertow.start();

        new Thread(() -> {
            try {
                Thread.sleep(4000);

                URL obj = new URL("http://localhost:8065/");
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                con.setRequestProperty("User-Agent", "Achievements");
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setDoOutput(true);

                OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                wr.write("{\"hi\": 1}");
                wr.close();

                con.getResponseCode();

                InputStreamReader reader = new InputStreamReader(con.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(reader);

                String s;
                while ((s = bufferedReader.readLine()) != null)
                    System.out.println(s);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
