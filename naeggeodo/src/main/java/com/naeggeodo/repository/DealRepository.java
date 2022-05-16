package com.naeggeodo.repository;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.naeggeodo.entity.deal.Deal;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DealRepository {
	private final EntityManager em;
	
	public void save(Deal deal) {
		em.persist(deal);
	}
	
	public List<Deal> findByChatMain(Long chatMain_id){
		return em.createQuery("SELECT d FROM Deal d WHERE d.chatMain.id = :chatMain_id",Deal.class)
		.setParameter("chatMain_id", chatMain_id).getResultList();
	}
	
}
