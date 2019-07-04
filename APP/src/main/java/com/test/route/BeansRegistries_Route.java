package com.test.route;

import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Predicate;

import org.apache.camel.builder.PredicateBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.cdi.ContextName;

import com.test.bean.BeanPrintData;
import com.test.bean.BeanPrintData3;

@ContextName("context1")
public class BeansRegistries_Route extends RouteBuilder{

//	@Inject
//	@Uri("file:C:/WEB/RECORDS/Input_BeansRouteCDI") 
//	Endpoint in;

	@EndpointInject(uri="file:C:/WEB/RECORDS/Input_BeansRouteCDI") Endpoint in2;
	
//	@Inject
//	@Uri("file:C:/WEB/OUTPUTS/Output_BeansRouteCDI")
//	Endpoint out;
		
	@EndpointInject(uri="file:C:/WEB/RECORDS/Output_BeansRouteCDI") Endpoint out;

	
	Predicate isNullHeader = PredicateBuilder.or(header("CamelFileName").isNull(), simple("${header.CamelFileName==''}"));
	
	@Override
	public void configure() throws Exception {
		from(in2).id("Route_DINAMIC_ROUTIND_CDI")
			.log(LoggingLevel.INFO, "com.pruebas", "--> INIT BEANS | BODY ==> ${body}")
			.log("***** ROUTE  WITH CDI *****")
			.bean(BeanPrintData3.class,"generateListRoute")			
			.to("direct:routeSlip")
			.to(out)
		;
		
		from("direct:routeSlip").routingSlip(header("DEST-SLIP"))
//		.dynamicRouter(method(BeanPrintData3.class, "generateMethodRoute"))
			.log("*** LIST SLIP => ${header.DEST-SLIP}")
			.log("*** \n BODY ROUTE SPLIP => ${body} \n *** ")
			.to("file:C:/WEB/RECORDS/OutPut_Slip_0")
			.to("file:C:/WEB/RECORDS/OutPut_Slip_4")
		;
		
		from("file:C:/WEB/RECORDS/Inputs_BeanMethodsInPredicates")
			.log(LoggingLevel.INFO, "com.pruebas", "--> INIT BEANS | BODY ==> ${body}")
			.filter(method(BeanPrintData3.class,"isValidNumber")).log("*** FILTER TRUE 1 ***").end()
			.log("***** PASS FILTER 1 | NUMBER *****")
			.setHeader("CamelFileName",simple(""))
			.filter(isNullHeader).log("*** FILTER TRUE 2 ***").end()
			.log("***** END ROUTE BEANS METHODS IN PREDICATES *****") 
		;		
		
		/*
		 * CHOICE
		 * 		->WHEN => "/order/id/text() = '1-A'"
		 * 			//call bean for JAVA IMPORT
		 * 		->WHEN => "/order[(contains(id,'1-B'))]"
		 * 			//call bean CALL BEAN METHODS DECLARING BEAN IN CAMEL-CONTEXT
		 * 		->WHEN => "/order/id/text() = '1-C'"
		 * 			//call bean
		 *		->WHEN => "/order/id/text() = '1-D'"
		 *			//CALL BEAN PRINT DATABINDING
		 *		->WHEN => "/order/id/text() = '1-E'"
		 *			//CALL BEAN GENERATE RECIPIENTLIST
		 * */
		from("file:C:/WEB/RECORDS/Input_BeansRegistries").id("Route_JAVA-BEANS")			
			.log(LoggingLevel.INFO, "com.pruebas", "--> INIT BEANS | BODY ==> ${body}")			
			.choice()
				.when().xpath("/order/id/text() = '1-A'")
					.log("*** INIT WHEN 1-A | DECLARING JAVA BEANS***")
					.bean(BeanPrintData.class)
					.log("*** END WHEN 1-A ***")
				.when().xpath("/order[(contains(id,'1-B'))]")
					.log("*** WHEN 1-B | CALL BEAN METHOD DECLARE IN CAMEL-CONTEXT ***")
					.to("bean://printData2?method=printDataNAME")
					.to("bean://printData2?method=printDataDireccion")
				.when().xpath("/order/id/text() = '1-C'")
					.log("*** WHEN 1-C | CALL BEAN METHOD DECLARE WITH AUTOWIRED ***")
					.bean(BeanPrintData3.class,"printDataNAME")
					.bean(BeanPrintData3.class,"printDataDireccion")
				.when().xpath("/order/id/text() = '1-D'")
					.log("*** WHEN 1-D | PARAMETERS BINDING ***")
					.to("bean://printData2?method=printDataBinding")
				.when().xpath("/order/id/text() = '1-E'")
					.log("*** WHEN 1-E | PARAMETERS RECIPIENT LIST ***")
					.to("bean://printData2?method=generateListDestination")
				.otherwise()
					.log("*** OTHERWISE ***")
				.end()
			.log("RECIPIENT LIST EN ROUTE -> ${header.DESTINATION}")
			.recipientList(header("DESTINATION"),",")
			.log("******  END BEANS  ******")
		;		
	}

}
