package com.naeggeodo.entity.chat;

import java.util.Arrays;
import java.util.List;

// CREATE - 생성됨
// PROGRESS  - 진행중(참여불가)
// END - 종료됨
// FULL - 인원수 꽉참
// INCOMPLETE - 방터짐(조회안되야함)
public enum ChatState {
    CREATE,
    PROGRESS,
    END,
    FULL,
    INCOMPLETE;
    // TODO : State not in 으로 insearchableList이 들어가면
    //  -> 검색 할수 있는 상태가 `아닌것이 아닌것` 이 되어서 혼동될듯
    //  -> State in으로 하는것이 더 자연스러울것 같음
    public static final List<ChatState> insearchableList = Arrays.asList(END, INCOMPLETE);
    public static final List<ChatState> searchableList = Arrays.asList(CREATE, FULL);

}
