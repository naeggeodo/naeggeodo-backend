package com.naeggeodo.dto;

import com.naeggeodo.entity.chat.ChatMain;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.util.List;

import static org.modelmapper.config.Configuration.AccessLevel.PRIVATE;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoomVO {
    private String address;
    private String endDate;
    private String buildingCode;
    private String link;
    private String orderTimeType;
    private int maxCount;
    private List<String> tags;
    private String bookmarks;
    private String user_id;
    private String imgPath;
    private int currentCount;
    private String bookmarksDate;
    private long id;
    private String state;
    private String place;
    private String category;
    private String createDate;

    public static ChatRoomVO convert(ChatMain chatMain) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                   .setFieldAccessLevel(PRIVATE)
                   .setFieldMatchingEnabled(true);
        return modelMapper.map(chatMain, ChatRoomVO.class);
    }
}