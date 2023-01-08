package com.naeggeodo.controller;

import com.naeggeodo.dto.ChatRoomDTO;
import com.naeggeodo.dto.ChatRoomUpdateDTO;
import com.naeggeodo.dto.ChatRoomVO;
import com.naeggeodo.dto.ResponseDTO;
import com.naeggeodo.entity.chat.Category;
import com.naeggeodo.entity.chat.OrderTimeType;
import com.naeggeodo.service.ChatMainService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.naeggeodo.util.ResponseUtils.created;
import static com.naeggeodo.util.ResponseUtils.success;

@RestController
@RequiredArgsConstructor
public class ChatMainController {

    private final ChatMainService chatMainService;


    //채팅방 리스트
    @GetMapping(value = "/chat-rooms", produces = "application/json", params = "category")
    public ResponseDTO<List<ChatRoomVO>> getChatListWithCategory(
            @RequestParam(name = "category") String category,
            @RequestParam(name = "buildingCode") String buildingCode) {
        //카테고리 조회시
        Category categoryEnum = Category.valueOf(category.toUpperCase());
        List<ChatRoomVO> chatList = chatMainService.getChatListWithCategory(buildingCode, categoryEnum);
        return success(chatList);
    }

    @GetMapping(value = "/chat-rooms", produces = "application/json", params = "!category")
    public ResponseDTO<List<ChatRoomVO>> getChatListWithoutCategory(@RequestParam(name = "buildingCode") String buildingCode) {
        List<ChatRoomVO> chatList = chatMainService.getChatListWithoutCategory(buildingCode);
        return success(chatList);
    }

    //채팅방 생성
    @PostMapping(value = "/chat-rooms", produces = "application/json")
    public ResponseDTO createChatRoom(@RequestPart @Valid ChatRoomDTO chat,
                                      @RequestPart(required = false) MultipartFile file) {
        chatMainService.createChatRoom(chat, file);
        return created();
    }

    // 채팅방 주문목록 리스트에서 생성
    @PostMapping(value = "/chat-rooms/{chatMain_id}/copy")
    public ResponseDTO copyChatRoom(@RequestParam("orderTimeType") String timeTypeStr,
                                                         @PathVariable("chatMain_id") Long chatMain_id) {
        OrderTimeType orderTimeType = OrderTimeType.valueOf(timeTypeStr);
        chatMainService.copyChatRoom(chatMain_id, orderTimeType);
        return created();
    }


    //해당 채팅방 data
    @GetMapping(value = "/chat-rooms/{chatMain_id}", produces = "application/json")
    public ResponseDTO<ChatRoomVO> getChatMain(@PathVariable(name = "chatMain_id") Long id) {
        ChatRoomVO chatRoomVO = chatMainService.getChatMain(id);
        return success(chatRoomVO);
    }

    //채팅방 상태 업데이트
    @PatchMapping(value = "/chat-rooms/{chatMain_id}", params = "state")
    public ResponseDTO<ChatRoomUpdateDTO> updateRoomState(@PathVariable(name = "chatMain_id") Long chatMain_id,
                                                          @RequestParam(name = "state") String state) {
        ChatRoomUpdateDTO dto = chatMainService.updateRoomState(chatMain_id, state);
        return success(dto);
    }

    //채팅방 제목 업데이트
    @PatchMapping(value = "/chat-rooms/{chatMain_id}", params = "title")
    public ResponseDTO<ChatRoomUpdateDTO> updateTitle(@PathVariable(name = "chatMain_id") Long chatMain_id,
                                                      @RequestParam(name = "title") String title) {
        ChatRoomUpdateDTO dto = chatMainService.updateTitle(chatMain_id, title);
        return success(dto);
    }

    //참여중인 채팅방
    @GetMapping(value = "/chat-rooms/progressing/user/{user_id}", produces = "application/json")
    public ResponseDTO<ChatRoomVO> getProgressingChatList(@PathVariable(name = "user_id") String user_id) {
        List<ChatRoomVO> chatList = chatMainService.getProgressingChatList(user_id);
        return success(chatList);
    }

    @GetMapping(value = "/chat-rooms/user/{user_id}", produces = "application/json")
    public ResponseDTO<List<ChatRoomVO>> getChatListByStateAndUserId(@PathVariable(name = "user_id") String user_id, @RequestParam(name = "state") String state) {
        List<ChatRoomVO> chatList = chatMainService.getChatListByStateAndUserId(user_id, state);
        return success(chatList);
    }


    //카테고리 리스트
    @GetMapping(value = "/categories", produces = "application/json")
    public ResponseDTO<List<String>> getCategoryList() {
        List<String> categoryList = Arrays.stream(Category.values())
                .map(Category::name)
                .collect(Collectors.toList());
        return success(categoryList);
    }


    @GetMapping(value = "/chat-rooms/tag", produces = "application/json")
    public ResponseDTO<List<ChatRoomVO>> getChatListByTag(@RequestParam("keyWord") String keyWord) {
        List<ChatRoomVO> chatList = chatMainService.getChatListByTagName(keyWord);
        return success(chatList);
    }

    @GetMapping(value = "/chat-rooms/search", produces = "application/json")
    public ResponseDTO<List<ChatRoomVO>> getChatListByKeyWord(@RequestParam("keyWord") String keyWord) {
        List<ChatRoomVO> chatList = chatMainService.getChatListByKeyWord(keyWord);
        return success(chatList);
    }


    //즐겨찾기 버튼 클릭시
    @PatchMapping(value = "/chat-rooms/{chatMain_id}/bookmarks/{user_id}")
    public ResponseDTO<ChatRoomUpdateDTO> addBookmarks(@PathVariable("chatMain_id") Long chatMain_id,
                                                       @PathVariable("user_id") String user_id) {
        ChatRoomUpdateDTO dto = chatMainService.addBookmarks(chatMain_id, user_id);
        return success(dto);
    }

    //주문목록 리스트 개선
    @GetMapping(value = "/chat-rooms/order-list/{user_id}", produces = "application/json")
    public ResponseDTO<List<ChatRoomVO>> getOrderList(@PathVariable("user_id") String user_id) {
        List<ChatRoomVO> chatList = chatMainService.getOrderList(user_id);
        return success(chatList);
    }

    @DeleteMapping(value = "/chat-rooms/{chatMain_id}", produces = "application/json")
    public ResponseDTO<ChatRoomUpdateDTO> deleteChatMain(@PathVariable("chatMain_id") Long chatMain_id) {
        ChatRoomUpdateDTO dto = chatMainService.deleteChatMain(chatMain_id);
        return success(dto);
    }

}
