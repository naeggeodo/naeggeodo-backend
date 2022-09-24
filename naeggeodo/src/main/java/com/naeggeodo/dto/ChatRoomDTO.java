package com.naeggeodo.dto;

import com.naeggeodo.entity.chat.*;
import com.naeggeodo.entity.user.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomDTO {
    @NotBlank
    private String buildingCode;
    @NotBlank
    private String address;
    @NotBlank
    private String category;
    private String link;
    private String place;
    @NotBlank
    private String title;
    @NotBlank
    private String user_id;
    @NotBlank
    private String orderTimeType;
    private List<String> tag;
    private int maxCount;

    public ChatMain createChatMain(Users user, List<Tag> tags) {
        return ChatMain.builder()
                .title(this.title)
                .buildingCode(buildingCode)
                .address(address)
                .state(ChatState.CREATE)
                .link(link)
                .place(place)
                .category(Category.valueOf(category))
                .orderTimeType(OrderTimeType.valueOf(orderTimeType))
                .maxCount(maxCount)
                .user(user)
                .tag(tags)
                .build();
    }
}
