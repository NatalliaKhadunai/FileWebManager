package com.epam.training.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    String saveImage(MultipartFile imageSourceFile, String destinationDirectory, String newFilename);
}
