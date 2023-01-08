package com.naeggeodo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatRoomUpdateDTO {
    private long chatMain_id;
    private boolean deleted;
    private String bookmarks;
    private String title;
    private String state;
}
