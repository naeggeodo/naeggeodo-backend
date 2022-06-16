package com.naeggeodo.repository;

import java.util.List;

import com.naeggeodo.dto.UserNameIdDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import com.naeggeodo.entity.chat.BanState;
import com.naeggeodo.entity.chat.ChatUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatUserRepository extends JpaRepository<ChatUser, Long>{
	
	ChatUser findByChatMainIdAndUserId(Long chatMain_id,String user_id);
	
	List<ChatUser> findByChatMainId(Long chatMain_id);

	@Query("select new com.naeggeodo.dto.UserNameIdDTO(u.id,u.nickname,cu.state) from ChatUser cu join fetch Users u on u.id = cu.user.id where cu.chatMain.id = :chatMain_id and cu.chatMain.user.id <> u.id")
	List<UserNameIdDTO> findForRemit(@Param("chatMain_id")Long chatMain_id);

	Long countByChatMainIdAndUserId(Long chatMain_id,String user_id);
	
	void deleteByChatMainIdAndUserId(Long chatMain_id,String user_id);
	
	void deleteBySessionId(String session_id);
	

	List<ChatUser> findByChatMainIdAndBanState(Long chatMain_id,BanState banState);
	

}
