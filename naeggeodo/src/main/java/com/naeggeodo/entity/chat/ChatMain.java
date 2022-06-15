package com.naeggeodo.entity.chat;

import com.naeggeodo.dto.ChatRoomDTO;
import com.naeggeodo.entity.user.Users;
import com.naeggeodo.exception.CustomHttpException;
import com.naeggeodo.exception.ErrorCode;
import com.naeggeodo.interfaces.JSONConverterAdapter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.util.ObjectUtils;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
	@CreationTimestamp
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
	
	
	
	@OneToMany(mappedBy = "chatMain",cascade = CascadeType.MERGE)
	private List<ChatUser> chatUser;
	
	@OneToMany(mappedBy = "chatMain",cascade = CascadeType.MERGE)
	private List<Tag> tag = new ArrayList<>();

	@Enumerated(EnumType.STRING)
	private Bookmarks bookmarks;

	private LocalDateTime bookmarksDate;
	//생성
	public static ChatMain create(ChatRoomDTO dto,Users user) {
		return ChatMain.builder().title(dto.getTitle())
				.buildingCode(dto.getBuildingCode()).address(dto.getAddress())
				.state(ChatState.CREATE).link(dto.getLink())
				.place(dto.getPlace()).category(Category.valueOf(dto.getCategory()))
				.orderTimeType(OrderTimeType.valueOf(dto.getOrderTimeType()))
				.maxCount(dto.getMaxCount()).user(user)
				.build();
	}

	public ChatMain copy(OrderTimeType orderTimeType){
		return   ChatMain.builder().title(this.title)
				.buildingCode(this.buildingCode).address(this.address)
				.state(ChatState.CREATE).link(this.link)
				.place(this.place).category(this.category)
				.orderTimeType(orderTimeType)
				.maxCount(this.maxCount).user(this.user)
				.imgPath(this.imgPath)
				.build();
	}

	// 태그 카피
	public List<Tag> copyTags(List<Tag> tagList){
		List<Tag> newTagsList = new ArrayList<>();
		for (Tag t : tagList) {
			newTagsList.add(Tag.create(this,t.getName()));
		}
		return newTagsList;
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
			Object obj  = field.get(this);

			if(ObjectUtils.isEmpty(obj)){
				if(obj instanceof List){
					continue;
				}
				if(!field.getName().equals("user")){
					json.put(field.getName(),JSONObject.NULL);
					continue;
				} else {
					json.put("user_id",JSONObject.NULL);
				}
			}

			if(obj instanceof Users) {
				json.put("user_id", ((Users) field.get(this)).getId());
			}else if(obj instanceof LocalDateTime) {
				json.put(field.getName(), ((LocalDateTime)field.get(this)).toString());
			}else if(obj instanceof Enum) {
				json.put(field.getName(),((Enum<?>)field.get(this)).name());
			}else if(!(obj instanceof List)){
				json.put(field.getName(), field.get(this));
			}


		}
		json.put("currentCount",this.getAllowedUserCnt());
		json.put("tags", tagsToJSONArray());
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
			Object obj  = field.get(this);

			if(ObjectUtils.isEmpty(obj)){
				if(obj instanceof List){
					continue;
				}
				if(!field.getName().equals("user")){
					json.put(field.getName(),JSONObject.NULL);
					continue;
				} else {
					json.put("user_id",JSONObject.NULL);
				}
			}

			if(obj instanceof Users) {
				json.put("user_id", ((Users) obj).getId());
			}else if(obj instanceof LocalDateTime) {
				json.put(field.getName(), ((LocalDateTime)field.get(this)).toString());
			}else if(obj instanceof Enum) {
				json.put(field.getName(),((Enum<?>)field.get(this)).name());
			}else if(!(obj instanceof List)){
				json.put(field.getName(), field.get(this));
			}


		}
		json.put("tags", tagsToJSONArray());
		return json;
	}

	private JSONArray tagsToJSONArray() {
		JSONArray arr_json = new JSONArray();
		for (Tag t : tag) {
			arr_json.put(t.getName());
		}
		return arr_json;
	}

	public void updateBookmarks(){
		if(this.bookmarks.equals(Bookmarks.N)){
			this.bookmarksDate = LocalDateTime.now();
			this.bookmarks = Bookmarks.Y;
		}else if(this.bookmarks.equals(Bookmarks.Y)){
			this.bookmarksDate = null;
			this.bookmarks = Bookmarks.N;
		}else {
			throw new CustomHttpException(ErrorCode.INVALID_FORMAT);
		}

	}

	public void updateOrderTimeType(OrderTimeType orderTimeType){
		this.orderTimeType = orderTimeType;
	}

	public void updateTitle(String title){
		this.title = title;
	}
}
