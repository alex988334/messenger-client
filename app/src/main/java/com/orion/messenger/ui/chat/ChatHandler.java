package com.orion.messenger.ui.chat;

import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

//import com.gradinas.masterpech.filemanager.FileDUThread;


import androidx.lifecycle.ViewModelProvider;

import com.orion.messenger.MainActivity;
import com.orion.messenger.ui.about.AboutProjectViewModel;
import com.orion.messenger.ui.data.Chat;
import com.orion.messenger.ui.data.ChatUser;
import com.orion.messenger.ui.data.Message;
import com.orion.messenger.ui.data.MessageStatus;
import com.orion.messenger.ui.data.Model;
import com.orion.messenger.ui.data.User;
import com.orion.messenger.ui.data.UserPhone;
import com.orion.messenger.ui.message.MessageFragment;
import com.orion.messenger.ui.message.MessageViewModel;
import com.orion.messenger.ui.settings.SettingsFragment;
import com.orion.messenger.ui.user.AboutUserFragment;
import com.orion.messenger.ui.user.AboutUserViewModel;
import com.orion.messenger.ui.user.AppendUserFragment;
import com.orion.messenger.ui.user.AppendUserViewModel;
import com.orion.messenger.ui.user.AutorizateFragment;
import com.orion.messenger.ui.user.AutorizateViewModel;
import com.orion.messenger.ui.user.ListUsersFragment;
import com.orion.messenger.ui.user.ListUsersViewModel;
import com.orion.messenger.ui.user.RegistrationFragment;
import com.orion.messenger.ui.user.RegistrationViewModel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

//  класс служит оберткой для класса WSThread, отвечает за полное функционирование чат потока и вызов нужных команд
public class ChatHandler {

    private ISupportOperation handlerUI;
    private IBufferWS dataBuffer;
    private ChatHandlerProperty settings;
    private WSThread wsThread;
    private boolean isAutorizate;

    private int authorizationErrorCounter;

    private Integer currentFragment = 0;

    private boolean allowedRequestingListMessages = true;

    public ChatHandler(ISupportOperation handler){
        super();
        Log.d(ConstWSThread.LOG_TAG, "ChatHandler construct()!");
        this.handlerUI = handler;
        dataBuffer = new BufferWS(this);

        ChatHandler c = this;

        Runnable r = new Runnable() {
            @Override
            public void run() {
                c.onConnectedServer();
            }
        };

     /*   if (WSThread.wsThread == null || WSThread.wsThread.getState() == Thread.State.TERMINATED) {
            Log.d(ConstWSThread.LOG_TAG, "ChatHandler WSThread.wsThread = new WSThread(dataBuffer, r)!");
            WSThread.wsThread = new WSThread(dataBuffer, r);
            WSThread.wsThread.setCallbackOnOpenConnection(r);
        }*/

        wsThread = new WSThread(dataBuffer, r);
       // wsThread.setCallbackOnOpenConnection(r);
        settings = new ChatHandlerProperty();
    }

    public void acceptNewRequest(){
        allowedRequestingListMessages = true;
    }

    private String getFragmentName(int idFragment){
        switch (idFragment) {
            case ChatFragment.ID: return "ChatFragment";
            case AutorizateFragment.ID: return "ChatFragment";
            case MessageFragment.ID: return "ChatFragment";
            case ListUsersFragment.ID: return "ChatFragment";
            case AppendUserFragment.ID: return "ChatFragment";
            case RegistrationFragment.ID: return "ChatFragment";
            case AboutUserFragment.ID: return "ChatFragment";
            case SettingsFragment.ID: return "ChatFragment";
            default: return "";
        }
    }

    public void setCurrentFragment(int currentFragment) {
        Log.e("", "CURRENT FRAGMENT -> " + getFragmentName(currentFragment));
        this.currentFragment = currentFragment;
    }
    public int getCurrentFragment(){
        return currentFragment;
    }

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

                handlerUI.stopProgress();

                ResponseServer responseServer = dataBuffer.getResponseData();
                if (responseServer == null) {
                    Log.d(ConstWSThread.LOG_TAG, "ChatHandler get empty response server!");
                    return;
                }

                responseServer.fillCollectionForNullLinks();

