package com.naeggeodo.entity.chat;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

//import com.naeggeodo.listener.ChatMainListener;
import com.naeggeodo.exception.CustomHttpException;
import com.naeggeodo.exception.ErrorCode;
import org.hibernate.annotations.DynamicUpdate;
import org.json.JSONArray;
import org.json.JSONObject;

import com.naeggeodo.dto.ChatRoomDTO;
import com.naeggeodo.entity.user.Users;
import com.naeggeodo.interfaces.JSONConverterAdapter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
//@EntityListeners(ChatMainListener.class)
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

	@Enumerated(EnumType.STRING)
	private Bookmarks bookmarks;

	private LocalDateTime bookmarksDate;
	//생성
	public static ChatMain create(ChatRoomDTO dto) {
		return ChatMain.builder().title(dto.getTitle()).createDate(LocalDateTime.now())
				.buildingCode(dto.getAddr()).state(ChatState.CREATE).link(dto.getLink())
				.place(dto.getPlace()).category(Category.valueOf(dto.getCategory()))
				.orderTimeType(OrderTimeType.valueOf(dto.getOrderTimeType()))
				.maxCount(dto.getMaxCount())
				.build();
	}
	
	//state upadate
	public void updateState() {
		
		if(isFull()) {
			this.state = ChatState.FULL;
			return;
		} 
		this.state = ChatState.CREATE;
	}
	
	public void changeState(ChatState state) {
		this.state = state;
		if(state.equals(ChatState.END)) this.bookmarks = Bookmarks.N;
	}
	
	// is Full
	public boolean isFull() {
		int maxCount = this.getMaxCount();
		int currentCount = getAllowedUserCnt();


		return currentCount >= maxCount;
	}
	
	// 입장가능?
	public boolean canEnter() {
		return this.state == ChatState.CREATE || this.state == ChatState.FULL;
	}
	
	public int getAllowedUserCnt() {
		int allowedUserCnt = 0;
		if(!chatUser.isEmpty()) {
			for (ChatUser cu : chatUser) {
				if(BanState.ALLOWED.equals(cu.getBanState())) {
					allowedUserCnt++;
				}
			}
		}
		return allowedUserCnt;
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
    				if(!cu.getUser().getId().equals(this.getUser().getId()) && BanState.ALLOWED.equals(cu.getBanState())) {
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
			}else if(field.get(this) instanceof LocalDateTime) {
				json.put(field.getName(), ((LocalDateTime)field.get(this)).toString());
			}else if(field.get(this) instanceof Enum) {
				json.put(field.getName(),((Enum<?>)field.get(this)).name());
			}else if(!(field.get(this) instanceof List)){
				json.put(field.getName(), field.get(this));
			}


		}
		json.put("currentCount",this.getAllowedUserCnt());
		json.put("tags", tagsToJSONArrayString());
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
			}else if(field.get(this) instanceof LocalDateTime) {
				json.put(field.getName(), ((LocalDateTime)field.get(this)).toString());
			}else if(field.get(this) instanceof Enum) {
				json.put(field.getName(),((Enum<?>)field.get(this)).name());
			}else if(!(field.get(this) instanceof List)){
				json.put(field.getName(), field.get(this));
			}


		}
		json.put("tags", tagsToJSONArrayString());
		return json;
	}

	private String tagsToJSONArrayString() {
		JSONArray arr_json = new JSONArray();
		for (Tag t : tag) {
			arr_json.put(t.getName());
		}
		return arr_json.toString();
	}

	public void updateBookmarks(){
		if(this.bookmarks.equals(Bookmarks.Y)){
			this.bookmarksDate = null;
			this.bookmarks = Bookmarks.N;
		} else if(this.bookmarks.equals(Bookmarks.N)){
			this.bookmarksDate = LocalDateTime.now();
			this.bookmarks = Bookmarks.Y;
		} else {
			throw new CustomHttpException(ErrorCode.INVALID_FORMAT);
		}

	}
}
