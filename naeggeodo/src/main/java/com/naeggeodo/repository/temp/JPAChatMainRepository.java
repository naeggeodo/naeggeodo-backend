package com.naeggeodo.repository.temp;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.naeggeodo.entity.chat.Category;
import com.naeggeodo.entity.chat.ChatMain;

public interface JPAChatMainRepository extends JpaRepository<ChatMain, Long>{
	public List<ChatMain> findByCategoryAndBuildingCode(Category category,String buildingCode);
	public List<ChatMain> findByBuildingCode(String buildingCode);
	
	@Query("SELECT cm FROM ChatMain cm join ChatUser cu on cm.id = cu.chatMain.id WHERE cu.user.id = :user_id")
	public List<ChatMain> findByUserIdInChatUser(String user_id);
}
