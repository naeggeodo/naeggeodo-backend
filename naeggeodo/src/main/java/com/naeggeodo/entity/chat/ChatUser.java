package com.naeggeodo.entity.chat;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.naeggeodo.entity.user.Users;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ChatUser {
	@Id @GeneratedValue
	@Column(name="chatUser_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private Users user;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chatmain_id")
	private ChatMain chatMain;
	
	private LocalDateTime enterDate;
	
	private String session_id;
	
	@Enumerated(EnumType.STRING)
	private RemittanceState state;
	//생성
	public static ChatUser create(Users user,ChatMain chatMain,String session_id) {
		ChatUser chatUser = new ChatUser();
		chatUser.setUser(user);
		chatUser.setChatMain(chatMain);
		chatUser.setEnterDate(LocalDateTime.now());
		chatUser.setSession_id(session_id);
		chatUser.setState(RemittanceState.N);
		return chatUser;
	}
	
	public void setChatMain(ChatMain chatMain) {
		this.chatMain = chatMain;
		chatMain.getChatUser().add(this);
	}
}
