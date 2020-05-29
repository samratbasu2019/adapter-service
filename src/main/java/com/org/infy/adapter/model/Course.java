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
@Document(collection = "col_icount_course")
public class Course {
	@Id
	@Field(value = "_id")
	private String id;
	
	@Field(value = "courseCreatorName")
	private String courseCreatorName;
	
	@Field(value = "courseCreatorEmail")
	private String courseCreatorEmail;
	
	@Field(value = "courseName")
	private String courseName;
	
	@Field(value = "courseDesc")
	private String courseDesc;
	
	@Field(value = "courseStartDate")
	private long courseStartDate;
	
	@Field(value = "courseEndDate")
	private long courseEndDate;
	
	@Field(value = "totalHrsAttended")
	private long totalHrsAttended;
	
	@Field(value = "completionPercentage")
	private String completionPercentage;
	
	@Field(value = "fileInfo")
    private List<FileInfo> fileInfo;

}
