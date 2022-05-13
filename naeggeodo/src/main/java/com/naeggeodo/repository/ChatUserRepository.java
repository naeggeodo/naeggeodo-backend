package com.naeggeodo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.naeggeodo.entity.chat.ChatUser;

public interface ChatUserRepository extends JpaRepository<ChatUser, Long>{
	
	public ChatUser findByChatMainIdAndUserId(Long chatMain_id,String user_id);
	
	public List<ChatUser> findByChatMainId(Long chatMain_id);
	
	public Long countByChatMainIdAndUserId(Long chatMain_id,String user_id);
	
	public void deleteByChatMainIdAndUserId(Long chatMain_id,String user_id);
	
	public void deleteBySessionId(String session_id);
	
	public String findSessionIdByChatMainIdAndUserId(Long chatMain_id,String user_id);
	
	@Modifying
	@Query("UPDATE ChatUser c SET c.sessionId = :newSessionId WHERE c.sessionId = :oldSessionId")
	public void updateSessionId(@Param("newSessionId")String newSessionId,@Param("oldSessionId")String oldSessionId);
	
//	@Query("SELECT cu FROM ChatUser cu WHERE cu.chatMain.id = :chatMain_id AND cu.enterDate = (SELECT MIN(cu.enterDate) FROM ChatUser cu)")
//	public ChatUser findForChangeHost(@Param("chatMain_id")Long chatMain_id);
}
