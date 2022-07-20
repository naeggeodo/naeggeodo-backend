package com.naeggeodo.controller;

import com.naeggeodo.repository.TagRepository;
import com.naeggeodo.util.MyUtility;
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
    @Transactional(readOnly = true)
    public ResponseEntity<Object> getMostWantedTagList() {
        List<String> list = tagRepository.findTop10Tag();
        JSONObject json = MyUtility.convertStringListToJSONObject(list, "tags");
        return ResponseEntity.ok(json.toMap());
    }
}
