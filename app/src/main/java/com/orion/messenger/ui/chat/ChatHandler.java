package com.orion.messenger.ui.chat;

import android.app.Fragment;
import android.util.Log;

//import com.gradinas.masterpech.filemanager.FileDUThread;


import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orion.messenger.MainActivity;
import com.orion.messenger.R;
import com.orion.messenger.ui.data.Chat;
import com.orion.messenger.ui.data.Message;
import com.orion.messenger.ui.data.Model;
import com.orion.messenger.ui.data.User;
import com.orion.messenger.ui.user.RegistrationFragment;

import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

//  класс служит оберткой для класса WSThread, отвечает за полное функционирование чат потока и вызов нужных команд
public class ChatHandler {

    private IGenerateUI handlerUI;
    private IBufferWS dataBuffer;
    private ChatHandlerProperty settings;
    private WSThread wsThread;

    public ChatHandler(IGenerateUI handlerUI){
        super();

        this.handlerUI = handlerUI;
        dataBuffer = new BufferWS(this);

        if (WSThread.wsThread == null || WSThread.wsThread.getState() == Thread.State.TERMINATED) {
            WSThread.wsThread = new WSThread(dataBuffer);
        }

        wsThread = WSThread.wsThread;
        settings = new ChatHandlerProperty();
    }

 /*   test
 public void appendResponseData(Map<String, byte[]> data) {
        dataBuffer.addResponseData(data);
    }*/



    public void stopWSThread() {
        wsThread.StopWS();
    }

    public ChatHandlerProperty getSettings(){
        return settings;
    }

    public void newData() {

        handlerUI.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ResponseServer responseServer = dataBuffer.getResponseData();
                if (responseServer == null) {
                    Log.d(ConstWSThread.LOG_TAG, "ChatHandler get empty response server!");
                    return;
                }

                if (responseServer.Status.Status == ConstWSThread.STATUS_ERROR) {
                    showMessage("Operation failed, validate input data");
                    Log.d(ConstWSThread.LOG_TAG, "Operation failed! "
                            + responseServer.Status.Message);
                    return;
                }

                Log.d(ConstWSThread.LOG_TAG, "response => " + responseServer.toString());
                processData(responseServer);
            }
        });
    }

    public void newMessage(Message m) {

        StatusRequest st = new StatusRequest();
        st.Operation = ResponseServer.OP_NEW_MESSAGE;
        m.ChatId = settings.currentChat;
        m.Author = settings.userId;

        Map<String, String> data = new HashMap<>();
        data.put(Model.MODEL_STATUS, ModelLoader.encodeByteArr(st));
        data.put(Model.MODEL_MESSAGE, ModelLoader.encodeByteArr(m));

        wsThread.sendMessage(data);
    }

    private void processData(ResponseServer response) {

        switch (response.Status.Operation) {
            case ResponseServer.OP_AUTORIZATE:
                if (response.User.length == 0) {
                    Log.e(ConstWSThread.LOG_TAG, "ChatHandler error! User data in response is empty");
                    settings.password = "";
                    settings.login = "";
                    return;
                }
                User u = response.User[0];
                settings.userId = u.Id;
                settings.authKey = u.AuthKey;
                MainActivity.activvity.createChatUi();
                break;
            case ResponseServer.OP_STATUS_MESSAGE:
                break;
            case ResponseServer.OP_NEW_MESSAGE:
                break;
            case ResponseServer.OP_LIST_USERS:
                break;
            case ResponseServer.OP_CREATE_NEW_CHAT:
                ChatViewModel viewModel =
                        new ViewModelProvider(MainActivity.activvity).get(ChatViewModel.class);
                ResponseServer r = viewModel.getResponseServer();
                Chat[] chats = new Chat[r.Chat.length + 1];
                for (int i=0; i < r.Chat.length; i++) {
                    chats[i] = r.Chat[i];
                }

                chats[chats.length - 1] = response.Chat[0];
                r.Chat = chats;
                viewModel.setResponseServer(r);

                break;
            case ResponseServer.OP_WRITEN:
                break;
            case ResponseServer.OP_SYSTEM:
                break;
            case ResponseServer.OP_SEARCH_USER:
                break;
            case ResponseServer.OP_GET_CHATS:
                MainActivity.activvity.showChats(response);
                break;
            case ResponseServer.OP_LIST_PREVIOUS_MESSAGES:
                break;
            case ResponseServer.OP_LIST_NEXT_MESSAGES:
                break;
            case ResponseServer.OP_EXIT_CHAT:
                break;
            case ResponseServer.OP_REMOVE_USER:
                break;
            case ResponseServer.OP_ADD_USER:
                break;
            case ResponseServer.OP_REMOVE_CHAT:
                break;
            case ResponseServer.OP_BLOCK_USERS:
                break;
            case ResponseServer.OP_UNLOOCK_USERS:
                break;
            case ResponseServer.OP_BLACK_LIST_USERS:
                break;
            case ResponseServer.OP_GET_FILE:
                break;
            case ResponseServer.OP_MY_DATA:
                break;
            case ResponseServer.OP_REGISTRATION:
                if (response.User.length == 0) {
                    Log.e(ConstWSThread.LOG_TAG,
                            "ChatHandler error! User data in response is empty");
                    settings.password = "";
                    return;
                }
                u = response.User[0];
                settings.authKey = u.AuthKey;
                settings.login = u.Login;
                settings.alias = u.Alias;
                MainActivity.activvity.createAutorizateUI();
                break;
        }
    }

    public void setCurrentMessage(long messageId) {
        settings.currentMessage = messageId;
    }

    public void getListMessages() {

        StatusRequest st = new StatusRequest();
        st.Operation = ResponseServer.OP_LIST_PREVIOUS_MESSAGES;

        Message m = new Message();
        m.ChatId = settings.currentChat;

        Map<String, String> data = new HashMap<>();
        data.put(Model.MODEL_STATUS, ModelLoader.encodeByteArr(st));
        data.put(Model.MODEL_MESSAGE, ModelLoader.encodeByteArr(m));

        wsThread.sendMessage(data);
    }

    public void getListChats() {

        StatusRequest st = new StatusRequest();
        st.Operation = ResponseServer.OP_GET_CHATS;

        Map<String, String> data = new HashMap<>();
        data.put(Model.MODEL_STATUS, ModelLoader.encodeByteArr(st));

        wsThread.sendMessage(data);
    }

    public void createNewChat(String chatName){

        StatusRequest st = new StatusRequest();
        st.Operation = ResponseServer.OP_CREATE_NEW_CHAT;

        Chat c = new Chat();
        c.Name = chatName;

        Map<String, String> data = new HashMap<>();
        data.put(Model.MODEL_STATUS, ModelLoader.encodeByteArr(st));

        data.put(Model.MODEL_CHAT, ModelLoader.encodeByteArr(c));
        wsThread.sendMessage(data);
    }

   /* private ResponseServer decodeData(Map<String, byte[]> data) throws Exception {

        if (!data.containsKey(Model.MODEL_STATUS)) {
            Log.e(ConstWSThread.LOG_TAG, "ChatHandler ERROR! Status model not finded!");
            return null;
        }

        Gson g = new Gson();
        Type type = new TypeToken<StatusRequest>(){}.getType();
        StatusRequest st = g.fromJson(new String(data.get(Model.MODEL_STATUS), StandardCharsets.UTF_8), type);

        if (st.Status != ConstWSThread.STATUS_ACCEPT) {
            Log.d(ConstWSThread.LOG_TAG, "ChatHandler ERROR! Status operation is not accept, operation: " + st.Operation);
            handlerUI.showMessage("Status operation error");
            return null;
        }
        data.remove(Model.MODEL_STATUS);

        return new ResponseServer();
    }*/

    //  запускает поток ws соединения
    public void startHandler(){

        Thread.State s = wsThread.getState();
        if (s == Thread.State.NEW) {
            wsThread.start();
        }
    }

    public void onConnectedServer(){
        if (settings.login == null) {
            MainActivity.activvity.createAutorizateUI();
            // createRegistrationUI();
            return;
        }

        if (settings.authKey == null) {
            MainActivity.activvity.createAutorizateUI();
            return;
        }

        MainActivity.activvity.createChatUi();
    }

    public void autorizationUser(User u) {

        StatusRequest st = new StatusRequest();
        st.Operation = ResponseServer.OP_AUTORIZATE;

        Map<String, String> data = new HashMap<>();
        data.put(Model.MODEL_STATUS, ModelLoader.encodeByteArr(st));

        data.put(Model.MODEL_USER, ModelLoader.encodeByteArr(u));
        wsThread.sendMessage(data);
    }

    public void registrationUser(User u) {

        StatusRequest st = new StatusRequest();
        st.Operation = ResponseServer.OP_REGISTRATION;

        Map<String, String> data = new HashMap<>();
        data.put(Model.MODEL_STATUS, ModelLoader.encodeByteArr(st));

        data.put(Model.MODEL_USER, ModelLoader.encodeByteArr(u));
        settings.password = u.PassHash;
        wsThread.sendMessage(data);
    }

    private void createRegistrationUI(){
        handlerUI.createRegisterUI();
    }

    public void showMessage(String msg) {
        this.handlerUI.showMessage(msg);
    }

    public void setSettings(ChatHandlerProperty settings) {
        this.settings = settings;
    }
    public void setOperation(int operation) {
        settings.operation = operation;
    }
    public Integer getUserId(){
        return settings.userId;
    }
    public void setCurrentChat(int chatId) {
        settings.currentChat = chatId;
    }
    public Integer getCurrentChat(){
        return  settings.currentChat;
    }
    public Long getCurrentMessage() {
        return settings.currentMessage;
    }
    public String getLogin(){
        return settings.login;
    }
    public String getAlias(){
        return settings.alias;
    }
    public String getPassword(){
        return settings.password;
    }
    public String getAuthKey(){
        return settings.authKey;
    }
    //  возвращает список сообщений чата из списка представления
