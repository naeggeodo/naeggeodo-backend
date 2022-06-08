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
	List<ChatMain> findByCategoryAndBuildingCodeAndStateNot(Category category,String buildingCode,ChatState state);
	@EntityGraph(attributePaths = {"chatUser"})
	List<ChatMain> findByBuildingCodeAndStateNot(String buildingCode,ChatState state);
	
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
	List<ChatMain> findByTagNameAndStateNot(String tagName,ChatState state);
	
	@EntityGraph(attributePaths = {"chatUser"})
	List<ChatMain> findByTagNameOrTitleContainsAndStateNot(String tagName,String title,ChatState state);

	@Modifying
	@Query("update ChatMain c set c.imgPath = :imgPath where c.id = :chatMain_id")
	void updateForImgPath(@Param("imgPath")String imgPath,@Param("chatMain_id")Long id);


	@Query(value = "(SELECT * from chat_main cm WHERE bookmarks = \"Y\" AND user_id = :user_id ORDER BY bookmarks_date LIMIT 10 )\n" +
			"UNION \n" +
			"(SELECT * from chat_main cm WHERE state = 'END' OR state = 'INCOMPLETE' AND user_id = :user_id ORDER BY create_date)"
			,nativeQuery = true)
	List<ChatMain> findOrderList(@Param("user_id")String user_id);

}
