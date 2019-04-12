/*
 * Copyright (c) 2019 Cerus
 * File created at 11.04.19 17:34
 * Last modification: 11.04.19 17:34
 * All rights reserved.
 */

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class FontTest {

    public static void main(String[] args) {
        for (String availableFontFamilyName : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
            System.out.println("- "+availableFontFamilyName);
        }
    }

}
