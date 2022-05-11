package com.naeggeodo.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.naeggeodo.entity.user.Users;

public interface UserRepository extends JpaRepository<Users, String>{
}
