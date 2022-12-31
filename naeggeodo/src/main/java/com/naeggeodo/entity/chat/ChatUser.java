package com.naeggeodo.entity.chat;

import com.naeggeodo.entity.user.Users;
import com.naeggeodo.interfaces.JSONConverterAdapter;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.json.JSONObject;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@DynamicUpdate
public class ChatUser extends JSONConverterAdapter {
    @Id
    @GeneratedValue
    @Column(name = "chatUser_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatmain_id")
    private ChatMain chatMain;

    @CreationTimestamp
    private LocalDateTime enterDate;

    private String sessionId;

    @Enumerated(EnumType.STRING)
    private BanState banState;

    @Enumerated(EnumType.STRING)
    private RemittanceState state;

    //생성
    public static ChatUser create(Users user, ChatMain chatMain, String session_id) {
        ChatUser chatUser = new ChatUser();
        chatUser.setUser(user);
        chatUser.setChatMain(chatMain);
        chatUser.setSessionId(session_id);
        chatUser.setState(RemittanceState.N);
        chatUser.setBanState(BanState.ALLOWED);
        return chatUser;
    }

    public void setChatMain(ChatMain chatMain) {
        this.chatMain = chatMain;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("user_id", this.user.getId());
        json.put("remittanceState", this.state.name());
        json.put("nickname", this.getUser().getNickname());
        return json;
    }

}
