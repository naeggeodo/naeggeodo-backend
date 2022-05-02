package com.naeggeodo.entity.deal;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.naeggeodo.entity.chat.ChatMain;
import com.naeggeodo.entity.user.Users;

import lombok.Setter;

import lombok.Getter;

@Entity
@Getter
@Setter
public class DealHistory {
	
	@Id @GeneratedValue
	@Column(name="deal_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private Users user;
	
	@OneToOne(mappedBy = "dealHistory",fetch = FetchType.LAZY)
	private ChatMain chatMain;
	
	@OneToMany(mappedBy = "dealHistory")
	private List<DealDetail> dealDetail;
	
	private LocalDateTime deal_compDate;
	private int totalPrice;
	
	
}
