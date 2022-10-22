package com.naeggeodo.repository;

import com.naeggeodo.entity.chat.*;
import com.naeggeodo.entity.user.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ChatMainRepositoryTest {

    @Autowired
    ChatMainRepository chatMainRepository;
    @Autowired
    ChatUserRepository chatUserRepository;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void init(){
        List<Users> usersList = Arrays.asList(
                createUser("user0"),
                createUser("user1"),
                createUser("user2")
        );
        userRepository.saveAllAndFlush(usersList);

        List<Tag> tags0 = Arrays.asList(
                Tag.create("tag0"),
                Tag.create("tag1"),
                Tag.create("tag2")
        );

        List<Tag> tags1 = Arrays.asList(
                Tag.create("tag1"),
                Tag.create("tag2"),
                Tag.create("tag3")
        );

        List<Tag> tags2 = Arrays.asList(
                Tag.create("tag2"),
                Tag.create("tag3"),
                Tag.create("tag4")
        );

        List<Tag> tags3 = Arrays.asList(
                Tag.create("tag0"),
                Tag.create("tag1"),
                Tag.create("tag2"),
                Tag.create("tag3"),
                Tag.create("tag4")
        );

        List<ChatMain> chatMainList = Arrays.asList(
                ChatMain.builder()
                        .category(Category.CHINESE)
                        .state(ChatState.CREATE)
                        .buildingCode("111")
                        .title("1st")
                        .user(usersList.get(0))
                        .build()
                ,
                ChatMain.builder()
                        .category(Category.CHINESE)
                        .state(ChatState.CREATE)
                        .buildingCode("111")
                        .title("2nd")
                        .user(usersList.get(1))
                        .build()
                ,
                ChatMain.builder()
                        .category(Category.CHINESE)
                        .state(ChatState.FULL)
                        .buildingCode("111")
                        .title("3rd")
                        .user(usersList.get(2))
                        .bookmarks(Bookmarks.Y)
                        .build()
                ,
                ChatMain.builder()
                        .category(Category.PIZZA)
                        .state(ChatState.CREATE)
                        .buildingCode("112")
                        .title("4th")
                        .user(usersList.get(2))
                        .bookmarks(Bookmarks.N)
                        .build()
        );

        chatMainList.get(0).setTags(tags0);
        chatMainList.get(1).setTags(tags1);
        chatMainList.get(2).setTags(tags2);
        chatMainList.get(3).setTags(tags3);
        chatMainRepository.saveAllAndFlush(chatMainList);

        for (int i = 0; i < 3; i++) {
            chatUserRepository.saveAndFlush(ChatUser.create(usersList.get(i),chatMainList.get(i),"ss"+i));
        }
    }



    @Test
    void saveTest() {
        ChatMain chatMain = new ChatMain();

        ChatMain savedChatMain = chatMainRepository.save(chatMain);
        chatMainRepository.flush();

        assertThat(savedChatMain).isEqualTo(chatMain);
    }

    @Test
    @DisplayName("카테고리 , 빌딩코드로 검색 (참여 가능한 채팅방)")
    void findByCategoryAndBuildingCodeAndStateNotInOrderByCreateDateDescTest() {
        //g

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
    }

    @Test
    @DisplayName("빌딩코드로 검색 (참여가능한 채팅방)")
    void findByBuildingCodeAndStateNotInOrderByCreateDateDesc() {

        List<ChatMain> list = chatMainRepository.findByBuildingCodeAndStateNotInOrderByCreateDateDesc("112", ChatState.insearchableList);
        // 빌딩코드가 112인 요소는 1개이다.
        assertThat(list).filteredOn(
                item -> item.getBuildingCode().equals("112")
        ).size().isEqualTo(1);
        assertThat(list).filteredOn(
                item -> item.getBuildingCode().equals("111")
        ).size().isEqualTo(0);

    }

    @Test
    @DisplayName("참여중인 유저id로 검색")
    void findByUserIdInChatUser() {
        List<ChatMain> list = chatMainRepository.findByUserIdInChatUser("user2", ChatState.insearchableList);

        assertThat(list).size().isEqualTo(1);
        assertThat(list.get(0).getTitle()).isEqualTo("3rd");

    }

//    @Test
//    void findChatMainEntityGraph() {
//    }
//
//    @Test
//    void findTagEntityGraph() {
//    }

    @Test
    @DisplayName("상태 , 방장 id 로 검색")
    void findByStateAndUserId() {
        List<ChatMain> list = chatMainRepository.findByStateAndUserId(ChatState.FULL, "user2");

        assertThat(list).size().isEqualTo(1);
        assertThat(list.get(0).getState()).isEqualTo(ChatState.FULL);
    }

    @Test
    @DisplayName("검색 가능한 상태의 채팅방중 태그이름으로 검색")
    void findByTagNameAndStateNotInOrderByCreateDateDesc() {
        List<ChatMain> list0 = chatMainRepository.findByTagNameAndStateNotInOrderByCreateDateDesc("tag0", ChatState.insearchableList);
        List<ChatMain> list1 = chatMainRepository.findByTagNameAndStateNotInOrderByCreateDateDesc("tag1", ChatState.insearchableList);
        List<ChatMain> list2 = chatMainRepository.findByTagNameAndStateNotInOrderByCreateDateDesc("tag2", ChatState.insearchableList);
        List<ChatMain> list3 = chatMainRepository.findByTagNameAndStateNotInOrderByCreateDateDesc("tag3", ChatState.insearchableList);
        List<ChatMain> list4 = chatMainRepository.findByTagNameAndStateNotInOrderByCreateDateDesc("tag4", ChatState.insearchableList);

        assertThat(list0).size().isEqualTo(2);
        assertThat(list1).size().isEqualTo(3);
        assertThat(list2).size().isEqualTo(4);
        assertThat(list3).size().isEqualTo(3);
        assertThat(list4).size().isEqualTo(2);

    }

    @Test
    @DisplayName("검색 가능한 상태의 채팅방중 태그이름 혹은 방 제목 키워드로 검색 (Contains)")
    void findByTagNameOrTitleContainsAndStateNotInOrderByCreateDateDesc() {
        List<ChatMain> list0 = chatMainRepository.findByTagNameOrTitleContainsAndStateNotInOrderByCreateDateDesc("tag0", "a", ChatState.insearchableList);
        List<ChatMain> list1 = chatMainRepository.findByTagNameOrTitleContainsAndStateNotInOrderByCreateDateDesc("a", "1st", ChatState.insearchableList);

        assertThat(list0).size().isEqualTo(2);
        assertThat(list1).size().isEqualTo(1);

    }

//    1차캐시로 인해 테스트안됨
//    @Test
//    void updateForImgPath() {
//        ChatMain chatMain = chatMainRepository.findAll().get(0);
//        Long id = chatMain.getId();
//        chatMainRepository.updateForImgPath("imgPath",id);
//        chatMainRepository.flush();
//
//        Optional<ChatMain> findChatMain = chatMainRepository.findById(id);
//        assertThat(findChatMain.get().getImgPath()).isEqualTo("imgPath");
//    }

    @Test
    @DisplayName("방장 id 및 즐겨찾기 여부로 검색")
    void findTop10ByBookmarksAndUserIdOrderByBookmarksDateDesc() {
        List<ChatMain> list = chatMainRepository.findTop10ByBookmarksAndUserIdOrderByBookmarksDateDesc(Bookmarks.Y, "user2");
        assertThat(list).size().isEqualTo(1);
    }

    @Test
    @DisplayName("방장 id , 상태 , 즐겨찾기 여부로 검색")
    void findByStateInAndUserIdAndBookmarksOrderByCreateDateDesc() {
        List<ChatMain> list = chatMainRepository.findByStateInAndUserIdAndBookmarksOrderByCreateDateDesc(ChatState.searchableList, "user2", Bookmarks.N);
        assertThat(list).size().isEqualTo(1);
    }


    private Users createUser(String id){
        Users user = new Users();
        user.setId(id);
        return user;
    }
}