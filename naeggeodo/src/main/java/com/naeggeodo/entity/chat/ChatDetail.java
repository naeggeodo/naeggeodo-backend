package com.naeggeodo.entity.chat;

import com.naeggeodo.entity.user.Users;
import com.naeggeodo.interfaces.JSONConverterAdapter;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.json.JSONObject;
import org.springframework.util.ObjectUtils;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@Setter
public class ChatDetail extends JSONConverterAdapter {

    @Id
    @GeneratedValue
    @Column(name = "chatdetail_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatmain_id")
    private ChatMain chatmain;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @Lob
    private String contents;

    @CreationTimestamp
    private LocalDateTime regDate;

    @Enumerated(EnumType.STRING)
    private ChatDetailType type;


    //생성
    public static ChatDetail create(String contents, Users user, ChatMain chatmain, ChatDetailType type) {
        ChatDetail chatDetail = new ChatDetail();
        chatDetail.setContents(contents);
        chatDetail.setChatmain(chatmain);
        chatDetail.setUser(user);
        chatDetail.setType(type);
        return chatDetail;
    }

    //toJSON
    @Override
    public JSONObject toJSON() throws Exception {
        JSONObject json = new JSONObject();

        for (Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object obj = field.get(this);

            if (ObjectUtils.isEmpty(obj)) {
                json.put(field.getName(), JSONObject.NULL);
                continue;
            }

            if (obj instanceof Users) {
                json.put("user_id", ((Users) obj).getId());
            } else if (obj instanceof ChatMain) {
                json.put("chatMain_id", ((ChatMain) obj).getId());
            } else if (obj instanceof LocalDateTime) {
                json.put(field.getName(), ((LocalDateTime) obj).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
            } else if (obj instanceof ChatDetailType) {
                json.put(field.getName(), ((ChatDetailType) obj).name());
            } else {
                json.put(field.getName(), obj);
            }
            json.put("nickname", this.user.getNickname());
        }
        return json;
    }

    // TODO 모델매퍼 -> 맵스트럭트 적용시 삭제
    public String getNickName() {
        return this.user.getNickname();
    }

}
