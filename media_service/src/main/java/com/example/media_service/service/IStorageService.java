package com.example.media_service.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;

public interface IStorageService {
    void init();
    String store(MultipartFile file, String specificSubFolder);  
    ResponseEntity<Resource> loadAndServe(String subfolder, String filename, HttpServletRequest request);
}
