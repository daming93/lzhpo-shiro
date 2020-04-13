package com.lzhpo.admin.service;

import org.springframework.web.multipart.MultipartFile;

import com.lzhpo.admin.entity.Rescource;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * <p> Author：lzhpo </p>
 * <p> Title：</p>
 * <p> Description：</p>
 */
public interface UploadService {

    public String upload(MultipartFile file) throws IOException, NoSuchAlgorithmException;
    
    
    public String upload(MultipartFile file,String from) throws IOException, NoSuchAlgorithmException;

    public Rescource getRescource(String rescourseId);
}
