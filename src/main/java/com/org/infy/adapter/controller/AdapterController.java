package com.org.infy.adapter.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.org.infy.adapter.model.ICountStore;
import com.org.infy.adapter.service.FileStorageService;
import com.org.infy.adapter.util.ResponseHelper;
import com.org.infy.adapter.util.Utility;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class AdapterController {
	protected final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private FileStorageService fileStorageService;
	ICountStore iCountStore = null;
	boolean status = false;
	int index = 0;

	@PostMapping("/adapter/appreciation/upload")
	public ResponseEntity<?> uploadAppreciation(@RequestParam(value = "files", required = false) MultipartFile[] files,
			String icountStore) {

		long starttime = System.currentTimeMillis();
		iCountStore = Utility.payloadToObject(icountStore);

		if (iCountStore.getAppreciation() != null) {
			iCountStore.getAppreciation().parallelStream().forEach(action -> {
				logger.info("Employee id value :" + iCountStore.getEmployeeId());
				//final String dir = System.getProperty("user.dir");
				//logger.info("File stored in : " + dir);
				status = fileStorageService.storeiCountAppreciation(files, iCountStore, index);
				long endtime = System.currentTimeMillis();
				logger.info("Total processing time " + (endtime - starttime) + " ms.");
				index++;
			});
		} else {
			index = 0;
			return new ResponseEntity<>(ResponseHelper.populateRresponse("Bad request", "failed"), HttpStatus.BAD_REQUEST);
		}
		index = 0;
		return new ResponseEntity<>(ResponseHelper.populateRresponse("Data saved sucessfully", "Success"),
				HttpStatus.OK);

	}

	@PostMapping("/adapter/course/upload")
	public ResponseEntity<?> uploadCourse(@RequestParam(value = "files", required = false) MultipartFile[] files,
			String icountStore) {
		long starttime = System.currentTimeMillis();
		iCountStore = Utility.payloadToObject(icountStore);
		

		if (iCountStore.getCourse() != null) {
			iCountStore.getCourse().parallelStream().forEach(action -> {
				logger.info("Employee id value :" + iCountStore.getEmployeeId());
				//final String dir = System.getProperty("user.dir");
				//logger.info("File stored in : " + dir);
				status = fileStorageService.storeiCountCourse(files, iCountStore, index);
				long endtime = System.currentTimeMillis();
				logger.info("Total processing time " + (endtime - starttime) + " ms.");
				index = index + 1;
			});
		} else {
			index = 0;
			return new ResponseEntity<>(ResponseHelper.populateRresponse("Bad request", "failed"), HttpStatus.BAD_REQUEST);
		}
		index = 0;
		return new ResponseEntity<>(ResponseHelper.populateRresponse("Data saved sucessfully", "Success"),
				HttpStatus.OK);
	}

	@PostMapping("/adapter/feedback/upload")
	public ResponseEntity<?> uploadFeedback(@RequestParam(value = "files", required = false) MultipartFile[] files,
			String icountStore) {
		long starttime = System.currentTimeMillis();
		iCountStore = Utility.payloadToObject(icountStore);
		

		if (iCountStore.getFeedback() != null) {
			iCountStore.getFeedback().parallelStream().forEach(action -> {
				logger.info("Employee id value :" + iCountStore.getEmployeeId());
				//final String dir = System.getProperty("user.dir");
				//logger.info("File stored in : " + dir);
				status = fileStorageService.storeiCountFeedback(files, iCountStore, index);
				long endtime = System.currentTimeMillis();
				logger.info("Total processing time " + (endtime - starttime) + " ms.");
				index = index + 1;
			});
		} else {
			index = 0;
			return new ResponseEntity<>(ResponseHelper.populateRresponse("Bad request", "failed"), HttpStatus.BAD_REQUEST);
		}
		index = 0;
		return new ResponseEntity<>(ResponseHelper.populateRresponse("Data saved sucessfully", "Success"),
				HttpStatus.OK);
	}

	@PostMapping("/adapter/task/upload")
	public ResponseEntity<?> uploadTask(@RequestParam(value = "files", required = false) MultipartFile[] files,
			String icountStore) {
		long starttime = System.currentTimeMillis();
		iCountStore = Utility.payloadToObject(icountStore);
		

		if (iCountStore.getTask() != null) {
			iCountStore.getTask().parallelStream().forEach(action -> {
				logger.info("Employee id value :" + iCountStore.getEmployeeId());
				logger.info("Task Status value is :" + iCountStore.getTask().get(0).getTaskStatus());
				//final String dir = System.getProperty("user.dir");
				//logger.info("File stored in : " + dir);
				status = fileStorageService.storeiCountTask(files, iCountStore, index);
				long endtime = System.currentTimeMillis();
				logger.info("Total processing time " + (endtime - starttime) + " ms.");
				index = index + 1;
			});
		} else {
			index = 0;
			return new ResponseEntity<>(ResponseHelper.populateRresponse("Bad request", "failed"), HttpStatus.BAD_REQUEST);
		}
		index = 0;
		return new ResponseEntity<>(ResponseHelper.populateRresponse("Data saved sucessfully", "Success"),
				HttpStatus.OK);
	}
	
	@GetMapping("/adapter/fetchTasks")
	public ResponseEntity<?> getTask(@RequestParam String email) {
		long starttime = System.currentTimeMillis();

		List<ICountStore> iStoreTaskList = fileStorageService.findByTasks(email);
		
		long endtime = System.currentTimeMillis();
		logger.info("Total processing time " + (endtime - starttime) + " ms.");
				
		return new ResponseEntity<>(iStoreTaskList,	HttpStatus.OK);
	}
	
}
