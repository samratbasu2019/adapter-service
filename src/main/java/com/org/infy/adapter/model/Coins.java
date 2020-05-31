package com.org.infy.adapter.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Document(collection = "col_user_coins")
public class Coins {

	@Id
	@Field(value = "_id")
	private String id;
	
	@Field(value = "employeeId")
	private String employeeId;
	
	@Field(value = "name")
	private String name;
	
	@Field(value = "email")
	private String email;
	
	@Field(value = "coins")
	private int coins;
	
	@Field(value = "lastupdated")
	private long lastupdated;
	
	@Field(value = "redeemed")
	private String redeemed;
	
	@Field(value = "redeemdate")
	private long redeemdate;
	
}
