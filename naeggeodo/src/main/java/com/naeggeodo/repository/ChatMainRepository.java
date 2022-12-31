package com.naeggeodo.repository;

import com.naeggeodo.entity.chat.Bookmarks;
import com.naeggeodo.entity.chat.Category;
import com.naeggeodo.entity.chat.ChatMain;
import com.naeggeodo.entity.chat.ChatState;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMainRepository extends JpaRepository<ChatMain, Long> {
    @EntityGraph(attributePaths = {"chatUser"})
    List<ChatMain> findByCategoryAndBuildingCodeAndStateNotInOrderByCreateDateDesc(Category category, String buildingCode, List<ChatState> state);

    @EntityGraph(attributePaths = {"chatUser"})
    List<ChatMain> findByBuildingCodeAndStateNotInOrderByCreateDateDesc(String buildingCode, List<ChatState> state);

    @Query("SELECT cm FROM ChatMain cm join fetch ChatUser cu on cm.id = cu.chatMain.id WHERE cu.user.id = :user_id AND cm.state not in :stateList AND cu.banState = 'ALLOWED' order by cm.createDate desc")
    @EntityGraph(attributePaths = {"chatUser"})
    List<ChatMain> findByUserIdInChatUser(@Param("user_id") String user_id, @Param("stateList") List<ChatState> stateList);

    @Query("SELECT c FROM ChatMain c WHERE c.id = :id")
    @EntityGraph(attributePaths = {"chatUser"})
    ChatMain findChatMainEntityGraph(@Param("id") Long id);

    @Query("select c from ChatMain c where c.id = :id")
    @EntityGraph(attributePaths = {"tag"})
    ChatMain findTagEntityGraph(@Param("id") Long id);

    List<ChatMain> findByStateAndUserId(ChatState state, String user_id);

    @EntityGraph(attributePaths = {"chatUser"})
    List<ChatMain> findByTagNameAndStateNotInOrderByCreateDateDesc(String tagName, List<ChatState> state);

    @EntityGraph(attributePaths = {"chatUser"})
    List<ChatMain> findByTagNameOrTitleContainsAndStateNotInOrderByCreateDateDesc(String tagName, String title, List<ChatState> state);

    @Modifying
    @Query("update ChatMain c set c.imgPath = :imgPath where c.id = :chatMain_id")
    void updateForImgPath(@Param("imgPath") String imgPath, @Param("chatMain_id") Long id);

    @EntityGraph(attributePaths = {"tag"})
    List<ChatMain> findTop10ByBookmarksAndUserIdOrderByBookmarksDateDesc(Bookmarks bookmarks, String user_id);

    @EntityGraph(attributePaths = {"tag"})
    List<ChatMain> findByStateInAndUserIdAndBookmarksOrderByCreateDateDesc(List<ChatState> states, String user_id, Bookmarks bookmarks);
}
