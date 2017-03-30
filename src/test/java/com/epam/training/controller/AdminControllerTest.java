package com.epam.training.controller;

import com.epam.training.config.GlobalExceptionHandler;
import com.epam.training.dao.UserDAO;
import com.epam.training.entity.Role;
import com.epam.training.entity.User;
import com.epam.training.service.FileService;
import com.epam.training.util.ByteConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FilenameUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AdminControllerTest.SpringTestConfiguration.class)
public class AdminControllerTest {
    @Autowired
    private AdminController adminController;
    @Autowired
    private FileService fileService;
    @Autowired
    private UserDAO userDAO;

    private MockMvc mockMvc;
    private File tempFile;
    private File tempDir;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Configuration
    public static class SpringTestConfiguration {
        @Bean
        public AdminController authenticationController() {
            return new AdminController();
        }

        @Bean
        public UserDAO userDAO() {
            return mock(UserDAO.class);
        }

        @Bean
        public FileService fileService() {
            return mock(FileService.class);
        }
    }

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(adminController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        tempDir = new File("tempDir");
        if (!tempDir.exists()) tempDir.mkdir();
        tempFile = new File("tempDir\\testFile.txt");
        if (!tempFile.exists()) tempFile.createNewFile();

        reset(fileService, userDAO);
    }

    @After
    public void cleanUp() {
        if (tempFile.exists()) tempFile.delete();
        if (tempDir.exists()) tempDir.delete();
    }

