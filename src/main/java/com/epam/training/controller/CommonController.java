package com.epam.training.controller;

import com.epam.training.dto.FileDTO;
import com.epam.training.util.ByteConverter;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class CommonController {
    private static final int BUFFER_SIZE = 4096;

    @RequestMapping("/main")
    public String getIndexPage(Principal principal) {
        return "index.jsp";
    }

    @RequestMapping("/files")
    @ResponseBody
    public List<FileDTO> getFile(@RequestParam(required = false) String path) throws IOException {
        List<FileDTO> fileList = new ArrayList<FileDTO>();
        if (path == null) {
            for (File file : File.listRoots()) {
                FileDTO fileDTO = createDTO(file);
                fileDTO.setFileName(file.getPath());
                fileList.add(fileDTO);
            }
            return fileList;
        }
        else {
            File chosenFile = new File(path);
            if (chosenFile.isDirectory()) {
                for (File file : chosenFile.listFiles()) {
                    FileDTO fileDTO = createDTO(file);
                    fileList.add(fileDTO);
                }
            }
        }
        return fileList;
    }

    @RequestMapping("/downloadFile")
    public void downloadChosenFile(HttpServletRequest request,
                                   HttpServletResponse response,
                                   @RequestParam String path) throws IOException {
        ServletContext context = request.getServletContext();
        File downloadFile = new File(path);
        FileInputStream inputStream = new FileInputStream(downloadFile);

        String mimeType = context.getMimeType(path);
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }

        response.setContentType(mimeType);
        response.setContentLength((int) downloadFile.length());

        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"",
                downloadFile.getName());
        response.setHeader(headerKey, headerValue);

        OutputStream outStream = response.getOutputStream();

        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = -1;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }

        inputStream.close();
        outStream.close();
    }

    //TODO: move method, make it public for AdminController and CommonController to use it
    private FileDTO createDTO(File file) throws IOException {
        FileDTO fileDTO = new FileDTO();
        BasicFileAttributes fileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        fileDTO.setFullPath(file.getPath());
        fileDTO.setFileName(file.getName());
        if (file.isFile()) fileDTO.setContentType(FilenameUtils.getExtension(file.getAbsolutePath()));
        fileDTO.setDirectory(file.isDirectory());
        fileDTO.setFileSize(ByteConverter.toKilobyte(file.length()));
        fileDTO.setCreationDate(new Date(fileAttributes.creationTime().toMillis()));
        fileDTO.setModificationDate(new Date(fileAttributes.lastModifiedTime().toMillis()));
        return fileDTO;
    }
}
