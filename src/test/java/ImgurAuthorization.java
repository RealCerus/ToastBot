/*
 * Copyright (c) 2019 Cerus
 * File created at 11.04.19 14:26
 * Last modification: 11.04.19 14:26
 * All rights reserved.
 */

import java.net.URLConnection;

public class ImgurAuthorization {

    public static void main(String[] args) {
        try {
            URLConnection connection = new java.net.URL("https://api.imgur.com/oauth2/authorize").openConnection();
            connection.setRequestProperty("response_type", "token");
            connection.setRequestProperty("client_id", "b7e0e4d80584918");
            connection.connect();

            System.out.println(connection.getInputStream());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
