package com.ecommerce.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

	String uploadImage(String imagePath, MultipartFile file) throws IOException;

}
