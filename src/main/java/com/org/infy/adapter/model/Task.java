package com.org.infy.adapter.model;

import java.util.List;

import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Task {
	
	@Field(value = "taskCreatorName")
	private String taskCreatorName;
	
	@Field(value = "taskCreatorEmail")
	private String taskCreatorEmail;
	
	@Field(value = "taskName")
	private String taskName;
	
	@Field(value = "taskDescription")
	private String taskDescription;
	
	@Field(value = "taskStartDate")
	private long taskStartDate;
	
	@Field(value = "taskEndDate")
	private long taskEndDate;
	
	@Field(value = "taskCompletionPercentage")
	private String taskCompletionPercentage;
	
	@Field(value = "fileInfo")
    private List<FileInfo> fileInfo;
}
