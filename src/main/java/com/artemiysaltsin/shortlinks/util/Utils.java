package com.artemiysaltsin.shortlinks.util;

import org.springframework.http.HttpStatus;

import java.net.URL;

public class Utils {

    public static boolean isCorrectURL(String link) {

        try { // Проверка корректности ссылки
            URL url = new URL(link);
            url.toURI();
        } catch (Exception ex) {
            return false;
        }
        return true;

    }


}
