package com.orion.messenger.ui.chat;

import android.util.Log;

import com.google.gson.Gson;
import com.orion.messenger.MainActivity;
import com.orion.messenger.ui.data.Model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

//  класс является оберткой нижнего уровня для работы с WS сервером
public class WSThread extends Thread {

    public static WSThread wsThread;

    private IBufferWS dataBuf;
    private MyWebSocket server = null;

    boolean stop;

    public WSThread(IBufferWS data){
        super();
        this.dataBuf = data;
        WSThread.wsThread = this;
    }

    public void StopWS() {
        this.stop = true;
    }

    @Override
    public void run() {
        super.run();

    int interation = 0;
        stop = false;
        do {
            try {
                if (server == null || !server.isConnected()){
                    createWSServer();
                    runWSServer();
                    Log.e(ConstWSThread.LOG_TAG, "WS BLOCK server == null || !server.isConnected()");
                }

                sleep(2000);
            } catch (InterruptedException e) {
                Log.e(ConstWSThread.LOG_TAG, "WSThread don't stopped!");
            } catch (URISyntaxException e) {
                Log.e(ConstWSThread.LOG_TAG, "BAD URL ADDRESS, WORK STOPED!");
                break;
            }

            interation++;
           Log.e(ConstWSThread.LOG_TAG,"INteration WSThread => " + interation);
        }while (!stop);
    }

    public void sendMessage(Map<String, String> data) {

        if (this.server != null && !this.server.isConnected()) {
            return;
        }

        String msg = new Gson().toJson(data);
        Log.d(ConstWSThread.LOG_TAG, "Sended data => " + msg);
        server.send(msg);
    }

