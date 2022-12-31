package com.naeggeodo.controller;

import com.naeggeodo.entity.Likes;
import com.naeggeodo.exception.CustomHttpException;
import com.naeggeodo.exception.ErrorCode;
import com.naeggeodo.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashMap;

@Controller
@RequiredArgsConstructor
public class LikeController {
    private final LikeRepository likeRepository;

    @GetMapping(value = "/like")
    @Transactional
    public ResponseEntity<?> getLikeCount() {
        Likes likes = likeRepository.findById(1L)
                .orElseThrow(() -> new CustomHttpException(ErrorCode.RESOURCE_NOT_FOUND));
        return ResponseEntity.ok(new HashMap<String, Object>() {
            {
                put("likeCount", likes.getCount());
            }
        });
    }

    @PostMapping(value = "/like")
    @Transactional
    public ResponseEntity<?> increaseLikeCount() {
        Likes likes = likeRepository.findById(1L)
                .orElseThrow(() -> new CustomHttpException(ErrorCode.RESOURCE_NOT_FOUND));
        likes.updateCount();
        return ResponseEntity.ok(new HashMap<String, Object>() {
            {
                put("likeCount", likes.getCount());
            }
        });
    }
}
