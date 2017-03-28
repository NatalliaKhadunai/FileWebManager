package com.epam.training.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public interface FileService {
    String saveFile(MultipartFile sourceFile, String destinationDirectory, String newFilename);
    void writeFile(File file, OutputStream outputStream) throws IOException;
}
