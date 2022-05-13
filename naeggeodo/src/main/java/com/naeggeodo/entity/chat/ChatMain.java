package com.naeggeodo.entity.chat;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
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

import org.hibernate.annotations.DynamicUpdate;
import org.json.JSONException;
import org.json.JSONObject;

import com.naeggeodo.dto.ChatRoomDTO;
import com.naeggeodo.entity.user.Users;
import com.naeggeodo.interfaces.JSONConverter;
import com.naeggeodo.interfaces.JSONConverterAdapter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
public class ChatMain extends JSONConverterAdapter{

	
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
	
	@Enumerated(EnumType.STRING)
	private OrderTimeType orderTimeType;
	
	
	
	@OneToMany(mappedBy = "chatMain")
	private List<ChatUser> chatUser;
	
	@OneToMany(mappedBy = "chatMain")
	private List<Tag> tag = new ArrayList<>();
	
	//생성
	public static ChatMain create(ChatRoomDTO dto) {
		return ChatMain.builder().title(dto.getTitle()).createDate(LocalDateTime.now())
				.buildingCode(dto.getAddr()).state(ChatState.CREATE).link(dto.getLink())
				.place(dto.getPlace()).category(dto.getCategory()).orderTimeType(dto.getOrderTimeType()).build();
	}
	
	//state upadate
	public void updateState() {
		
		if(this.getChatUser().size()>=this.getMaxCount()) {
			this.state = ChatState.FULL;
			return;
		} 
		this.state = ChatState.CREATE;
	}
	
	public void changeState(ChatState state) {
		this.state = state;
	}
	
	// is Full
	public boolean isFull() {
		int maxCount = this.getMaxCount();
		int currentCount = this.getChatUser().size();
		if(currentCount>=maxCount) {
			return true;
		}
		return false;
	}
	
	// 입장가능?
	public boolean canEnter() {
		if(this.state==ChatState.CREATE||this.state==ChatState.FULL) {
			return true;
		}
		return false;
	}
	
	
	//imgPath update
	public void updateImgPath(String imgPath) {
		this.imgPath = imgPath;
	}
	
	public ChatUser findChatUserBySender(String sender) {
    	ChatUser chatUser = null;
    	if(!this.getChatUser().isEmpty()) {
    		for (ChatUser cu : this.getChatUser()) {
    			if(cu.getUser().getId().equals(sender)) {
    				chatUser = cu;
    			}
    		}
    	}
    	return chatUser;
    }
	
	//다음 방장 찾기
	public ChatUser findChatUserForChangeHost() {
		ChatUser chatUser = null;
		LocalDateTime tempTime = LocalDateTime.MAX;
		if(!this.getChatUser().isEmpty()) {
    		for (ChatUser cu : this.getChatUser()) {
    			if(tempTime.isAfter(cu.getEnterDate())) {
    				if(!cu.getUser().getId().equals(this.getUser().getId())) {
    					tempTime = cu.getEnterDate();
        				chatUser = cu;	
    				}
    			}
    		}
    	}
		return chatUser;
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
				continue;
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
	
	public void changeUser(Users user) {
		this.user = user;
	}
	
	public void removeChatUser(ChatUser chatUser) {
		this.chatUser.remove(chatUser);
	}

	@Override
	public JSONObject toJSONIgnoringCurrentCount() throws Exception {
		JSONObject json = new JSONObject();
		for (Field field : this.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			if(field.get(this) instanceof Users) {
				json.put("user_id", ((Users) field.get(this)).getId());
			}else if(field.get(this) instanceof List) {
				continue;
			}else if(field.get(this) instanceof LocalDateTime) {
				json.put(field.getName(), ((LocalDateTime)field.get(this)).toString());
			}else if(field.get(this) instanceof Enum) {
				json.put(field.getName(),((Enum)field.get(this)).name());
			}else {
				json.put(field.getName(), field.get(this));
			}
			
			
		}
		return json;
	}
	
}
