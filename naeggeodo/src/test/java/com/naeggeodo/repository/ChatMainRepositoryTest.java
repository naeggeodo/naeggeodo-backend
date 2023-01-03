package com.naeggeodo.repository;

import com.naeggeodo.entity.chat.Bookmarks;
import com.naeggeodo.entity.chat.Category;
import com.naeggeodo.entity.chat.ChatMain;
import com.naeggeodo.entity.chat.ChatState;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@DataJpaTest
@Sql("classpath:h2/chatMainRepository.sql")
class ChatMainRepositoryTest {

    @Autowired
    ChatMainRepository chatMainRepository;
    @Autowired
    EntityManager entityManager;

    @Test
    void saveTest() {
        ChatMain chatMain = new ChatMain();

        ChatMain savedChatMain = chatMainRepository.save(chatMain);
        chatMainRepository.flush();

        assertThat(savedChatMain).isEqualTo(chatMain);
    }

    @Test
    @DisplayName("카테고리 , 빌딩코드로 검색 (참여 가능한 채팅방) , 생성일시 내림차순 정렬")
    void findByCategoryAndBuildingCodeAndStateNotInOrderByCreateDateDescTest() {
        // given
        String buildingCode = "111";
        Category category = Category.CHINESE;
        // when
        List<ChatMain> list = chatMainRepository.findByCategoryAndBuildingCodeAndStateNotInOrderByCreateDateDesc(
                category, buildingCode, ChatState.insearchableList
        );

        // then
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
        //정렬 테스트
        assertCreateDateIsSortedDesc(list);
    }

    @Test
    @DisplayName("빌딩코드로 검색 (참여가능한 채팅방) , 생성일시 내림차순 정렬")
    void findByBuildingCodeAndStateNotInOrderByCreateDateDesc() {
        // given
        String buildingCode = "111";
        // when
        List<ChatMain> list = chatMainRepository.findByBuildingCodeAndStateNotInOrderByCreateDateDesc(buildingCode, ChatState.insearchableList);
        // then
        assertThat(list).size().isEqualTo(3);
        assertCreateDateIsSortedDesc(list);
    }

    @Test
    @DisplayName("참여중인 유저id로 검색")
    void findByUserIdInChatUser() {
        // given
        String user_id = "user2";
        //when
        List<ChatMain> list = chatMainRepository.findByUserIdInChatUser(user_id, ChatState.insearchableList);
        //then
        assertThat(list).size().isEqualTo(1);
        assertThat(list.get(0).getTitle()).isEqualTo("3rd");
    }

    @Test
    @DisplayName("fetch join - tag")
    void findChatMainEntityGraph() {
        log.info("findAll");
        Long id = chatMainRepository.findAll().get(0).getId();
        log.info("findChatMainEntityGraph");
        chatMainRepository.findChatUserEntityGraph(id);
    }

    @Test
    @DisplayName("fetch join - chatUser")
    void findTagEntityGraph() {
        log.info("findAll");
        Long id = chatMainRepository.findAll().get(0).getId();
        log.info("findTagEntityGraph");
        chatMainRepository.findTagEntityGraph(id);
    }

    @Test
    @DisplayName("상태 , 방장 id 로 검색")
    void findByStateAndUserId() {
        // given
        String user_id = "user2";
        ChatState state = ChatState.FULL;
        // when
        List<ChatMain> list = chatMainRepository.findByStateAndUserId(state, user_id);
        // then
        assertThat(list).size().isEqualTo(1);
        assertThat(list.get(0).getState()).isEqualTo(ChatState.FULL);
    }

    @Test
    @DisplayName("검색 가능한 상태의 채팅방중 태그이름으로 검색 , 생성일시 내림차순 정렬")
    void findByTagNameAndStateNotInOrderByCreateDateDesc() {
        // given
        String tagName = "tag2";
        // when
        List<ChatMain> list = chatMainRepository.findByTagNameAndStateNotInOrderByCreateDateDesc(tagName, ChatState.insearchableList);
        // then
        assertThat(list).size().isEqualTo(4);
        assertCreateDateIsSortedDesc(list);
    }

    @Test
    @DisplayName("검색 가능한 상태의 채팅방중 태그이름 혹은 방 제목 키워드로 검색 (Contains) , 생성일시 내림차순 정렬")
    void findByTagNameOrTitleContainsAndStateNotInOrderByCreateDateDesc() {
        //given
        String tagName = "tag0";
        String title = "a";
        //when
        List<ChatMain> list = chatMainRepository.findByTagNameOrTitleContainsAndStateNotInOrderByCreateDateDesc(tagName, title, ChatState.insearchableList);
        //then
        assertThat(list).size().isEqualTo(2);
        assertThat(list).isSortedAccordingTo(
                (c1,c2) -> c2.getCreateDate().compareTo(c1.getCreateDate())
        );
    }

    // em.clear 를 하지 않으면 findById() 를 캐시에서 조회하여 테스트 실패함
    // em을 주입받지 않고 clear 하는 방법은 없는가?
    @Test
    void updateForImgPath() {
        // given
        ChatMain chatMain = chatMainRepository.findAll().get(0);
        Long id = chatMain.getId();
        // when
        chatMainRepository.updateForImgPath("imgPath",id);
        chatMainRepository.flush();
        entityManager.clear();
        // then
        Optional<ChatMain> findChatMain = chatMainRepository.findById(id);
        assertThat(findChatMain.get().getImgPath()).isEqualTo("imgPath");
    }

    @Test
    @DisplayName("방장 id 및 즐겨찾기 여부로 상위 10개 검색 , 즐겨찾기일시 내림차순 정렬 ")
    void findTop10ByBookmarksAndUserIdOrderByBookmarksDateDesc() {
        // given
        String user_id = "bookmarker";
        Bookmarks bookmarks = Bookmarks.Y;
        // when
        List<ChatMain> list = chatMainRepository.findTop10ByBookmarksAndUserIdOrderByBookmarksDateDesc(bookmarks,user_id);
        //then
        assertThat(list).size().isEqualTo(10);
        assertBookmarkDateIsSortedDesc(list);
    }

    @Test
    @DisplayName("방장 id , 즐겨찾기 여부로 검색 , 생성일시 내림차순 정렬")
    void findByStateInAndUserIdAndBookmarksOrderByCreateDateDesc() {
        // given
        String user_id = "bookmarker";
        Bookmarks bookmarks = Bookmarks.N;
        //when
        List<ChatMain> list = chatMainRepository.findByStateInAndUserIdAndBookmarksOrderByCreateDateDesc(ChatState.insearchableList,user_id,bookmarks);
        //then
        assertThat(list).size().isEqualTo(2);
        assertCreateDateIsSortedDesc(list);
    }


    private void assertCreateDateIsSortedDesc(List<ChatMain> list){
        assertThat(list).isSortedAccordingTo(
                (c1,c2) -> c2.getCreateDate().compareTo(c1.getCreateDate())
        );
    }

    private void assertBookmarkDateIsSortedDesc(List<ChatMain> list){
        assertThat(list).isSortedAccordingTo(
                (c1,c2) -> c2.getBookmarksDate().compareTo(c1.getBookmarksDate())
        );
    }
}

