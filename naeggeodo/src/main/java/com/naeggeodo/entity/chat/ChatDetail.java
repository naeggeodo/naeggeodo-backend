package com.naeggeodo.entity.chat;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.naeggeodo.entity.user.Users;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ChatDetail {

	@Id @GeneratedValue
	@Column(name="chatdetail_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chatmain_id")
	private ChatMain chatmain;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private Users user;
	
	private String contents;
	
	private LocalDateTime regDate;
}
