package com.naeggeodo.repository;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.naeggeodo.entity.chat.ChatDetail;


@Repository
public class ChatDetailRepository {
	@Autowired
	private EntityManager em;
	
	public void save(ChatDetail chatDetail) {
		em.persist(chatDetail);
	}
	
	public List<ChatDetail> load(Long chatMain_id, Long user_id){
		return em.createQuery("SELECT cd FROM ChatDetail cd WHERE cd.chatmain.id = :chatMain_id AND cd.regDate >= (SELECT cu.enterDate FROM ChatUser cu WHERE cu.chatMain.id = :chatMain_id AND cu.user.id =:user_id)",ChatDetail.class)
				.setParameter("chatMain_id", chatMain_id).setParameter("user_id", user_id).getResultList();
	}
}
