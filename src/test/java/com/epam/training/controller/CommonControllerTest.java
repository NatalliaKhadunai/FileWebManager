package com.epam.training.controller;

import com.epam.training.config.GlobalExceptionHandler;
import com.epam.training.entity.Role;
import com.epam.training.service.FileService;
import com.epam.training.util.ByteConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FilenameUtils;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CommonControllerTest.SpringTestConfiguration.class)
public class CommonControllerTest {
    @Autowired
    private CommonController commonController = new CommonController();
    @Autowired
    private FileService fileService;

    private MockMvc mockMvc;
    private File tempFile;
    private File tempDir;
    private BasicFileAttributes tempFileAttributes;
    private BasicFileAttributes tempDirAttributes;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Configuration
    public static class SpringTestConfiguration {
        @Bean
        public CommonController commonController() {
            return new CommonController();
        }
        @Bean
        public FileService fileService() {
            return mock(FileService.class);
        }
    }

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(commonController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        tempDir = new File("tempDir");
        if (!tempDir.exists()) tempDir.mkdir();
        tempFile = new File("tempDir\\testFile.txt");
        if (!tempFile.exists()) tempFile.createNewFile();
        tempFileAttributes = Files.readAttributes(tempFile.toPath(), BasicFileAttributes.class);
        tempDirAttributes = Files.readAttributes(tempDir.toPath(), BasicFileAttributes.class);

        reset(fileService);
    }

    @After
    public void cleanUp() {
        if (tempFile.exists()) tempFile.delete();
        if (tempDir.exists()) tempDir.delete();
    }

    @Test
    public void testGetIndexPage() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/main")).andExpect(status().isOk()).andReturn();
        assertEquals(mvcResult.getModelAndView().getViewName(), "index.jsp");
    }

    @Test
    public void testGetFile_NoPathSpecified() throws Exception {
        File rootFile = File.listRoots()[0];
        BasicFileAttributes rootFileAttrs = Files.readAttributes(rootFile.toPath(), BasicFileAttributes.class);

        String expectedResult = "{\"fileName\":\"" + rootFile.getPath() + "\\\","
                + "\"fullPath\":\"" + rootFile.getPath() + "\\\","
                + "\"contentType\":null,"
                + "\"creationDate\":" + rootFileAttrs.creationTime().toMillis() + ","
                + "\"modificationDate\":" + rootFileAttrs.lastModifiedTime().toMillis() + ","
                + "\"fileSize\":" + ByteConverter.toKilobyte(rootFile.length()) + ","
                + "\"directory\":" + rootFile.isDirectory() + "}";

        MvcResult mvcResult = mockMvc.perform(get("/files")).andExpect(status().isOk()).andReturn();
        String actualResult = mvcResult.getResponse().getContentAsString();
        assertTrue(actualResult.contains(expectedResult));
    }

    @Test
    public void testGetFile_PathSpecified() throws Exception {
        String expectedResult = "{\"fileName\":\"" + tempFile.getName() + "\","
                + "\"fullPath\":\"" + tempFile.getAbsolutePath().replaceAll("\\\\+","\\\\\\\\") + "\","
                + "\"contentType\":\"" + FilenameUtils.getExtension(tempFile.getAbsolutePath()) + "\","
                + "\"creationDate\":" + tempFileAttributes.creationTime().toMillis() + ","
                + "\"modificationDate\":" + tempFileAttributes.lastModifiedTime().toMillis() + ","
                + "\"fileSize\":" + ByteConverter.toKilobyte(tempFile.length()) + ","
                + "\"directory\":" + tempFile.isDirectory() + "}";

        MvcResult mvcResult = mockMvc.perform(get("/files")
                .param("path", tempDir.getAbsolutePath()))
                .andExpect(status().isOk())
                .andReturn();
        String actualResult = mvcResult.getResponse().getContentAsString();
        assertTrue(actualResult.contains(expectedResult));
    }

    @Test
    public void testDownloadChosenFile() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/downloadFile")
                .param("path", tempFile.getAbsolutePath()))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals(mvcResult.getResponse().getContentType(), "text/plain");
        assertEquals(mvcResult.getResponse().getContentAsString().length(), 0);
        verify(fileService, times(1)).writeFile(any(File.class), any(OutputStream.class));
    }

    @Test
    public void testDownloadChosenFile_FileNotFound() throws Exception {
        String expectedResult = "{\"httpStatusCode\":\"404\",\"developerMessage\":\"File for downloading wasn't found\",\"url\":\"/downloadFile\",\"method\":\"GET\",\"moreInfo\":null}";

        MvcResult mvcResult = mockMvc.perform(get("/downloadFile")
                .param("path", tempFile.getAbsolutePath() + "ABRAKADABRA"))
                .andExpect(status().isNotFound())
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains(expectedResult));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles={"ADMIN"})
    public void testGetCurrentUserRole() throws Exception {
        String expectedResult = "{\"authority\":\"ROLE_ADMIN\"}";
        MvcResult mvcResult = mockMvc.perform(get("/role")
                .with(user("user").roles(Role.GUEST.toString())))
                .andExpect(status().isOk())
                .andReturn();
        assertNotNull(mvcResult.getResponse().getContentAsString().contains(expectedResult));
    }

    private String toJSON(Object object) throws Exception {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }
}