                if (responseServer.Status.Status == ConstWSThread.STATUS_ERROR) {

                    if (responseServer.Status.Message.equals("ERROR! You must be logged in")) {
                        autorizationUser();
                        return;
                    }

                    if (responseServer.Status.Operation == StatusRequest.OP_REGISTRATION){

                        handlerUI.showMessage(responseServer.Status.Message);
                        return;
                    }
                    if (responseServer.Status.Operation == StatusRequest.OP_AUTORIZATE){

                        if (authorizationErrorCounter > 2) {
                            handlerUI.showMessage("Failed to log in automatically, please enter your credentials and try again");
                            handlerUI.createAutorizateUI();
                        } else {
                            authorizationErrorCounter++;
                            autorizationUser();
                        }
                        return;
                    }

                    handlerUI.showMessage("Operation failed, validate input data or repeat operation");
                    Log.d(ConstWSThread.LOG_TAG, "Operation failed! " + responseServer.Status.Message);
                    return;
                }

             //   Log.d(ConstWSThread.LOG_TAG, "response => " + responseServer.toString());
                processData(responseServer);
            }
        });
    }

    private void processOkAutorizate(ResponseServer response){

        authorizationErrorCounter = 0;

        if (response.User.isEmpty()) {
            Log.e(ConstWSThread.LOG_TAG, "ChatHandler error! User data in response is empty");
            settings.password = "";
            settings.login = "";
            return;
        }
        User u = response.User.get(0);
        settings.authKey = u.AuthKey;
        settings.userId = u.Id;

        isAutorizate = true;
        handlerUI.onAutorization();

        if (currentFragment == ChatFragment.ID) {
            getListChats();
        }
        if (currentFragment == AutorizateFragment.ID || currentFragment == RegistrationFragment.ID) {
            getListChats();
            handlerUI.createChatUI();
        }
    }

    private void processOkRegistration(ResponseServer response){

        if (response.User.isEmpty()) {
            Log.e(ConstWSThread.LOG_TAG,
                    "ChatHandler error! User data in response is empty");
            settings.password = "";
            return;
        }
        User u = response.User.get(0);
        settings.authKey = u.AuthKey;

        handlerUI.onRegisteration();
        autorizationUser();
    }

    private void processOkGetMessages(ResponseServer response){

        MessageViewModel mMessages =
                (MessageViewModel) handlerUI.getViewModel(MessageViewModel.class.getName());

        ResponseServer old = mMessages.getResponseServer();
        Collections.reverse(response.Message);

        if (old == null) {
            int ind = response.Message.size()-1;
            mMessages.setCurrentMessageInd(ind);
        } else {
            response.appendData(old);
            int ind = response.Message.size()- old.Message.size() - 1;
            mMessages.setCurrentMessageInd(ind);
        }
        mMessages.setResponseServer(response);

        if (response.MessageStatus.size() > 0 &&
                !response.MessageStatus.get(response.MessageStatus.size() - 1)
                        .Status.equals(MessageStatus.MESSAGE_READED)){

            changeMessageStatus(MessageStatus.MESSAGE_READED);
        }
    }

    public void changeMessageStatus(String status) {

        AboutUserViewModel mUser =
                (AboutUserViewModel) handlerUI.getViewModel(AboutUserViewModel.class.getName());

        if (currentFragment == MessageFragment.ID) {
            ResponseServer r = ((MessageViewModel)
                    handlerUI.getViewModel(MessageViewModel.class.getName())).getResponseServer();

            if (r.Message.size() > 0) {
                MessageStatus ms = new MessageStatus();
                ms.MessageId = r.Message.get(r.Message.size() - 1).Id;
                ms.UserId = mUser.getUserId();
                ms.Status = status;

                wsThread.sendMessage(generateRequestData(StatusRequest.OP_STATUS_MESSAGE, ms));
            }
        }

        if (currentFragment == ChatFragment.ID) {
            ResponseServer r = ((ChatViewModel)
                    handlerUI.getViewModel(ChatViewModel.class.getName())).getResponseServer();

            for (MessageStatus ms : r.MessageStatus) {
                if (!ms.Status.equals(MessageStatus.MESSAGE_READED)) {

                    Timer timer = new Timer();
                    TimerTask tt = new TimerTask() {
                        @Override
                        public void run() {
                            MessageStatus mss = new MessageStatus();
                            mss.MessageId = ms.MessageId;
                            mss.UserId = mUser.getUserId();
                            mss.Status = status;

                            wsThread.sendMessage(generateRequestData(StatusRequest.OP_STATUS_MESSAGE, mss));
                        }
                    };
                    timer.schedule(tt, 150);
                }
            }
        }
    }

    private void processOkGetChats(ResponseServer response){

        ChatViewModel mChat = (ChatViewModel) handlerUI.getViewModel(ChatViewModel.class.getName());
        mChat.setResponseServer(response);

        changeMessageStatus(MessageStatus.MESSAGE_DELIVERED);
    }

    private void processOkNewChat(ResponseServer response) {

        ChatViewModel mChat =
                (ChatViewModel) handlerUI.getViewModel(ChatViewModel.class.getName());
        ResponseServer r = mChat.getResponseServer();

        r.Chat.add(response.Chat.get(0));
        mChat.setResponseServer(r);
    }

    private void processOkListUsers(ResponseServer response) {

        ListUsersViewModel viewModel =
                (ListUsersViewModel) handlerUI.getViewModel(ListUsersViewModel.class.getName());
        viewModel.setResponseServer(response);
    }

    private void processOkRemoveUserFromChat(ResponseServer response) {

        ChatViewModel mChat = (ChatViewModel) handlerUI.getViewModel(ChatViewModel.class.getName());
        ListUsersViewModel mUsers =
                (ListUsersViewModel) handlerUI.getViewModel(ListUsersViewModel.class.getName());

        if (mChat.getCurrentChat() == response.ChatUser.get(0).Chat) {
            ResponseServer r1 = mUsers.getResponseServer();
            for (int i=0; i < r1.ChatUser.size(); i++) {
                if (r1.ChatUser.get(i).User == response.ChatUser.get(0).User) {
                    r1.ChatUser.remove(i);
                    break;
                }
            }
            mUsers.setResponseServer(r1);
        }
    }

    private void processOkAppendUserInChat(ResponseServer response){

        handlerUI.showMessage("User append");
       /* MessageViewModel viewModel = (MessageViewModel) handlerUI.getViewModel(MessageViewModel.class.getName());
        ResponseServer r = viewModel.getResponseServer();
        r.appendData(response);
        viewModel.setResponseServer(r);*/
    }

    private MessageStatus createMSmodel(long messageId, int userId) {

        MessageStatus ms = new MessageStatus();
        ms.MessageId = messageId;
        ms.UserId = userId;
        ms.Status = MessageStatus.MESSAGE_DELIVERED;

        return ms;
    }

    private void appendMessageToChatModel(ChatViewModel mChat, ResponseServer response, MessageStatus ms) {

        ResponseServer r = mChat.getResponseServer();
        for (int i=0; i < r.Message.size(); i++) {

            if (r.Message.get(i).ChatId == response.Message.get(0).ChatId) {

                r.Message.set(i, response.Message.get(0));
                r.MessageStatus.set(i, ms);
                mChat.setResponseServer(r);
                return;
            }
        }
    }

    private void appendMessageToMessageModel(MessageViewModel mMessage, ResponseServer response, MessageStatus ms) {

        ResponseServer r = mMessage.getResponseServer();
        r.appendData(response);
        mMessage.setResponseServer(r);
    }

    private void processOkNewMessage(ResponseServer response) {

        ChatViewModel mChat = (ChatViewModel) handlerUI.getViewModel(ChatViewModel.class.getName());
        MessageViewModel mMessage = (MessageViewModel) handlerUI.getViewModel(MessageViewModel.class.getName());
        AboutUserViewModel mUser =
                (AboutUserViewModel) handlerUI.getViewModel(AboutUserViewModel.class.getName());

        MessageStatus ms = createMSmodel(response.Message.get(0).Id, mUser.getUserId());

        if (currentFragment == MessageFragment.ID || currentFragment == ListUsersFragment.ID
                || currentFragment == AppendUserFragment.ID
                && mChat.getCurrentChat() == response.Message.get(0).ChatId) {

            appendMessageToMessageModel(mMessage, response, ms);
            appendMessageToChatModel(mChat, response, ms);

            changeMessageStatus(MessageStatus.MESSAGE_READED);
            return;
        }

        if (currentFragment == ChatFragment.ID ) {
            appendMessageToChatModel(mChat, response, ms);

            if (mChat.getCurrentChat() != null &&
                    mChat.getCurrentChat() == response.Message.get(0).ChatId) {

                appendMessageToMessageModel(mMessage, response, ms);
                changeMessageStatus(MessageStatus.MESSAGE_DELIVERED);
            }
            return;
        }
    }

    private void processOkStatusMessage(ResponseServer response){

        if (currentFragment == MessageFragment.ID || currentFragment == ListUsersFragment.ID
                || currentFragment == AppendUserFragment.ID) {

            AboutUserViewModel mUser =
                    (AboutUserViewModel) handlerUI.getViewModel(AboutUserViewModel.class.getName());
            ChatViewModel mChat = (ChatViewModel) handlerUI.getViewModel(ChatViewModel.class.getName());
            MessageViewModel mMessage = (MessageViewModel) handlerUI.getViewModel(MessageViewModel.class.getName());

            if (mChat.getCurrentChat() != null && mChat.getCurrentChat() == response.Message.get(0).ChatId) {
                ResponseServer r = mMessage.getResponseServer();

                boolean finded = false;
                for (int i=0; i < r.MessageStatus.size(); i++){
                    Long id1 = r.MessageStatus.get(i).MessageId;
                    Long id2 = response.MessageStatus.get(0).MessageId;
                    if (id1 == id2) {
                        r.MessageStatus.remove(i);
                    }
                }
                r.MessageStatus.add(response.MessageStatus.get(0));

                mMessage.setResponseServer(r);
            }
        }

    /*    if (currentFragment == ChatFragment.ID) {
            ChatViewModel mChat = (ChatViewModel) handlerUI.getViewModel(ChatViewModel.class.getName());
            MessageViewModel mMessage = (MessageViewModel) handlerUI.getViewModel(MessageViewModel.class.getName());
        }*/
    }

    private void processOkSearchUser(ResponseServer response){

        AppendUserViewModel viewModel =
                (AppendUserViewModel) handlerUI.getViewModel(AppendUserViewModel.class.getName());
        viewModel.setResponseServer(response);
    }

    private void processData(ResponseServer response) {

        switch (response.Status.Operation) {
            case StatusRequest.OP_AUTORIZATE:
                processOkAutorizate(response);
                break;

            case StatusRequest.OP_STATUS_MESSAGE:
                processOkStatusMessage(response);
                break;

            case StatusRequest.OP_NEW_MESSAGE:
                processOkNewMessage(response);
                break;

            case StatusRequest.OP_LIST_USERS:
                processOkListUsers(response);
                break;

            case StatusRequest.OP_CREATE_NEW_CHAT:
                processOkNewChat(response);
                break;

            case StatusRequest.OP_WRITEN:
                break;
            case StatusRequest.OP_SYSTEM:
                break;
            case StatusRequest.OP_SEARCH_USER:
                processOkSearchUser(response);
                break;

            case StatusRequest.OP_GET_CHATS:
                processOkGetChats(response);
                break;

            case StatusRequest.OP_LIST_PREVIOUS_MESSAGES:
                processOkGetMessages(response);
                break;
          /*  case ResponseServer.OP_LIST_NEXT_MESSAGES:
            //    processOkGetMessages(response);
                break;*/
            case StatusRequest.OP_EXIT_CHAT:
             //   handlerUI.createChatUI();
                break;

            case StatusRequest.OP_REMOVE_USER:
                processOkRemoveUserFromChat(response);
                break;

            case StatusRequest.OP_ADD_USER:
                processOkAppendUserInChat(response);
                break;

            case StatusRequest.OP_REMOVE_CHAT:
                handlerUI.showMessage("The chat was deleted");
                handlerUI.createChatUI();
                break;

            case StatusRequest.OP_BLOCK_USERS:
                break;
            case StatusRequest.OP_UNLOOCK_USERS:
                break;
            case StatusRequest.OP_BLACK_LIST_USERS:
                break;
            case StatusRequest.OP_GET_FILE:
                break;
            case StatusRequest.OP_MY_DATA:
                break;
            case StatusRequest.OP_REGISTRATION:
                processOkRegistration(response);
                break;
        }
    }

    public void createNewMessage(String message, Long parent, String fileUrl) {

        handlerUI.runProgress();
        ChatViewModel mChat = (ChatViewModel) handlerUI.getViewModel(ChatViewModel.class.getName());

        Message m = new Message();
        m.ChatId = mChat.getCurrentChat();
        m.Author = getUserId();

        if (message != null && !message.isEmpty()) {
          m.Message = message;
        }
        if (parent != null) {
          m.ParrentMessage = parent;
        }
        if (fileUrl != null && !fileUrl.isEmpty()) {
          //  TODO  add file in message
        }

        wsThread.sendMessage(generateRequestData(StatusRequest.OP_NEW_MESSAGE, m));
    }

    public void getDescriptionProject(){
        new HttpThread(this).initOperation(HttpThread.OP_ABOUT_PROJECT, null).start();
    }

    public void responseHttpThread(int operation, Object data) {

        Log.v("", "Response HTTP! operation -> " + operation + "; data -> " + (String) data);
        handlerUI.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (operation) {
                    case HttpThread.OP_ABOUT_PROJECT:

                        if (data instanceof String) {
                            ((AboutProjectViewModel) handlerUI
                                    .getViewModel(AboutProjectViewModel.class.getName()))
                                    .description.setValue((String) data);
                        }
                        break;
                }
            }
        });
    }

    public void removeUserFromChat(){

        handlerUI.runProgress();
        ListUsersViewModel mChatUser =
                (ListUsersViewModel) handlerUI.getViewModel(ListUsersViewModel.class.getName());
        ChatViewModel mChat =
                (ChatViewModel) handlerUI.getViewModel(ChatViewModel.class.getName());
        AboutUserViewModel mUser =
                (AboutUserViewModel) handlerUI.getViewModel(AboutUserViewModel.class.getName());

        if ((int) mUser.getUserId() == (int) mChatUser.getCurrentUser()) {
            handlerUI.showMessage(
                    "You can't remove yourself from the chat, select the \"leave chat\" function");
            return;
        }

        ChatUser m = new ChatUser();
        m.Chat = mChat.getCurrentChat();
        m.User = mChatUser.getCurrentUser();

        wsThread.sendMessage(generateRequestData(StatusRequest.OP_REMOVE_USER, m));
    }

    public void exitChat() {

        handlerUI.runProgress();
        AboutUserViewModel mUser =
                (AboutUserViewModel) handlerUI.getViewModel(AboutUserViewModel.class.getName());
        ChatViewModel mChat =
                (ChatViewModel) handlerUI.getViewModel(ChatViewModel.class.getName());
        ListUsersViewModel mChatUser =
                (ListUsersViewModel) handlerUI.getViewModel(ListUsersViewModel.class.getName());

        ChatUser m = new ChatUser();
        m.Chat = mChat.getCurrentChat();
        int authorChat = mChat.getResponseServer().Chat.get(mChat.getCurrentChatInd()).Author;

        if (authorChat == mUser.getUserId()) {
            if (mChatUser.getCurrentUser() == null) {
                handlerUI.showMessage("You are the author of the chat, when leaving the chat you "+
                        "must specify the user to whom the moderator rights are transferred");
                return;
            }

            if (mChatUser.getCurrentUser() == mUser.getUserId()) {
                handlerUI.showMessage("You cannot transfer moderator rights to yourself");
                return;
            }

            m.User = mChatUser.getCurrentUser();
        } else {
            m.User = mUser.getUserId();
        }

        wsThread.sendMessage(generateRequestData(StatusRequest.OP_EXIT_CHAT, m));
        handlerUI.createChatUI();
    }

    public void appendUserInChat(){

        handlerUI.runProgress();
        AppendUserViewModel mAppend =
                (AppendUserViewModel) handlerUI.getViewModel(AppendUserViewModel.class.getName());
        ChatViewModel mChat =
                (ChatViewModel) handlerUI.getViewModel(ChatViewModel.class.getName());

        ChatUser m = new ChatUser();
        m.Chat = mChat.getCurrentChat();
        m.User = mAppend.getCurrentUser();

        wsThread.sendMessage(generateRequestData(StatusRequest.OP_ADD_USER, m));
    }

    public void removeChat(){

        handlerUI.runProgress();
        AboutUserViewModel mUser =
                (AboutUserViewModel) handlerUI.getViewModel(AboutUserViewModel.class.getName());
        ChatViewModel mChat =
                (ChatViewModel) handlerUI.getViewModel(ChatViewModel.class.getName());
        int authorChat = mChat.getResponseServer().Chat.get(mChat.getCurrentChatInd()).Author;

        if (authorChat != mUser.getUserId()) {
            handlerUI.showMessage("You are not the author of the chat!");
            return;
        }

        Chat m = new Chat();
        m.Id = mChat.getCurrentChat();

        wsThread.sendMessage(generateRequestData(StatusRequest.OP_REMOVE_CHAT, m));
        handlerUI.createChatUI();
    }

    public void getPreviousListMessages() {

        handlerUI.runProgress();
        /*  if (allowedRequestingListMessages) {
            allowedRequestingListMessages = false;
            Log.e("", "UPDATE LIST MESSAGES!");
            Timer timer = new Timer();
            TimerTask tt = new TimerTask() {
                @Override
                public void run() {
                    allowedRequestingListMessages = true;
                }
            };
            timer.schedule(tt, 4000);
        } else {
            return;
        }*/

        MessageViewModel mMessage = (MessageViewModel) handlerUI.getViewModel(MessageViewModel.class.getName());
        ChatViewModel mChat = (ChatViewModel) handlerUI.getViewModel(ChatViewModel.class.getName());

        Message m = new Message();
        m.ChatId = mChat.getCurrentChat();
        m.Id = mMessage.getCurrentMessage();

        wsThread.sendMessage(generateRequestData(StatusRequest.OP_LIST_PREVIOUS_MESSAGES, m));
    }

    public void getListChatUsers() {

        handlerUI.runProgress();
        ChatViewModel mChat = (ChatViewModel) handlerUI.getViewModel(ChatViewModel.class.getName());

        ChatUser m = new ChatUser();
        m.Chat = mChat.getCurrentChat();

        wsThread.sendMessage(generateRequestData(StatusRequest.OP_LIST_USERS, m));
    }

    public void getListChats() {

        handlerUI.runProgress();
        wsThread.sendMessage(generateRequestData(StatusRequest.OP_GET_CHATS));
    }

    public void createNewChat(String chatName){

        handlerUI.runProgress();
        Chat c = new Chat();
        c.Name = chatName;

        wsThread.sendMessage(generateRequestData(StatusRequest.OP_CREATE_NEW_CHAT, c));
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

        handlerUI.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                handlerUI.showMessage("Server connected!");
            }
        });
    }

    public void searchUser(){

        handlerUI.runProgress();
        AppendUserViewModel mAppend =
                (AppendUserViewModel) handlerUI.getViewModel(AppendUserViewModel.class.getName());

        String alias = mAppend.getSearchAlias();
        if (alias != null && !alias.isEmpty()) {
            User u = new User();
            u.Alias = alias;

            wsThread.sendMessage(generateRequestData(StatusRequest.OP_SEARCH_USER, u));
            return;
        }

        String phone = mAppend.getSearchPhone();
        if (phone != null && !phone.isEmpty()) {
            UserPhone u = new UserPhone();
            u.Phone = phone;

            wsThread.sendMessage(generateRequestData(StatusRequest.OP_SEARCH_USER, u));
        }
    }

    public void autorizationUser() {

        handlerUI.runProgress();
        AutorizateViewModel mAutoriz =
                (AutorizateViewModel) handlerUI.getViewModel(AutorizateViewModel.class.getName());
        User u = new User();
        u.Login = mAutoriz.getLogin();
        u.PassHash = mAutoriz.getPassword();

        wsThread.sendMessage(generateRequestData(StatusRequest.OP_AUTORIZATE, u));
    }

    public void registrationUser() {

        handlerUI.runProgress();
        RegistrationViewModel mRegister =
                (RegistrationViewModel) handlerUI.getViewModel(RegistrationViewModel.class.getName());

        User u = new User();
        u.Login = mRegister.getLogin();
        u.PassHash = mRegister.getPassword();
        u.Alias = mRegister.getAlias();
        u.Email = mRegister.getEmail();

        settings.password = u.PassHash;
        wsThread.sendMessage(generateRequestData(StatusRequest.OP_REGISTRATION, u));
    }

    public boolean isConnected(){
        return wsThread.isConnected();
    }

    private Map<String, String> generateRequestData(int operation, Model... models){

        StatusRequest st = new StatusRequest();
        st.Operation = operation;

        Map<String, String> data = new HashMap<>();
        if (models != null) {
            for (Model m: models) {
                data.put(m.getNameModel(), ModelLoader.encodeByteArr(m));
            }
        }
        data.put(Model.MODEL_STATUS, ModelLoader.encodeByteArr(st));

        return  data;
    }

    public void setSettings(ChatHandlerProperty settings) {
        this.settings = settings;
    }
    public Integer getUserId(){
        return settings.userId;
    }
    public void setUserId(int userId){
        settings.userId = userId;
    }
    public String getLogin(){
        return settings.login;
    }
    public void setLogin(String login){
        settings.login = login;
    }
    public String getAlias(){
        return settings.alias;
    }
    public void setAlias(String alias){
        settings.alias = alias;
    }
    public String getPassword(){
        return settings.password;
    }
    public void setPassword(String password){
        settings.password = password;
    }
    public String getAuthKey(){
        return settings.authKey;
    }
    public void setAuthKey(String authKey){
        settings.authKey = authKey;
    }
}
