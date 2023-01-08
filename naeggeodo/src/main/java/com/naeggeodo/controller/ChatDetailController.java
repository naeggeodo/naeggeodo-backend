package com.naeggeodo.controller;

import com.naeggeodo.dto.ChatDetailDTO;
import com.naeggeodo.dto.ResponseDTO;
import com.naeggeodo.service.ChatDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.naeggeodo.util.ResponseUtils.success;

@RestController
@RequiredArgsConstructor
public class ChatDetailController {
    private final ChatDetailService chatDetailService;

    @GetMapping(value = "/chat/messages/{chatMain_id}/{user_id}", produces = "application/json")
    public ResponseDTO<List<ChatDetailDTO>> load(@PathVariable Long chatMain_id, @PathVariable String user_id) {
        List<ChatDetailDTO> dtoList = chatDetailService.load(chatMain_id, user_id);
        return success(dtoList);
    }
}
