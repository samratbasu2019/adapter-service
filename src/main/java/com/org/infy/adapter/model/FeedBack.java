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
@Document(collection = "col_icount_feedback")
public class FeedBack {
	@Id
	@Field(value = "_id")
	private String id;
	
	@Field(value = "feedbackerName")
	private String feedbackerName;
	
	@Field(value = "feedbackerEmail")
	private String feedbackerEmail;
	
	@Field(value = "feedbackName")
	private String feedbackName;
	
	@Field(value = "feedbackDesc")
	private String feedbackDesc;
	
	@Field(value = "feedbackDate")
	private long feedbackDate;
	
	@Field
    private List<Binary> file;

}