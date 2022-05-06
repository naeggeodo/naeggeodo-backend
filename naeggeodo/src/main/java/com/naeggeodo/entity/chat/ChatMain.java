package com.naeggeodo.entity.chat;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.json.JSONObject;

import com.naeggeodo.dto.ChatRoomDTO;
import com.naeggeodo.entity.user.Users;
import com.naeggeodo.interfaces.JSONConverter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMain implements JSONConverter{

	
	
	@Id @GeneratedValue
	@Column(name = "chatmain_id")
	private Long id; 
	private String title;
	private LocalDateTime createDate;
	private String address;
	private String buildingCode;
	private String imgPath;
	private String link;
	@Enumerated(EnumType.STRING)
	private ChatState state;
	private String place;
	private LocalDateTime endDate; 
	
	@Enumerated(EnumType.STRING)
	private Category category;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private Users user;
	
	
	private int maxCount;
	
	@OneToMany(mappedBy = "chatMain")
	private List<ChatUser> chatUser;
	//생성
	public static ChatMain create(ChatRoomDTO dto) {
		return ChatMain.builder().title(dto.getTitle()).createDate(LocalDateTime.now())
				.buildingCode(dto.getAddr()).state(ChatState.CREATE).link(dto.getLink())
				.place(dto.getPlace()).category(dto.getCategory()).build();
	}
	
	//state upadate
	public void updateState(ChatState state) {
		this.state = state;
	}
	
	//imgPath update
	public void updateImgPath(String imgPath) {
		this.imgPath = imgPath;
	}
	
	//toJSON
	@Override
	public JSONObject toJSON() throws Exception {
		JSONObject json = new JSONObject();
		for (Field field : this.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			if(field.get(this) instanceof Users) {
				json.put("user_id", ((Users) field.get(this)).getId());
			}else if(field.get(this) instanceof List) {
				
			}else if(field.get(this) instanceof LocalDateTime) {
				json.put(field.getName(), ((LocalDateTime)field.get(this)).toString());
			}else if(field.get(this) instanceof Enum) {
				json.put(field.getName(),((Enum)field.get(this)).name());
			}else {
				json.put(field.getName(), field.get(this));
			}
			
			
		}
		json.put("currentCount", this.chatUser.size());
		return json;
	}

}
