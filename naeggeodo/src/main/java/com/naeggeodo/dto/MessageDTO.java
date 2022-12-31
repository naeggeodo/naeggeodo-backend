package com.naeggeodo.dto;


import com.naeggeodo.entity.chat.ChatDetailType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {

    private String chatMain_id;
    private String contents;
    //전송한 user_id
    private String sender;
    private ChatDetailType type;
    private String nickname;

    public Long chatMain_idToLong() {
        return Long.parseLong(chatMain_id);
    }
}
