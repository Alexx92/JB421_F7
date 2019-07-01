package com.test.route;

import org.apache.camel.builder.RouteBuilder;


public class ImplementRestService_Route extends RouteBuilder{
	@Override
	public void configure() throws Exception {
		restConfiguration()
			.component("spark-rest")
			.port(8080)
//			.bindingMode(RestBindingMode.json)
//			.dataFormatProperty("prettyPrint", "true")
		;
		
		rest("/spark")
			.get("/test")
				.to("direct:getA")
			.post()
				.to("direct:postB");
		
		from("direct:getA")
			.routeId("DIRECT GET")
			.log("${headers}")
			.log("${body}")
			.log("CLASS A-1 ${body.class}")
			.setBody().constant("{ \"Route_GET\":\"TEXT JSON TEST\"}")
			.log("CLASS A-2 ${body.class}")
			.log("DIRECT TEST A")
			.to("mock:direct:testA");
		from("direct:postB")
			.routeId("DIRECT POST")
			.log("${headers}")
			.log("${body}")
			.log("${body.class}")
			.log("DIRECT TEST B")
			.to("mock:direct:testB");
	}
	

}
