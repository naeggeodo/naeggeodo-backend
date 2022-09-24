package com.naeggeodo.repository;

import com.naeggeodo.entity.chat.Category;
import com.naeggeodo.entity.chat.ChatMain;
import com.naeggeodo.entity.chat.ChatState;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ChatMainRepositoryTest {

    @Autowired
    private ChatMainRepository chatMainRepository;

    @Test
    void saveTest() {
        ChatMain chatMain = new ChatMain();

        ChatMain savedChatMain = chatMainRepository.save(chatMain);
        chatMainRepository.flush();

        assertThat(savedChatMain).isEqualTo(chatMain);
    }

    @Test
    void findByCategoryAndBuildingCodeAndStateNotInOrderByCreateDateDescTest() {
        //g
        chatMainRepository.saveAll(
                Arrays.asList(
                        ChatMain.builder()
                                .category(Category.CHINESE)
                                .state(ChatState.CREATE)
                                .buildingCode("111")
                                .title("1st")
                                .createDate(LocalDateTime.of(2022, 9, 24, 5, 3, 3))
                                .build()
                        ,
                        ChatMain.builder()
                                .category(Category.CHINESE)
                                .state(ChatState.CREATE)
                                .buildingCode("111")
                                .title("2nd")
                                .createDate(LocalDateTime.of(2022, 9, 24, 5, 3, 2))
                                .build()
                        ,
                        ChatMain.builder()
                                .category(Category.CHINESE)
                                .state(ChatState.FULL)
                                .buildingCode("111")
                                .title("3rd")
                                .createDate(LocalDateTime.of(2022, 9, 24, 5, 3, 1))
                                .build()
                        ,
                        ChatMain.builder()
                                .category(Category.PIZZA)
                                .state(ChatState.CREATE)
                                .buildingCode("112")
                                .title("4th")
                                .createDate(LocalDateTime.of(2022, 9, 24, 5, 3, 1))
                                .build()
                )
        );
        chatMainRepository.flush();

        //w
        List<ChatMain> list = chatMainRepository.findByCategoryAndBuildingCodeAndStateNotInOrderByCreateDateDesc(
                Category.CHINESE, "111", ChatState.insearchableList
        );

        //t
        // 검색된 리스트의 사이즈는 3이다
        assertThat(list).size().isEqualTo(3);
        // Category.CHINESE는 3개이다.
        assertThat(list).filteredOn(
                item -> item.getCategory().equals(Category.CHINESE)
        ).size().isEqualTo(3);
        // 빌딩코드가 111인 요소는 3개이다.
        assertThat(list).filteredOn(
                item -> item.getBuildingCode().equals("111")
        ).size().isEqualTo(3);
        // FULL 상태의 채팅방은 1개다
        assertThat(list).filteredOn(
                item -> item.getState().equals(ChatState.FULL)
        ).size().isEqualTo(1);
        // 정렬 테스트
        assertThat(list.get(0).getCreateDate()).isAfter(list.get(1).getCreateDate());
        assertThat(list.get(1).getCreateDate()).isAfter(list.get(2).getCreateDate());
        assertThat(list.get(2).getCreateDate()).isBefore(list.get(0).getCreateDate());


    }

    @Test
    void findByBuildingCodeAndStateNotInOrderByCreateDateDesc() {
    }

    @Test
    void findByUserIdInChatUser() {
    }

    @Test
    void findChatMainEntityGraph() {
    }

    @Test
    void findTagEntityGraph() {
    }

    @Test
    void findByStateAndUserId() {
    }

    @Test
    void findByTagNameAndStateNotInOrderByCreateDateDesc() {
    }

    @Test
    void findByTagNameOrTitleContainsAndStateNotInOrderByCreateDateDesc() {
    }

    @Test
    void updateForImgPath() {
    }

    @Test
    void findTop10ByBookmarksAndUserIdOrderByBookmarksDateDesc() {
    }

    @Test
    void findByStateInAndUserIdAndBookmarksOrderByCreateDateDesc() {
    }
}