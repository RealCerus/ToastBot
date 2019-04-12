/*
 * Copyright (c) 2019 Cerus
 * File created at 11.04.19 11:38
 * Last modification: 11.04.19 11:38
 * All rights reserved.
 */

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import java.io.File;
import java.io.IOException;

public class ConfigTest {

    public static void main(String[] args) {
        File settingsFile = new File("./Test.toml");
        if(!settingsFile.exists()) {
            try {
                settingsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        CommentedFileConfig settings = CommentedFileConfig.builder(settingsFile).autosave().autoreload().build();
        settings.set("path", "value");
        settings.setComment("path", "test comment");
    }

}
