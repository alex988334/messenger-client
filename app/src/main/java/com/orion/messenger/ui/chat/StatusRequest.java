package com.orion.messenger.ui.chat;

import android.media.VolumeShaper;

import androidx.annotation.NonNull;

public class StatusRequest {

    public static final int OP_AUTORIZATE             = 100;
    public static final int OP_STATUS_MESSAGE         = 101;
    public static final int OP_NEW_MESSAGE            = 103;
    public static final int OP_LIST_USERS             = 105;
    public static final int OP_CREATE_NEW_CHAT        = 106;
    public static final int OP_WRITEN                 = 107;
    public static final int OP_SYSTEM                 = 108;
    public static final int OP_SEARCH_USER            = 109;
    public static final int OP_GET_CHATS              = 110;
    public static final int OP_LIST_PREVIOUS_MESSAGES = 111;
    public static final int OP_LIST_NEXT_MESSAGES     = 112;
    public static final int OP_EXIT_CHAT              = 113;
    public static final int OP_REMOVE_USER            = 114;
    public static final int OP_ADD_USER               = 115;
    public static final int OP_REMOVE_CHAT            = 116;
    public static final int OP_BLOCK_USERS            = 117;
    public static final int OP_UNLOOCK_USERS          = 118;
    public static final int OP_BLACK_LIST_USERS       = 119;
    public static final int OP_GET_FILE               = 120;
    public static final int OP_MY_DATA                = 122;
    public static final int OP_REGISTRATION           = 123;

    public static final int STATUS_ACCEPT = 1;
    public static final int STATUS_DENIED = 0;

    public int Operation;
    public int Status;
    public String Message;

    public StatusRequest() {
        Operation = 0;
        Status = 0;
        Message = "";
    }

    public StatusRequest(int operation) {
        Operation = operation;
        Status = 0;
        Message = "";
    }

    public static String getDescriptionOperation(int operation) {
        switch (operation) {
            case OP_AUTORIZATE: return "OP_AUTORIZATE";
            case OP_STATUS_MESSAGE: return "OP_STATUS_MESSAGE";
            case OP_NEW_MESSAGE: return "OP_NEW_MESSAGE";
            case OP_LIST_USERS: return "OP_LIST_USERS";
            case OP_CREATE_NEW_CHAT: return "OP_CREATE_NEW_CHAT";
            case OP_WRITEN: return "OP_WRITEN";
            case OP_SYSTEM: return "OP_SYSTEM";
            case OP_SEARCH_USER: return "OP_SEARCH_USER";
            case OP_GET_CHATS: return "OP_GET_CHATS";
            case OP_LIST_PREVIOUS_MESSAGES: return "OP_LIST_PREVIOUS_MESSAGES";
            case OP_LIST_NEXT_MESSAGES: return "OP_LIST_NEXT_MESSAGES";
            case OP_EXIT_CHAT: return "OP_EXIT_CHAT";
            case OP_REMOVE_USER: return "OP_REMOVE_USER";
            case OP_ADD_USER: return "OP_ADD_USER";
            case OP_REMOVE_CHAT: return "OP_REMOVE_CHAT";
            case OP_BLOCK_USERS: return "OP_BLOCK_USERS";
            case OP_UNLOOCK_USERS: return "OP_UNLOOCK_USERS";
            case OP_BLACK_LIST_USERS: return "OP_BLACK_LIST_USERS";
            case OP_GET_FILE: return "OP_GET_FILE";
            case OP_MY_DATA: return "OP_MY_DATA";
            case OP_REGISTRATION: return "OP_REGISTRATION";
        }
        return "";
    }

    public static String getDescriptionStatus(int status) {
        switch (status) {
            case STATUS_ACCEPT: return "ACCEPT";
            case STATUS_DENIED: return "DENIED";
        }
        return "";
    }

    @NonNull
    @Override
    public String toString() {
        return "{\"Operation\":"+Operation+",\"Status\":"+Status+",\"Message\":\""+Message+"\"}";
    }
}
