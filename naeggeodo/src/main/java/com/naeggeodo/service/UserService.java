package com.naeggeodo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.naeggeodo.entity.user.Users;
import com.naeggeodo.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Transactional
	public void updateProfile(String id,String imgpath) {
		Users user = userRepository.findOne(id);
		user.setImgpath(imgpath);
	}
	
}