/*    public List<Map<String, String>> getChatMessages(){
        ListView list = (ListView) head.findViewById(R.id.spisok);                                  //  найдем представление
        AdapterChatMessages adapter = (AdapterChatMessages) list.getAdapter();                      //  получим ссылку на его адаптер
        if (adapter != null)  return adapter.getArrayData();                                        //  если адаптер не пуст вернем данные из него
        else return new ArrayList<>();                                                              //  иначе вернем пустую коллекцию
    }
/*
    //  производит обновление адаптера после изменения данных
    public void updateAdapter(){
        ListView list = (ListView) head.findViewById(R.id.spisok);                                  //  получаем представление
        if (list == null) return;                                                                   //  проверяем на пустоту
        switch (head.getOperation()){                                                               //  в зависимости от типа операции головного класса
            case ConstHead.CHATS:                                                                   //  при списке чатов
                ((AdapterListChats) list.getAdapter()).notifyDataSetChanged();
                break;
            case ConstHead.CHAT_MESSAGES:                                                           //  при списке сообщений
                ((AdapterChatMessages) list.getAdapter()).notifyDataSetChanged();
                break;
            case ConstHead.CHAT_USERS:                                                              //  при списке пользователей чата
                ((AdapterChatUsers) list.getAdapter()).notifyDataSetChanged();
                break;
            case ConstHead.CHAT_BLACK_LIST:                                                         //  при черном списке пользователей
            case ConstHead.CHAT_CREATE:                                                             //  при создании чата
            case ConstHead.CHAT_SEARCH_USER:                                                        //  при поиске пользователей
                ((AdapterBlackList) list.getAdapter()).notifyDataSetChanged();
                break;
        }
    }*/
