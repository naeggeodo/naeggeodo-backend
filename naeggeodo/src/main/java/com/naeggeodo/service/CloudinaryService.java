package com.naeggeodo.service;



import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import com.naeggeodo.exception.CustomHttpException;
import com.naeggeodo.exception.ErrorCode;
import com.naeggeodo.repository.ChatMainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.naeggeodo.config.CloudinaryConfig;

@Service
@RequiredArgsConstructor
public class CloudinaryService {
	
	private final CloudinaryConfig cloudinaryConfig;
	private final ChatMainRepository chatMainRepository;
	@Async
	@Transactional
	public String upload(MultipartFile file,String folder,Long chatMain_id) {
		
		File fileToUpload = convertMultiPartFileToFile(file);
		Map uploadResult = null;
		Cloudinary cloudinary = cloudinaryConfig.generateCloudinary();
		try {
			uploadResult = cloudinary.uploader().upload(fileToUpload, 
					ObjectUtils.asMap(
							"folder",folder,
 							"unique_filename",true,
							"use_filename",true
							)
					
					);
		} catch (Exception e) {
			throw new CustomHttpException(ErrorCode.UPLOAD_FAIL);
		}
		//ChatMain findChatMain = chatMainRepository.getById(chatMain_id);
		//findChatMain.updateImgPath(uploadResult.get("url").toString());
		chatMainRepository.updateForImgPath(uploadResult.get("url").toString(),chatMain_id);
		System.out.println(uploadResult.get("url").toString());
		return uploadResult.get("url").toString();
	}
	
	
	private File convertMultiPartFileToFile(MultipartFile file) {
		File convertedFile = new File(file.getOriginalFilename());
		try(FileOutputStream fos = new FileOutputStream(convertedFile)){
			fos.write(file.getBytes());
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		return convertedFile;
	}
	
	
}
