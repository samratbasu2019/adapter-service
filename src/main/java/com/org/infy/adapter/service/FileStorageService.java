package com.org.infy.adapter.service;

import com.org.infy.adapter.exception.FileStorageException;
import com.org.infy.adapter.exception.MyFileNotFoundException;
import com.org.infy.adapter.model.Appreciation;
import com.org.infy.adapter.model.Coins;
import com.org.infy.adapter.model.Course;
import com.org.infy.adapter.model.FeedBack;
import com.org.infy.adapter.model.FileInfo;
import com.org.infy.adapter.model.ICountStore;
import com.org.infy.adapter.model.Task;
import com.org.infy.adapter.property.FileStorageProperties;
import com.org.infy.adapter.repository.ICountRepository;
import com.org.infy.adapter.repository.UserCoinRepository;
import com.org.infy.adapter.util.Constants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
public class FileStorageService {
	protected final Log logger = LogFactory.getLog(this.getClass());
	private final Path fileStorageLocation;
	boolean status = true;

	@Value("${app.adapter.appreciation.given}")
	private String appreciationGiven;

	@Value("${app.adapter.appreciation.received}")
	private String appreciationReceived;

	@Value("${app.adapter.feedback.given}")
	private String feedbackGiven;

	@Value("${app.adapter.feedback.received}")
	private String feedbackReceived;

	@Value("${app.adapter.course.complete}")
	private String courseComplete;

	@Value("${app.adapter.task.complete}")
	private String taskComplete;

	@Autowired
	private ICountRepository icountRepo;

	@Autowired
	private UserCoinRepository coinRepo;

