package com.org.infy.adapter.model;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
public class FileInfo {

	@Field(value = "fileName")
	private String fileName;
	
	@Field(value = "filePath")
	private String filePath;
}