/*
    //  точка сбора результатов запросов по ws соединению, получает объект с данными и тип операции выполненной ws соединением
    public void webSocketThreadReturn(Object obj, int operation) {

        if (head.getAnimation()) head.deleteAnimProgress();                                         //  если была запущена анимация то выключим ее
        Map<String, Map<String, Map<String, String>>> map = (Map<String, Map<String, Map<String, String>>>) obj;

        switch (operation) {                                                                        //  по типу операции выберем действие
            case ConstWSThread.OP_GET_CHATS:                                                        //  список чатов
            case ConstWSThread.OP_GET_HISTORY_MESSAGE:                                              //  история сообщений
            case ConstWSThread.OP_LIST_USERS:                                                       //  список пользователей чата
            case ConstWSThread.OP_BLACK_LIST_USERS:                                                 //  черный список пользователей
                new DBManager(head).writeDB(obj);                                                   //  передадим объект для записи в бд
                break;
            case ConstWSThread.OP_ADD_USER:                                                         //  добавление пользователя в чат
                if (Integer.parseInt(map.get("status").get("status").get("status")) != Config.STATUS_ACCEPT) {
                    //  проверяем что операция завершилась успехом
                    Snackbar.make(head.getRoditel(), "Не удалось добавить пользователей", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();                          //  если нет, то сообщение об ошибке
                    return;
                }
                if (chatNumber != 0) getUsers();                                                    //  проверяем что выбран какой либо из чатов и запускаем получение пользователей для него
                break;
            case ConstWSThread.OP_INPUT_MESSAGE:                                                    //  входящее новое сообщение
                Set<String> keys = map.get("messages").keySet();                                    //  получаем ключи сообщений
                for (String k: keys){                                                               //  и пробегаемся по ним
                    if (Integer.parseInt(map.get("messages").get(k).get(DBConnect.KEY_ID_USER))
                            != Integer.parseInt(head.getIdMastera())) {                             //  проверяем что не являемся автором сообщения
                        String status = ConstWSThread.MESSAGE_SEND;                                 //  готовим переменную статуса сообщения
                        if (Integer.parseInt(map.get("messages").get(k).get(DBConnect.KEY_ID_CHAT)) == chatNumber) {
                            //  если входящее сообщение из текущего чата
                            status = ConstWSThread.MESSAGE_READED;                                  //  выставляем переменную статуса как прочитано
                        } else status = ConstWSThread.MESSAGE_DELIVERED;                            //  иначе выставляем как доставлено

                        map.get("messages").get(k).put(DBConnect.KEY_STATUS, status);               //  записываем новое значение статуса сообщения в ответ сервера, для последующей записи в бд
                        wsThread.sendStatus(Integer.parseInt(map.get("messages").get(k).get(DBConnect.KEY_ID_CHAT)),
                                Integer.parseInt(map.get("messages").get(k).get(DBConnect.KEY_ID)), status);
                        //  отправляем на сервер сообщение со измененым статусом входящего сообщения
                    }
                }
                new DBManager(head).writeDB(obj);                                                   //  передадим объект для записи в бд
                newMessage(obj);                                                                    //  для оптимизации не производится повторное чтение из бд, а просто передается ответ сервера в метод визуализации
                break;
            case ConstWSThread.OP_CREATE_NEW_CHAT:                                                  //  создание нового чата, вы автор
                getChats(false, true);                                              //  запрашиваем новый список чатов
                //newChat(obj);
                break;
            case ConstWSThread.OP_EXIT_CHAT:                                                        //  выход из чата
            case ConstWSThread.OP_REMOVE_CHAT:                                                      //  удаление чата
                List<String> list = new ArrayList<>();                                              //  ответ сервера перепишем в новую переменную
                keys = map.get("chats").keySet();                                                   //  получим ключи удаленных чатов
                for (String k: keys) { list.add(k); }                                               //  перепишем ключи в новую коллекцию
                new DBManager(head).removeChatList(list);                                           //  отправим коллекцию чатов для удаления в локальную бд
                getChats(false, true);                                              //  запросим обновленный список чатов
                break;
            case ConstWSThread.OP_BLOCK_USERS:                                                      //  блокирование пользователей
                list = new ArrayList<>();                                                           //  создадим новую коллекцию с идентификаторами пользователей
                keys = map.get("users").keySet();
                for (String k: keys) { list.add(k); }                                               //  наполним ее идентификаторами
                new DBManager(head).addBlackList(list);                                             //  запишем в бд новых заблокированных пользователей
                getUsers();                                                                         //  запросим обновление списка пользователей
                break;
            case ConstWSThread.OP_UNLOOCK_USERS:                                                    //  разблокирование пользователей
                if (Boolean.valueOf(map.get("status").get("status").get("blackList"))) getBlackList();
                    //  если разблокирование было вызвано из операции черного списка, то запросим его обновление
                else {                                                                              //  иначе оно было вызвано из списка пользователей чата и соответственно:
                    list = new ArrayList<>();                                                       //  создадим коллекцию с идентификаторами пользователей
                    keys = map.get("users").keySet();
                    for (String k: keys) { list.add(k); }                                           //  заполним ее
                    new DBManager(head).removeBlackList(list);                                      //  и запустим очистку черного списка в бд
                    getUsers();                                                                     //  обновим список пользователей
                }
                break;
            case ConstWSThread.OP_REMOVE_USER:                                                      //  удаление пользователя из чата
                if (Integer.parseInt(map.get("status").get("status").get("status")) != Config.STATUS_ACCEPT) {
                    //  проверим что операция была успешной
                    Snackbar.make(head.getRoditel(), "Не удалось удалить пользователей", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }
                if (head.getOperation() == ConstHead.CHAT_USERS) getUsers();                        //  если текущая операция была список пользователей
                if (head.getOperation() == ConstHead.CHAT_BLACK_LIST) getBlackList();               //  если была черный список
                break;
            case ConstWSThread.OP_SEARCH_USER:                                                      //  поиск пользователей
                if (head.getOperation() == ConstHead.CHAT_SEARCH_USER || head.getOperation() == ConstHead.CHAT_CREATE)
                    showSearchUser(obj);                                                    //  если текущая операция была поиск пользователей или создание чата, то продолжим
                //  и запустим отображение найденных пользователей
                break;
            case ConstWSThread.OP_WRITEN:                                                           //  операция оповещения о том что пользователь печатает новое сообщение
                if (head.getOperation() == ConstHead.CHAT_MESSAGES) {                               //  проверяем текущую операцию, которая является индикатором текущего представления
                    userWrite(map);                                                                 //  если представление является месенджером, то реагируем
                }
                break;
            case ConstWSThread.OP_STATUS_MESSAGE:                                                   //  операция смены статуса сообщения
                if (head.getOperation() == ConstHead.CHAT_MESSAGES) {                               //  проверяем текущее представление на месенджер
                    new DBManager(head).writeDB(obj);                                               //  если да то пишем в бд
                    changeStatus(map);                                                              //  отображаем измененный статус сообщения
                }
                break;
            case ConstWSThread.OP_MY_DATA:                                                          //  мусорный метод для отладки
                Snackbar.make(head.getRoditel(), map.get("status").get("status").get("user"), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
            default:
        }
        head.invalidateOptionsMenu();                                                               //  запускаем переделывание меню в соотвествии с главным представлением
    }
/*
    //  отображает измененный статус сообщения в мессенджере
    public void changeStatus(Map<String, Map<String, Map<String, String>>> response){

        if (head.getOperation() != ConstHead.CHAT_MESSAGES) {                                       //  проверяем что текущая операция мессенджер
            return;
        }
        Set<String> keys = response.get("messages").keySet();
        for (String k: keys) {                                                                      //  пробегаемся по всем ключам коллекции сообщений
            Map<String, String> message = response.get("messages").get(k);
            if (chatNumber != Integer.parseInt(message.get("id_chat"))) {                           //  если сообщение не из текущего чата,
                continue;                                                                           //  то игнорируем
            }
            AdapterChatMessages adapter = (AdapterChatMessages) ((ListView) head.findViewById(R.id.spisok)).getAdapter();
            //  получаем адаптер списка сообщений
            if (adapter != null) {                                                                  //  проверяем что он не пуст
                List<Map<String, String>> list = adapter.getArrayData();                            //  пролучаем коллекцию карт сообщений
                for (int i = 0; i < list.size(); i++) {                                             //  пробегаемся по ним
                    if (Integer.parseInt(list.get(i).get("id")) == Integer.parseInt(message.get("id"))) {
                        //  ищем наше сообщение с нужным id
                        list.get(i).put(DBConnect.KEY_STATUS, message.get("status"));               //  при нахождении меняем статус на новый
                    }
                }
                adapter.notifyDataSetChanged();                                                     //  обновляем представление списка сообщений
            }
        }
    }

    //  обработка сообщения извещения о том, что пользователь печатает новое сообщение или окончил печатать
    public void userWrite(Map<String, Map<String, Map<String, String>>> response){

        Set<String> keys = response.get("users").keySet();
        for (String k: keys) {                                                                      //  пробегаемся по списку печатающих пользователей
            Map<String, String> user = response.get("users").get(k);                                //  выбираем каждого по отдельности
            LinearLayout write = (LinearLayout) head.findViewById(R.id.messenger_write);            //  находим представление-контейнер для вывода оповещений
            if (Boolean.parseBoolean(user.get("write"))) {                                          //  проверяем печатает (true) или окончил печатать (false) пользователь
                boolean flag = true;                                                                //  устанавливаем флаг
                for (int i = 0; i < write.getChildCount(); i++) {                                   //  пробегаемся по всем уже показанным оповещениям
                    if (((String) write.getChildAt(i).getTag()).equalsIgnoreCase(user.get("username"))) {
                        //  ищем совпадение по имени пользователя
                        flag = false;                                                               //  и если находим, то инверсируем
                    }
                }
                if (flag) {                                                                         //  истина - если оповещение о данном пользователе еще не показано
                    TextView textView = (TextView) ((LayoutInflater)
                            head.getLayoutInflater()).inflate(R.layout.user_write, write, false);
                    //  создаем представление оповещения
                    textView.setText(user.get("username") + " печатает");                           //  указываем имя печатающего пользователя
                    textView.setTag(user.get("username"));                                          //  пишем имя печатающего пользователя в тег
                    write.addView(textView);                                                        //  добавляем созданное представление в контейнер
                }
                //        flag = false;
            } else {
                for (int i = 0; i < write.getChildCount(); i++) {                                   //  если пользователь окончил печатать
                    if (((String) write.getChildAt(i).getTag()).equalsIgnoreCase(user.get("username"))) {
                        //  ищем представление оповещения о том что пользователь печатает
                        write.removeViewAt(i);                                                      //  и если находим удаляем его
                    }
                }
            }
        }
    }*/
