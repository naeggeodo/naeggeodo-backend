package com.naeggeodo.repository.temp;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.naeggeodo.entity.chat.ChatUser;

public interface JPAChatUserRepository extends JpaRepository<ChatUser, Long>{
	
	public ChatUser findByChatMainIdAndUserId(Long chatMain_id,String user_id);
	
	public List<ChatUser> findByChatMainId(Long chatMain_id);
	
	public Long countByChatMainIdAndUserId(Long chatMain_id,String user_id);
	
	public void deleteByChatMainIdAndUserId(Long chatMain_id,String user_id);
	
	public void deleteBySessionId(String session_id);
}
