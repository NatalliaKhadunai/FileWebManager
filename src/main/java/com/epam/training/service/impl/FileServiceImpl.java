package com.epam.training.service.impl;

import com.epam.training.exception.FileDuplicateException;
import com.epam.training.service.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Service
public class FileServiceImpl implements FileService {
    private static final int BUFFER_SIZE = 4096;

    @Override
    public String saveFile(MultipartFile sourceFile, String destinationDirectory, String newFilename) throws IOException {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        inputStream = sourceFile.getInputStream();
        File newFile = new File(destinationDirectory + "\\" + newFilename);
        if (!newFile.exists()) {
            newFile.createNewFile();
            outputStream = new FileOutputStream(newFile);
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            inputStream.close();
            outputStream.close();
            return newFilename;
        }
        else throw new FileDuplicateException("Such file already exists");
    }

    @Override
    public void writeFile(File file, OutputStream outputStream) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);

        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = -1;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        inputStream.close();
        outputStream.close();
    }
}
