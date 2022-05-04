package com.naeggeodo.entity.chat;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.json.JSONObject;

import com.naeggeodo.entity.user.Users;
import com.naeggeodo.interfaces.JSONConverter;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
public class QuickChat{
	@Id @GeneratedValue
	@Column(name = "quickChat_id")
	private Long id;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private Users user;
	
	@Column(columnDefinition = "varchar(255) default '안녕하세요. 지금 주문 가능하신가요?'")
	private String msg1;
	@Column(columnDefinition = "varchar(255) default '백석고등학교 정문 앞에서 만나고싶습니다.'")
	private String msg2;
	@Column(columnDefinition = "varchar(255) default '잠시 메뉴를 고르겠습니다. 2분만 기다려주세요!'")
	private String msg3;
	@Column(columnDefinition = "varchar(255) default 'default'")
	private String msg4;
	@Column(columnDefinition = "varchar(255) default 'default'")
	private String msg5;
	
	public void setUser(Users user) {
		this.user = user;
		user.setQuickChat(this);
	}
	
	public static QuickChat create(Users user) {
		QuickChat quickChat = new QuickChat();
		quickChat.setUser(user);
		return quickChat;
	}
	
	
	public List<String> getMsgList(){
		List<String> list = new ArrayList<>();
		list.add(this.msg1);
		list.add(this.msg2);
		list.add(this.msg3);
		list.add(this.msg4);
		list.add(this.msg5);
		return list;
	}
}
