package com.test.bean;

import javax.inject.Named;

import org.apache.camel.Exchange;
import org.apache.camel.builder.xml.XPathBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@Named("print")s
public class BeanPrintData3 {

	private static final Logger LOGGER = LoggerFactory.getLogger("BEAN AUTOWIRED/ERROR ");

	public void printDataNAME(Exchange exchange) throws Exception{
		String bodyXML  = exchange.getIn().getBody(String.class);
		String name = XPathBuilder.xpath("/order/name").evaluate(exchange.getContext(), bodyXML);
		LOGGER.info("---> BEAN DECLARE WITCH AUTOWIRED <--- | NAME Order => " +  name);
	}
	
	public void printDataDireccion(Exchange exchange) throws Exception {
		String bodyXML  = exchange.getIn().getBody(String.class);
		String name = XPathBuilder.xpath("/order/direccion").evaluate(exchange.getContext(), bodyXML);
		LOGGER.info("---> BEAN DECLARE WITCH AUTOWIRED <--- | DIRECCION Order => " +  name);
	}
	
	
	/*
	 * Metodos Utilizados para validar ruta de ERROR
	 * */
	
	public void printTransformExceptionA(Exchange exchange) {
		String bodyXML  = exchange.getIn().getBody(String.class);
		String name = XPathBuilder.xpath("/orders/order/name").evaluate(exchange.getContext(), bodyXML);
		LOGGER.info("---> TRANSFORM INCORRECT <--- | VALUE => " +  name);
		int value = Integer.parseInt(name);
	}
	
	public void printTransformExceptionB(Exchange exchange) {
		String bodyXML  = exchange.getIn().getBody(String.class);
		String tel = XPathBuilder.xpath("/orders/order/tel").evaluate(exchange.getContext(), bodyXML);
		LOGGER.info("---> TRANSFORM CORRECT <--- | VALUE => " +  tel);
		int value = Integer.parseInt(tel);
		
	}
	/*
	 * METHODS EXECUTING PREDICATES
	 * */
	
	public boolean isValidNumber(Exchange exchange)throws Exception {
		String bodyXML  = exchange.getIn().getBody(String.class);
		String name = XPathBuilder.xpath("/order/tel").evaluate(exchange.getContext(), bodyXML);
		try {
			int value = Integer.parseInt(name);
			LOGGER.info("---> TRANSFORM CORRECT <--- | VALUE => " +  name);
			return true;
		}catch (NumberFormatException e) {
			LOGGER.info("---> TRANSFORM INCORRECT <--- | VALUE => " +  name);
			return false;			
		}
	}
	
	/**/
	
	public void generateListRoute(Exchange exchange) {
		String list = "";
		list = list.concat("file:C:/WEB/RECORDS/OutPut_Slip_1,");
		list = list.concat("file:C:/WEB/RECORDS/OutPut_Slip_2");		
		exchange.getIn().getHeaders().put("DEST-SLIP", list);
	}
	public String generateMethodRoute(Exchange exchange) {
		String list = "";
		list = list.concat("file:C:/WEB/RECORDS/OutPut_Slip_3");
//		list = list.concat("file:C:/WEB/RECORDS/OutPut_Slip_4");	
		return 	list;
	}
	

}