/*
    //  производит блокирование пользователей чата
    public void blockUsers(){

        if (!securityWS()) {                                                                        //  проверяем соединение с сервером
            head.showMessage("Отсутствует соединение с сервером");
            return;
        }

        ListView spisok = (ListView) head.findViewById(R.id.spisok);                                //  ищем представление списока пользователей
        if (spisok == null || head.getOperation() != ConstHead.CHAT_USERS) return;                  //  проверяем что оно найдено и текущая операция - список пользователей

        ArrayList<String> mass = new ArrayList<>();                                                 //  создаем коллекцию в которую будем класть идентификаторы пользователей
        List<Map<String, String>> list = ((AdapterChatUsers) spisok.getAdapter()).getArrayData();   //  получаем коллекцию данных из адаптера пользователей

        for (int i=0; i<list.size(); i++) {                                                         //  пробегаемся по всей коллекции
            if (Boolean.parseBoolean(list.get(i).get("checked")))
                mass.add(list.get(i).get(DBConnect.KEY_ID_USER));                                   //  ищем выбранных пользователей и добавляем их в уже созданную коллекцию
        }

        if (mass.size() > 0) wsThread.blockUser(mass);                                              //  проверяем что список пользователей для блокирования не пуст и далее отправляем в метод
        else Snackbar.make(head.getRoditel(), "Выберите пользователей", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();                                     //   иначе показываем сообщение о том что пользователи не выбраны
    }

    //  метод для разблокирования пользователей
    public void unlockUsers(){

        if (!securityWS()) {                                                                        //  проверяем соединение с сервером
            head.showMessage("Отсутствует соединение с сервером");
            return;
        }

        ListView spisok = (ListView) head.findViewById(R.id.spisok);                                //  ищем представление списка пользователей

        if (spisok == null ||
                (head.getOperation() != ConstHead.CHAT_USERS && head.getOperation() != ConstHead.CHAT_BLACK_LIST)) {
            //  проверяем что представление найдено и тип операции или - список пользователей или - черный список
            return;
        }

        ArrayList<String> mass = new ArrayList<>();                                                 //  создаем коллекцию для хранения идентификаторов пользователей для разблокировки
        List<Map<String, String>> list = null;                                                      //  коллекция для хранения данных адаптера пользователей или адаптера черного списка
        String key = null;                                                                          //  ключ под которым хранится идентификатор пользователя в коллекции
        if (head.getOperation() == ConstHead.CHAT_BLACK_LIST) {                                     //  если текущее представление черный список
            list = ((AdapterBlackList) spisok.getAdapter()).getArrayData();                         //  получаем коллекцию данных адаптера
            key = DBConnect.KEY_LOCKED;                                                             //  устанвливаем значение ключа из коллекции
        } else if (head.getOperation() == ConstHead.CHAT_USERS) {                                   //  если тип операции - список пользователей
            list = ((AdapterChatUsers) spisok.getAdapter()).getArrayData();                         //  получаем коллекцию данных адаптера
            key = DBConnect.KEY_ID_USER;                                                            //  устанвливаем значение ключа из коллекции
        } else {                                                                                    //  если по какойто причине проскочит иной тип операции
            return;                                                                                 //  то завершаем работу метода
        }

        for (int i=0; i<list.size(); i++) {                                                         //  пробегаемся по коллекции полученной из адаптера
            if (Boolean.parseBoolean(list.get(i).get("checked"))) {                                 //  выбираем отмеченных пользователей
                mass.add(list.get(i).get(key));                                                     //  и добавляем их идентификаторы в результирующую кооллекцию
            }
        }

        if (mass.size() > 0) wsThread.unlockUser(mass);                                             //  проверяем, что пользователи для разблокирования выбраны и отправляем их дальше
        else Snackbar.make(head.getRoditel(), "Выберите пользователей", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();                                     //   иначе показываем сообщение о том, что пользователи не выбраны
    }*/
