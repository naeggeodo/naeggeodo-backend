package com.naeggeodo.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.naeggeodo.entity.chat.*;
import com.naeggeodo.exception.CustomHttpException;
import com.naeggeodo.exception.ErrorCode;
import com.naeggeodo.repository.*;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.naeggeodo.dto.ChatRoomDTO;
import com.naeggeodo.service.ChatMainService;
import com.naeggeodo.util.MyUtility;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class ChatMainController {

    private final ChatMainRepository chatMainRepository;
    private final ChatMainService chatMainService;
    private final ChatUserRepository chatUserRepository;

    private final TagRepository tagRepository;

    //채팅방 리스트
    @GetMapping(value = "/chat-rooms", produces = "application/json")
    @Transactional(readOnly = true)
    public ResponseEntity<Object> getChatList(
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "buildingCode", required = true) String buildingCode) throws Exception {

        JSONObject json = null;
        if (category == null) {
            //전체조회시
            json = MyUtility.convertListToJSONobj(
                    chatMainRepository.findByBuildingCodeAndStateNotIn
                            (buildingCode, ChatState.insearchableList), "chatRoom"
            );
        } else {
            //카테고리 조회시
            Category categoryEnum = Category.valueOf(category.toUpperCase());
            json = MyUtility.convertListToJSONobj(
                    chatMainRepository.findByCategoryAndBuildingCodeAndStateNotIn
                            (categoryEnum, buildingCode, ChatState.insearchableList), "chatRoom"
            );
        }

        return ResponseEntity.ok(json.toMap());
    }

    //채팅방 생성
    @PostMapping(value = "/chat-rooms", produces = "application/json")
    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseEntity<Object> createChatRoom(@RequestPart @Valid ChatRoomDTO chat,
                                                 @RequestPart(required = false) MultipartFile file) {
        JSONObject json = chatMainService.createChatRoom(chat, file);
        return ResponseEntity.ok(json.toMap());
    }
