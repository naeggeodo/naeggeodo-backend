package com.naeggeodo.service;



import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.naeggeodo.config.CloudinaryConfig;

@Service
public class CloudinaryService {
	
	@Autowired
	private CloudinaryConfig cloudinaryConfig;
	
	public String upload(MultipartFile file,String folder) {
		
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
		} catch (IOException e) {
			e.printStackTrace();
		}
		return (String)uploadResult.get("url");
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
