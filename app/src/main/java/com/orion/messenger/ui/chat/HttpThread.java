package com.orion.messenger.ui.chat;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HttpThread extends Thread {

    public static final int OP_AVATAR = 1;
    public static final int OP_VERSION_CLIENT = 2;
    public static final int OP_DOWNLOAD_CLIENT = 3;
    public static final int OP_ABOUT_PROJECT = 4;

    private static final String LOCAL_PATH_AVATAR = "avatar";
    private static final String LOCAL_PATH_VERSION = "version-client";
    private static final String LOCAL_PATH_DOWNLOAD_CLIENT = "download-client";
    private static final String LOCAL_PATH_ABOUT_PROJ = "about-project";

    private Integer operation = null;
    private Map<String, Object> params = new HashMap<>();
    private ChatHandler handler;

    public HttpThread(ChatHandler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        super.run();

        Object r = null;

        switch (operation) {
            case OP_AVATAR:
                //  TODO
                break;
            case OP_VERSION_CLIENT:
                //  TODO
                break;
            case OP_DOWNLOAD_CLIENT:
                //  TODO
                break;
            case OP_ABOUT_PROJECT:
                r = getDescriptionProject();
                break;
        }

        if (r != null) {
            handler.responseHttpThread(operation, r);
        } else {
            Log.e("", "ERROR HttpThread! Response server is null! operation -> "
                    + String.valueOf(operation));
        }
    }

    public HttpThread initOperation(int operation, Map<String, Object> params) {

        this.operation = operation;
        this.params = params;

        return this;
    }

    public String getDescriptionProject(){

        String description = "";
        try {
            URL url = new URL( Config.HTTPS + Config.DOMAIN + ":" +Config.WS_PORT+"/" + Config.ABOUT_PROJECT);
          //  Log.d("", "downloadDescriptionProject URL => " + url);
            URLConnection conex = url.openConnection();
            conex.connect();
            InputStream input = new BufferedInputStream(url.openStream());

            byte[] data = new byte[10240];
            while (input.read(data) != -1) {
                description = description + new String(data);
            }
            input.close();
        } catch (MalformedURLException e) {
            Log.d("", "EXCEPTION111000! message => " + e.getMessage() + ", stack => " + Arrays.toString(e.getStackTrace()));
        } catch (IOException e) {
            Log.d("", "EXCEPTION333000! message => " + e.getMessage() + ", stack => " + Arrays.toString(e.getStackTrace()));
        }

        return description;
    }

    private void getAvatar(String fileName) {

      /*  File file = new File(Config.PATH_SD_CARD + path);                                 //  проверяем существует ли указанная папка во внешней памяти
        if (!file.exists()) {
            file.mkdirs();                                                                          //  если нет то создаем ее
        }*/
        File file = new File(fileName);                               //  проверяем существует ли требуемый файл

        //   Log.d("GRADINAS", "file path => " + file.getPath());
        if (!file.exists()) {                                                                       //  если не существует,
            try {
                //        Log.d("GRADINAS", "path => " + path);
                //          Log.d("GRADINAS", "fileName => " + fileName);
                URL url = new URL( Config.HTTPS + Config.DOMAIN + "/" + Config.AVATAR);      //  создаем ссылку на файл
                Log.d("GRADINAS", "URL => " + url);
                URLConnection conex = url.openConnection();                                         //  и открываем соединение
                conex.connect();
                InputStream input = new BufferedInputStream(url.openStream());                      //  открываем входящий поток
                OutputStream output = new FileOutputStream(file);                                   //  создаем исходящий поток
                byte data[] = new byte[1024];                                                       //  создаем буфер
                int count;
                while ((count = input.read(data)) != -1) {                                          //  читаем из потока пока не закончатся данные
                    output.write(data, 0, count);                                               //  пишем в исходящий поток
                }
                output.flush();                                                                     //  закрываем потоки
                output.close();
                input.close();
            } catch (MalformedURLException e) {
                Log.d("GRADINAS", "EXCEPTION111000! message => " + e.getMessage() + ", stack => " + Arrays.toString(e.getStackTrace()));
            } catch (IOException e) {
                Log.d("GRADINAS", "EXCEPTION333000! message => " + e.getMessage() + ", stack => " + Arrays.toString(e.getStackTrace()));
            }
        } else {
            Log.d("", "FILE_THREAD FILE EXISTS => " + file.getPath());
        }

    }

}
