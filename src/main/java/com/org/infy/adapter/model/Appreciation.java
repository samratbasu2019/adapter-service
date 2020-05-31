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
public class Appreciation {
	
	@Field(value = "appreciatorName")
	private String appreciatorName;
	
	@Field(value = "appreciatorEmail")
	private String appreciatorEmail;
	
	@Field(value = "appreciationSub")
	private String appreciationSub;
	
	@Field(value = "description")
	private String description;
	
	@Field(value = "appreciationDate")
	private long appreciationDate;
	
	@Field(value = "fileInfo")
    private List<FileInfo> fileInfo;
	
}
