package com.naeggeodo.entity.deal;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.naeggeodo.entity.chat.ChatMain;
import com.naeggeodo.entity.user.Users;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Deal {
	@Id @GeneratedValue
	@Column(name ="deal_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chatmain_id")
	private ChatMain chatMain;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private Users user;
	
	private LocalDateTime regDate;
	
	public static Deal create(Users user, ChatMain chatMain) {
		Deal deal = new Deal();
		deal.setUser(user);
		deal.setChatMain(chatMain);
		deal.setRegDate(LocalDateTime.now());
		return deal;
	}
	
	public void setUser(Users user) {
		this.user = user;
		user.getDeals().add(this);
	}
}
