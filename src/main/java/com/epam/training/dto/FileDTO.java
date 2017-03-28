package com.epam.training.dto;

import com.epam.training.util.ByteConverter;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

public class FileDTO {
    private String fileName;
    private String fullPath;
    String contentType;
    private boolean isDirectory;
    private Date creationDate;
    private Date modificationDate;
    private Long fileSize;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public static FileDTO createDTO(File file) throws IOException {
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
