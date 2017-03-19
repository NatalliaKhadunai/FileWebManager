package com.epam.training;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class CommonController {
    private static final int BUFFER_SIZE = 4096;

    @RequestMapping("/")
    public String getIndexPage() {
        return "/static/index.html";
    }

    @RequestMapping("/files")
    @ResponseBody
    public List<FileDTO> getFile(@RequestParam(required = false) String path) throws IOException {
        List<FileDTO> fileList = new ArrayList<FileDTO>();
        if (path == null) {
            for (File file : File.listRoots()) {
                FileDTO fileDTO = new FileDTO();
                fileDTO.setFullPath(file.getPath());
                fileDTO.setFileName(file.getPath());
                fileDTO.setDirectory(file.isDirectory());
                fileList.add(fileDTO);
            }
            return fileList;
        }
        else {
            File chosenFile = new File(path);
            if (chosenFile.isDirectory()) {
                for (File file : chosenFile.listFiles()) {
                    FileDTO fileDTO = new FileDTO();
                    fileDTO.setFullPath(file.getPath());
                    fileDTO.setFileName(file.getName());
                    fileDTO.setDirectory(file.isDirectory());
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

        // get MIME type of the file
        String mimeType = context.getMimeType(path);
        if (mimeType == null) {
            // set to binary type if MIME mapping not found
            mimeType = "application/octet-stream";
        }
        System.out.println("MIME type: " + mimeType);

        // set content attributes for the response
        response.setContentType(mimeType);
        response.setContentLength((int) downloadFile.length());

        // set headers for the response
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"",
                downloadFile.getName());
        response.setHeader(headerKey, headerValue);

        // get output stream of the response
        OutputStream outStream = response.getOutputStream();

        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = -1;

        // write bytes read from the input stream into the output stream
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }

        inputStream.close();
        outStream.close();
    }

}
