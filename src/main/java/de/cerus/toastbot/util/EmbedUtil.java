/*
 * Copyright (c) 2019 Cerus
 * File created at 11.04.19 13:44
 * Last modification: 11.04.19 13:44
 * All rights reserved.
 */

package de.cerus.toastbot.util;

import com.dropbox.core.BadRequestException;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DeleteErrorException;
import com.dropbox.core.v2.files.FileMetadata;
import de.cerus.toastbot.settings.Settings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ThreadLocalRandom;

public class EmbedUtil {

    private static DbxClientV2 client;

    public static void sendToast(User user, TextChannel channel, boolean nameToast) {
        if (client == null) return;

        // Do this stuff in another thread to avoid blocking
        new Thread(() -> {
            // Generate the toast image thing
            File toastFile = nameToast ? ImageUtil.nameToastUser(user) : ImageUtil.toastUser(user);

            // Send an error message in case the generated file is somehow null
            if (toastFile == null) {
                channel.sendMessage(
                        new EmbedBuilder()
                                .setColor(13632027)
                                .setTitle("Failed to toast " + user.getAsTag(), "https://cerus-dev.de/")
                                .setDescription("Failed to execute the toast command. This should NOT happen.")
                                .build()
                ).complete();
                return;
            }

            // Sending the message stating the image is being generated
            Message message = channel.sendMessage(
                    new EmbedBuilder()
                            .setColor(8311585)
                            .setTitle("Toasted " + user.getAsTag(), "https://cerus-dev.de/")
                            .setDescription("Generating the toasted version of " + user.getAsTag() + "...")
                            .build()
            ).complete();

            // Try-with-resources to upload the image and send it to the user
            try (InputStream inputStream = new FileInputStream(toastFile)) {
                // Uploading the image
                String link;
                FileMetadata metadata;
                try {
                    metadata = client.files().uploadBuilder("/" + toastFile.getName()).uploadAndFinish(inputStream);
                    link = client.files().getTemporaryLink(metadata.getPathDisplay()).getLink();
                } catch (BadRequestException e) {
                    link = null;
                    metadata = null;
                    e.printStackTrace();
                }

                // Trying to edit the message to add the download link and the generated image
                try {
                    if (link != null) {
                        message.editMessage(
                                new EmbedBuilder()
                                        .setColor(8311585)
                                        .setTitle("Toasted " + user.getAsTag(), "https://cerus-dev.de/")
                                        .setDescription("Here is the toasted version of " + user.getAsTag() + ":\n[Download link](" + link + ")")
                                        .setImage(link)
                                        .build()
                        ).complete();
                    } else {
                        message.editMessage(
                                new EmbedBuilder()
                                        .setColor(8311585)
                                        .setTitle("Toasted " + user.getAsTag(), "https://cerus-dev.de/")
                                        .setDescription("Here is the toasted version of " + user.getAsTag() + "!")
                                        .build()
                        ).complete();
                        message.getChannel().sendFile(toastFile).complete();
                    }
                } catch (ErrorResponseException e) {
                    e.printStackTrace();
                }

                if (link == null) return;
                // Waiting five minutes to delete the image and remove the download link
                Thread.sleep((5 * 60) * 1000);

                // Deleting the uploaded image so we don't waste any DropBox storage space
                try {
                    client.files().deleteV2(metadata.getPathDisplay());
                    if (toastFile.exists())
                        // Deleting the saved file so we don't waste any disk space
                        toastFile.delete();
                } catch (DeleteErrorException ignored) {
                }

                try {
                    // Trying to edit the message to remove the download link
                    message.editMessage(
                            new EmbedBuilder()
                                    .setColor(8311585)
                                    .setTitle("Toasted " + user.getAsTag(), "https://cerus-dev.de/")
                                    .setDescription("Here is the toasted version of " + user.getAsTag() + ":\n*[Link removed]*")
                                    .build()
                    ).complete();
                } catch (ErrorResponseException e) {
                    e.printStackTrace();
                }
            } catch (IOException | DbxException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void sendToastBattle(User userOne, User userTwo, TextChannel channel) {
        if (client == null) return;

        // Do this stuff in another thread to avoid blocking
        new Thread(() -> {
            // Generate the toastbattle image thing
            File battleImage = ImageUtil.toastBatlle(userOne, userTwo);

            // Send an error message in case the generated file is somehow null
            if (battleImage == null) {
                channel.sendMessage(
                        new EmbedBuilder()
                                .setColor(13632027)
                                .setTitle("Error", "https://cerus-dev.de/")
                                .setDescription("Failed to do a battle between " + userOne.getAsTag() + " and " + userTwo.getAsTag())
                                .build()
                ).complete();
                return;
            }

            // Sending the message stating the image is being generated
            Message message = channel.sendMessage(
                    new EmbedBuilder()
                            .setColor(8311585)
                            .setTitle("Starting battle", "https://cerus-dev.de/")
                            .setDescription("Starting the toast battle between " + userOne.getAsTag() + " and " + userTwo.getAsTag() + "...")
                            .build()
            ).complete();

            // Try-with-resources to upload the image and send it to the user
            try (InputStream inputStream = new FileInputStream(battleImage)) {
                // Uploading the image
                String link;
                FileMetadata metadata;
                try {
                    metadata = client.files().uploadBuilder("/" + battleImage.getName()).uploadAndFinish(inputStream);
                    link = client.files().getTemporaryLink(metadata.getPathDisplay()).getLink();
                } catch (BadRequestException e) {
                    link = null;
                    metadata = null;
                    e.printStackTrace();
                }

                // Trying to edit the message to add the download link and the generated image
                try {
                    if (link != null) {
                        message.editMessage(
                                new EmbedBuilder()
                                        .setColor(8311585)
                                        .setTitle("Toast battle", "https://cerus-dev.de/")
                                        .setDescription("Battle started!")
                                        .addField(userOne.getAsTag(), "**100** HP", true)
                                        .addField(userTwo.getAsTag(), "**100** HP", true)
                                        .setImage(link)
                                        .build()
                        ).complete();
                        Thread.sleep(3000);
                        battle(message, 100, 100, userOne, userTwo, link);
                    } else {
                        message.editMessage(
                                new EmbedBuilder()
                                        .setColor(8311585)
                                        .setTitle("Toast battle", "https://cerus-dev.de/")
                                        .setDescription("Battle started!")
                                        .addField(userOne.getAsTag(), "**100** HP", true)
                                        .addField(userTwo.getAsTag(), "**100** HP", true)
                                        .build()
                        ).complete();
                        message.getChannel().sendFile(battleImage).complete();
                        Thread.sleep(3000);
                        battle(message, 100, 100, userOne, userTwo, "http://some.link");
                        message.getChannel().sendFile(battleImage).complete();
                    }
                } catch (ErrorResponseException e) {
                    e.printStackTrace();
                }

                if (link == null) return;
                // Waiting five minutes to delete the image and remove the download link
                Thread.sleep((10 * 60) * 1000);

                // Deleting the uploaded image so we don't waste any DropBox storage space
                try {
                    client.files().deleteV2(metadata.getPathDisplay());
                    if (battleImage.exists())
                        // Deleting the saved file so we don't waste any disk space
                        battleImage.delete();
                } catch (DeleteErrorException ignored) {
                }

                try {
                    // Trying to delete the message
                    message.editMessage(
                            new EmbedBuilder(message.getEmbeds().get(0)).setImage("http://no.image").build()
                    ).complete();
                } catch (ErrorResponseException e) {
                    e.printStackTrace();
                }
            } catch (IOException | DbxException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void battle(Message message, int userOneHP, int userTwoHP, User userOne, User userTwo, String image) {
        boolean userOneStarts = ThreadLocalRandom.current().nextBoolean();
        String pun = randomPun().replace("{user1}", userOneStarts ? userOne.getAsTag() : userTwo.getAsTag()).replace("{user2}", !userOneStarts ? userOne.getAsTag() : userTwo.getAsTag());
        int damage = ThreadLocalRandom.current().nextInt(4, 17);
        int newHPUserOne = (userOneStarts ? userOneHP : userOneHP - damage);
        int newHPUserTwo = (userOneStarts ? userTwoHP - damage : userTwoHP);
        if(pun.endsWith(" <TOAST BOOST ACTIVATED>")){
            if(userOneStarts)
                newHPUserTwo+=50;
            else newHPUserOne+=50;
        }
        if (newHPUserOne < 0) newHPUserOne = 0;
        if (newHPUserTwo < 0) newHPUserTwo = 0;

        boolean someoneWon = newHPUserOne <= 0 || newHPUserTwo <= 0;
        boolean userOneWon = newHPUserTwo <= 0;

        if (someoneWon) {
            try {
                message.editMessage(new EmbedBuilder()
                        .setColor(8311585)
                        .setTitle("Toast battle", "https://cerus-dev.de/")
                        .setDescription(pun+"\n**" + (userOneWon ? userOne.getAsTag() : userTwo.getAsTag()) + " won the battle!**")
                        .addField(userOne.getAsTag(), "**" + newHPUserOne + "** HP", true)
                        .addField(userTwo.getAsTag(), "**" + newHPUserTwo + "** HP", true)
                        .setImage(image)
                        .build()
                ).complete();
            } catch (Exception ignored) {
            }
            return;
        }

        try {
            message.editMessage(new EmbedBuilder()
                    .setColor(8311585)
                    .setTitle("Toast battle", "https://cerus-dev.de/")
                    .setDescription(pun)
                    .addField(userOne.getAsTag(), "**" + newHPUserOne + "** HP", true)
                    .addField(userTwo.getAsTag(), "**" + newHPUserTwo + "** HP", true)
                    .setImage(image)
                    .build()
            ).complete();
            Thread.sleep(5000);
            battle(message, newHPUserOne, newHPUserTwo, userOne, userTwo, image);
        } catch (Exception ignored) {
        }
    }

    private static String randomPun() {
        switch (ThreadLocalRandom.current().nextInt(0, 10)) {
            case 0:
                if(ThreadLocalRandom.current().nextBoolean() && ThreadLocalRandom.current().nextInt(0, 10) == 5)
                    return "{user2} saw a wild <@325957450807115776>! <TOAST BOOST ACTIVATED>";
                else return "{user2} fell asleep!";
            case 1:
                return "{user2} accidentally hit himself / herself!";
            case 2:
                return "{user2} got somehow confused!";
            case 3:
                return "{user2} burned his tongue on freshly baked bread!";
            case 4:
                return "{user1} stabbed {user2} with a sharp piece of bread!";
            case 5:
                return "{user1} poisoned {user2} with poisoned bread!";
            case 6:
                return "{user2} tripped on a loaf of bread!";
            case 7:
                return "{user2} slipped on breadcrumbs!";
            case 8:
                return "A box of bread fell onto {user2}!";
            case 9:
                return "{user2} got beaten by a loaf of bread!";
            default:
                return "{user1} slapped {user2} with a bag of breadcrumbs!";
        }
    }

    public static void sendToastify(User user, TextChannel channel) {
        // 'Generating' the percent
        String id = user.getId();
        int percent = (user.getName().toLowerCase().contains("toast") ? 100 : Integer.valueOf(id.substring(id.length() / 3, (id.length() / 3) + 2)));

        // Sending the message with the generated percent
        Message message = channel.sendMessage(
                new EmbedBuilder()
                        .setColor(8311585)
                        .setTitle("Toastified " + user.getAsTag())
                        .setDescription(user.getAsTag() + " got toastified! The user is to **" + percent + "%** a toast!\n*[Generating image, please wait...]*")
                        .build()
        ).complete();

        new Thread(() -> {
            // Generating the image
            File file = ImageUtil.toastify(user, percent);

            if (file == null) return;

            // Try-with-resources to upload the image and send it to the user
            try (InputStream inputStream = new FileInputStream(file)) {
                // Uploading the image
                FileMetadata metadata;
                String link;
                try {
                    metadata = client.files().uploadBuilder("/" + file.getName()).uploadAndFinish(inputStream);
                    link = client.files().getTemporaryLink(metadata.getPathDisplay()).getLink();
                } catch (BadRequestException ignored) {
                    link = null;
                    metadata = null;
                }

                // Trying to edit the message to set the image
                try {
                    message.editMessage(
                            new EmbedBuilder()
                                    .setColor(8311585)
                                    .setTitle("Toastified " + user.getAsTag())
                                    .setDescription(user.getAsTag() + " got toastified! The user is to **" + percent + "%** a toast!")
                                    .setImage(link == null ? "http://placeholder.something" : link)
                                    .build()
                    ).complete();
                    if (link == null)
                        message.getChannel().sendFile(file).complete();
                } catch (ErrorResponseException ignored) {
                }

                if (link == null) return;
                // Waiting five minutes and delete the image from DropBox afterwards
                Thread.sleep((5 * 60) * 1000);

                // Deleting the uploaded image so we don't waste any DropBox storage space
                try {
                    client.files().deleteV2(metadata.getPathDisplay());
                    if (file.exists())
                        file.delete();
                } catch (DeleteErrorException ignored) {
                }
            } catch (IOException | DbxException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void initialize(Settings settings) {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/Apps/Toast Bot").build();
        client = new DbxClientV2(config, settings.getDropboxToken());
    }
}
