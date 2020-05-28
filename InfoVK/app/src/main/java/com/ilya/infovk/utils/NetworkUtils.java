package com.ilya.infovk.utils;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Scanner;
import javax.net.ssl.HttpsURLConnection;

public class NetworkUtils {
    private static final String API_URL = "https://api.vk.com/";
    private static final String VK_USERS_GET = "method/users.get";
    private static final String PARAM_USER = "user_ids";
    private static final String PARAM_VERSION = "v";
    private static final String ACCESS_TOKEN = "access_token";

    public static URL generateURL(String userIDs){
        Uri builtUri = Uri.parse(API_URL+VK_USERS_GET)
                .buildUpon()
                .appendQueryParameter(PARAM_USER, userIDs)
                .appendQueryParameter(PARAM_VERSION, "5.8")
                .appendQueryParameter(ACCESS_TOKEN, "8eeb14028eeb14028eeb14026d8e9955c888eeb8eeb1402d024b3134fbb8a38c11ccb64")
                .build();

        URL url = null;
        try {
            url = new URL (builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    // создаем входной поток
    public static String getResponseFromUrl(URL url) throws IOException {
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection(); // устанавливаем соединение openConnection, downcasting до HttpsURLConnection
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A"); // разделитель

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } catch (UnknownHostException e){
            return null;
        }
        finally {
            urlConnection.disconnect();
        }
    }
}
