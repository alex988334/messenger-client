package com.orion.messenger.ui.settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.util.Log;

import com.orion.messenger.ui.chat.ChatHandlerProperty;

public class SettingsHandler {

    private Context context;

    public SettingsHandler(Context context) {
        this.context = context;
    }

    //  метод записывающий данные пользователя в файл
    public void writeSettingsToFile(ChatHandlerProperty settings) {

        Gson gson = new Gson();                                                                     //  создаем JSON конвертер
        String encode = gson.toJson(settings);

        Log.d("GRADINAS", "WRITE SETTINGS => " + encode);                    //    печатаем сохраняемые настройки в лог
        try {
            OutputStreamWriter out = new OutputStreamWriter(context.openFileOutput("settings.txt", context.MODE_PRIVATE));
            out.write(encode);
            out.flush();                                                                            //  пишем в файл
            out.close();                                                                            //  закрываем поток
        } catch (
                FileNotFoundException e) {                                                         //  отлавливаем исключения ошибки поиска файла и ошибок ввода вывода
            e.printStackTrace();
            Log.d("GRADINAS", "ERROR Settings: SETTINGS ФАЙЛ не найден");
        } catch (IOException e) {
            Log.d("GRADINAS", "ERROR Settings: SETTINGS ОШИБКА ВВОДА ВЫВОДА");
        }
    }

    //  осуществляет чтение файла настроек
    public ChatHandlerProperty readSettingsFile() {

        File file = new File(context.getFilesDir(), "settings.txt");

        ChatHandlerProperty settings = new ChatHandlerProperty();

        if (!file.exists()) {
            Log.d("GRADINAS", "ERROR Settings: ФАЙЛ НАСТРОЕК НЕ НАЙДЕН");
            return settings;
        }

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            StringBuilder stringBuilder = new StringBuilder();

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            bufferedReader.close();
            line = stringBuilder.toString();

            Log.e("", "SETTINGS read => " + line);

            Type type = new TypeToken<ChatHandlerProperty>(){}.getType();
            settings = new Gson().fromJson(line, type);
            Log.e("", "READ SETTINGS => "+ line);
        } catch (FileNotFoundException ex) {
            Log.d("", "ERROR Settings: файл не найден");
        } catch (IOException ex) {
            Log.d("", "ERROR Settings: ОШИБКА ЧТЕНИЯ ФАЙЛА - iput output");
        }

        return settings;
    }
}
