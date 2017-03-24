package com.epam.training.controller;

import com.epam.training.dto.FileDTO;
import com.epam.training.service.FileService;
import com.epam.training.util.ByteConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

@Controller
@RequestMapping("/files")
public class AdminController {
    @Autowired
    FileService fileService;

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity deleteFile(@RequestParam(required = true) String path) {
        File file = new File(path);
        if (file.exists()) {
            if (file.delete()) return new ResponseEntity(HttpStatus.ACCEPTED);
            else return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        } else return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/addDirectory", method = RequestMethod.POST)
    public ResponseEntity addDirectory(@RequestBody String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            if (file.mkdir()){
                FileDTO fileDTO = createDTO(file);
                return new ResponseEntity(fileDTO, HttpStatus.CREATED);
            }
            else return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        } else return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/addFile", method = RequestMethod.POST)
    public ResponseEntity addFile(HttpServletRequest request,
                                  @RequestParam String path,
                                  @RequestParam MultipartFile file) {
        String createdFilename = fileService.saveFile(file, path, file.getOriginalFilename());
        return new ResponseEntity(createdFilename, HttpStatus.CREATED);
    }

    private FileDTO createDTO(File file) throws IOException {
        FileDTO fileDTO = new FileDTO();
        BasicFileAttributes fileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        fileDTO.setFullPath(file.getPath());
        fileDTO.setFileName(file.getName());
        fileDTO.setDirectory(file.isDirectory());
        fileDTO.setFileSize(ByteConverter.toKilobyte(file.length()));
        fileDTO.setCreationDate(new Date(fileAttributes.creationTime().toMillis()));
        fileDTO.setModificationDate(new Date(fileAttributes.lastModifiedTime().toMillis()));
        return fileDTO;
    }
}
