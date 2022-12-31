package com.naeggeodo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;


@Getter
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class QuickChatDTO {
    private final List<String> quickChat;
}

