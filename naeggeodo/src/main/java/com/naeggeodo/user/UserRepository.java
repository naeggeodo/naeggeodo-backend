package com.naeggeodo.user;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.naeggeodo.entity.user.Users;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepository{
	
	private final EntityManager em;
	
	public void save(Users user) {
		em.persist(user);
	}
	
	public Users findOne(String id) {
		return em.find(Users.class, id);
	}
	
	public List<Users> findAll(){
		return em.createQuery("select m from Users m", Users.class).
				getResultList();
	}
	
	public List<Users> findById(String id){
		return em.createQuery("select u from Users u", Users.class).setParameter("user_id", id).getResultList();
	}

}
