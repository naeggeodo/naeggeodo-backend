package com.naeggeodo.repository.temp;

import com.naeggeodo.entity.chat.QuickChat;

public interface JPAQuickChatRepository {
	public QuickChat findByUserId(String user_id);
}
