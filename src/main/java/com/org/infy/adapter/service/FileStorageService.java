package com.org.infy.adapter.service;

//import com.atlassian.jira.rest.client.api.IssueRestClient;
//import com.atlassian.jira.rest.client.api.SearchRestClient;
//import com.atlassian.jira.rest.client.api.domain.BasicIssue;
//import com.atlassian.jira.rest.client.api.domain.Issue;
//import com.atlassian.jira.rest.client.api.domain.SearchResult;
//import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
//import com.atlassian.util.concurrent.Promise;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.org.infosys.IJiraAdapterService;
//import com.org.infosys.JiraAdapterService;
//import com.org.infosys.model.JiraResponse;
import com.org.infy.adapter.dto.CoinDTO;
import com.org.infy.adapter.dto.RequestCoinPayload;
import com.org.infy.adapter.exception.FileStorageException;
import com.org.infy.adapter.exception.MyFileNotFoundException;
import com.org.infy.adapter.model.Appreciation;
import com.org.infy.adapter.model.Coins;
import com.org.infy.adapter.model.Course;
import com.org.infy.adapter.model.FeedBack;
import com.org.infy.adapter.model.FileInfo;
import com.org.infy.adapter.model.ICountStore;
import com.org.infy.adapter.model.JiraTaskStore;
import com.org.infy.adapter.model.Task;
import com.org.infy.adapter.property.FileStorageProperties;
import com.org.infy.adapter.repository.ICountRepository;
import com.org.infy.adapter.repository.JiraRepository;
import com.org.infy.adapter.repository.UserCoinRepository;
import com.org.infy.adapter.util.Constants;
import com.org.infy.adapter.util.Utility;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Service
public class FileStorageService {
	protected final Log logger = LogFactory.getLog(this.getClass());
	private final Path fileStorageLocation;
	boolean status = true;

	@Value("${app.redis.service.rest.coins.url}")
	private String restCoinUrl;

	@Value("${app.kafka.service.rest.url}")
	private String restKafkaUrl;

	@Value("${app.jira.url}")
	private String jiraURL;

	@Value("${app.jira.username}")
	private String userName;

	@Value("${app.jira.password}")
	private String password;

	@Value("${app.jira.client.lib}")
	private String jiraLib;

	@Autowired
	private ICountRepository icountRepo;

	@Autowired
	private UserCoinRepository coinRepo;

	@Autowired
	private JiraRepository jiraRepo;


