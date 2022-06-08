package com.naeggeodo.entity.chat;

// CREATE - 생성됨
// PROGRESS  - 진행중(참여불가)
// END - 종료됨
// FULL - 인원수 꽉참
// INCOMPLETE - 방터짐(조회안되야함)
public enum ChatState {
	CREATE,PROGRESS,END,FULL,INCOMPLETE
}
