package com.epam.training.service.impl;

import com.epam.training.service.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Service
public class FileServiceImpl implements FileService {

    @Override
    public String saveFile(MultipartFile sourceFile, String destinationDirectory, String newFilename) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = sourceFile.getInputStream();
            File newFile = new File(destinationDirectory + "\\" + newFilename);
            if (!newFile.exists()) {
                newFile.createNewFile();
            }
            outputStream = new FileOutputStream(newFile);
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            inputStream.close();
            outputStream.close();
            return newFilename;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
