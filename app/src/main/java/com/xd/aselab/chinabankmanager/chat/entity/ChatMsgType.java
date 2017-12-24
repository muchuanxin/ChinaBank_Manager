package com.xd.aselab.chinabankmanager.chat.entity;

/**
 * Created by xinye on 2017/11/5.
 */

public enum ChatMsgType {

    TEXT, PICTURE, VOICE, VIDEO;

    public static ChatMsgType getType(String type){
        switch (type){
            case ("TEXT"):
                return TEXT;
            case ("PICTURE"):
                return PICTURE;
            case ("VOICE"):
                return VOICE;
            case ("VIDEO"):
                return VIDEO;
        }
        return null;
    }
}
