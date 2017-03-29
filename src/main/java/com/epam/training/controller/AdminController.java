package com.epam.training.controller;

import com.epam.training.dao.UserDAO;
import com.epam.training.dto.FileDTO;
import com.epam.training.entity.Role;
import com.epam.training.entity.User;
import com.epam.training.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private FileService fileService;
    @Autowired
    private UserDAO userDAO;

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity deleteFile(@RequestParam(required = true) String path) {
        File file = new File(path);
        if (file.exists()) {
            if (isRootDirectory(file)) return new ResponseEntity(HttpStatus.BAD_REQUEST);
            if (file.delete()) return new ResponseEntity(HttpStatus.OK);
            else return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        } else return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/addDirectory", method = RequestMethod.POST)
    public ResponseEntity addDirectory(@RequestBody String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            if (file.mkdir()) {
                FileDTO fileDTO = FileDTO.createDTO(file);
                return new ResponseEntity(fileDTO, HttpStatus.CREATED);
            } else return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        } else return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/addFile", method = RequestMethod.POST)
    public String addFile(HttpServletRequest request,
                          @RequestParam String path,
                          @RequestParam MultipartFile file) throws IOException {
        fileService.saveFile(file, path, file.getOriginalFilename());
        return "redirect:/training/main";
    }

    @RequestMapping("/users")
    @ResponseBody
    public ResponseEntity getUsers() {
        return new ResponseEntity(userDAO.getUsers(), HttpStatus.OK);
    }

    @RequestMapping(value = "/addAdminPermissions", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity addAdminPermissions(@RequestBody User user) {
        User userPersistent = userDAO.getUser(user.getUsername());
        if (userPersistent != null) {
            userPersistent.addRole(Role.ADMIN);
            userDAO.update(userPersistent);
            return new ResponseEntity(HttpStatus.OK);
        } else return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/revokeAdminPermissions", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity revokeAdminPermissions(@RequestBody User user) {
        User userPersistent = userDAO.getUser(user.getUsername());
        if (userPersistent != null) {
            userPersistent.getRoles().remove(Role.ADMIN);
            userDAO.update(userPersistent);
            return new ResponseEntity(HttpStatus.OK);
        } else return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/deleteUser", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity deleteUser(@RequestParam String username) {
        User userPersistent = userDAO.getUser(username);
        if (userPersistent != null) {
            userDAO.delete(userPersistent);
            return new ResponseEntity(HttpStatus.OK);
        } else return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    private boolean isRootDirectory(File file) {
        File[] rootDirs = File.listRoots();
        for (File rootDir : rootDirs) {
            if (file.equals(rootDir)) return true;
        }
        return false;
    }
}