/*
    //  осуществляет удаление пользователей из чата
    public void removeUsers(){

        if (!securityWS()) {                                                                        //  проверяем соединение с сервером
            head.showMessage("Отсутствует соединение с сервером");
            return;
        }

        ListView spisok = (ListView) head.findViewById(R.id.spisok);                                //  ищем представление списка пользователей
        if (spisok == null || head.getOperation() != ConstHead.CHAT_USERS) return;                  //  проверяем что оно найдено и тип операции - список пользователей

        ArrayList<Integer> mass = new ArrayList<>();                                                //  создаем коллекцию в которую поместим идентификаторы пользователей
        List<Map<String, String>> list = ((AdapterChatUsers) spisok.getAdapter()).getArrayData();   //  получаем ссылку на коллекцию адаптера списка пользователей

        for (int i=0; i<list.size(); i++){                                                          //  пробегаемся по коллекции
            if (Boolean.parseBoolean(list.get(i).get("checked")))
                mass.add(Integer.parseInt(list.get(i).get(DBConnect.KEY_ID_USER)));                 //  выбираем отмеченные и добавляем в коллекцию
        }

        if (mass.size() > 0) wsThread.removeUser(mass);                                             //  проверяем, что пользователи были выбраны и передаем коллекцию дальше
    }

    //  отображает найденых пользователей
    public void showSearchUser(Object obj){

        Map<String, Map<String, Map<String, String>>> map = (Map<String, Map<String, Map<String, String>>>) obj;
        Set<String> keys = map.get("users").keySet();
        if (keys.size() == 0) {                                                                     //  проверяем, что пользователи были найдены
            Snackbar.make(head.getRoditel(), "Список пуст", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
            return;
        }

        ListView spisok = (ListView) head.findViewById(R.id.spisok);                                //  ищем представление списка пользователей
        if (spisok == null) return;                                                                 //  проверяем что представление было найдено

        List<Map<String, String>> list = new ArrayList<>();                                         //  создаем коллекцию параметров пользователей
        for (String k: keys) {                                                                      //  пробегаемся по всем пользователям
            Map<String, String> m = map.get("users").get(k);                                        //  получаем ссылку на карту пользователя
            m.put("checked", "false");                                                        //    устанавливаем флаг выборки
            list.add(m);                                                                            //  добавляем пользователя в коллекцию
        }
        AdapterBlackList adapter = new AdapterBlackList(head, list);                                //  создаем новый адаптер
        spisok.setAdapter(adapter);                                                                 //  добавляем адаптер к списку

        spisok.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {                                 //  вешаем обработчик событий на список
                Map<String, String> map = (Map<String, String>) adapterView.getItemAtPosition(i);   //  получаем ссылку на карту текущего элемента
                if (Boolean.parseBoolean(map.get("checked"))) map.put("checked", "false");    //  инверсируем флаг выборки
                else map.put("checked", "true");
                updateAdapter();                                                                    //  обновляем адаптер
            }
        });
    }


    //  создает представление создания нового чата
    private void showGeneratorChats(View view){

        if (!securityWS()) {                                                                        //  проверяем соединение с сервером
            head.showMessage("Отсутствует соединение с сервером");
            return;
        }
        head.setOperation(ConstHead.CHAT_CREATE);                                                   //  устанавливаем тип операции
        LinearLayout roditel = (LinearLayout) head.findViewById(R.id.roditel);                      //  найдем родителя и почистим его
        roditel.removeAllViews();
        LayoutInflater inflater = (LayoutInflater) head.getLayoutInflater();                        //  получаем ссылку на сервис генерации представлений
        final RelativeLayout search = (RelativeLayout) inflater.inflate(R.layout.chat_search_user, roditel, false);
        //  создаем представление поиска пользователей
        roditel.addView(search);                                                                    //  добавляем созданное представление в родительское

        ((ImageButton) search.findViewById(R.id.search_user)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {         // вешаем слушателя на кнопку поиска пользователей
                head.closeKeyBoard();                                                               //  прячем клавиатуру
                Map<String, String> map = new HashMap<>();                                          //  создаем хэш карту параметров поиска
                //  заполняем карту параметрами поиска пользователей
                map.put("username", ((TextView) search.findViewById(R.id.search_login)).getText().toString());
                map.put("familiya", ((TextView) search.findViewById(R.id.search_familiya)).getText().toString());
                map.put("imya", ((TextView) search.findViewById(R.id.search_name)).getText().toString());
                map.put("otchestvo", ((TextView) search.findViewById(R.id.search_otchestvo)).getText().toString());
                map.put("phone", ((TextView) search.findViewById(R.id.search_phone)).getText().toString());
                wsThread.searchUser(map);                                                           //  отправляем на обработку далее
            }
        });
        ((ImageButton) search.findViewById(R.id.create_chat)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {        //  обработчик кнопки создания нового чата

                List<Map<String, String>> list = ((AdapterBlackList) ((ListView) head.findViewById(R.id.spisok)).getAdapter()).getArrayData();
                //  получаем коллекцию списка пользователей
                ArrayList mass = new ArrayList<Integer>();                                          //  создаем коллекцию идентификаторов пользователей
                for (int i=0; i<list.size(); i++) {                                                 //  пробегаемся по списку пользователей
                    if (Boolean.parseBoolean(list.get(i).get("checked"))) mass.add(list.get(i).get(DBConnect.KEY_ID_USER));
                    //  добавляем в коллекцию идентификаторы отмеченных пользователей
                }
                if (mass.size() == 0) {                                                             //  проверяем, что пользователи для нового чата были выбраны
                    Snackbar.make(head.getRoditel(), "Выберите пользователей", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }

                String name = ((TextView) head.findViewById(R.id.chat_name)).getText().toString();  //  считываем введенное название создаваемого чата
                if (name.length() == 0) {                                                           //  проверяем, что имя не пустое
                    Snackbar.make(head.getRoditel(), "Название чата пусто", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }

                wsThread.createNewChat(name, mass);                                                 //  отправляем собранные данные дальше
                getChats(false, true);                                              //  запрашиваем обновление списка чатов
            }
        });
    }

    //  отображает список чатов


    //  запрашивает список пользователей чата
    public void getUsers(){

        head.setOperation(ConstHead.CHAT_USERS);                                                    //  устанавливаем тип операции
        ((TextView) head.findViewById(R.id.head_zagolov)).setText("Пользователи");                  //  устанавливаем заголовок представления
        head.readDB();                                                                              //  читаем из бд
        wsThread.getChatUsers();                                                                    //  запрашиваем список пользователей с сервера
    }

    //  запрашивает список сообщений чата на последнюю дату в чате
    public void getMessages(){

        head.setOperation(ConstHead.CHAT_MESSAGES);                                                 //  устанавливаем тип операции
        ((TextView) head.findViewById(R.id.head_zagolov)).setText("Сообщения");                     //  устанавливаем заголовок представления
        head.readDB();                                                                              //  читаем из бд
        wsThread.getMessages(null);                                                           //    апрашиваем с сервера, если нужны сообщения на определенную дату, то передаем параметром
    }

    //  запрашивает черный список участников чата
    public void getBlackList(){

        head.setOperation(ConstHead.CHAT_BLACK_LIST);                                               //  устанавливаем тип операции
        ((TextView) head.findViewById(R.id.head_zagolov)).setText("Черный список");                 //  устанавливаем заголовок
        head.readDB();                                                                              //  читаем из бд
        wsThread.getBlackList();                                                                    //  запрашиваем черный список с сервера
    }

    //  собирает тело нового сообщения
    private void generateNewMessage(){

        if (!securityWS()) {                                                                        //  проверяем соединение с сервером
            head.showMessage("Отсутствует соединение с сервером");
            return;
        }
        EditText v = (EditText) head.findViewById(R.id.edit_message);                               //  ищем поле ввода сообщения
        String msg = v.getText().toString();                                                        //  получаем из него текст
        v.setText("");                                                                              //  сбрасываем значение текста
        v.requestFocus();                                                                           //  устанавливаем на него фокус ввода

        if (msg.length() > 0) {                                                                     //  проверяем длинну сообщения
            Integer parent = null;                                                                  //  ссылка на идентификатор родительского сообщения
            if (messagePosition != null) {                                                          //  проверяем выделенно ли какое либо сообщение, установлен его порядковый номер в коллекции
                AdapterChatMessages adapter = (AdapterChatMessages) ((ListView) head.findViewById(R.id.spisok)).getAdapter();
                //  получаем ссылку на адаптер сообщений
                List<Map<String, String>> list = adapter.getArrayData();                            //  получаем ссылку на коллекцию сообщений из адаптера
                parent = Integer.parseInt(list.get(messagePosition).get(DBConnect.KEY_ID));         //  получаем идентификатор родительского сообщения
                list.get(messagePosition).put("checked", "false");                            //  сбрасываем флаг выборки для родительского сообщения
                messagePosition = null;                                                             //  сбрасываем номер родительского сообщения
            }
            fileName = null;                                                                        //  устанавливаем имя файла в нуль
            wsThread.sendMessage(msg, chatNumber, parent, fileName);                                //  отправляем параметры сообщения далее для обработки
        } else {
            Toast.makeText(head, "Введите текст сообщения", Toast.LENGTH_SHORT).show();        //   если длинна сообщения равна 0
        }
    }

    //  собирает тело сообщения содержащего файл
    private void generateNewFileMessage(){

        if (!securityWS()) {                                                                        //  проверяем соединение с сервером
            head.showMessage("Отсутствует соединение с сервером");
            return;
        }
        if (!(new Permissions(head)).proverkaExternalStorage()) return;                             //  проверяем разрешение на работу с внешней памятью

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);                                      //  генерируем намерение выбора файла из проводника
        intent.setType("*//*");      //all files
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            head.startActivityForResult(Intent.createChooser(intent, "Select"), Head.FILE_SELECT_CODE);
            //  запускаем намерение
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(head, "Установите файловый менеджер", Toast.LENGTH_SHORT).show();   //   если файловый менеджер не был найден
        }
    }

    //  собирает тело звукового сообщения
    private boolean generateNewSoundMessage(View view, MotionEvent motionEvent){
        if (!securityWS()) {                                                                        //  проверяем соединение с сервером
            head.showMessage("Отсутствует соединение с сервером");
            return false;
        }
        switch (motionEvent.getAction()) {                                                          //  ветвим по типу касания
            case MotionEvent.ACTION_DOWN:                                                           // нажатие
                if (!(new Permissions(head)).proverkaMicrophoneRecord()) {                          //  проверяем разрешение на запись аудио
                    Toast.makeText(head, "Включите разрешение на запись с микрофона", Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (!(new Permissions(head)).proverkaExternalStorage()) {                           //  проверяем разрешение на работу с внешней памятью
                    Toast.makeText(head, "Включите разрешение на работу с внешней памятью", Toast.LENGTH_SHORT).show();
                    return false;
                }
                LinearLayout layout = (LinearLayout) head.findViewById(R.id.messenger_write);
                //  находим представление отображающее ход записи аудио
                TextView display = (TextView) ((LayoutInflater) head.getLayoutInflater()).inflate(R.layout.user_write, layout, false);
                //  создаем текстовое представление
                timer = new ChatService.Timer(display);                                                    //  создаем таймер
                layout.addView(display);                                                            //  добавляем текстовое представление
                timer.start();                                                                      //  стартуем таймер

                File f = new File(Config.PATH_SD_CARD + Config.DIRECTORY_FILES + "/" + Config.DIRECTORY_CHATS + "/" + chatNumber);
                //  проверяем путь к папке чатов
                if (!f.exists()) {                                                                  //  если не существует
                    if (f.mkdirs()) {                                                               //  создаем полный путь с номером чата
                        Log.d("GRADINAS", "Невозможно создать путь DIRECTORY 4475767876 => " + f.getPath());
                        return false;
                    }
                }
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");           //  создаем объект форматирования даты
                fileName = Config.PATH_SD_CARD + Config.DIRECTORY_FILES + "/" + Config.DIRECTORY_CHATS + "/" + chatNumber
                        + "/" + format.format(new Date()) + ".mp4";                                 //  создаем полый путь к файлу с именем текущей даты

                mediaRecorder = new MediaRecorder();                                                //  создаем объект записывающий медиа файлы
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);                        //  указываем писать с микрофона
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);                   //  указываем конечный формат файла
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);                      //  указываем аудио кодек
                mediaRecorder.setOutputFile(fileName);                                              //  указываем полный путь к файлу
                try {
                    mediaRecorder.prepare();                                                        //  подготавливаем медиа рекордер
                } catch (IOException ex) {
                    Log.d("GRADINAS", "mediaRecorder IOException ex => " + ex.getMessage());
                    return false;
                }
                mediaRecorder.start();                                                              //  стартуем запись
                break;
            case MotionEvent.ACTION_UP:                                                             // отпускание зажатия кнопки записи
            case MotionEvent.ACTION_CANCEL:                                                         //  код отмены или завершения записи
                if (mediaRecorder == null) return false;                                            //  проверяем что медиа рекордер существует
                mediaRecorder.stop();                                                               //  останавливаем запись
                mediaRecorder.release();                                                            //  очищаем медиа рекордер
                mediaRecorder = null;                                                               //  очищаем ссылку на медиа рекордер

                timer.flag = false;                                                                 //  останавливаем отсчет времени
                timer = null;                                                                       //  очищаем ссылку на таймер
                ((LinearLayout) head.findViewById(R.id.messenger_write)).removeAllViews();          //  очищаем представление вывода процесса записи сообщения

                head.addDUFile(FileDUThread.UPLOAD, Config.DIRECTORY_CHATS,
                        String.valueOf(chatNumber), null, fileName, null);           //  отправляем созданный файл сообщения в очередь выгрузки на сервер
                break;
        }
        return false;
    }

    //  выводит список сообщений
    public void showMessages(Object obj){

        List<Map<String, String>> chatMessages = (List<Map<String, String>>) obj;
        LinearLayout roditel = (LinearLayout) head.findViewById(R.id.roditel);                      //  найдем родителя и почистим его
        roditel.removeAllViews();

        LayoutInflater inflater = (LayoutInflater) head.getLayoutInflater();
        RelativeLayout messenger = (RelativeLayout) inflater.inflate(R.layout.messenger, roditel, false);
        roditel.addView(messenger);                                                                 //  создадим и добавим представление месенджера в родительское представление
        ((TextView) messenger.findViewById(R.id.messenger_name_chat)).setText(chatName);            //  установим название чата

        for (int i=0; i<chatMessages.size(); i++) {                                                 //  пробежимся по коллекции собщений чата
            chatMessages.get(i).put("checked", "false");                                      //    добавив в нее флаг выборки сообщения
        }

        AdapterChatMessages messageAdapter = new AdapterChatMessages(head, chatMessages);           //  создадим адаптер сообщений
        ListView spisok = (ListView) messenger.findViewById(R.id.spisok);                           //  найдем представление списка сообщений

        head.fileObserver.setOnCompleteListener(R.id.send_message, new Runnable() {                 //  добавим задачу, которая будет выполнена после загрузки файлов сообщений
            @Override
            public void run() {
                head.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        head.chat.updateAdapter();                                                  //  обновим адаптер сообщений
                    }
                });
            }
        });
        spisok.setAdapter(messageAdapter);                                                          //  установим адаптер в список
        spisok.setSelection(chatMessages.size() - 1);                                               //  установим отображаемое сообщение как последнее из списка

        ((EditText) messenger.findViewById(R.id.edit_message)).addTextChangedListener(new TextWatcher() {
            //  повесим обработчик на поле ввода текста новых сообщений
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {                                       //  после изменения текста сообщения
                if (!securityWS()) {                                                                //  проверяем соединение с сервером
                    head.showMessage("Отсутствует соединение с сервером");
                    return;
                }
                if (totalChar == 0 || (totalChar + 15) < editable.length()) {                       //  проверяем колличество введеннных символов и на каждые новые набранные 15 символов
                    wsThread.userWrite(true);                                                       //  отправляем новое сообщение о печатании пользователя
                    totalChar = editable.length();                                                  //  запоминаем колличество введенных символов
                } else if (editable.length() == 0){                                                 //  если колличество символов равно 0, то пользователь стер сообщение
                    wsThread.userWrite(false);                                                      //  отправляем уведомление что пользователь закончил печатать
                    totalChar = 0;                                                                  //  обнуляем общее коллличество символов
                }
            }
        });

        spisok.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {               //  обработчик нажатия на сообщение
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Map<String, String> map = (Map<String, String>) adapterView.getItemAtPosition(i);   //  получаем параметры сообщения из коллекции
                if (Boolean.parseBoolean(map.get("checked"))) {                                     //  проверяем что это сообщение было выбрано ранее
                    map.put("checked", "false");                                              //  и в этом случае снимаем флаг выборки сообщения
                    messagePosition = null;                                                         //  очищаем номер текущего сообщения
                } else {                                                                            //  иначе уже было выбрано сообщение под номером messagePosition, и нам надо снять флаг выборки с него
                    if (messagePosition != null)                                                    //  проверим что значение не пусто
                        ((Map<String, String>) adapterView.getItemAtPosition(messagePosition)).put("checked", "false");
                    //  и уберем флаг выборки у старого выбранного сообщения
                    map.put("checked", "true");                                               //  установим у свежевыбранного сообщения флаг выборки
                    messagePosition = i;                                                            //  и запомним позицию выбранного сообщения
                }
                updateAdapter();                                                                    //  обновим адаптер
                return false;
            }
        });

        spisok.setOnScrollListener(new AbsListView.OnScrollListener() {                             //  обработчик скролинга списка сообщений
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if (i == 1 & listNumber == 0 /*messageAdapter.getNowItem()*//*) {                     //  если прокрутка была завершена и достигнуто начало списка сообщений, то подгружаем предыдущие сообщения
                    AdapterChatMessages adapter = (AdapterChatMessages) ((ListView) head.findViewById(R.id.spisok)).getAdapter();
                    //  получаем адаптер сообщений и из него извлечем дату
                    wsThread.getMessages(adapter.getDate());                                        //  запрашиваем сообщения с указанной датой
                }
            }
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                listNumber = i;                                                                     //  запоминаем номер первого отображенного в списке сообщения
            }
        });

        ((ImageButton) messenger.findViewById(R.id.sound_message)).setOnTouchListener(new View.OnTouchListener() {
            //  обработчик зажатия кнопки записи звукового сообщения
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return generateNewSoundMessage(view, motionEvent);                                  //  запускаем генератор звукового сообщения
            }
        });

        ((ImageButton) messenger.findViewById(R.id.send_message)).setOnClickListener(new View.OnClickListener() {
            //  обработчик нажатия кнопки - отправка сообщения
            @Override
            public void onClick(View view) {
                generateNewMessage();                                                               //  создаем и отправляем новое сообщение
            }
        });

        ((ImageButton) messenger.findViewById(R.id.attach_file)).setOnClickListener(new View.OnClickListener() {
            //  обрабатывает создание файлового сообщения
            @Override
            public void onClick(View view) {
                generateNewFileMessage();                                                           //  создаем и отправяем файловое сообщение
            }
        });
    }

    //  создает представление добавления нового пользователя в чат
    private void addUserToChat(){

        if (!securityWS()) {                                                                        //  проверяем соединение с сервером
            head.showMessage("Отсутствует соединение с сервером");
            return;
        }

        head.setOperation(ConstHead.CHAT_SEARCH_USER);                                              //  выставляем нужный тип операции
        LinearLayout roditel = (LinearLayout) head.findViewById(R.id.roditel);                      //  найдем родителя и почистим его
        roditel.removeAllViews();

        LayoutInflater inflater = (LayoutInflater) head.getLayoutInflater();
        final RelativeLayout search = (RelativeLayout) inflater.inflate(R.layout.chat_search_user, roditel, false);
        roditel.addView(search);                                                                    //  создадим и добавим к родителю представление поиска и добавления пользователей
        ((EditText) head.findViewById(R.id.chat_name)).setVisibility(View.INVISIBLE);               //  прячем поле ввода названия нового чата,
        // представление "создание чата" и "добавление пользователя в чат" используют один и тот же шаблон
        //  обработка нажатия кнопки поиска пользователей
        ((ImageButton) search.findViewById(R.id.search_user)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                head.closeKeyBoard();                                                               //  прячем клавиатуру
                Map<String, String> map = new HashMap<>();                                          //  создаем карту с параметрами поиска пользователей и заполняем ее
                map.put("username", ((TextView) search.findViewById(R.id.search_login)).getText().toString());
                map.put("familiya", ((TextView) search.findViewById(R.id.search_familiya)).getText().toString());
                map.put("imya", ((TextView) search.findViewById(R.id.search_name)).getText().toString());
                map.put("otchestvo", ((TextView) search.findViewById(R.id.search_otchestvo)).getText().toString());
                map.put("phone", ((TextView) search.findViewById(R.id.search_phone)).getText().toString());
                wsThread.searchUser(map);                                                           //  передаем карту дальше для обработки
            }
        });
        //  обработчик нажатия кнопки добавления пользователей
        ((ImageButton) search.findViewById(R.id.create_chat)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AdapterBlackList adapter = (AdapterBlackList) ((ListView) head.findViewById(R.id.spisok)).getAdapter();
                if (adapter == null) return;                                                        //  ищем и проверяем адаптер списка найденных пользователей по заданным параметрам

                List<Map<String, String>> list = adapter.getArrayData();                            //  получаем коллекцию параметров найденных пользователей
                ArrayList mass = new ArrayList<Integer>();                                          //  создаем коллекцию с идентификаторами пользователей
                for (int i=0; i<list.size(); i++) {                                                 //  пробегаемся по коллекции найденных пользователей
                    if (Boolean.parseBoolean(list.get(i).get("checked")))                           //  выбираем всех отмеченных пользователей
                        mass.add(list.get(i).get(DBConnect.KEY_ID_USER));                           //  и добавляем их в результирующую коллекцию
                }
                if (mass.size() == 0) {                                                             //  проверяем что результат не пуст
                    Snackbar.make(head.getRoditel(), "Выберите пользователей", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }
                wsThread.addUsersToChat(chatNumber, mass);                                          //  полученный результат передаем дальше для обработки
            }
        });
    }

    //  отображает представление списка пользователей
    public void showListUsers(Object obj){

        List<Map<String, String>> response = (List<Map<String, String>>) obj;
        if (response.size() == 0) {                                                                 //  проверяем что данные не пустые
            Snackbar.make(head.getRoditel(), "Список пуст", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

        LinearLayout roditel = (LinearLayout) head.findViewById(R.id.roditel);                      //  найдем родителя и почистим его
        roditel.removeAllViews();

        LayoutInflater inflater = (LayoutInflater) head.getLayoutInflater();
        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) inflater.inflate(R.layout.chat, roditel, false);
        roditel.addView(coordinatorLayout);                                                         //  сгенерируем и добавим представление списка пользователей чата в родительское представление

        for (int i=0; i<response.size(); i++) {                                                     //  пробегаемся по списку пользователей
            if (Integer.parseInt(response.get(i).get(DBConnect.KEY_ID_USER)) != Integer.parseInt(head.getIdMastera())) {
                //  проверяем что пользователь коллекции не он сам
                response.get(i).put("checked", "false");                                      //    и добавляем ему флаг выборки
            } else {                                                                                //  если находим себя в списке
                response.remove(i);                                                                 //  то удаляем себя
            }
        }

        final AdapterChatUsers adapter = new AdapterChatUsers(head, response);                      //  создаем адаптер списка пользователей
        ListView spisok = (ListView) coordinatorLayout.findViewById(R.id.spisok);                   //  находим представление списока
        spisok.setAdapter(adapter);                                                                 //  добавляем списку адаптер

        spisok.setOnItemClickListener(new AdapterView.OnItemClickListener() {                       //  обработчик выбора элемента списка
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Map<String, String> map = (Map<String, String>) adapterView.getItemAtPosition(i);   //  получаем ссылку на элемент коллекции пользователей который вызвал событие

                if (Boolean.parseBoolean(map.get("checked"))) map.put("checked", "false");    //    инверсируем флаг выборки
                else map.put("checked", "true");
                updateAdapter();                                                                    //  обновляем адаптер
            }
        });

        FloatingActionButton fab = (FloatingActionButton) head.findViewById(R.id.create_chat);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUserToChat();
            }
        });
    }

    //  отображает представление черного списка пользователей
    public void showBlackList(Object obj){

        LinearLayout roditel = (LinearLayout) head.findViewById(R.id.roditel);                      //  найдем родителя и почистим его
        roditel.removeAllViews();

        List<Map<String, String>> map = (List<Map<String, String>>) obj;
        if (map.size() == 0) {                                                                      //  проверим что коллекция черного списка не пуста
            Snackbar.make(head.getRoditel(), "Список пуст", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
            return;
        }

        LayoutInflater inflater = (LayoutInflater) head.getLayoutInflater();
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) inflater.inflate(R.layout.chat, roditel, false);
        roditel.addView(coordinatorLayout);                                                         //  создадим и добавим к родителю представление черного списка
        ((FloatingActionButton) head.findViewById(R.id.create_chat)).setVisibility(View.INVISIBLE); //  скроем кнопку создания нового чата,
        // представления "чаты" и "черный список" испоьзуют один шаблон
        for (int i=0; i<map.size(); i++) {                                                          //  пробегаемся по коллекции пользователей
            map.get(i).put("checked", "false");                                               //    и добавляем к каждому элементу флаг выборки
        }

        AdapterBlackList adapter = new AdapterBlackList(head, map);                                 //  создаем адаптер
        ListView spisok = (ListView) coordinatorLayout.findViewById(R.id.spisok);                   //  находим список
        spisok.setAdapter(adapter);                                                                 //  устанавливаем списку адаптер

        spisok.setOnItemClickListener(new AdapterView.OnItemClickListener() {                       //  обработчик клика по элементу списка
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Map<String, String> map = (Map<String, String>) adapterView.getItemAtPosition(i);   //  получаем ссылку на элемент коллекции вызвавший событие
                if (Boolean.parseBoolean(map.get("checked"))) map.put("checked", "false");   //     инверсируем флаг выборки
                else map.put("checked", "true");
                updateAdapter();                                                                    //  обновляем адаптер
            }
        });
    }

    //  запрашивает удаление чата
    public void deleteChat(){

        if (!securityWS()) {                                                                        //  проверяем соединение с сервером
            head.showMessage("Отсутствует соединение с сервером");
            return;
        }
        wsThread.deleteChat();                                                                      //  вызываем метод удаления текущего чата
    }

    //  вызывает выход из чата
    public void exitChat(){

        if (!securityWS()) {                                                                        //  проверяем соединение с сервером
            head.showMessage("Отсутствует соединение с сервером");
            return;
        }
        if (chatNumber != 0 && !(head.getOperation() == ConstHead.CHAT_MESSAGES)) return;            //  проверяем что номер чата определен и текущая операция - список сообщений чата

        wsThread.exitChat();                                                                        //  удаляем чат
    }

 /*    не помню где использовал, возможно нужен
    public void newChat(Object obj){
     //   Log.d("GRADINAS", "Chat newChat() operation => " + head.getOperation());

        Map<String, Map<String, Map<String, String>>> response = (Map<String, Map<String, Map<String, String>>>) obj;

        if (head.getOperation() != ConstHead.CHATS) {
            Log.d(head.wer, "OPERACIYA != CHATS");
            return;
        } else {
            Log.d(head.wer, "OPERACIYA ==  CHATS");
        }
        ListView spisok = (ListView) head.findViewById(R.id.spisok);
        if (spisok == null) return;

        Set<String> keys = response.get("chats").keySet();

        final AdapterListChats adapter = (AdapterListChats) ((ListView) head.findViewById(R.id.spisok)).getAdapter();
        List<Map<String, String>> list = adapter.getData();
        for (String k: keys) { list.add(response.get("chats").get(k)); }
        updateAdapter();

        spisok.post(new Runnable() {
            @Override
            public void run() {
                ((ListView) head.findViewById(R.id.spisok)).smoothScrollToPosition(adapter.getCount() - 1);
            }
        });
    }
*/
/*
    //  отображает новое входящее сообщение
    public void newMessage(Object obj){

        Map<String, Map<String, Map<String, String>>> response = (Map<String, Map<String, Map<String, String>>>) obj;
        Map<String, String> message = null;                                                         //  ссылка на новое входящее сообщение
        Set<String> keys = response.get("messages").keySet();
        for (String k : keys) {                                                                     //  пробегаемся по коллекции сообщений (в коллекции только одно)
            message = response.get("messages").get(k);                                              //  получаем ссылку на наше сообщение
            break;
        }
        if (message == null) return;                                                                //  проверяем что оно не пустое

        if (head.getOperation() == ConstHead.CHATS) {                                               //  если текущее представление - чаты
            AdapterListChats adapter = (AdapterListChats) ((ListView) head.findViewById(R.id.spisok)).getAdapter();
            List<Map<String, String>> list = adapter.getData();                                     //  получаем ссылку на коллекцию списка чатов из адаптера

            for (int i=0; i<list.size(); i++) {                                                     //  пробегаемся по всей коллекции
                if (Integer.parseInt(message.get(DBConnect.KEY_ID_CHAT)) == Integer.parseInt(list.get(i).get(DBConnect.KEY_ID_CHAT))) {
                    //  ищем чат для которого пришло новое сообщение
                    Map<String, String> map = list.remove(i);                                       //  удаляем наш чат из коллекции
                    list.add(0, map);                                                            //     добавляем его в начало коллекции, чтобы он занял первое место в списке
                    map.put("alarm", String.valueOf(Integer.parseInt(map.get("alarm")) + 1));    //  выставляем значение входящих непрочитанных сообщений

                    ((ListView) head.findViewById(R.id.spisok)).setSelection(i);                    //  устанавливаем выбранный элемент в коллекции, тот который был выбран ранне
                    break;
                }
            }
            adapter.notifyDataSetChanged();                                                         //  обновляем адаптер
        }


        этот кусок обрабатывает оповещение о входящем сообщении для свернутого окна приложения или операциях отличных от чатовских,
        метод рабочий, но при разворачивании активности вызывает пересоздание потока чата, что приводит к постепенному накоплению одновременно работающих потоков,
        вызовет колосальную перегрузку сервера и клиента уже через несколько минут работы
        if (!head.activityStarted) {
            Intent resultIntent = new Intent(head, Head.class);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(head, 0, resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(head)
                    .setSmallIcon(R.drawable.logo30)
                    .setLargeIcon(BitmapFactory.decodeResource(head.getResources(), R.drawable.logo100))
                    .setContentTitle(message.get(DBConnect.KEY_AUTHOR))
                    .setContentText(message.get(DBConnect.KEY_MESSAGE))
                    .setContentIntent(resultPendingIntent)
                    .setAutoCancel(true);
            Notification notification = builder.build();
            notification.defaults = Notification.DEFAULT_SOUND;
            NotificationManager notificationManager = (NotificationManager) head.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(head.pechnoyMir, notification);

            return;
        } else {
        /***  этот условие будет излишним после раскоментирования основного куска */
