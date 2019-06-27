package com.test.bean;

import org.apache.camel.Exchange;
import org.apache.camel.builder.xml.XPathBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanPrintData {

	private static final Logger LOGGER = LoggerFactory.getLogger("RECEPTION");

	public void printDataID(Exchange exchange) {
		String bodyXML  = exchange.getIn().getBody(String.class);
		String type = XPathBuilder.xpath("/order/id").evaluate(exchange.getContext(), bodyXML);
		LOGGER.info("---> BEAN <--- | ID Order => " +  type);
	}
	
//	public void printDataNAME(Exchange exchange) {
//		String bodyXML  = exchange.getIn().getBody(String.class);
//		String name = XPathBuilder.xpath("/order/name").evaluate(exchange.getContext(), bodyXML);
//		LOGGER.info("---> BEAN <--- | NAME Order => " +  name);
//	}
}
