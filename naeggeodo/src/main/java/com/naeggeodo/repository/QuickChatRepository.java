package com.naeggeodo.repository;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.naeggeodo.entity.chat.QuickChat;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QuickChatRepository {
	private final EntityManager em;
	
	public void save(QuickChat quickChat) {
		em.persist(quickChat);
	}
	
	public QuickChat findOne(Long id) {
		return em.find(QuickChat.class, id);
	}
	
	public QuickChat findByUserId(String user_id) {
		return em.createQuery("SELECT q FROM QuickChat q WHERE user_id = :user_id",QuickChat.class)
				.setParameter("user_id", user_id).getSingleResult();
	}
}
