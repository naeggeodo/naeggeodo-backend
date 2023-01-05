package com.naeggeodo.controller;

import com.naeggeodo.dto.ChatRoomDTO;
import com.naeggeodo.dto.ChatRoomVO;
import com.naeggeodo.entity.chat.*;
import com.naeggeodo.exception.CustomHttpException;
import com.naeggeodo.exception.ErrorCode;
import com.naeggeodo.repository.ChatMainRepository;
import com.naeggeodo.service.ChatMainService;
import com.naeggeodo.util.MyUtility;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ChatMainController {

    private final ChatMainRepository chatMainRepository;
    private final ChatMainService chatMainService;


    //채팅방 리스트
    @GetMapping(value = "/chat-rooms", produces = "application/json", params = "category")
    public List<ChatRoomVO> getChatListWithCategory(
            @RequestParam(name = "category") String category,
            @RequestParam(name = "buildingCode") String buildingCode) {
        //카테고리 조회시
        Category categoryEnum = Category.valueOf(category.toUpperCase());
        return chatMainService.getChatListWithCategory(buildingCode, categoryEnum);
    }

    @GetMapping(value = "/chat-rooms", produces = "application/json", params = "!category")
    public List<ChatRoomVO> getChatListWithoutCategory(@RequestParam(name = "buildingCode") String buildingCode) {
        return chatMainService.getChatListWithoutCategory(buildingCode);
    }

    //채팅방 생성
    @PostMapping(value = "/chat-rooms", produces = "application/json")
    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseEntity<Object> createChatRoom(@RequestPart @Valid ChatRoomDTO chat,
                                                 @RequestPart(required = false) MultipartFile file) {
        JSONObject json = chatMainService.createChatRoom(chat, file);
        return ResponseEntity.ok(json.toMap());
    }

    // 채팅방 주문목록 리스트에서 생성
    @PostMapping(value = "/chat-rooms/{chatMain_id}/copy")
    public ResponseEntity<Object> copyChatRoom(@RequestParam("orderTimeType") String timeTypeStr,
                                               @PathVariable("chatMain_id") String chatMain_idStr) {
        Long chatMain_id = Long.parseLong(chatMain_idStr);
        OrderTimeType orderTimeType = OrderTimeType.valueOf(timeTypeStr);

        JSONObject json = chatMainService.copyChatRoom(chatMain_id, orderTimeType);
        return ResponseEntity.ok(json.toMap());
    }


    //해당 채팅방 data
    @GetMapping(value = "/chat-rooms/{chatMain_id}", produces = "application/json")
    public ChatRoomVO getChatMain(@PathVariable(name = "chatMain_id") Long id) {
        return chatMainService.getChatMain(id);
    }

    //채팅방 상태 업데이트
    @PatchMapping(value = "/chat-rooms/{chatMain_id}", params = "state")
    public ResponseEntity<Object> updateRoomState(@PathVariable(name = "chatMain_id") Long chatMain_id,
                                                  @RequestParam(name = "state") String state) {
        JSONObject json = chatMainService.updateRoomState(chatMain_id, ChatState.valueOf(state.toUpperCase()));
        return ResponseEntity.ok(json.toMap());
    }

    //채팅방 제목 업데이트
    @PatchMapping(value = "/chat-rooms/{chatMain_id}", params = "title")
    @Transactional
    public ResponseEntity<Object> updateTitle(@PathVariable(name = "chatMain_id") Long chatMain_id,
                                              @RequestParam(name = "title") String title) {
        JSONObject json = new JSONObject();
        ChatMain chatMain = chatMainRepository.findById(chatMain_id)
                .orElseThrow(() -> new CustomHttpException(ErrorCode.RESOURCE_NOT_FOUND));
        chatMain.updateTitle(title);
        json.put("chatMain_id", chatMain.getId());
        json.put("title", chatMain.getTitle());
        return ResponseEntity.ok(json.toMap());
    }

    //참여중인 채팅방
    @Transactional(readOnly = true)
    @GetMapping(value = "/chat-rooms/progressing/user/{user_id}", produces = "application/json")
    public ResponseEntity<Object> getProgressingChatList(@PathVariable(name = "user_id") String user_id) throws Exception {
        JSONObject json = chatMainService.getProgressingChatList(user_id);
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
    public List<String> getCategoryList() {
        return Arrays.stream(Category.values())
                .map(Category::name)
                .collect(Collectors.toList());
    }


    @GetMapping(value = "/chat-rooms/tag", produces = "application/json")
    @Transactional(readOnly = true)
    public List<ChatRoomVO> getChatListByTag(@RequestParam("keyWord") String keyWord) {
        return chatMainService.getChatListByTagName(keyWord);
    }

    @GetMapping(value = "/chat-rooms/search", produces = "application/json")
    public List<ChatRoomVO> getChatListByKeyWord(@RequestParam("keyWord") String keyWord) {
        return chatMainService.getChatListByKeyWord(keyWord);
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
            JSONObject json = new JSONObject();
            json.put("chatMain_id", chatMain.getId());
            json.put("bookmarks", chatMain.getBookmarks().name());
            return ResponseEntity.ok(json.toMap());
        } else {
            throw new CustomHttpException(ErrorCode.RESOURCE_NOT_FOUND);
        }
    }

    //주문목록 리스트 개선
    @GetMapping(value = "/chat-rooms/order-list/{user_id}", produces = "application/json")
    @Transactional(readOnly = true)
    public ResponseEntity<Object> getOrderList(@PathVariable("user_id") String user_id) throws Exception {
        List<ChatMain> bookmarkedList =
                chatMainRepository.findTop10ByBookmarksAndUserIdOrderByBookmarksDateDesc(Bookmarks.Y, user_id);
        List<ChatMain> unBookmarkedList =
                chatMainRepository.findByStateInAndUserIdAndBookmarksOrderByCreateDateDesc(ChatState.insearchableList, user_id, Bookmarks.N);
        bookmarkedList.addAll(unBookmarkedList);
        JSONObject json = MyUtility.convertListToJSONobjIgnoringCurrentCount(bookmarkedList, "chatRooms");
        return ResponseEntity.ok(json.toMap());
    }

    @DeleteMapping(value = "/chat-rooms/{chatMain_id}", produces = "application/json")
    @Transactional
    public ResponseEntity<Object> deleteChatMain(@PathVariable("chatMain_id") String chatMain_idStr) {
        Long chatMain_id = Long.parseLong(chatMain_idStr);
        ChatMain chatMain = chatMainRepository.findById(chatMain_id)
                .orElseThrow(() -> new CustomHttpException(ErrorCode.RESOURCE_NOT_FOUND));
        if (!chatMain.isDeletable()) throw new CustomHttpException(ErrorCode.INVALID_FORMAT);
        chatMainRepository.delete(chatMain);

        return ResponseEntity.ok(new HashMap<String, Object>() {
            {
                put("chatMain_id", chatMain_id);
                put("deleted", true);
            }
        });
    }

}
