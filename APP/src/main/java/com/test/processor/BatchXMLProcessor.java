package com.test.processor;

import java.util.ArrayList;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.test.object.Object_A;

public class BatchXMLProcessor implements Processor{
	private static final Logger LOGGER = LoggerFactory.getLogger("RECEPTION");

	@Override
	public void process(Exchange arg0) throws Exception {
		ArrayList<Object_A> list = (ArrayList<Object_A>) arg0.getIn().getBody();
		for (Object_A obj : list) {
			LOGGER.info("ID => " + obj.getId() +" | " + "NAME => " + obj.getName() + " | " +"TEL => " + obj.getTel());
			
		}
		
	}

}
