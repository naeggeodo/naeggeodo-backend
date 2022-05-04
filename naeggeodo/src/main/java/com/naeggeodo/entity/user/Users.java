package com.naeggeodo.entity.user;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.naeggeodo.entity.chat.ChatMain;
import com.naeggeodo.entity.chat.QuickChat;
import com.naeggeodo.entity.deal.Deal;
import com.naeggeodo.entity.post.Notice;
import com.naeggeodo.entity.post.Qna;
import com.naeggeodo.entity.post.Report;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Users {

	@Id @Column(name="user_id")
	private String id;
	
	private String password;
	private String token;
	private String phone;
	private String addr;
	private String nickname;
	private LocalDateTime joindate;
	private LocalDateTime withdrawalDate;
	@Enumerated(EnumType.STRING)
	private TosCheck tosCheck;
	@Enumerated(EnumType.STRING)
	private Authority authority;
	
	private String imgpath;
	
	@OneToMany(mappedBy = "user")
	private List<ChatMain> chatMain;
	@OneToMany(mappedBy = "user")
	private List<Notice> notice;
	@OneToMany(mappedBy = "user")
	private List<Qna> qna;
	@OneToMany(mappedBy = "user")
	private List<Report> report;
	@OneToMany(mappedBy = "user")
	private List<Deal> deals = new ArrayList<>();
	
	@OneToOne(mappedBy = "user")
	private QuickChat quickChat;
	
}