    @Test
    public void testAddDirectory_DirectoryExists() throws Exception {
        String expectedResult = "{\"httpStatusCode\":\"400\",\"developerMessage\":\"Such file already exists\",\"url\":\"/admin/addDirectory\",\"method\":\"POST\",\"moreInfo\":null}";

        MvcResult mvcResult = mockMvc.perform(post("/admin/addDirectory")
                .content(tempDir.getAbsolutePath()))
                .andExpect(status().isBadRequest()).andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains(expectedResult));
    }

    @Test
    public void testAddDirectory_CreatedAlongWithRootDirectories() throws Exception {
        String expectedResult = "{\"httpStatusCode\":\"400\",\"developerMessage\":\"Directory cannot be created along with root directories\",\"url\":\"/admin/addDirectory\",\"method\":\"POST\",\"moreInfo\":null}";

        MvcResult mvcResult = mockMvc.perform(post("/admin/addDirectory")
                .content("\\\\"))
                .andExpect(status().isBadRequest()).andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains(expectedResult));
    }

    @Test
    public void testAddDirectory() throws Exception {
        String tempDirPath = tempDir.getAbsolutePath();
        String extension = FilenameUtils.getExtension(tempDir.getAbsolutePath());
        tempFile.delete();
        tempDir.delete();

        String expectedResultPart1 = "\"fileName\":\"" + tempDir.getName()
                + "\",\"fullPath\":\"" + tempDir.getAbsolutePath().replaceAll("\\\\+", "\\\\\\\\") + "\""
                + ",\"contentType\":" + (extension.equals("") ? "null" : extension);
        String expectedResultPart2 = "\"creationDate\"";
        String expectedResultPart3 = "\"modificationDate\"";
        String expectedResultPart4 = "\"fileSize\":" + ByteConverter.toKilobyte(tempDir.length())
                + ",\"directory\":true";

        MvcResult mvcResult = mockMvc.perform(post("/admin/addDirectory")
                .content(tempDirPath))
                .andExpect(status().isOk()).andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains(expectedResultPart1));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(expectedResultPart2));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(expectedResultPart3));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(expectedResultPart4));
    }

    @Test
    public void testAddFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", tempFile.getName(),
                "text/plain", "some text".getBytes());

        MvcResult mvcResult = mockMvc.perform(fileUpload("/admin/addFile")
                .file(file).param("path", "path"))
                .andExpect(status().is3xxRedirection()).andReturn();
        assertEquals(mvcResult.getModelAndView().getViewName(), "redirect:/training/main");
        verify(fileService, times(1)).saveFile(file, "path", file.getOriginalFilename());
    }

    @Test
    public void testDeleteFile() throws Exception {
       mockMvc.perform(delete("/admin/delete")
                .param("path", tempFile.getAbsolutePath()))
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    public void testDeleteFile_DeleteRootDirectory() throws Exception {
        String rootDirectoryPath = File.listRoots()[0].getAbsolutePath();
        String expectedResult = "{\"httpStatusCode\":\"400\",\"developerMessage\":\"Root directory cannot be deleted\",\"url\":\"/admin/delete\",\"method\":\"DELETE\",\"moreInfo\":null}";

        MvcResult mvcResult = mockMvc.perform(delete("/admin/delete")
                .param("path", rootDirectoryPath))
                .andExpect(status().isBadRequest()).andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains(expectedResult));
    }

    @Test
    public void testDeleteFile_DeleteFileThatNotExists() throws Exception {
        tempFile.delete();
        String expectedResult = "{\"httpStatusCode\":\"404\",\"developerMessage\":\"File wasn't found\",\"url\":\"/admin/delete\",\"method\":\"DELETE\",\"moreInfo\":null}";

        MvcResult mvcResult = mockMvc.perform(delete("/admin/delete")
                .param("path", tempFile.getAbsolutePath()))
                .andExpect(status().isNotFound()).andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains(expectedResult));
    }

    @Test
    public void testGetUsers() throws Exception {
        List<User> userList = new ArrayList<>();
        userList.add(new User());

        when(userDAO.getUsers()).thenReturn(userList);

        String expectedResult = "[{\"id\":null,\"username\":null,\"passwordHash\":null,\"roles\":[]}]";
        MvcResult mvcResult = mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk()).andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains(expectedResult));
        verify(userDAO, times(1)).getUsers();
    }

    @Test
    @Ignore
    public void testAddAdminPermissions() throws Exception {
        User user = new User();
        user.setId(1);
        user.setUsername("usr");
        user.setPasswordHash("123");
        List<Role> roles = new ArrayList<>();
        roles.add(Role.GUEST);
        user.setRoles(roles);
        when(userDAO.getUser(anyString())).thenReturn(user);

        String expectedResult = "";

        MvcResult mvcResult = mockMvc.perform(post("/admin/addAdminPermissions")
        .content(toJSON(user)))
                .andExpect(status().isOk()).andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains(expectedResult));
        verify(userDAO, times(1)).getUser("usr");
        verify(userDAO, times(1)).update(user);
    }

    @Test
    @WithMockUser(username = "admin")
    @Ignore
    public void testRevokeAdminPermissions() throws Exception {
        User user = new User();
        user.setId(1);
        user.setUsername("usr");
        user.setPasswordHash("123");
        List<Role> roles = new ArrayList<>();
        roles.add(Role.ADMIN);
        user.setRoles(roles);
        when(userDAO.getUser(anyString())).thenReturn(user);

        String expectedResult = "";

        MvcResult mvcResult = mockMvc.perform(post("/admin/revokeAdminPermissions")
                .content(toJSON(user)))
                .andExpect(status().isOk()).andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains(expectedResult));
        verify(userDAO, times(1)).getUser("usr");
        verify(userDAO, times(1)).update(user);
    }

    @Test
    public void testDeleteUser() throws Exception {
        User user = new User();
        user.setId(1);
        user.setUsername("usr");
        when(userDAO.getUser(anyString())).thenReturn(user);

        mockMvc.perform(delete("/admin/deleteUser")
                .param("username", "usr"))
                .andExpect(status().isOk()).andReturn();
        verify(userDAO, times(1)).getUser("usr");
        verify(userDAO, times(1)).delete(user);
    }

    @Test
    public void testDeleteUser_UserNotFound() throws Exception {
        when(userDAO.getUser(anyString())).thenReturn(null);

        String expectedResult = "{\"httpStatusCode\":\"404\",\"developerMessage\":\"User doesn't exist\",\"url\":\"/admin/deleteUser\",\"method\":\"DELETE\",\"moreInfo\":null}";

        MvcResult mvcResult = mockMvc.perform(delete("/admin/deleteUser")
                .param("username", "usr"))
                .andExpect(status().isNotFound()).andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains(expectedResult));
        verify(userDAO, times(1)).getUser("usr");
    }

    private String toJSON(Object object) throws Exception {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }
}