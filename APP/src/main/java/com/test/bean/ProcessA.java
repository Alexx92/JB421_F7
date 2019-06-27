package com.test.bean;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.xml.XPathBuilder;

public class ProcessA implements Processor {
	
	@Override
	public void process(Exchange exchange) throws Exception {
		
		String bodyXML  = exchange.getIn().getBody(String.class);
		String type = XPathBuilder.xpath("/orders/type").evaluate(exchange.getContext(), bodyXML);
		exchange.getIn().setHeader("TYPE", type);
		
	}
}
