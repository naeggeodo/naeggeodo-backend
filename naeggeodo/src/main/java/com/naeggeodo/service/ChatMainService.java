package com.naeggeodo.service;

import com.naeggeodo.dto.ChatRoomDTO;
import com.naeggeodo.dto.ChatRoomUpdateDTO;
import com.naeggeodo.dto.ChatRoomVO;
import com.naeggeodo.entity.chat.*;
import com.naeggeodo.entity.deal.Deal;
import com.naeggeodo.entity.user.Users;
import com.naeggeodo.exception.CustomHttpException;
import com.naeggeodo.exception.ErrorCode;
import com.naeggeodo.repository.*;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ChatMainService {

    private final ChatMainRepository chatMainRepository;
    private final CloudinaryService cloudinaryService;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final ChatDetailRepository chatDetailRepository;
    private final DealRepository dealRepository;
    private final ChatUserRepository chatUserRepository;

    @Transactional(readOnly = true)
    public List<ChatRoomVO> getChatListWithCategory(String buildingCode, Category category) {
        List<ChatMain> chatList = chatMainRepository.findByCategoryAndBuildingCodeAndStateNotInOrderByCreateDateDesc(category, buildingCode, ChatState.insearchableList);
        return chatList.stream()
                .map(ChatRoomVO::convert)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ChatRoomVO> getChatListWithoutCategory(String buildingCode) {
        List<ChatMain> chatList = chatMainRepository.findByBuildingCodeAndStateNotInOrderByCreateDateDesc(buildingCode, ChatState.insearchableList);
        return chatList.stream()
                .map(ChatRoomVO::convert)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ChatRoomVO> getChatListByKeyWord(String keyWord) {
        List<ChatMain> chatList = chatMainRepository.findByTagNameOrTitleContainsAndStateNotInOrderByCreateDateDesc(keyWord, keyWord, ChatState.insearchableList);
        return chatList.stream()
                .map(ChatRoomVO::convert)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ChatRoomVO> getChatListByTagName(String keyWord) {
        List<ChatMain> chatList = chatMainRepository.findByTagNameAndStateNotInOrderByCreateDateDesc(keyWord, ChatState.insearchableList);
        return chatList.stream()
                .map(ChatRoomVO::convert)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ChatRoomVO getChatMain(Long id) {
        ChatMain chatMain = chatMainRepository.findChatUserEntityGraph(id);
        return ChatRoomVO.convert(chatMain);
    }

    //채팅방 생성
    @Transactional
    public JSONObject createChatRoom(ChatRoomDTO dto, MultipartFile file) {
        Users user = userRepository.getById(dto.getUser_id());

        List<String> tagStringList = dto.getTag();
        List<Tag> tags = new ArrayList<>();
        if (tagStringList != null) {
            for (String name : tagStringList) {
                tags.add(Tag.create(name));
            }
        }
        ChatMain chatMain = dto.createChatMain(user, tags);
        ChatMain savedChatMain = chatMainRepository.save(chatMain);

        if (file != null)
            cloudinaryService.upload(file, "chatMain/" + savedChatMain.getId(), savedChatMain.getId());
        else
            chatMain.setDefaultImgPath();

        chatDetailRepository.save(ChatDetail.create("채팅방이 생성 되었습니다", user, savedChatMain, ChatDetailType.CREATED));

        JSONObject json = new JSONObject();
        json.put("chatMain_id", savedChatMain.getId());
        return json;
    }

    @Transactional(readOnly = true)
    public List<ChatRoomVO> getOrderList(String userId) {
        List<ChatMain> bookmarkedList =
                chatMainRepository.findTop10ByBookmarksAndUserIdOrderByBookmarksDateDesc(Bookmarks.Y, userId);
        List<ChatMain> unBookmarkedList =
                chatMainRepository.findByStateInAndUserIdAndBookmarksOrderByCreateDateDesc(ChatState.insearchableList, userId, Bookmarks.N);
        bookmarkedList.addAll(unBookmarkedList);

        return bookmarkedList.stream()
                .map(ChatRoomVO::convert)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<ChatRoomVO> getChatListByStateAndUserId(String userId, String state) {
        ChatState chatState = ChatState.valueOf(state.toUpperCase());
        List<ChatMain> list = chatMainRepository.findByStateAndUserId(chatState, userId);
        return list.stream()
                .map(ChatRoomVO::convert)
                .collect(Collectors.toList());
    }

    @Transactional
    public ChatRoomUpdateDTO deleteChatMain(Long chatMain_id) {
        ChatMain chatMain = chatMainRepository.findById(chatMain_id)
                .orElseThrow(() -> new CustomHttpException(ErrorCode.RESOURCE_NOT_FOUND));
        if (!chatMain.isDeletable()) {
            throw new CustomHttpException(ErrorCode.INVALID_FORMAT);
        }
        chatMainRepository.delete(chatMain);

        return ChatRoomUpdateDTO.builder()
                .chatMain_id(chatMain_id)
                .deleted(true)
                .build();
    }

    @Transactional
    public ChatRoomUpdateDTO addBookmarks(Long chatMain_id, String userId) {
        ChatMain chatMain = chatMainRepository.findById(chatMain_id)
                .orElseThrow(() -> new CustomHttpException(ErrorCode.RESOURCE_NOT_FOUND));

        if (chatMain.getUser().getId().equals(userId)) {
            chatMain.updateBookmarks();
            return ChatRoomUpdateDTO.builder()
                    .chatMain_id(chatMain.getId())
                    .bookmarks(chatMain.getBookmarks().name())
                    .build();
        } else {
            throw new CustomHttpException(ErrorCode.RESOURCE_NOT_FOUND);
        }

    }

    @Transactional
    public ChatRoomUpdateDTO updateTitle(Long chatMain_id, String title) {
        ChatMain chatMain = chatMainRepository.findById(chatMain_id)
                .orElseThrow(() -> new CustomHttpException(ErrorCode.RESOURCE_NOT_FOUND));
        chatMain.updateTitle(title);

        return ChatRoomUpdateDTO.builder()
                .chatMain_id(chatMain.getId())
                .title(chatMain.getTitle())
                .build();
    }

    // 기존 내역으로 생성
    @Transactional
    public void copyChatRoom(Long targetId, OrderTimeType orderTimeType) {
        ChatMain targetChatMain = chatMainRepository.findTagEntityGraph(targetId);
        ChatMain savedChatMain = chatMainRepository.save(targetChatMain.copy(orderTimeType));
        List<Tag> saveTags = savedChatMain.copyTags(targetChatMain.getTag());
        tagRepository.saveAll(saveTags);

        chatDetailRepository.save(ChatDetail.create("채팅방이 생성 되었습니다", savedChatMain.getUser(), savedChatMain, ChatDetailType.CREATED));
    }

    // 채팅방 상태 업데이트
    @Transactional
    public ChatRoomUpdateDTO updateRoomState(Long chatMain_id, String state) {
        ChatState chatState = ChatState.valueOf(state);
        ChatMain chatMain = chatMainRepository.findById(chatMain_id)
                .orElseThrow(() -> new CustomHttpException(ErrorCode.RESOURCE_NOT_FOUND));
        chatMain.changeState(chatState);

        if (ChatState.END.equals(chatMain.getState())) {
            chatMain.getAllowedUserList()
                    .forEach(cu -> dealRepository.save(Deal.create(cu.getUser(), chatMain)));
            chatUserRepository.deleteAll(chatMain.getChatUser());
            chatMain.getChatUser().clear();
        }
        return ChatRoomUpdateDTO.builder()
                .chatMain_id(chatMain.getId())
                .state(chatMain.getState().name())
                .build();
    }

    //참여중인 채팅방 리스트(내꺼톡)
    @Transactional
    public List<ChatRoomVO> getProgressingChatList(String user_id) {
        List<ChatMain> list = chatMainRepository.findByUserIdInChatUser(user_id, ChatState.insearchableList);
        List<Long> idList = new ArrayList<>();
        for (ChatMain c : list) {
            idList.add(c.getId());
        }
        List<String> latestMessages = chatDetailRepository.findLatestContents(idList);
        Collections.reverse(latestMessages);

        List<ChatRoomVO> chatList = list.stream()
                .map(ChatRoomVO::convert)
                .collect(Collectors.toList());
        ChatRoomVO.setLatestMessage(chatList, latestMessages);

        return chatList;
    }

}
