package com.epam.training.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String saveFile(MultipartFile sourceFile, String destinationDirectory, String newFilename);
}
