package com.epam.training.controller;

import com.epam.training.dto.FileDTO;
import com.epam.training.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CommonController {
    @Autowired
    private FileService fileService;

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
                FileDTO fileDTO = FileDTO.createDTO(file);
                fileDTO.setFileName(file.getPath());
                fileList.add(fileDTO);
            }
            return fileList;
        }
        else {
            File chosenFile = new File(path);
            if (chosenFile.isDirectory()) {
                for (File file : chosenFile.listFiles()) {
                    FileDTO fileDTO = FileDTO.createDTO(file);
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
        fileService.writeFile(downloadFile, response.getOutputStream());
    }
}
