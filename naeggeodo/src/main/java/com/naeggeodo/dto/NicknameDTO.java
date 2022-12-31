package com.naeggeodo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class NicknameDTO {
    private final String nickname;
    private final String user_id;
}
