package com.naeggeodo.entity.chat;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import org.json.JSONObject;

import com.naeggeodo.entity.user.Users;
import com.naeggeodo.interfaces.JSONConverter;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ChatDetail implements JSONConverter{

	@Id @GeneratedValue
	@Column(name="chatdetail_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chatmain_id")
	private ChatMain chatmain;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private Users user;
	
	@Lob
	private String contents;
	
	private LocalDateTime regDate;
	
	@Enumerated(EnumType.STRING)
	private ChatDetailType type;
	
	
	
	//생성
	public static ChatDetail create(String contents,Users user,ChatMain chatmain,ChatDetailType type) {
		ChatDetail chatDetail = new ChatDetail();
		
		chatDetail.setContents(contents);
		chatDetail.setChatmain(chatmain);
		chatDetail.setUser(user);
		chatDetail.setType(type);
		chatDetail.setRegDate(LocalDateTime.now());
		return chatDetail;
	}
	
	//toJSON
	@Override
	public JSONObject toJSON() throws Exception {
		JSONObject json = new JSONObject();
		
		for (Field field : this.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			if(field.get(this) instanceof Users) {
				json.put("user_id",((Users) field.get(this)).getId());
			}else if(field.get(this) instanceof ChatMain){
				json.put("chatMain_id",((ChatMain) field.get(this)).getId());
			}else if(field.get(this) instanceof LocalDateTime) {
				json.put(field.getName(), ((LocalDateTime)field.get(this)).toString());
			}else if(field.get(this) instanceof ChatDetailType){
				json.put(field.getName(), ((ChatDetailType)field.get(this)).name());
			}else {
				json.put(field.getName(), field.get(this));
			}
			
		}
		return json;
	}

	
	
}