    public void onConnectedServer() {
        MainActivity.activvity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainActivity.activvity.chat.onConnectedServer();
            }
        });
    }

    private void createWSServer() throws URISyntaxException {
        server = new MyWebSocket(
                new URI(Config.WSS  + Config.DOMAIN  + ":" + Config.WS_PORT),
                this);
    }

    private void runWSServer(){
        server.connect();
    }

    public void newResponse(ResponseServer responseServer) {
        dataBuf.addResponseData(responseServer);
    }

    //  подготовка строки сообщения о том что пользователь печатает новое сообщение
 /*   public void userWrite(boolean write){

        if (chat.head.getOperation() != ConstHead.CHAT_MESSAGES) return;                            //  проверяем тип операции
        String msg = "";                                                                            //  создаем ссылку на строку сообщения
        if (write){                                                                                 //  если пользователь печатает
            msg = "{\"action\":"+ ConstWSThread.OP_WRITEN +", \"id_chat\":" + chat.chatNumber + ", \"write\":true}";
        } else {                                                                                    //  если пользователь окончил печатать
            msg = "{\"action\":"+ ConstWSThread.OP_WRITEN +", \"id_chat\":" + chat.chatNumber + ", \"write\":false}";
        }
        wsSend(msg);                                                                                //  перенаправляем с метод отправки
    }*/

 /*   //  отвечаете за отправку служебного сообщения менеджеру о действиях мастера
    public void sendManager(int status, int id){

        String msg = "{\"action\":" + ConstWSThread.OP_SYSTEM + ", \"status\":" + status + ", \"id\":" + id + "}";
        wsSend(msg);
    }*/

    //  осуществляет отправку и нормализацию сообщения в логах
  /*  private void wsSend(String msg){

        if (server != null && server.isOpen()) {                                                    //  проверяем соединение с сервером
            String clone1 = new String(msg);                                                        //  клонируем строку, на сервер уйдет нетронутый вариант
            int index = clone1.lastIndexOf("action");                                           //  ищем индекс первого вхождения строки, которая указывает тип операции
            if (index != -1) {                                                                      //  проверяем что вхождение найдено
                int number = Integer.parseInt(clone1.substring(index + 8, index + 11));             //  парсим трех значный код операции из строки
                String clone2 = clone1.substring(index + 11, clone1.length());                      //  получаем вторую часть сообщения после кода операции
                clone1 = clone1.substring(0, index + 8);                                            //  получаем первую часть сообщения до номера операции
                clone1 = clone1 + ConstWSThread.getString(number) + clone2;                         //  сливаем части, встраивая текстовое представление кода операции
                Log.d("GRADINAS", "WS: SEND MESSAGE => " + clone1);                       //    печатаем сообщение в лог
            } else {
                Log.d("GRADINAS", "WS: SEND MESSAGE => " + msg);                          //    если вхождение не было найдено, то печатаем оригинал сообщения
            }

            server.send(msg);                                                                       //  отправляем сообщение на сервер
        }
    }

    //  запрашивает список чатов
    public void getChats(){

        if (server == null) {                                                                       //  проверяем, что сервер не  пуст
            Log.d(wer, "ERROR 65645426: WS сервер не создан");
            return;
        }

        String msg = "{\"action\":"+ ConstWSThread.OP_GET_CHATS +"}";                               //  создаем строку сообщения
        if (server.isOpen()) {                                                                      //  проверяем что соединение открыто
            wsSend(msg);                                                                            //  отправляем
        } else {
            Log.d(wer, "ИСХОДЯЩЕЕ ОТЛОЖЕННОЕ СООБЩЕНИЕ => " + msg);
            addDefferedMessages(msg);                                                               //  или если соединения нет, то помещаем сообщение в отложенные
        }
    }

    //  запрашивает список сообщений для чата, на указанную или последнюю дату
    public void getMessages(String date){

        int id = chat.getChatNumber();                                                              //  копируем номер чата
        if (id <= 0) {                                                                              //  проверяем на допустимость
            Log.d(wer, "ERROR 6512623: GET MESSAGES ERROR => chatNumber = 0");
            return;
        }
        String msg = "{\"action\":"+ ConstWSThread.OP_GET_HISTORY_MESSAGE +", \"id\":\"" + id
                + "\", ";                                                                           //  создаем строку сообщения
        if (date != null) msg += "\"date\":\"" + date + "\", \"loadingData\":\"" + true + "\"}";    //  если дата указана, то добавляем ее и выставляем флаг подгрузочного сообщения
        else msg += "\"loadingData\":\"" + false + "\"}";                                           //  если даты нет, то придут сообщения на последнюю доступную дату

        if (server.isOpen()) {                                                                      //  проверяем соединение с сервером
            wsSend(msg);                                                                            //  отправляем
        } else {
            Log.d(wer, "ИСХОДЯЩЕЕ ОТЛОЖЕННОЕ СООБЩЕНИЕ => " + msg);
            addDefferedMessages(msg);                                                               //  иначе помещаем в отложенные сообщения
        }
    }

    //  отправляет статус сообщения для данного пользователя
    public void sendStatus(int idChat, int idMessage, String status){

        String msg = "{\"action\":"+ ConstWSThread.OP_STATUS_MESSAGE +", \"id_chat\":\"" + idChat
                + "\", \"id\":\"" + idMessage + "\", \"status\":\"" + status + "\"}";
        wsSend(msg);
    }

    //  класс используется временный шаблон сообщения
    class SearchData {
        public int action;                                                                          //  тип операции
        public Map<String, String> search;                                                          //  набор параметров операции
    }
    //  производит запрос поиска пользователей по указанным параметрам
    public void searchUser(Map<String, String> data){

        if (data.size() == 0) return;                                                               //  проверяем что параметры заданы
        SearchData searchData = new SearchData();                                                   //  создаем класс обертку
        searchData.action = ConstWSThread.OP_SEARCH_USER;                                           //  указываем тип операции
        searchData.search = data;                                                                   //  добавляем данные
        Gson gson = new Gson();                                                                     //  создаем JSON конвертер
        String msg = gson.toJson(searchData);                                                       //  конвертируем объект в строку

        wsSend(msg);                                                                                //  отправляем
    }

    //  запрашивает список пользователей чата
    public void getChatUsers(){

        if (chat.head.getOperation() != ConstHead.CHAT_MESSAGES
                & chat.head.getOperation() != ConstHead.CHAT_USERS
                & chat.head.getOperation() != ConstHead.CHAT_BLACK_LIST
                & chat.head.getOperation() != ConstHead.CHAT_SEARCH_USER) return;                   //  проверяем тип операции
        if (chat.chatNumber <= 0) {                                                                 //  проверяем номер чата
            Log.d(wer, "ERROR 15578513: GET CHAT USERS ERROR => chatNumber = 0");
            return;
        }
        String msg = "{\"action\":"+ ConstWSThread.OP_LIST_USERS +", \"id\":\"" + chat.chatNumber + "\"}";
        //  создаем строку сообщения
        if (server.isOpen()) {                                                                      //  проверяем соединение
            wsSend(msg);                                                                            //  отправляем
        } else {
            Log.d(wer, "ИСХОДЯЩЕЕ ОТЛОЖЕННОЕ СООБЩЕНИЕ => " + msg);
            addDefferedMessages(msg);                                                               //  если соединение отсутствует, то помещаем в отложенные сообщения
        }
    }

    //  запрос черного списка
    public void getBlackList(){

        if (chat.head.getOperation() != ConstHead.CHATS
                & chat.head.getOperation() != ConstHead.CHAT_MESSAGES
                & chat.head.getOperation() != ConstHead.CHAT_USERS
                & chat.head.getOperation() != ConstHead.CHAT_BLACK_LIST) return;                    //  проверяем тип операции
        String msg = "{\"action\":" + ConstWSThread.OP_BLACK_LIST_USERS + "}";                      //  генерируем сообщение
        if (server.isOpen()) {                                                                      //  проверяем соединение
            wsSend(msg);                                                                            //  отправляем
        } else {
            Log.d(wer, "ИСХОДЯЩЕЕ ОТЛОЖЕННОЕ СООБЩЕНИЕ => " + msg);
            addDefferedMessages(msg);                                                               //  помещаем в отложенные
        }
    }

    //  осуществляет выход из чата
    public void exitChat(){

        if (chat.chatNumber != 0 & chat.head.getOperation() != ConstHead.CHAT_MESSAGES
                & chat.head.getOperation() != ConstHead.CHAT_USERS
                & chat.head.getOperation() != ConstHead.CHAT_BLACK_LIST) return;                    //  проверяем тип операции
        String msg = "{\"action\":"+ ConstWSThread.OP_EXIT_CHAT +", \"id\":\"" + chat.chatNumber + "\"}";
        //  создаем сообщение
        wsSend(msg);                                                                                //  отправляем
    }

    //  осуществляет блокирование пользоваелей, в коллекции находятся номера пользователей в строковом виде
    public void blockUser(ArrayList<String> users){

        if (chat.head.getOperation() != ConstHead.CHAT_USERS) return;                               //  проверяем тип операции
        String msg = "{\"action\":"+ ConstWSThread.OP_BLOCK_USERS +", \"users\":"
                + (new Gson()).toJson(users) + ", \"blackList\": \"false\" }";                      //  создаем сообщение, blackList указывает идет ли вызов из представления списка пользователей
        wsSend(msg);                                                                                //  отправляем
    }

    //  разблокирование пользователей, в коллекции находятся номера пользователей в строковом виде
    public void unlockUser(ArrayList<String> users){

        if (chat.head.getOperation() != ConstHead.CHAT_USERS
                & chat.head.getOperation() != ConstHead.CHAT_BLACK_LIST) return;                    //  проверяем тип операции
        boolean blackList = false;                                                                  //  флаг определяет из какого представления был осуществлен вызов (черный список или список пользователей)
        if (chat.head.getOperation() == ConstHead.CHAT_BLACK_LIST) blackList = true;                //  проверяем что из черного списка

        String msg = "{\"action\":"+ ConstWSThread.OP_UNLOOCK_USERS +", \"users\":"
                + (new Gson()).toJson(users) + ", \"blackList\": \"" + String.valueOf(blackList) + "\" }";
        wsSend(msg);                                                                                //  отправляем
    }

    //  удаляет пользователей из чата, в коллекции указаны номера поьзователей
    public void removeUser(ArrayList<Integer> users){

        if (chat.head.getOperation() != ConstHead.CHAT_USERS || chat.chatNumber == 0) return;       //  проверяем тип операции
        String msg = "{\"action\":"+ ConstWSThread.OP_REMOVE_USER +", \"id_chat\": \""
                + chat.chatNumber + "\", \"users\":" + (new Gson()).toJson(users) + "}";            //  gson используем для конвертирования коллекции идентификаторов
        wsSend(msg);                                                                                //  отправляем
    }

    //  удаляет чат
    public void deleteChat(){

        if (chat.head.getOperation() != ConstHead.CHAT_MESSAGES) return;                            //  проверяем тип операции
        String msg = "{\"action\":"+ ConstWSThread.OP_REMOVE_CHAT +", \"id\":\"" + chat.chatNumber + "\"}";
        wsSend(msg);
    }

    //  отправляет новое сообщение пользователя, где parentId - номер родительского сообщения, fileName - имя файла прилогающегося к сообщению
    public void sendMessage(String msg, int idChat, Integer parentId, String fileName){

        if (server != null && server.isOpen()) {                                                    //  проверяем соединение
            String msg1 = "{\"action\":"+ ConstWSThread.OP_NEW_MESSAGE +", \"id_chat\":\""
                    + idChat + "\", \"message\":\"" + msg + "\"";
            if (fileName != null) msg1 += ", \"file\":\"" + fileName + "\"";                        //  если имя файла не пусто, то записываем в параметр
            if (parentId != null) msg1 += ", \"parent_id\":" + parentId.intValue();                 //  если родительское сообщение указано, то записываем в параметр
            msg1 += "}";
            wsSend(msg1);                                                                           //  отправляем
        }
    }

    //  вспомогательный класс для генерации тела сообщения
    class AddUsers{
        public int action = ConstWSThread.OP_ADD_USER;                                              //  тип операции
        public String id_chat;                                                                      //  номер чата
        public ArrayList<Integer> users;                                                            //  список идентификаторов добавляемых пользователей
    }
    //  осуществляет запрос добавления пользователей в чат, id - номер чата, users - коллекция идентификаторов пользователей
    public void addUsersToChat(int id, ArrayList<Integer> users){

        AddUsers data = new AddUsers();                                                             //  созаем новый вспомогательный объект
        data.id_chat = String.valueOf(id);                                                          //  указываем номер чата
        data.users = users;                                                                         //  указываем коллекцию идентификаторов
        String msg = (new Gson()).toJson(data);                                                     //  конвертируем объект в строку json
        wsSend(msg);                                                                                //  отправляем
    }

    //  вспомогательный класс для запроса создания нового чата
    class NewChat {
        public int action = ConstWSThread.OP_CREATE_NEW_CHAT;                                       //  тип операции
        public String chat_name;                                                                    //  название нового чата
        public ArrayList<Integer> users;                                                            //  коллекция идентификаторов пользователей чата
    }
    //  осуществляет запрос создания нового чата
    public void createNewChat(String name, ArrayList<Integer> users){

        NewChat data = new NewChat();                                                               //  создаем объект запроса
        data.chat_name = name;                                                                      //  устанавливаем имя чата
        data.users = users;                                                                         //  указываем коллекцию идентификаторов пользователей чата
        String msg = (new Gson()).toJson(data);                                                     //  конвертируем объект в строку json
        wsSend(msg);                                                                                //  отправляем
    }

    //  добавляет строку сообщения в очередь отложенных сообщений
    public void addDefferedMessages(String msg){

        messages.add(msg);
    }

    //  класс представляет собой  WS клиента
*/
}


