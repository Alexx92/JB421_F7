package com.test.route;

import org.apache.camel.builder.RouteBuilder;

import com.test.bean.BeanPrintData3;

public class ErrorHandling_Route extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		
		errorHandler(defaultErrorHandler()
				.log("-----> ERROR errorHandler(defaultErrorHandler()) IN JAVA ROUTE <-----")
		);
		
		
		onException(NumberFormatException.class)
			.log("-----> ERROR onException(NumberFormatException.class)  <-----")
			.to("direct:log")
			.handled(true)
		;
		
		/*
		 * Definiciones para tratamiento de ERRORES mediante try/catch
		 * */
		from("file:C:/WEB/RECORDS/Input_RouteTryCatch")
			.log("-----> INPUT FILE => ${body}")
			.doTry()
				.bean(BeanPrintData3.class, "printTransformExceptionA")
				.log("*** PASS BEAN INCORRECT ***")
			.doCatch(NumberFormatException.class)
				.log("***  CATCH NumberFormatException.class  ***")
//			.doFinally()
//				.log("***  DO FINALLY  ***")
			.endDoTry()
			.log("*****  END SERVICE  *****")
		;		
		
		/*
		 * Definiciones para tratamiento de ERRORES mediante ErrorHandler
		 * */
		
		from("file:C:/WEB/RECORDS/Input_RouteIOException")
			.log("-----> INPUT FILE => ${body}")
			.setProperty("TYPE", xpath("/orders/type/text()"))
			.choice()
				.when().xpath("/orders/type/text() = 'A'")
					.log("***** WHEN 1 | TYPE => A *****")
					.bean(BeanPrintData3.class, "printTransformExceptionA")
					.log("*** PASS BEAN INCORRECT ***")
				.when(xpath("/orders/type/text() = 'B'"))
					.log("***** WHEN 2 | TYPE => B *****")
					.to("direct:testSecondError")
					.log("*** PASS BEAN ROUTE INCORRECT ***")
				.when(constant("${exchangeProperty.TYPE}").contains('C'))
					.log("***** WHEN 3 | TYPE => C *****")
					.bean(BeanPrintData3.class,"printTransformExceptionB")
					.log("*** PASS BEAN CORRECT ***")
				.otherwise()
					.log("***** OTHERWISE *****")
			.log("*****  END SERVICE  *****")
		;
		
		/*
		 * Definiciones para tratamiento de ERRORES mediante ErrorHandler
		 * */
		from("file:C:/WEB/RECORDS/Input_RouteErrorHandler")
			.errorHandler(defaultErrorHandler()
					.log("-----> ERROR | defaultErrorHandler() in ROUTE <-----")
					.maximumRedeliveries(5)
					)
			.log("-----> INPUT FILE => ${body}")
			.setProperty("TYPE", xpath("/orders/type/text()"))
			.choice()
				.when().xpath("/orders/type/text() = 'A'")
					.log("***** WHEN 1 | TYPE => A *****")
					.bean(BeanPrintData3.class, "printTransformExceptionA")
					.log("*** PASS BEAN INCORRECT ***")
				.when(xpath("/orders/type/text() = 'B'"))
					.log("***** WHEN 2 | TYPE => B *****")
					.to("direct:testSecondError")
					.log("*** PASS BEAN ROUTE INCORRECT ***")
				.when(xpath("/orders/type/text() = 'C'"))
					.log("***** WHEN 3 | TYPE => C *****")
					.bean(BeanPrintData3.class,"printTransformExceptionB")
					.log("*** PASS BEAN CORRECT ***")
				.otherwise()
					.log("***** OTHERWISE *****")
			.log("*****  END SERVICE  *****")
		;
		
		from("direct:log")
			.log("********* LOG ROUTE **********")
			.log("-> BODY <- ${body}")
			.log("********* END LOG ROUTE **********")
		;
		
		from("direct:testSecondError")
			.errorHandler(
					deadLetterChannel("direct:log")
					.log("--> EXECUTE deadLetterChannel(\"direct:log\") <--")
					.maximumRedeliveries(3)
			 )
			
			.log("********* ROUTE LOG Second ERROR **********")
			.bean(BeanPrintData3.class, "printTransformExceptionA")

		;
//		.doTry()
//		.doCatch(NumberFormatException.class)
//			.log("¡¡¡ CATCH => NumberFormatException.class !!!")
//		.doFinally()
	}

}
