package com.test.route;

import java.io.IOException;

import javax.xml.xpath.XPathExpressionException;

import org.apache.camel.builder.RouteBuilder;
import com.test.bean.BeanOrder;

public class Create_And_Maintance_Route extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		onException(XPathExpressionException.class)
			.log("**** ERROR ****")
			.log("HEADERS => ${headers}")
			.log("BODY => ${body}")
			.to("file:C:/WEB/records/inputs2/ERROR")
		;
		onException(IOException.class)
		.log("**** ERROR ****")
		.log("HEADERS => ${headers}")
		.log("BODY => ${body}")
		.to("file:C:/WEB/records/inputs2/ERROR")
		;
		/*
		 * -> Para varificar la lectura del archivo por nombre: include=.*camel.*xml
		 * -> Filtra de acuerdo a el valor de parametro en Objeto creado en Bean java utilizando valores de XMl entrante
		 * -> Choice
		 * 		-> WHEN => NUMERO > 0
		 * 		-> OTHERWISE
		 * 			-> TO -> ACTIVEMQ => XML_A
		 * */
		from("file:C:/WEB/records/inputs1?include=.*camel.*xml")
			.log("HEADERS => ${headers}")
			.log("CamelFileName ==> ${headers.CamelFileName}")
			.log("BODY ==> ${body}")
			.setProperty("XML",simple("${body}"))			
			.setProperty("NO", xpath("/orders/order/no/text()"))
			.setProperty("NRO" , xpath("/orders/order/nro/text()"))
			.setProperty("TYPE" , xpath("/orders/order/type/text()"))
			.setProperty("DESCRIPTION" , xpath("/orders/order/description/text()"))
			
			.bean(BeanOrder.class)
			.filter(simple("${body.getNumero()} > 0"))
			.log("***** PASS FILTER | NUMERO > 0 | *****")
			
			.choice()
				.when().simple("${body.getTipo()} contains 'A'")
					.log("***** WHEN 1 | TYPE => A|  *****")		
				.otherwise()
					.log("***** OTHERWISE *****")
					.setBody(simple("${exchangeProperty.XML}"))
					.to("activemq:queue:XML_A")
		;
		/*
		 *	Lectura de archivo y separacion por nombre de archivo
		 * -> Filtra nro > 0 
		 * -> Filtra @Name == ORDER-1B
		 * -> Choice 
		 *		-> archivos que contiene nombre 'CAMEL'
		 *		-> Choice
		 *			-> WHEN =>  TYPE == 'A'
		 *			-> WHEN =>  TYPE == 'B'
		 *			-> OTHERWISE
		 *				-> TO => "COMPUESTO"				 
		 *		-> otros
		 *  
		 * */ 
		from("file:C:/WEB/records/inputs2")	
			.log("HEADERS 2 => ${headers}")
			.log("CamelFileName 2 ==> ${headers.CamelFileName}")
			.setProperty("NAME_TAG", xpath("/orders/order/@name"))
			.log("==> @NAME TAG -> ${exchangeProperty.NAME_TAG} <==")
			.filter(xpath("/orders/order/nro > 0"))
			.log("***** PASS FILTER 1 | /orders/order/nro > 0 | *****")
			.filter(xpath("/orders/order/no > 0"))
			.log("***** PASS FILTER 2 | /orders/order/no > 0 | *****")
//			.filter(xpath("/orders/order[@name='ORDER-1B']"))
//			.log("***** PASS FILTER 2 | /orders/order[@name='ORDER-1B'] | *****")
			.choice()
				.when(simple("${headers.CamelFileName} contains 'CAMEL'")).id("Test 1")
					.log("***** WHEN 1 *****")
					.choice()
						.when().xpath("/orders/order/type/text() = 'A'")
							.log("***** WHEN 1.1 *****")
						.when(xpath("/orders/order[(contains(type,'B'))]"))
							.log("***** WHEN 1.2 *****")
						.otherwise()
							.log("***** OTHERWISE 1.1 *****")
							.setProperty("TO_FILESYSTEM", xpath("/orders/order/type/text()"))
							.toD("file:C:/WEB/RECORDS/OUTPUT/${exchangeProperty.TO_FILESYSTEM}?fileName=${headers.CamelFileName}")
					.endChoice()
				.when().simple("${headers.CamelFileName} contains 'OBJ'")
					.log("***** WHEN 2 *****")
					.setProperty("TO_END_AMQ", xpath("/orders/order/type/text()"))
					.log("***** TO_END_AMQ => ${exchangeProperty.TO_END_AMQ} *****")
					.choice()
						.when().xpath("/orders/order/type/text() = 'A'")
							.log("***** WHEN 2.1 ***** \n BODY.class ==> ${body.class}\n")
							.convertBodyTo(String.class)
							.toD("activemq:queue:XML_" + "${exchangeProperty.TO_END_AMQ}")
						.when(constant("${exchangeProperty.TO_END_AMQ}").contains('B'))
							.log("***** WHEN 2.2 ***** \n BODY.class ==> ${body.class}\n")
							.toD("activemq:queue:XML_" + "${exchangeProperty.TO_END_AMQ}")
						.otherwise()
							.log("***** OTHERWISE 2.1 *****")
							.convertBodyTo(String.class)
							.toD("activemq:queue:XML_" + "${exchangeProperty.TO_END_AMQ}")
					.endChoice()
					
				.otherwise()
					.log("***** OTHERWISE *****")				
		;
	
	}
}
