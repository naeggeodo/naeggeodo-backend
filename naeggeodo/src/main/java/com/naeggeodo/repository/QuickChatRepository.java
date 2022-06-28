package com.naeggeodo.repository;

import com.naeggeodo.entity.chat.QuickChat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuickChatRepository extends JpaRepository<QuickChat, Long>{
	QuickChat getByUserId(String user_id);
}
