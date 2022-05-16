package com.naeggeodo.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.naeggeodo.entity.user.Users;

public interface UserRepository extends JpaRepository<Users, String>{
}
