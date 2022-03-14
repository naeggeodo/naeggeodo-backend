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

import com.naeggeodo.entity.deal.DealHistory;
import com.naeggeodo.entity.user.Users;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ChatMain {

	@Id @GeneratedValue
	@Column(name = "chatmain_id")
	private Long id; 
	private String title;
	private LocalDateTime createDate;
	private String addr;
	private String imgpath;
	private String link;
	@Enumerated(EnumType.STRING)
	private ChatState state;
	private String place;
	private LocalDateTime enddate; 
	private String category;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private Users user;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "deal_id")
	private DealHistory dealHistory;
}
