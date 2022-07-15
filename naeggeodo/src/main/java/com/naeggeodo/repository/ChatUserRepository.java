package com.naeggeodo.repository;

import com.naeggeodo.dto.UserNameIdDTO;
import com.naeggeodo.entity.chat.BanState;
import com.naeggeodo.entity.chat.ChatUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatUserRepository extends JpaRepository<ChatUser, Long>{
	
	ChatUser findByChatMainIdAndUserId(Long chatMain_id,String user_id);
	
	@Query("select new com.naeggeodo.dto.UserNameIdDTO(u.id,u.nickname,cu.state) from ChatUser cu join fetch Users u on u.id = cu.user.id where cu.chatMain.id = :chatMain_id and cu.chatMain.user.id <> u.id and cu.banState = 'ALLOWED'")
	List<UserNameIdDTO> findForRemit(@Param("chatMain_id")Long chatMain_id);

	List<ChatUser> findByChatMainIdAndBanState(Long chatMain_id,BanState banState);
	

}