//    @PostMapping(value = "/chat-rooms", produces = "application/json")
//    @Transactional(propagation = Propagation.REQUIRED)
//    public ResponseEntity<Object> createChatRoom(@RequestPart(name = "chat") @Valid ChatRoomDTO chat,
//                                                 @RequestPart(required = false) MultipartFile file) {
//        Long startTime = System.currentTimeMillis();
//        JSONObject json = chatMainService.createChatRoom(chat, file);
//        return ResponseEntity.ok(json.toMap());
//    }

    //해당 채팅방 data
    @GetMapping(value = "/chat-rooms/{chatMain_id}", produces = "application/json")
    @Transactional(readOnly = true)
    public ResponseEntity<Object> getChatMain(@PathVariable(name = "chatMain_id") String chatMain_idstr) throws Exception {
        Long chatMain_id = Long.parseLong(chatMain_idstr);
        ChatMain chatMain = chatMainRepository.findChatMainEntityGraph(chatMain_id);
        return ResponseEntity.ok(chatMain.toJSON().toMap());
    }

    //채팅방 상태 업데이트
    @PatchMapping(value = "/chat-rooms/{chatMain_id}")
    @Transactional
    public ResponseEntity<Object> updateRoomState(@PathVariable(name = "chatMain_id") Long chatMain_id,
                                                  @RequestParam(name = "state") String state) {
        ChatState chatState = ChatState.valueOf(state.toUpperCase());
        ChatMain chatMain = chatMainRepository.findById(chatMain_id)
                .orElseThrow(() -> new CustomHttpException(ErrorCode.RESOURCE_NOT_FOUND));
        chatMain.changeState(chatState);
        return ResponseEntity.ok(chatMain.getState().name());
    }


    // 해당 채팅방접속중인 유저 list
    @GetMapping(value = "/chat-rooms/{chatMain_id}/users", produces = "application/json")
    @Transactional(readOnly = true)
    public ResponseEntity<Object> getChatUserList(@PathVariable(name = "chatMain_id") Long chatMain_id) throws Exception {
        JSONObject json = MyUtility.convertListToJSONobj(chatUserRepository.findForRemit(chatMain_id), "users");

        return ResponseEntity.ok(json.toMap());
    }

    //송금상태 변경
    @PatchMapping(value = "/chat-rooms/{chatMain_id}/users/{user_id}", produces = "application/json")
    @Transactional
    public ResponseEntity<Object> updateRemittanceState(@PathVariable(name = "chatMain_id") Long chatMain_id,
                                                        @PathVariable(name = "user_id") String user_id) {
        ChatUser chatUser = chatUserRepository.findByChatMainIdAndUserId(chatMain_id, user_id);
        if (RemittanceState.Y.equals(chatUser.getState())) {
            chatUser.setState(RemittanceState.N);
        } else {
            chatUser.setState(RemittanceState.Y);
        }
        return ResponseEntity.ok(chatUser.getState());
    }


    //참여중인 채팅방
    @Transactional(readOnly = true)
    @GetMapping(value = "/chat-rooms/progressing/user/{user_id}", produces = "application/json")
    public ResponseEntity<Object> getProgressingChatList(@PathVariable(name = "user_id") String user_id) throws Exception {
        JSONObject json = MyUtility.convertListToJSONobj(chatMainRepository.findByUserIdInChatUser(user_id), "chatRoom");
        return ResponseEntity.ok(json.toMap());
    }

    //상태,방장아이디로 조회
    @Transactional(readOnly = true)
    @GetMapping(value = "/chat-rooms/user/{user_id}", produces = "application/json")
    public ResponseEntity<Object> getChatListByStateAndUserId(@PathVariable(name = "user_id") String user_id, @RequestParam(name = "state") String state) throws Exception {

        ChatState chatState = ChatState.valueOf(state.toUpperCase());
        List<ChatMain> list = chatMainRepository.findByStateAndUserId(chatState, user_id);
        JSONObject json;
        if (ChatState.END.equals(chatState)) {
            json = MyUtility.convertListToJSONobjIgnoringCurrentCount(list, "chatRoom");
        } else {
            json = MyUtility.convertListToJSONobj(list, "chatRoom");
        }


        return ResponseEntity.ok(json.toMap());
    }


    //카테고리 리스트
    @GetMapping(value = "/categories", produces = "application/json")
    public ResponseEntity<Object> getCategoryList() {
        return ResponseEntity.ok(MyUtility.convertCategoryToJSONobj("categories").toMap());
    }

    @GetMapping(value = "/chat-rooms/tag/most-wanted", produces = "application/json")
    @Transactional(readOnly = true)
    public ResponseEntity<Object> getMostWantedTagList() {
        List<String> list = tagRepository.findTop10Tag();
        JSONObject json = MyUtility.convertStringListToJSONObject(list, "tags");
        return ResponseEntity.ok(json.toMap());
    }

    @GetMapping(value = "/chat-rooms/tag", produces = "application/json")
    @Transactional(readOnly = true)
    public ResponseEntity<Object> getChatListByTag(@RequestParam("keyWord") String keyWord) throws Exception {
        List<ChatMain> list = chatMainRepository.findByTagNameAndStateNotIn(keyWord, ChatState.insearchableList);
        JSONObject json = MyUtility.convertListToJSONobj(list, "chatRoom");
        return ResponseEntity.ok(json.toMap());
    }

    @GetMapping(value = "/chat-rooms/search", produces = "application/json")
    @Transactional(readOnly = true)
    public ResponseEntity<Object> getChatListByKeyWord(@RequestParam("keyWord") String keyWord) throws Exception {
        List<ChatMain> list = chatMainRepository.findByTagNameOrTitleContainsAndStateNotIn(keyWord, keyWord, ChatState.insearchableList);
        JSONObject json = MyUtility.convertListToJSONobj(list, "chatRoom");
        return ResponseEntity.ok(json.toMap());
    }

    @GetMapping(value = "/chat-rooms/{chatMain_id}/banned-user", produces = "application/json")
    @Transactional(readOnly = true)
    public ResponseEntity<Object> getBannedChatUserList(@PathVariable("chatMain_id") String chatMain_idstr) throws Exception {
        Long chatMain_id = Long.parseLong(chatMain_idstr);
        List<ChatUser> list = chatUserRepository.findByChatMainIdAndBanState(chatMain_id, BanState.BANNED);
        JSONObject json = MyUtility.convertListToJSONobj(list, "users");
        return ResponseEntity.ok(json.toMap());
    }

    //즐겨찾기 버튼 클릭시
    @PatchMapping(value = "/chat-rooms/{chatMain_id}/bookmarks/{user_id}")
    @Transactional
    public ResponseEntity<Object> addBookmarks(@PathVariable("chatMain_id") String chatMain_idstr,
                                               @PathVariable("user_id") String user_id) {

        Long chatMain_id = Long.parseLong(chatMain_idstr);
        ChatMain chatMain = chatMainRepository.findById(chatMain_id)
                .orElseThrow(() -> new CustomHttpException(ErrorCode.RESOURCE_NOT_FOUND));
        if (chatMain.getUser().getId().equals(user_id)) {
            chatMain.updateBookmarks();
            return ResponseEntity.ok("ok");
        } else {
            throw new CustomHttpException(ErrorCode.RESOURCE_NOT_FOUND);
        }
    }

    //주문목록 리스트 개선
    @GetMapping(value = "/chat-rooms/order-list/{user_id}", produces = "application/json")
    @Transactional(readOnly = true)
    public ResponseEntity<Object> getOrderListTest(@PathVariable("user_id") String user_id) throws Exception {
        List<ChatMain> bookmarkedList =
                chatMainRepository.findTop10ByBookmarksAndUserIdOrderByBookmarksDate(Bookmarks.Y, user_id);
        List<ChatMain> unBookmarkedList =
                chatMainRepository.findByStateInAndUserIdAndBookmarksOrderByCreateDate(ChatState.insearchableList, user_id, Bookmarks.N);
        bookmarkedList.addAll(unBookmarkedList);
        JSONObject json = MyUtility.convertListToJSONobjIgnoringCurrentCount(bookmarkedList, "chatRooms");
        return ResponseEntity.ok(json.toMap());
    }

//	@GetMapping(value = "/chat-rooms/order-list/{user_id}",produces = "application/json")
//	@Transactional(readOnly = true)
//	public ResponseEntity<Object> getOrderList(@PathVariable("user_id")String user_id) throws Exception{
//		List<ChatMain> list = chatMainRepository.findOrderList(user_id);
//		JSONObject json = MyUtility.convertListToJSONobj(list,"chatRooms");
//		return ResponseEntity.ok(json.toMap());
//	}
}
