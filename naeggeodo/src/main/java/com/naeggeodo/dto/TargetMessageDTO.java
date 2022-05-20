package com.naeggeodo.dto;

import com.naeggeodo.entity.chat.ChatDetailType;
import com.naeggeodo.exception.CustomWebSocketException;
import com.naeggeodo.exception.StompErrorCode;
import lombok.*;
import net.bytebuddy.implementation.bind.annotation.Empty;

import javax.validation.constraints.NotBlank;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TargetMessageDTO extends MessageDTO{

    private String target_id;

}
