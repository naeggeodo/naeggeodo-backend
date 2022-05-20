package com.naeggeodo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.naeggeodo.entity.chat.QuickChat;

public interface QuickChatRepository extends JpaRepository<QuickChat, Long>{
	QuickChat findByUserId(String user_id);
	QuickChat getByUserId(String user_id);
}
