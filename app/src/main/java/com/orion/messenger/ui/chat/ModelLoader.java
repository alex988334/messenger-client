package com.orion.messenger.ui.chat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orion.messenger.ui.data.BlackList;
import com.orion.messenger.ui.data.Chat;
import com.orion.messenger.ui.data.ChatUser;
import com.orion.messenger.ui.data.IModel;
import com.orion.messenger.ui.data.Message;
import com.orion.messenger.ui.data.MessageStatus;
import com.orion.messenger.ui.data.Model;
import com.orion.messenger.ui.data.User;
import com.orion.messenger.ui.data.UserPhone;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ModelLoader {

    private static Gson gson = new Gson();

    public static String encodeToString(Object object) {
        return gson.toJson(object);
    }

    public static String encodeByteArr(Object object) {
        return gson.toJson(object);
    }
    public static byte[] encodeByteArr(Object[] objects) {
        return gson.toJson(objects).getBytes();
    }

    public static Map<String, List<IModel>> loadData(Map<String, byte[]> response) throws Exception {

        Map<String, List<IModel>> data = new HashMap<>();
        String[] keys = response.keySet().toArray(new String[]{});

        for(String key: keys){
            byte[] objects = response.get(key);
            if (objects.length == 0) {
                continue;
            }

            switch (key) {
                case Model.MODEL_CHAT:
                    data.put(key, loadChats(new String(objects)));
                    break;
                case Model.MODEL_MESSAGE:
                    data.put(key, loadMessages(new String(objects)));
                    break;
                case Model.MODEL_CHAT_USER:
                    data.put(key, loadChatUsers(new String(objects)));
                    break;
                case Model.MODEL_STATUS_MESSAGE:
                    data.put(key, loadMessageStatuses(new String(objects)));
                    break;
                case Model.MODEL_USER:
                    data.put(key, loadUsers(new String(objects)));
                    break;
                case Model.MODEL_USER_PHONE:
                    data.put(key, loadUserPhones(new String(objects)));
                    break;
                case Model.MODEL_BLACK_LIST:
                    data.put(key, loadBlackLists(new String(objects)));
                    break;
                default: throw new Exception("Unsupported type of Model! Name: " + key);
            }
        }

        return data;
    }

  /*  public static <T> List<IModel> castingIModel(Collection<T> collection) {

        List<IModel> data = new ArrayList<>();
        for (T unit: collection) {
            data.add((IModel) unit);
        }

        return data;
    }*/

    public static List<IModel> loadChats(String msg) {
        Type type = new TypeToken<List<Chat>>(){}.getType();
        return gson.fromJson(msg, type);
    }

    public static List<IModel> loadBlackLists(String msg) {
        Type type = new TypeToken<List<BlackList>>(){}.getType();
        return gson.fromJson(msg, type);
    }

    public static List<IModel> loadChatUsers(String msg) {
        Type type = new TypeToken<List<ChatUser>>(){}.getType();
        return gson.fromJson(msg, type);
    }

    public static List<IModel> loadMessages(String msg) {
        Type type = new TypeToken<List<Message>>(){}.getType();
        return gson.fromJson(msg, type);
    }

    public static List<IModel> loadMessageStatuses(String msg) {
        Type type = new TypeToken<List<MessageStatus>>(){}.getType();
        return gson.fromJson(msg, type);
    }

    public static List<IModel> loadUsers(String msg) {
        Type type = new TypeToken<List<User>>(){}.getType();
        return gson.fromJson(msg, type);
    }

    public static List<IModel> loadUserPhones(String msg) {
        Type type = new TypeToken<List<UserPhone>>(){}.getType();
        return gson.fromJson(msg, type);
    }
}
