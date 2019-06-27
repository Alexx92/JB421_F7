package com.test.bean;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.apache.camel.builder.xml.XPathBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanPrintData2 {
	
	private static final Logger LOGGER = LoggerFactory.getLogger("RECEPTION");

	public void printDataNAME(Exchange exchange) throws Exception{
		String bodyXML  = exchange.getIn().getBody(String.class);
		String name = XPathBuilder.xpath("/order/name").evaluate(exchange.getContext(), bodyXML);
		LOGGER.info("---> BEAN DECLARE IN CAMEL-CONTEXT <--- | NAME Order => " +  name);
	}
	
	public void printDataDireccion(Exchange exchange) throws Exception {
		String bodyXML  = exchange.getIn().getBody(String.class);
		String name = XPathBuilder.xpath("/order/direccion").evaluate(exchange.getContext(), bodyXML);
		LOGGER.info("---> BEAN DECLARE IN CAMEL-CONTEXT <--- | DIRECCION Order => " +  name);
	}

	public void printDataBinding(@Body String data, @Header("CamelFileNameOnly") String filename) throws Exception {
		LOGGER.info("---> BEAN DATA BINDING <--- | FILENAME => " +  filename + "| DATA BODY => \n" +  data);
	}
	
	public void generateListDestination(@Body String data, @Header("CamelFileNameOnly") String filename , Exchange exchange) throws Exception {
		LOGGER.info("---> BEAN DATA BINDING in RecipientList <--- | FILENAME => " +  filename + "| DATA BODY => \n" +  data);
		String list = "";
		for (int i = 0; i < 3; i++) {
			if (i == 0) {
				list = list.concat("file:C:/WEB/RECORDS/Output_RecipientList_1,");
			}else {
				if (i == 1) {
					list = list.concat("file:C:/WEB/RECORDS/Output_RecipientList_2,");	
				}else {
					list = list.concat("file:C:/WEB/RECORDS/Output_RecipientList_3");
				}
			}
		}
		exchange.getIn().getHeaders().put("DESTINATION", list);
		LOGGER.info("---> BEAN RecipientList <--- | List => " + exchange.getIn().getHeader("DESTINATION"));
	}
}
