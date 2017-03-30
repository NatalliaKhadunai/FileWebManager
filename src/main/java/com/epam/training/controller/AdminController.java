package com.epam.training.controller;

import com.epam.training.dao.UserDAO;
import com.epam.training.dto.FileDTO;
import com.epam.training.entity.Role;
import com.epam.training.entity.User;
import com.epam.training.exception.BadRequest;
import com.epam.training.exception.FileDuplicateException;
import com.epam.training.exception.UserNotFoundException;
import com.epam.training.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private FileService fileService;
    @Autowired
    private UserDAO userDAO;

    @RequestMapping(value = "/addDirectory", method = RequestMethod.POST)
    @ResponseBody
    public FileDTO addDirectory(@RequestBody String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            if (path == null || path.replaceAll("\\\\*", "").isEmpty())
                throw new BadRequest("Directory cannot be created along with root directories");
            file.mkdir();
            FileDTO fileDTO = FileDTO.createDTO(file);
            return fileDTO;
        }
        else throw new FileDuplicateException("Such file already exists");
    }

    @RequestMapping(value = "/addFile", method = RequestMethod.POST)
    public String addFile(HttpServletRequest request,
                          @RequestParam String path,
                          @RequestParam MultipartFile file) throws Exception {
        fileService.saveFile(file, path, file.getOriginalFilename());
        return "redirect:/training/main";
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public void deleteFile(@RequestParam(required = true) String path) throws FileNotFoundException {
        File file = new File(path);
        if (file.exists()) {
            if (isRootDirectory(file)) throw new BadRequest("Root directory cannot be deleted");
            else file.delete();
        }
        else throw new FileNotFoundException("File wasn't found");
    }

    @RequestMapping("/users")
    @ResponseBody
    public List<User> getUsers() {
        return userDAO.getUsers();
    }

    @RequestMapping(value = "/addAdminPermissions", method = RequestMethod.POST)
    @ResponseBody
    public void addAdminPermissions(@RequestBody User user) {
        User userPersistent = userDAO.getUser(user.getUsername());
        if (userPersistent != null) {
            if (userPersistent.getRoles().contains(Role.ADMIN))
                throw new BadRequest("User already has ADMIN permissions");
            else {
                userPersistent.addRole(Role.ADMIN);
                userDAO.update(userPersistent);
            }
        }
        else throw new UserNotFoundException("User doesn't exist");
    }

    @RequestMapping(value = "/revokeAdminPermissions", method = RequestMethod.POST)
    @ResponseBody
    public void revokeAdminPermissions(@RequestBody User user, Principal principal) {
        User userPersistent = userDAO.getUser(user.getUsername());
        if (userPersistent != null) {
            if (!userPersistent.getRoles().contains(Role.ADMIN)
                    || userPersistent.getUsername().equals(principal.getName()))
                throw new BadRequest("User doesn't have ADMIN permissions OR permissions are revoked from logged user by logged user");
            userPersistent.getRoles().remove(Role.ADMIN);
            userDAO.update(userPersistent);
        }
        else throw new UserNotFoundException("User doesn't exist");
    }

    @RequestMapping(value = "/deleteUser", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteUser(@RequestParam String username) {
        User userPersistent = userDAO.getUser(username);
        if (userPersistent != null) {
            userDAO.delete(userPersistent);
        }
        else throw new UserNotFoundException("User doesn't exist");
    }

    private boolean isRootDirectory(File file) {
        File[] rootDirs = File.listRoots();
        for (File rootDir : rootDirs) {
            if (file.equals(rootDir)) return true;
        }
        return false;
    }
}
