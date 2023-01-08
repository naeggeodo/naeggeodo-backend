package com.naeggeodo.controller;

import com.naeggeodo.dto.ResponseDTO;
import com.naeggeodo.repository.TagRepository;
import com.naeggeodo.util.MyUtility;
import com.naeggeodo.util.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TagController {
    private final TagRepository tagRepository;

    @GetMapping(value = "/chat-rooms/tag/most-wanted", produces = "application/json")
    public ResponseDTO<List<String>> getMostWantedTagList() {
        List<String> tagList = tagRepository.findTop10Tag();
        return ResponseUtils.success(tagList);
    }
}
