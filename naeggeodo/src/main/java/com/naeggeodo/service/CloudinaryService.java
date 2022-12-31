package com.naeggeodo.service;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.naeggeodo.config.CloudinaryConfig;
import com.naeggeodo.exception.CustomHttpException;
import com.naeggeodo.exception.ErrorCode;
import com.naeggeodo.repository.ChatMainRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

    private final CloudinaryConfig cloudinaryConfig;
    private final ChatMainRepository chatMainRepository;

    @Async
    @Transactional
    public void upload(MultipartFile file, String folder, Long chatMain_id) {

        File fileToUpload = convertMultiPartFileToFile(file);
        Map<?, ?> uploadResult;
        Cloudinary cloudinary = cloudinaryConfig.generateCloudinary();
        try {
            uploadResult = cloudinary.uploader().upload(fileToUpload,
                    ObjectUtils.asMap(
                            "folder", folder,
                            "unique_filename", true,
                            "use_filename", true
                    )

            );
        } catch (Exception e) {
            log.info("Upload Exception = {}", e.getClass());
            e.printStackTrace();
            throw new CustomHttpException(ErrorCode.UPLOAD_FAIL);
        } finally {
            if (fileToUpload.exists()) fileToUpload.delete();
        }
        chatMainRepository.updateForImgPath(uploadResult.get("url").toString().replaceFirst("http://", "https://"), chatMain_id);
    }


    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return convertedFile;
    }


}
