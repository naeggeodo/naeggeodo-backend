package com.naeggeodo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.naeggeodo.entity.chat.ChatMain;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.IntStream;

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
    @Setter(AccessLevel.PROTECTED)
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Setter(AccessLevel.PRIVATE)
    private String latestMessage;

    public static ChatRoomVO convert(ChatMain chatMain) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setFieldAccessLevel(PRIVATE)
                .setFieldMatchingEnabled(true);

        modelMapper.typeMap(ChatMain.class, ChatRoomVO.class)
                .addMapping(ChatMain::getTagNames, ChatRoomVO::setTags);
        return modelMapper.map(chatMain, ChatRoomVO.class);
    }
    
    public static void setLatestMessage(List<ChatRoomVO> chatList ,List<String> messages) {
        if (chatList.size() != messages.size()) {
            throw new IllegalStateException("리스트의 크기가 서로 다릅니다.");
        }

        IntStream.range(0, chatList.size())
                .forEach(i -> chatList.get(i).setLatestMessage(messages.get(i)));
    }
}