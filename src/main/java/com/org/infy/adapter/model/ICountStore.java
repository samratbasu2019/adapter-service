package com.org.infy.adapter.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Document(collection = "col_icount_store")
public class ICountStore {
	@Id
	@Field(value = "_id")
	private String id;
	
	@Field(value = "employeeId")
	private String employeeId;
	
	@Field(value = "name")
	private String name;
	
	@Field(value = "email")
	private String email;
	
	@Field(value = "date")
	private long date;
	
	@Field(value = "istask")
	private String istask;
	
	@Field(value = "category")
	private String category;
	
	@Field(value = "appreciation")
	private List<Appreciation> appreciation;
	
	@Field(value = "course")
	private List<Course> course;
		
	@Field(value = "feedback")
	private List<FeedBack> feedback;
	
	@Field(value = "task")
	private List<Task> task;
	
	
}
