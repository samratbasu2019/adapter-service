package com.org.infy.adapter.service;

import com.org.infy.adapter.*;
import com.org.infy.adapter.exception.FileStorageException;
import com.org.infy.adapter.property.FileStorageProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {
	protected final Log logger = LogFactory.getLog(this.getClass());
    private final Path fileStorageLocation;
    private static Path targetLocation;
    
    public Path getTargetPlSqlLocation () {    	
    	return this.targetLocation;
    }
    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties) {
    	final String dir = System.getProperty("user.dir");
        this.fileStorageLocation = Paths.get(dir+fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();
        logger.info("Upload directory path : " + fileStorageLocation.toString());
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            targetLocation = this.fileStorageLocation.resolve(fileName);
            logger.info("File stored location "+targetLocation);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

}
