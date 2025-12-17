package io.github.ktpm.bluemoonmanagement.util;

import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility để convert File thành MultipartFile
 */
public class FileMultipartUtil {
    
    /**
     * Convert File thành MultipartFile
     */
    public static MultipartFile convertFileToMultipartFile(File file) throws IOException {
        return new FileMultipartFile(file);
    }
    
    /**
     * Custom implementation của MultipartFile từ File
     */
    public static class FileMultipartFile implements MultipartFile {
        private final File file;
        private final String contentType;
        
        public FileMultipartFile(File file) {
            this.file = file;
            this.contentType = determineContentType(file);
        }
        
        private String determineContentType(File file) {
            String name = file.getName().toLowerCase();
            if (name.endsWith(".xlsx")) {
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            } else if (name.endsWith(".xls")) {
                return "application/vnd.ms-excel";
            }
            return "application/octet-stream";
        }
        
        @Override
        public String getName() {
            return "file";
        }
        
        @Override
        public String getOriginalFilename() {
            return file.getName();
        }
        
        @Override
        public String getContentType() {
            return contentType;
        }
        
        @Override
        public boolean isEmpty() {
            return file.length() == 0;
        }
        
        @Override
        public long getSize() {
            return file.length();
        }
        
        @Override
        public byte[] getBytes() throws IOException {
            return Files.readAllBytes(file.toPath());
        }
        
        @Override
        public InputStream getInputStream() throws IOException {
            return new FileInputStream(file);
        }
        
        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {
            Files.copy(file.toPath(), dest.toPath());
        }
        
        @Override
        public void transferTo(Path dest) throws IOException, IllegalStateException {
            Files.copy(file.toPath(), dest);
        }
    }
} 