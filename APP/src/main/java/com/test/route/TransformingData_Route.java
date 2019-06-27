package com.test.route;


import java.io.IOException;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.dataformat.xmljson.XmlJsonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.spi.DataFormat;

import com.test.object.Object_Json;
import com.test.object.Order;
import com.test.processor.BatchXMLProcessor;
import com.test.strategy.BodyAggregationStrategy;
import com.test.strategy.ListAggregationStrategy;

public class TransformingData_Route extends RouteBuilder{
	
	@Override
	public void configure() throws Exception{
		
		DataFormat jaxb = new JaxbDataFormat(com.test.object.Object_A.class.getPackage().getName());

		XmlJsonDataFormat xmlJsonFormat = new XmlJsonDataFormat();
		
		DataFormat bindy = new BindyCsvDataFormat(Order.class);
		
		onException(IOException.class)
			.log("**** ERROR ****")
			.log("HEADERS => ${headers}")
			.log("BODY => ${body}")
			.to("file:C:/WEB/records/inputs2/ERROR")
		;
		
		from("file:C:/WEB/RECORDS/Input_Aggregation")
			.log(LoggingLevel.INFO, "com.pruebas", "Contenido ==> ${body}")			
			.split().tokenizeXML("order").streaming()
				.convertBodyTo(String.class)
				.to("direct:generateListString")
			.end()			
			.log("END SPLIT")	
		;
		
		from("file:C:/WEB/RECORDS/Input_Aggregation_Object")
			.log(LoggingLevel.INFO, "com.pruebas", "Contenido ==> ${body}")			
			.split().tokenizeXML("order").streaming()
				.convertBodyTo(String.class)
				.to("direct:generateListObject")
				.log("BODY -> ${body}")
			.end()
			.log("END SPLIT")			
		;
		
		/*
		 *  Genera un Array  de 12 objetos
		 * */
		from("direct:generateListObject")
			.aggregate(constant("true"), new ListAggregationStrategy())
				.completionSize(12)
				.completeAllOnStop()
			.log("END AGGREGATE | BODY => ${body}")
			.process(new BatchXMLProcessor())

		;
		
		/*
		 *  Genera un string de 12 elementos
		 * */
		from("direct:generateListString")
			.aggregate(constant("true"), new BodyAggregationStrategy())
				.completionSize(12)
				.completeAllOnStop()
			.log("END AGGREGATE | BODY => ${body}")
		;		

		from("file:C:/WEB/RECORDS/Input_Convert_SimpleSplitter")
			.log(LoggingLevel.INFO, "com.pruebas", "Contenido ==> ${body}")
			.log("CLASS BODY => ${body.class}")
			.choice()
				.when().xpath("/orders[(contains(type,'A'))]")
					.log("***** WHEN 1 | TYPE => A | CONVERT_TO() *****")
					.convertBodyTo(String.class)
					.log("CLASS BODY convertTo -> String  | ${body.class} |")
					.log("WHEN | Correct ROUTE A")
				.when().xpath("/orders[(contains(type,'B'))]")
					.log("***** WHEN 2 | TYPE => B | Splitter xpath *****")
					.to("direct:splitterTest")
					.log("WHEN | Correct ROUTE B")
				.when().xpath("/orders[(contains(type,'C'))]")
					.log("***** WHEN 3 | TYPE => B | Splitter tokenizer *****")
					.to("direct:splitterTest_tokenizer")
					.log("WHEN | Correct ROUTE C")
				.otherwise()
					.log("OTHERWISE")
					.log("WHEN | Correct ROUTE OTHERWISE")
		;
		
		from("direct:splitterTest")
			.log("Body Route Splitter => ${body}")
			.split(xpath("orders/items/order")).streaming()
				.log("Split Body => ${body}")
		;
		
		from("direct:splitterTest_tokenizer")
			.log("Body Route Splitter => ${body}")
			.split().tokenizeXML("order").streaming()
				.log("Split Body TOKENIZER XML=> ${body}")
		;
		/*
		 * Transforms CSV files a Java Object
		 * convertBodyTo(Order.class) -> convert object java with bean java
		 * unmarshal(bindy) -> Transformn csv file in object java Bindy
		 * */
		from("file:C:/WEB/RECORDS/Input_CSV")
			.log(LoggingLevel.INFO, "com.pruebas", "Contenido ==> ${body}")
			.log("CLASS BODY => ${body.class}")
//			.convertBodyTo(Order.class)
			.unmarshal(bindy)
			.log("Body Transform Bean => |no ->  ${body.getNo()} | tipo -> ${body.getTipo} | numero -> ${body.getNumero()} | Description -> ${body.getDescription()}")
			.log("WHEN | Correct ROUTE CSV")
		;
		
		from("file:C:/WEB/RECORDS/Input_Transform")
			.to("direct:setHeaders")
			.log(LoggingLevel.INFO, "com.pruebas", "Contenido ==> ${body}")
			.choice()
				.when()
					/*
					 * Realiza la transformacion de una parte del XMl entrante a 
					 * un Objecto Java mediante JAXB.
					 *  
					 * */
					.xpath("/orders[(contains(type,'A'))]")
					.log("***** WHEN 1 | TYPE => A | TRANSFORM JAXB *****")
					.setBody(xpath("/orders/items/*[6]"))
					.log("***** BODY => \n ${body} *****")
					.unmarshal(jaxb)
					.log("BODY CLASS AFTER unmarshal => ${body.class}")
					.log("OBJECT DATA | name=> ${body.getName()} | id=> ${body.getId()} | tel=> ${body.getTel()}")
					.marshal(jaxb)					
					.log("BODY AFTER marshal => ${body}")
					.log("WHEN | Correct ROUTE A")
				.when()
					/*
					 * Realiza la transformacion de objeto json que viene dentro de 
					 * una estructura CDATA a un objeto Java mediante JACKSON.
					 * */
					.xpath("/orders[(contains(type,'B'))]")
					.log("***** WHEN 2 | TYPE => B | TRANSFORM JACKSON-XML *****")				
					.setBody(xpath("/orders/contentJson/text()"))					
					.log("BODY json==> ${body}")					
					.unmarshal().json(Object_Json.class, JsonLibrary.class)
					.log("BODY CLASS AFTER unmarshal => ${body.class}")
					.log("OBJECT DATA | id=> ${body.class} | descripction=> ${body.getDescription()} | value=> ${body.getValue()} | tax=> ${body.getTax()}")
					.marshal().json(JsonLibrary.Jackson)				
					.log("BODY AFTER marshal => ${body}")				
					.log("WHEN | Correct ROUTE B")
				.when()
				/*
				 * Prueba de generacion JSON - XML
				*/
					.xpath("/orders[(contains(type,'C'))]")
					.log("***** WHEN 3 | TYPE => C | TRANSFORM XML-TO-JSON *****")			
					.setBody(xpath("/orders/items/*[1]"))
					.log("Body XML  ==> ${body}")
					.marshal(xmlJsonFormat)
					.log("Body Transform JSON  ==> ${body}")					
					.unmarshal(xmlJsonFormat)
					.log("Body Transform XML ==> ${body}")					
					.log("WHEN | Correct ROUTE C")
				.otherwise()
					.log("OTHERWISE QUEUE");
		
	}
}
