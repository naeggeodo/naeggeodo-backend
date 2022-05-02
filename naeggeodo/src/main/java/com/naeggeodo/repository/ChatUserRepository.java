package com.naeggeodo.repository;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.naeggeodo.entity.chat.ChatUser;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ChatUserRepository {
	private final EntityManager em;
	
	public void save(ChatUser chatUser) {
		em.persist(chatUser);
	}
	
	public ChatUser findByChatMainAndUserId(Long chatMain_id,String user_id) {
		return em.createQuery("SELECT c FROM ChatUser c WHERE c.chatMain.id = :chatMain_id AND c.user.id = :user_id",ChatUser.class)
				.setParameter("chatMain_id", chatMain_id).setParameter("user_id", user_id).getSingleResult();
	}
	
	public List<ChatUser> findByChatMainId(Long chatMain_id){
		return em.createQuery("SELECT c FROM ChatUser c WHERE c.chatMain.id = :chatMain_id",ChatUser.class)
				.setParameter("chatMain_id", chatMain_id).getResultList();
	}
	
	//입장해있었는지 체크
	public Long check(Long chatMain_id,String user_id) {
		return em.createQuery("SELECT COUNT(*) FROM ChatUser c WHERE c.chatMain.id = :chatMain_id AND c.user.id =:user_id",Long.class)
		.setParameter("chatMain_id", chatMain_id).setParameter("user_id", user_id).getSingleResult();
	}
	
	//퇴장처리 (삭제)
	public void deleteById(Long chatMain_id,String user_id) {
		em.createQuery("DELETE FROM ChatUser c WHERE c.chatMain.id = :chatMain_id AND c.user.id =:user_id")
		.setParameter("chatMain_id", chatMain_id).setParameter("user_id", user_id).executeUpdate();
	}
	
	public void deleteBySessionId(String session_id) {
		em.createQuery("DELETE FROM ChatUser c WHERE c.session_id = :session_id").setParameter("session_id", session_id).executeUpdate();
	}
	
	//강퇴 하기위한 session_id select하기
	public String getSession_id(Long chatMain_id,String user_id) {
		return em.createQuery("SELECT c.session_id FROM ChatUser c WHERE c.chatMain.id = :chatMain_id AND c.user.id = :user_id",String.class)
				.setParameter("chatMain_id", chatMain_id).setParameter("user_id", user_id).getSingleResult();
	}
}