	@Autowired
	public FileStorageService(FileStorageProperties fileStorageProperties) {
		Random pathSuffix = new Random();
		Long path = pathSuffix.nextLong();
		this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir() + path.toString()).toAbsolutePath()
				.normalize();
		System.out.println("Samrat absolute path is :" + fileStorageLocation);
		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (Exception ex) {
			throw new FileStorageException("Could not create the directory where the uploaded files will be stored.",
					ex);
		}
	}

	private void saveUserCoins(String type, ICountStore iStore) {
		Coins coins = new Coins();
		coins.setName(iStore.getName());
		coins.setEmployeeId(iStore.getEmployeeId());
		coins.setEmail(iStore.getEmail());
		coins.setLastupdated(iStore.getDate());
		if (type.equalsIgnoreCase(Constants.APPRECIATION))
			coins.setCoins(Integer.parseInt(appreciationReceived));
		else if (type.equalsIgnoreCase(Constants.FEEDBACK))
			coins.setCoins(Integer.parseInt(feedbackReceived));
		else if (type.equalsIgnoreCase(Constants.COURSE))
			coins.setCoins(Integer.parseInt(courseComplete));
		else if (type.equalsIgnoreCase(Constants.TASK))
			coins.setCoins(Integer.parseInt(taskComplete));
		coinRepo.save(coins);
	}

	public Path storeFile(MultipartFile file) {
		// Normalize file name
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());

		try {
			// Check if the file's name contains invalid characters
			if (fileName.contains("..")) {
				throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
			}

			// Copy file to the target location (Replacing existing file with the same name)
			Path targetLocation = this.fileStorageLocation.resolve(fileName);
			logger.info("Target Location is :" + targetLocation);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

			return targetLocation;
		} catch (IOException ex) {
			throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
		}
	}

	public Resource loadFileAsResource(String fileName) {
		try {
			Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				throw new MyFileNotFoundException("File not found " + fileName);
			}
		} catch (MalformedURLException ex) {
			throw new MyFileNotFoundException("File not found " + fileName, ex);
		}
	}

	public boolean storeiCountAppreciation(MultipartFile[] files, ICountStore icountDTO, int index) {
		List<Appreciation> appretiationList = new ArrayList<>();

		status = true;
		Appreciation appretiation = new Appreciation();
		appretiation.setAppreciatorName(icountDTO.getAppreciation().get(index).getAppreciatorName());
		appretiation.setAppreciatorEmail(icountDTO.getAppreciation().get(index).getAppreciatorEmail());
		appretiation.setAppreciationSub(icountDTO.getAppreciation().get(index).getAppreciationSub());
		appretiation.setDescription(icountDTO.getAppreciation().get(index).getDescription());
		appretiation.setAppreciationDate(icountDTO.getAppreciation().get(index).getAppreciationDate());

		List<FileInfo> finfoList = new ArrayList<FileInfo>();

		Arrays.asList(files).stream().forEach(fileItem -> {
			try {
				String fileName = StringUtils.cleanPath(fileItem.getOriginalFilename());
				Path filePath = storeFile(fileItem);
				logger.info("File Name :" + fileName + " FilePath :" + filePath.toString());
				FileInfo fileInfo = new FileInfo();
				fileInfo.setFileName(fileName);
				fileInfo.setFilePath(filePath.toString());
				finfoList.add(fileInfo);
			} catch (Exception e) {
				status = false;
				e.printStackTrace();
			}
		});
		appretiation.setFileInfo(finfoList);
		appretiationList.add(appretiation);

		icountDTO.setAppreciation(appretiationList);
		icountRepo.save(icountDTO);
		saveUserCoins(Constants.APPRECIATION,icountDTO);
		
		return status;

	}

	public boolean storeiCountCourse(MultipartFile[] files, ICountStore icountDTO, int index) {
		List<Course> courseList = new ArrayList<>();

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

		List<FileInfo> finfoList = new ArrayList<FileInfo>();

		Arrays.asList(files).stream().forEach(fileItem -> {
			try {
				String fileName = StringUtils.cleanPath(fileItem.getOriginalFilename());
				Path filePath = storeFile(fileItem);
				logger.info("File Name :" + fileName + " FilePath :" + filePath.toString());
				FileInfo fileInfo = new FileInfo();
				fileInfo.setFileName(fileName);
				fileInfo.setFilePath(filePath.toString());
				finfoList.add(fileInfo);
			} catch (Exception e) {
				status = false;
				e.printStackTrace();
			}
		});
		course.setFileInfo(finfoList);
		courseList.add(course);

		icountDTO.setCourse(courseList);
		icountRepo.save(icountDTO);
		saveUserCoins(Constants.COURSE,icountDTO);
		return status;

	}

	public boolean storeiCountFeedback(MultipartFile[] files, ICountStore icountDTO, int index) {
		List<FeedBack> feedbackList = new ArrayList<>();

		status = true;
		FeedBack feedback = new FeedBack();
		feedback.setFeedbackName(icountDTO.getFeedback().get(index).getFeedbackName());
		feedback.setFeedbackDesc(icountDTO.getFeedback().get(index).getFeedbackDesc());
		feedback.setFeedbackerName(icountDTO.getFeedback().get(index).getFeedbackerName());
		feedback.setFeedbackerEmail(icountDTO.getFeedback().get(index).getFeedbackerEmail());
		feedback.setFeedbackDate(icountDTO.getFeedback().get(index).getFeedbackDate());

		List<FileInfo> finfoList = new ArrayList<FileInfo>();

		Arrays.asList(files).stream().forEach(fileItem -> {
			try {
				String fileName = StringUtils.cleanPath(fileItem.getOriginalFilename());
				Path filePath = storeFile(fileItem);
				logger.info("File Name :" + fileName + " FilePath :" + filePath.toString());
				FileInfo fileInfo = new FileInfo();
				fileInfo.setFileName(fileName);
				fileInfo.setFilePath(filePath.toString());
				finfoList.add(fileInfo);
			} catch (Exception e) {
				status = false;
				e.printStackTrace();
			}
		});
		feedback.setFileInfo(finfoList);
		feedbackList.add(feedback);

		icountDTO.setFeedback(feedbackList);
		icountRepo.save(icountDTO);
		saveUserCoins(Constants.FEEDBACK,icountDTO);
		return status;

	}

	public boolean storeiCountTask(MultipartFile[] files, ICountStore icountDTO, int index) {
		List<Task> taskList = new ArrayList<>();

		status = true;
		Task task = new Task();
		task.setTaskName(icountDTO.getTask().get(index).getTaskName());
		task.setTaskDescription(icountDTO.getTask().get(index).getTaskDescription());
		task.setTaskCreatorName(icountDTO.getTask().get(index).getTaskCreatorName());
		task.setTaskCreatorEmail(icountDTO.getTask().get(index).getTaskCreatorEmail());
		task.setTaskStartDate(icountDTO.getTask().get(index).getTaskStartDate());
		task.setTaskEndDate(icountDTO.getTask().get(index).getTaskEndDate());
		task.setTaskCompletionPercentage(icountDTO.getTask().get(index).getTaskCompletionPercentage());

		List<FileInfo> finfoList = new ArrayList<FileInfo>();

		Arrays.asList(files).stream().forEach(fileItem -> {
			try {
				String fileName = StringUtils.cleanPath(fileItem.getOriginalFilename());
				Path filePath = storeFile(fileItem);
				logger.info("File Name :" + fileName + " FilePath :" + filePath.toString());
				FileInfo fileInfo = new FileInfo();
				fileInfo.setFileName(fileName);
				fileInfo.setFilePath(filePath.toString());
				finfoList.add(fileInfo);
			} catch (Exception e) {
				status = false;
				e.printStackTrace();
			}
		});
		task.setFileInfo(finfoList);
		taskList.add(task);

		icountDTO.setTask(taskList);
		icountRepo.save(icountDTO);
		saveUserCoins(Constants.TASK,icountDTO);
		return status;

	}

}
