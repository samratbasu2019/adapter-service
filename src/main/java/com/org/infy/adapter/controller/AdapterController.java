package com.org.infy.adapter.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.org.infy.adapter.service.FileStorageService;
import com.org.infy.adapter.util.ResponseHelper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class AdapterController {
	protected final Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	private FileStorageService fileStorageService;

	@PostMapping("/adapter/upload")
	public List<?> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
		return Arrays.asList(files).stream().map(file -> {
			long starttime = System.currentTimeMillis();

			final String dir = System.getProperty("user.dir");
			logger.info("File stored in : "+dir);
			String fileName = fileStorageService.storeFile(file);

			long endtime = System.currentTimeMillis();
			logger.info("Total processing time " + (endtime - starttime) + " ms.");
			return new ResponseEntity<>(ResponseHelper.populateRresponse("File name is : "+fileName,"Success"), HttpStatus.OK);
			
		}).collect(Collectors.toList());
	}
}
