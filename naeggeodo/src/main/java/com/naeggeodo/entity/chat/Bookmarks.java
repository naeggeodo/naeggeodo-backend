package com.naeggeodo.entity.chat;

import com.naeggeodo.exception.CustomHttpException;
import com.naeggeodo.exception.ErrorCode;

import java.time.LocalDateTime;

public enum Bookmarks {
    Y,N;

    public static Bookmarks getOpposite(Bookmarks bookmarks){
        if(bookmarks != null)
            return bookmarks.equals(Y) ? N : Y ;
        else
            throw new CustomHttpException(ErrorCode.INVALID_FORMAT);
    }
}
