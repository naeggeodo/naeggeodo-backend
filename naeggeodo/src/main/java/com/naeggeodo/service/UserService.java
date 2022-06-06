package com.naeggeodo.service;

import com.naeggeodo.exception.CustomHttpException;
import com.naeggeodo.exception.ErrorCode;
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
		Users user = userRepository.findById(id).orElseThrow(()-> new CustomHttpException(ErrorCode.RESOURCE_NOT_FOUND));
		user.setImgpath(imgpath);
	}
	
}
