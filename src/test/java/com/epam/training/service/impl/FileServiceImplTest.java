package com.epam.training.service.impl;

import com.epam.training.service.FileService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = FileServiceImplTest.SpringTestConfiguration.class)
public class FileServiceImplTest {
    @Autowired
    private FileService fileService;

    private File tempFile;
    private File tempDir;

    @Configuration
    public static class SpringTestConfiguration {
        @Bean
        public FileService fileService() {
            return new FileServiceImpl();
        }
    }

    @Before
    public void setup() throws IOException {
        tempDir = new File("tempDir");
        if (!tempDir.exists()) tempDir.mkdir();
        tempFile = new File("tempDir\\testFile.txt");
        if (!tempFile.exists()) tempFile.createNewFile();
    }

    @After
    public void cleanUp() {
        if (tempFile.exists()) tempFile.delete();
        if (tempDir.exists()) tempDir.delete();
    }


    @Test
    public void testSaveFile() throws IOException {
        tempFile.delete();
        MockMultipartFile newMultipartFile = new MockMultipartFile("file", tempFile.getName(),
                "text/plain", "some text".getBytes());

        String filename = fileService.saveFile(newMultipartFile, tempDir.getAbsolutePath(),
                newMultipartFile.getOriginalFilename());
        assertNotNull(filename);
    }

    @Test(expected = IOException.class)
    public void testSaveFile_FileExists() throws IOException {
        MockMultipartFile newMultipartFile = new MockMultipartFile("file", tempFile.getName(),
                "text/plain", "some text".getBytes());

        String filename = fileService.saveFile(newMultipartFile, tempDir.getAbsolutePath(),
                newMultipartFile.getOriginalFilename());
        assertNotNull(filename);
    }

    @Test
    public void testWriteFile() throws IOException {
        File file = new File("new-temp-file.txt");
        OutputStream outputStream = new FileOutputStream(file);
        String str = "ABRAKADABRASTRING";
        outputStream.write(str.getBytes());
        outputStream.close();

        fileService.writeFile(file, new FileOutputStream(tempFile));
        assertTrue(tempFile.length() > 0);

        file.delete();
    }
}
