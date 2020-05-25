package com.org.infy.adapter.util;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter

public class ResponseHelper {
	private static ResponseAPI resAPI = new ResponseAPI();
	
	public static ResponseAPI populateRresponse(String message, String status) {
		resAPI.setStatus(status);
		resAPI.setMessage(message);		
		return resAPI;
		
	}

}
