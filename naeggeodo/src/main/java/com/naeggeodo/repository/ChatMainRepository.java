package com.naeggeodo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.naeggeodo.entity.chat.Category;
import com.naeggeodo.entity.chat.ChatMain;
import com.naeggeodo.entity.chat.ChatState;

public interface ChatMainRepository extends JpaRepository<ChatMain, Long>{
	@EntityGraph(attributePaths = {"chatUser"})
	public List<ChatMain> findByCategoryAndBuildingCode(Category category,String buildingCode);
	@EntityGraph(attributePaths = {"chatUser"})
	public List<ChatMain> findByBuildingCode(String buildingCode);
	
	@Query("SELECT cm FROM ChatMain cm join ChatUser cu on cm.id = cu.chatMain.id WHERE cu.user.id = :user_id")
	@EntityGraph(attributePaths = {"chatUser"})
	public List<ChatMain> findByUserIdInChatUser(@Param("user_id") String user_id);
	
	@Query("SELECT c FROM ChatMain c WHERE c.id = :id")
	@EntityGraph(attributePaths = {"chatUser"})
	ChatMain findChatMainEntityGraph(@Param("id") Long id);
	
	public List<ChatMain> findByStateAndUserId(ChatState state,String user_id);
	
	@EntityGraph(attributePaths = {"chatUser"})
	public List<ChatMain> findByTagName(String tagName);
}
