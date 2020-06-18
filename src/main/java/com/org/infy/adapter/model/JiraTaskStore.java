package com.org.infy.adapter.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Document(collection = "col_jira_store")
public class JiraTaskStore {
	@Id
	@Field(value = "_id")
	private String id;
	
	@Field(value = "key")
	private String key;
	
	@Field(value = "summary")
	private String summary;
	
	@Field(value = "description")
	private String description;
	
	@Field(value = "statusName")
	private String statusName;
	
	@Field(value = "issueTypeName")
	private String issueTypeName;
	
	@Field(value = "asigneeDisplayName")
	private String asigneeDisplayName;
	
	@Field(value = "reporterDisplayName")
	private String reporterDisplayName;
	
	@Field(value = "creationDate")
	private String creationDate;
		
	@Field(value = "closureDate")
	private String closureDate;
	
	@Field(value = "resolution")
	private String resolution;
	
	@Field(value = "priority")
	private String priority;
	
	@Field(value = "project")
	private String project;
	
	@Field(value = "issueType")
	private String issueType;
	
	
}
