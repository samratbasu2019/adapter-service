package com.org.infy.adapter.service;

import com.org.infy.adapter.exception.FileStorageException;
import com.org.infy.adapter.model.Appreciation;
import com.org.infy.adapter.model.Course;
import com.org.infy.adapter.model.FeedBack;
import com.org.infy.adapter.model.ICountStore;
import com.org.infy.adapter.model.Task;
import com.org.infy.adapter.property.FileStorageProperties;
import com.org.infy.adapter.repository.ICountRepository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class FileStorageService {
	protected final Log logger = LogFactory.getLog(this.getClass());
	private final Path fileStorageLocation;
	boolean status = true;

	@Autowired
	private ICountRepository icountRepo;



	@Autowired
	public FileStorageService(FileStorageProperties fileStorageProperties) {
		final String dir = System.getProperty("user.dir");
		this.fileStorageLocation = Paths.get(dir + fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();
		logger.info("Upload directory path : " + fileStorageLocation.toString());
		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (Exception ex) {
			throw new FileStorageException("Could not create the directory where the uploaded files will be stored.",
					ex);
		}
	}

	public boolean storeiCountAppreciation(MultipartFile[] files, ICountStore icountDTO, int index) {
		List<Appreciation> appretiationList = new ArrayList<>();
		List<Binary> fileList = new ArrayList<Binary>();
		status = true;
		Appreciation appretiation = new Appreciation();
		appretiation.setAppreciatorName(icountDTO.getAppreciation().get(index).getAppreciatorName());
		appretiation.setAppreciatorEmail(icountDTO.getAppreciation().get(index).getAppreciatorEmail());
		appretiation.setAppreciationSub(icountDTO.getAppreciation().get(index).getAppreciationSub());
		appretiation.setDescription(icountDTO.getAppreciation().get(index).getDescription());
		appretiation.setAppreciationDate(icountDTO.getAppreciation().get(index).getAppreciationDate());

		Arrays.asList(files).stream().forEach(fileItem -> {
			try {
				// fileName = StringUtils.cleanPath(fileItem.getOriginalFilename());
				Binary file = new Binary(BsonBinarySubType.BINARY, fileItem.getBytes());
				fileList.add(file);
			} catch (Exception e) {
				status = false;
				e.printStackTrace();
			}
		});
		appretiation.setFile(fileList);
		appretiationList.add(appretiation);
		icountDTO.setAppreciation(appretiationList);
		icountRepo.save(icountDTO);
		return status;

	}
	
	public boolean storeiCountCourse(MultipartFile[] files, ICountStore icountDTO, int index) {
		List<Course> courseList = new ArrayList<>();
		List<Binary> fileList = new ArrayList<Binary>();
		status = true;
		Course course = new Course();
		course.setCourseName(icountDTO.getCourse().get(index).getCourseName());
		course.setCourseDesc(icountDTO.getCourse().get(index).getCourseDesc());
		course.setCourseCreatorName(icountDTO.getCourse().get(index).getCourseCreatorName());
		course.setCourseCreatorEmail(icountDTO.getCourse().get(index).getCourseCreatorEmail());
		course.setCompletionPercentage(icountDTO.getCourse().get(index).getCompletionPercentage());		
		course.setCourseStartDate(icountDTO.getCourse().get(index).getCourseStartDate());
		course.setCourseEndDate(icountDTO.getCourse().get(index).getCourseEndDate());
		course.setTotalHrsAttended(icountDTO.getCourse().get(index).getTotalHrsAttended());

		Arrays.asList(files).stream().forEach(fileItem -> {
			try {
				// fileName = StringUtils.cleanPath(fileItem.getOriginalFilename());
				Binary file = new Binary(BsonBinarySubType.BINARY, fileItem.getBytes());
				fileList.add(file);
			} catch (Exception e) {
				status = false;
				e.printStackTrace();
			}
		});
		course.setFile(fileList);
		courseList.add(course);
		icountDTO.setCourse(courseList);
		icountRepo.save(icountDTO);
		return status;

	}
	
	public boolean storeiCountFeedback(MultipartFile[] files, ICountStore icountDTO, int index) {
		List<FeedBack> feedbackList = new ArrayList<>();
		List<Binary> fileList = new ArrayList<Binary>();
		status = true;
		FeedBack feedback = new FeedBack();
		feedback.setFeedbackName(icountDTO.getFeedback().get(index).getFeedbackName());
		feedback.setFeedbackDesc(icountDTO.getFeedback().get(index).getFeedbackDesc());
		feedback.setFeedbackerName(icountDTO.getFeedback().get(index).getFeedbackerName());
		feedback.setFeedbackerEmail(icountDTO.getFeedback().get(index).getFeedbackerEmail());
		feedback.setFeedbackDate(icountDTO.getFeedback().get(index).getFeedbackDate());		

		Arrays.asList(files).stream().forEach(fileItem -> {
			try {
				// fileName = StringUtils.cleanPath(fileItem.getOriginalFilename());
				Binary file = new Binary(BsonBinarySubType.BINARY, fileItem.getBytes());
				fileList.add(file);
			} catch (Exception e) {
				status = false;
				e.printStackTrace();
			}
		});
		feedback.setFile(fileList);
		feedbackList.add(feedback);
		icountDTO.setFeedback(feedbackList);
		icountRepo.save(icountDTO);
		return status;

	}
	
	public boolean storeiCountTask(MultipartFile[] files, ICountStore icountDTO, int index) {
		List<Task> taskList = new ArrayList<>();
		List<Binary> fileList = new ArrayList<Binary>();
		status = true;
		Task task = new Task();
		task.setTaskName(icountDTO.getTask().get(index).getTaskName());
		task.setTaskDescription(icountDTO.getTask().get(index).getTaskDescription());
		task.setTaskCreatorName(icountDTO.getTask().get(index).getTaskCreatorName());
		task.setTaskCreatorEmail(icountDTO.getTask().get(index).getTaskCreatorEmail());
		task.setTaskStartDate(icountDTO.getTask().get(index).getTaskStartDate());
		task.setTaskEndDate(icountDTO.getTask().get(index).getTaskEndDate());	
		task.setTaskCompletionPercentage(icountDTO.getTask().get(index).getTaskCompletionPercentage());	

		Arrays.asList(files).stream().forEach(fileItem -> {
			try {
				// fileName = StringUtils.cleanPath(fileItem.getOriginalFilename());
				Binary file = new Binary(BsonBinarySubType.BINARY, fileItem.getBytes());
				fileList.add(file);
			} catch (Exception e) {
				status = false;
				e.printStackTrace();
			}
		});
		task.setFile(fileList);
		taskList.add(task);
		icountDTO.setTask(taskList);
		icountRepo.save(icountDTO);
		return status;

	}
	

}
