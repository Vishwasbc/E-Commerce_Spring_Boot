package com.ecommerce.service.impl;

import com.ecommerce.exceptions.APIException;
import com.ecommerce.service.FileService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Lazy
@Service
public class FileServiceImpl implements FileService {
    @Override
    public String uploadImage(String imagePath, MultipartFile file) throws IOException {
        // Filenames of original / current file
        String originalName = file.getOriginalFilename();
        /*
         * getName(): Returns the name of the parameter in the multipart form. This is
         * the field name used in the form submission, not the actual filename.
         * getOriginalFilename(): Returns the original filename from the client's
         * filesystem. Depending on the browser, it may include the full path (e.g.,
         * Internet Explorer might return C:\Users\YourName\file.txt, while Chrome would
         * return just file.txt
         */
        // Generate unique filename
        String randomId = UUID.randomUUID().toString();
        if (originalName == null) {
            throw new APIException("No Name Found");
        }
        String fileName = randomId.concat(originalName.substring(originalName.lastIndexOf('.')));
        String filePath = imagePath + File.separator + fileName;
        // check if path exists or create
        File folder = new File(imagePath);
        if (!folder.exists()) {
            folder.mkdir();
        }
        // upload to server
        Files.copy(file.getInputStream(), Paths.get(filePath));
        // Return filename
        return fileName;
    }
}
