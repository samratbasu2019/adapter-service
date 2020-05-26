package com.org.infy.adapter.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.org.infy.adapter.model.ICountStore;
import com.org.infy.adapter.service.FileStorageService;
import com.org.infy.adapter.util.ResponseHelper;


@RestController
public class AdapterController {
	protected final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private FileStorageService fileStorageService;
	ICountStore iCountStore = null;
	boolean status=false;
	int index = 0;

	@PostMapping("/adapter/upload")
	public ResponseEntity<?> uploadMultipleFiles(@RequestParam(value = "files", required = false) MultipartFile[] files,
			String icountStore) {
		
		long starttime = System.currentTimeMillis();
		
		try {
			iCountStore = new ObjectMapper().readValue(icountStore, ICountStore.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (iCountStore.getAppreciation()!=null) {
			iCountStore.getAppreciation().parallelStream().forEach(action -> {
				logger.info("Employee id value :" + iCountStore.getEmployeeId());
				final String dir = System.getProperty("user.dir");
				logger.info("File stored in : " + dir);
				status= fileStorageService.storeiCountAppreciation(files, iCountStore,index);
				long endtime = System.currentTimeMillis();
				logger.info("Total processing time " + (endtime - starttime) + " ms.");
				index=index+1;
			});
		}

		
		else if (iCountStore.getCourse()!=null) {
			iCountStore.getCourse().parallelStream().forEach(action -> {
				logger.info("Employee id value :" + iCountStore.getEmployeeId());
				final String dir = System.getProperty("user.dir");
				logger.info("File stored in : " + dir);
				status= fileStorageService.storeiCountCourse(files, iCountStore,index);
				long endtime = System.currentTimeMillis();
				logger.info("Total processing time " + (endtime - starttime) + " ms.");
				index=index+1;
			});
		}
		
		else if (iCountStore.getFeedback()!=null) {
			iCountStore.getFeedback().parallelStream().forEach(action -> {
				logger.info("Employee id value :" + iCountStore.getEmployeeId());
				final String dir = System.getProperty("user.dir");
				logger.info("File stored in : " + dir);
				status= fileStorageService.storeiCountFeedback(files, iCountStore,index);
				long endtime = System.currentTimeMillis();
				logger.info("Total processing time " + (endtime - starttime) + " ms.");
				index=index+1;
			});
		}
		
		else if (iCountStore.getTask()!=null) {
			iCountStore.getTask().parallelStream().forEach(action -> {
				logger.info("Employee id value :" + iCountStore.getEmployeeId());
				final String dir = System.getProperty("user.dir");
				logger.info("File stored in : " + dir);
				status= fileStorageService.storeiCountTask(files, iCountStore,index);
				long endtime = System.currentTimeMillis();
				logger.info("Total processing time " + (endtime - starttime) + " ms.");
				index=index+1;
			});
		}
		
		
		index=0;
		return new ResponseEntity<>(ResponseHelper.populateRresponse("Data saved sucessfully", "Success"),
				HttpStatus.OK);

	}
}
