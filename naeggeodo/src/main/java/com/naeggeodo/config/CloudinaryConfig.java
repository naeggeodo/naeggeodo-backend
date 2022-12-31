package com.naeggeodo.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class CloudinaryConfig {
	@Value("${cloud.cloudinary.cloud_name}")
	private String cloud_name;
	@Value("${cloud.cloudinary.api_key}")
	private String api_key;
	@Value("${cloud.cloudinary.api_secret}")
	private String api_secret;
	
	@Bean
	public Cloudinary generateCloudinary() {
		return new Cloudinary(ObjectUtils.asMap
				(
					"cloud_name",cloud_name,
					"api_key",api_key,
					"api_secret",api_secret)
				);
	}
}
