package com.naeggeodo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.naeggeodo.entity.chat.Category;
import com.naeggeodo.entity.chat.ChatMain;
import com.naeggeodo.entity.chat.ChatState;

public interface ChatMainRepository extends JpaRepository<ChatMain, Long>{
	@EntityGraph(attributePaths = {"chatUser"})
	List<ChatMain> findByCategoryAndBuildingCode(Category category,String buildingCode);
	@EntityGraph(attributePaths = {"chatUser"})
	List<ChatMain> findByBuildingCode(String buildingCode);
	
	@Query("SELECT cm FROM ChatMain cm join ChatUser cu on cm.id = cu.chatMain.id WHERE cu.user.id = :user_id")
	@EntityGraph(attributePaths = {"chatUser"})
	List<ChatMain> findByUserIdInChatUser(@Param("user_id") String user_id);
	
	@Query("SELECT c FROM ChatMain c WHERE c.id = :id")
	@EntityGraph(attributePaths = {"chatUser"})
	ChatMain findChatMainEntityGraph(@Param("id") Long id);
	
	List<ChatMain> findByStateAndUserId(ChatState state,String user_id);
	
//	@Query(value = "SELECT cm.* FROM chat_main cm \r\n"
//			+ "left join chat_user cu on cu.chatmain_id = cm.chatmain_id \r\n"
//			+ "LEFT join tag t on  t.chatmain_id = cm.chatmain_id WHERE t.name =:name ",nativeQuery = true)
//	public List<ChatMain> findByTagName(@Param("name") String tagName);
	
	@EntityGraph(attributePaths = {"chatUser"})
	List<ChatMain> findByTagName(String tagName);
	
	@EntityGraph(attributePaths = {"chatUser"})
	List<ChatMain> findByTagNameOrTitleContains(String tagName,String title);

	@Modifying
	@Query("update ChatMain c set c.imgPath = :imgPath where c.id = :chatMain_id")
	void updateForImgPath(@Param("imgPath")String imgPath,@Param("chatMain_id")Long id);
	
}