	@Autowired
	public FileStorageService(FileStorageProperties fileStorageProperties) {
		Random pathSuffix = new Random();
		Long path = pathSuffix.nextLong();
		this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir() + path.toString()).toAbsolutePath()
				.normalize();
		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (Exception ex) {
			throw new FileStorageException("Could not create the directory where the uploaded files will be stored.",
					ex);
		}
	}

	private HttpEntity<String> invokeKafkaService(String type, ICountStore iStore) {
		ObjectMapper obm = new ObjectMapper();
		String requestPayload = null;
		HttpEntity<String> response = null;
		try {
			requestPayload = obm.writeValueAsString(iStore);
			logger.info("Request Payload :" + requestPayload);
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> entity = new HttpEntity<String>(requestPayload, headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(restKafkaUrl).queryParam("type", type);
			// .queryParam("message", requestPayload);

			response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}

	private ResponseEntity<CoinDTO> getCoinDetails() {

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		RequestCoinPayload rcp = new RequestCoinPayload();
		rcp.setKey("coins");
		HttpEntity<RequestCoinPayload> entity = new HttpEntity<RequestCoinPayload>(rcp, headers);

		ResponseEntity<CoinDTO> coins = restTemplate.exchange(restCoinUrl, HttpMethod.POST, entity,
				new ParameterizedTypeReference<CoinDTO>() {
				});

		logger.info("Coin DTO is:" + coins);

		return coins;
	}

	private void saveUserCoins(String type, ICountStore iStore) {
		ResponseEntity<CoinDTO> result = getCoinDetails();
		CoinDTO coinsDetails = result.getBody();
		int appreciationGiven = 0, appreciationReceived = 0, feedbackGiven = 0, feedbackReceived = 0,
				courseComplete = 0, taskComplete = 0;

		if (null != coinsDetails.getDealStartDate()) {
			String today = Utility.getDateFromEpoc(iStore.getDate());
			String endDealDate = Utility.getDateFromEpoc(coinsDetails.getDealEndDate());
			String startDealDate = Utility.getDateFromEpoc(coinsDetails.getDealStartDate());

			if (Utility.compareDate(today, startDealDate) == 2 || Utility.compareDate(today, startDealDate) == 0) {
				if (Utility.compareDate(today, endDealDate) == 2) {
					appreciationGiven = coinsDetails.getDefaults().getAppreciationGivenCoins();
					appreciationReceived = coinsDetails.getDefaults().getAppreciationReceivedCoins();
					feedbackGiven = coinsDetails.getDefaults().getFeedbackGivenCoins();
					feedbackReceived = coinsDetails.getDefaults().getFeedbackReceivedCoins();
					courseComplete = coinsDetails.getDefaults().getCourseCompleteCoins();
					taskComplete = coinsDetails.getDefaults().getTaskCompleteCoins();
				}
				if (Utility.compareDate(today, endDealDate) == 1 || Utility.compareDate(today, endDealDate) == 0) {
					appreciationGiven = coinsDetails.getDeals().getAppreciationGivenCoins();
					appreciationReceived = coinsDetails.getDeals().getAppreciationReceivedCoins();
					feedbackGiven = coinsDetails.getDeals().getFeedbackGivenCoins();
					feedbackReceived = coinsDetails.getDeals().getFeedbackReceivedCoins();
					courseComplete = coinsDetails.getDeals().getCourseCompleteCoins();
					taskComplete = coinsDetails.getDeals().getTaskCompleteCoins();
				}
			} else {
				appreciationGiven = coinsDetails.getDefaults().getAppreciationGivenCoins();
				appreciationReceived = coinsDetails.getDefaults().getAppreciationReceivedCoins();
				feedbackGiven = coinsDetails.getDefaults().getFeedbackGivenCoins();
				feedbackReceived = coinsDetails.getDefaults().getFeedbackReceivedCoins();
				courseComplete = coinsDetails.getDefaults().getCourseCompleteCoins();
				taskComplete = coinsDetails.getDefaults().getTaskCompleteCoins();
			}

		} else {
			appreciationGiven = coinsDetails.getDefaults().getAppreciationGivenCoins();
			appreciationReceived = coinsDetails.getDefaults().getAppreciationReceivedCoins();
			feedbackGiven = coinsDetails.getDefaults().getFeedbackGivenCoins();
			feedbackReceived = coinsDetails.getDefaults().getFeedbackReceivedCoins();
			courseComplete = coinsDetails.getDefaults().getCourseCompleteCoins();
			taskComplete = coinsDetails.getDefaults().getTaskCompleteCoins();
		}

		Coins coins = new Coins();
		coins.setName(iStore.getName());
		coins.setEmployeeId(iStore.getEmployeeId());
		coins.setEmail(iStore.getEmail());
		coins.setLastupdated(iStore.getDate());
		if (type.equalsIgnoreCase(Constants.APPRECIATION))
			coins.setCoins(appreciationReceived);
		else if (type.equalsIgnoreCase(Constants.FEEDBACK)) 
			coins.setCoins(feedbackReceived);
		else if (type.equalsIgnoreCase(Constants.COURSE))
			coins.setCoins(courseComplete);
		else if (type.equalsIgnoreCase(Constants.TASK))
			coins.setCoins(taskComplete);

		logger.info("To be saved receiver coins :" + coins.getCoins());
		coinRepo.save(coins);

		if (type.equalsIgnoreCase(Constants.APPRECIATION)) {
			Coins coinsAppreciator = new Coins();
			coinsAppreciator.setName(iStore.getAppreciation().get(0).getAppreciatorName());
			coinsAppreciator.setEmail(iStore.getAppreciation().get(0).getAppreciatorEmail());
			coinsAppreciator.setLastupdated(iStore.getDate());
			coinsAppreciator.setCoins(appreciationGiven);
			logger.info("To be saved appreciation provider data :" + coins.getCoins());
			coinRepo.save(coinsAppreciator);
		} else if (type.equalsIgnoreCase(Constants.FEEDBACK)) {
			Coins coinsFeedbacker = new Coins();
			coinsFeedbacker.setName(iStore.getFeedback().get(0).getFeedbackerName());
			coinsFeedbacker.setEmail(iStore.getFeedback().get(0).getFeedbackerEmail());
			coinsFeedbacker.setLastupdated(iStore.getDate());
			coinsFeedbacker.setCoins(feedbackGiven);
			logger.info("To be saved feedback provider data :" + coins.getCoins());
			coinRepo.save(coinsFeedbacker);
		}

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
				logger.info("Appreciation file name :" + fileName);
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
		saveUserCoins(Constants.APPRECIATION, icountDTO);
		HttpEntity<String> response = invokeKafkaService(Constants.APPRECIATION, icountDTO);
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
		saveUserCoins(Constants.COURSE, icountDTO);
		HttpEntity<String> response = invokeKafkaService(Constants.COURSE, icountDTO);
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
		saveUserCoins(Constants.FEEDBACK, icountDTO);
		HttpEntity<String> response = invokeKafkaService(Constants.FEEDBACK, icountDTO);
		return status;

	}

	public boolean storeiCountTask(MultipartFile[] files, ICountStore icountDTO, int index) {
		List<Task> taskList = new ArrayList<>();

		status = true;
		Task task = new Task();
		task.setTaskName(icountDTO.getTask().get(index).getTaskName());
		task.setTaskDescription(icountDTO.getTask().get(index).getTaskDescription());
		task.setTaskCreatorName(icountDTO.getTask().get(index).getTaskCreatorName());
		task.setTaskStatus(icountDTO.getTask().get(index).getTaskStatus());
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
		saveUserCoins(Constants.TASK, icountDTO);
		HttpEntity<String> response = invokeKafkaService(Constants.TASK, icountDTO);
		return status;

	}

	public List<ICountStore> findByTasks(String email) {
		List<ICountStore> istore = icountRepo.findByEmailAndIstask(email, Constants.ISTASK);
		return istore;
	}

	public void pullJiraTask(String email) {

		logger.info("Jira Lib: " + jiraLib + "Jira url : " + jiraURL + " username : " + userName + " password: "
				+ password);
		try {
			JiraTaskStore[] jiraStore = null;
			Process proc = Runtime.getRuntime().exec(jiraLib + " " + jiraURL + " " + userName + " " + password);
			// Then retreive the process output
			InputStream in = proc.getInputStream();
			InputStream err = proc.getErrorStream();
			InputStreamReader isReader = new InputStreamReader(in);
			// Creating a BufferedReader object
			BufferedReader reader = new BufferedReader(isReader);
			StringBuffer sb = new StringBuffer();
			String str;
			while ((str = reader.readLine()) != null) {
				sb.append(str);
			}
			logger.info(sb.toString());
			
			
			ObjectMapper obm = new ObjectMapper();
			jiraStore = obm.readValue(sb.toString(), JiraTaskStore[].class);
			for (JiraTaskStore js : jiraStore) {				
				jiraRepo.save(js);
			}
			ICountStore iStore = new ICountStore();
			iStore.setEmail(email);
			iStore.setName(email);
			//iStore.setTask().get(0).setTaskCreatorName(email);
			HttpEntity<String> response = invokeKafkaService(Constants.JIRATASK,iStore);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
