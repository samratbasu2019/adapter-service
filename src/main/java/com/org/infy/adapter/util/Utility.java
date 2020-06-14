package com.org.infy.adapter.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.org.infy.adapter.model.ICountStore;

@JsonInclude(Include.NON_DEFAULT)
public class Utility {
	protected static final Log logger = LogFactory.getLog(Utility.class);
	public static ICountStore payloadToObject(String icountStore) {
		ICountStore iCountStore = null;
		try {
			iCountStore = new ObjectMapper().readValue(icountStore, ICountStore.class);
		} catch (Exception e) {
			e.printStackTrace();

		}
		return iCountStore;
	}

	public static String getDateFromEpoc(long epoch) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		String date = formatter.format(new Date(epoch * 1000));
		return date;
	}

	public static int compareDate(String startDate, String endDate) {
		try {
			Date start = new SimpleDateFormat("dd-MM-yyyy").parse(startDate);
			Date end = new SimpleDateFormat("dd-MM-yyyy").parse(endDate);

			if (start.compareTo(end) > 0) {
				logger.info("start is after end");
				return 2;
			} else if (start.compareTo(end) < 0) {
				logger.info("start is before end");
				return 1;
			} else if (start.compareTo(end) == 0) {
				logger.info("start is equal to end");
				return 0;
			} else {
				System.out.println("Something weird happened...");
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}

		return 4;
	}

}
