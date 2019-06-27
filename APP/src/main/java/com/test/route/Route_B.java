package com.test.route;

//import javax.inject.Inject;

//import org.apache.camel.Endpoint;
import org.apache.camel.builder.RouteBuilder;
//import org.apache.camel.cdi.ContextName;
//import org.apache.camel.cdi.Uri;
import org.apache.camel.model.rest.RestBindingMode;

//@ContextName("context2")
public class Route_B extends RouteBuilder{

//	@Inject
//	@Uri("file:C:/WEB/RECORDS/inputs3")
//	Endpoint in;
//	
//	@Inject
//	@Uri("file:C:/WEB/RECORDS/OUTPUT3")
//	Endpoint out;
	
	
	@Override
	public void configure() throws Exception {
		restConfiguration()
			.component("spark-rest").port(8585)
//			.bindingMode(RestBindingMode.json)
//			.dataFormatProperty("prettyPrint", "true")
		;
		
		rest("/spark")
			.get("{id}")
				.to("direct:getA")				
			.post()
				.to("direct:postB");
				
			
				
//		from(in)
//		.bean("test","processTax2")
//		.to(out);
		
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
