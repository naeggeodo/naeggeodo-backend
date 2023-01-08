package com.naeggeodo.dto;

import com.naeggeodo.entity.chat.ChatDetail;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;

import static org.modelmapper.config.Configuration.AccessLevel.PRIVATE;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatDetailDTO {
    private Long id;
    private Long chatMain_id;
    private String user_id;
    private String contents;
    private LocalDateTime regDate;
    private String type;
    @Setter(AccessLevel.PRIVATE)
    private String nickname;

    public static ChatDetailDTO convert(ChatDetail chatDetail) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setFieldAccessLevel(PRIVATE)
                .setFieldMatchingEnabled(true);

        modelMapper.typeMap(ChatDetail.class, ChatDetailDTO.class)
                .addMapping(ChatDetail::getNickName,ChatDetailDTO::setNickname);
        return modelMapper.map(chatDetail,ChatDetailDTO.class);
    }
}
