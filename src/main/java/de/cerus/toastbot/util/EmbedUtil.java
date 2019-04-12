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
                } catch (BadRequestException ignored) {
                    link = null;
                    metadata = null;
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
                } catch (ErrorResponseException ignored) {
                }

                if (link == null) return;
                // Waiting five minutes to delete the image and remove the download link
                Thread.sleep((5 * 60) * 1000);

                // Deleting the uploaded image so we don't waste any DropBox storage space
                try {
                    client.files().deleteV2(metadata.getPathDisplay());
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
                } catch (ErrorResponseException ignored) {
                }
            } catch (IOException | DbxException | InterruptedException e) {
                e.printStackTrace();
            }

            // Deleting the saved file so we don't waste any disk space
            toastFile.delete();
        }).start();
    }

    public static void sendToastify(User user, TextChannel channel) {
        // 'Generating' the percent
        int percent = ThreadLocalRandom.current().nextInt(1, 101);

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
                    if(link == null)
                        message.getChannel().sendFile(file).complete();
                } catch (ErrorResponseException ignored) {
                }

                if(link == null) return;
                // Waiting five minutes and delete the image from DropBox afterwards
                Thread.sleep((5 * 60) * 1000);

                // Deleting the uploaded image so we don't waste any DropBox storage space
                try {
                    client.files().deleteV2(metadata.getPathDisplay());
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
