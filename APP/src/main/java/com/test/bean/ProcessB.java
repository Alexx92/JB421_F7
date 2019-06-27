package com.test.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessB {
	private static final Logger LOGGER = LoggerFactory.getLogger("RECEPTION");

	public String processTax(String data) throws Exception{
		LOGGER.info("TestB -->  " + data);
		return data;
	}
	
	public String processTax2(String data) throws Exception{
		LOGGER.info("TestB METHOD 2  -->  " + data);
		return data;		
	}
	

}