/*        if (head.getOperation() == ConstHead.CHATS)
        /**      ***/
/*            (RingtoneManager.getRingtone(head, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))).play();   //  оповещение звуковым сигналом
        //  }


        //  дальнейший код выполняется для сообщения из текущего чата и при текущей операции - мессенджер
        if (head.getOperation() != ConstHead.CHAT_MESSAGES || Integer.parseInt(message.get("id_chat")) != chatNumber) {
            return;
        }

        LinearLayout write = (LinearLayout) head.findViewById(R.id.messenger_write);                //  находим представление печатания пользователя от которого пришло сообщение
        for (int i=0; i<write.getChildCount(); i++) {
            if (((String) write.getChildAt(i).getTag()).equalsIgnoreCase(message.get("author"))) {
                write.removeViewAt(i);                                                              //  и удаляем его
            }
        }

        Map<String, String> map = new HashMap<String, String>();                                                 //     создаем новую карту параметров сообщения, т.к. ответ сервера и адаптер не совместимы
        map.put("id", String.valueOf(message.get("id")));                                        //     заполняем карту соотносящимися параметрами
        map.put("id_chat", String.valueOf(message.get("id_chat")));
        map.put("id_user", String.valueOf(message.get("id_user")));
        if (message.get("parent_id").length() != 0) map.put("parent_id", String.valueOf(message.get("parent_id")));
        map.put("message", message.get("message"));
        if (message.get("file").length() != 0) map.put("file", message.get("file"));
        map.put("date", message.get("date"));
        map.put("time", message.get("time"));
        map.put("author", message.get("author"));
        map.put("status_message", message.get("status_m"));

        ListView spisok = (ListView) head.findViewById(R.id.spisok);                                //  находим представление списка сообщений
        AdapterChatMessages adapter = (AdapterChatMessages) spisok.getAdapter();                    //  получаем адаптер
        adapter.getArrayData().add(message);                                                        //  производим слияние данных адаптера и нового сообщения
        adapter.notifyDataSetChanged();                                                             //  обновляем адаптер

        spisok.post(new Runnable() {                                                                //  добавляем задачу прокрутки списку
            @Override
            public void run() {
                ListView spisok = (ListView) head.findViewById(R.id.spisok);                        //  ищем списк
                AdapterChatMessages adapter = (AdapterChatMessages) spisok.getAdapter();            //  получаем адаптер
                spisok.smoothScrollToPosition(adapter.getCount() - 1);                              //  прокручивам список сообщений до конца
            }
        });
    }

    //  запрашивает обновление списка чатов, флаги указывают источники обновления
    public void getChats(boolean dbRead, boolean network){

        ((TextView) head.findViewById(R.id.head_zagolov)).setText("Чаты");                          //  устанавливаем заголовок представления
        head.setOperation(ConstHead.CHATS);                                                         //  устанавливаем тип операции
        if (dbRead) head.readDB();                                                                  //  запрос обновления из локальной бд
        if (network & securityWS()) wsThread.getChats();                                            //  запрос обновления по сети
    }

    //  производит проверку соединения с сервером по протоколу WS, возвращает истину или ложь в зависимоти от результата проверки
    public boolean securityWS(){

        if (wsThread != null && wsThread.server != null && wsThread.server.isOpen()) return true;
        else return false;
    }

    //  возвращает номер текущего чата
    public int getChatNumber(){
        return chatNumber;
    }
    //  устанавливает номер текущего чата
    public void setChatNumber(int chatNumber){
        this.chatNumber = chatNumber;
    }
    //  возвращает ссылку на соединение WS
    public WSThread getWsThread(){
        return this.wsThread;
    }
*/
}
