package com.naeggeodo.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.naeggeodo.entity.user.Users;

@Repository
public class UserRepository {
	
	@PersistenceContext
	private EntityManager em;
	
	public Users findOne(String id) {
		return em.find(Users.class, id);
	}
	
	public void save(Users user) {
		em.persist(user);
	}
}
