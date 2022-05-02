package com.naeggeodo.repository;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.naeggeodo.entity.chat.Category;
import com.naeggeodo.entity.chat.ChatMain;
import com.naeggeodo.entity.chat.ChatState;


@Repository
public class ChatMainRepository {
	@Autowired
	private EntityManager em;
	
	public Long save(ChatMain chatmain) {
		em.persist(chatmain);
		em.flush();
		return chatmain.getId();
	}
	
	public ChatMain findOne(Long id) {
		return em.find(ChatMain.class, id);
	}
	
	public List<ChatMain> findAll(){
		return em.createQuery("SELECT c FROM ChatMain c",ChatMain.class).getResultList();
	}
	
	public List<ChatMain> findBycategoryAndBuildingCode(Category category,String buildingCode){
		return em.createQuery("SELECT c FROM ChatMain c WHERE c.category = :"
				+ "category AND c.buildingCode = :buildingCode",ChatMain.class).setParameter("category", category)
				.setParameter("buildingCode", buildingCode).getResultList();
	}
	
	public List<ChatMain> findByBuildingCode(String buildingCode){
		return em.createQuery("SELECT c FROM ChatMain c WHERE c.buildingCode = :buildingCode",ChatMain.class)
				.setParameter("buildingCode", buildingCode).getResultList();
	}
	
	public void updateState(Long id ,ChatState state) {
		ChatMain chatMain = em.find(ChatMain.class, id);
		chatMain.setState(state);
	}
	
}
