package com.test.object;

import org.apache.camel.Converter;
import org.apache.camel.Exchange;
import org.apache.camel.TypeConverter;
import org.apache.camel.component.file.GenericFile;

@Converter
public class OrderTypeConverter {

	@Converter
	public static <T>Order toOrder(GenericFile<T> data, Exchange exchange) {
		TypeConverter converter = exchange.getContext().getTypeConverter();
		String s = converter.convertTo(String.class, data);
		if (s == null) {
			throw new IllegalArgumentException("data is invalid");
		}
		String[] split = s.split(",");
		String idAsString = split[0];
		String name = split[1];
		String telAsint = split[2];
		String desAsint = split[3];		
		int tel2 = converter.convertTo(Integer.class, telAsint.trim());		
		return new Order(idAsString, name, tel2, desAsint);
	}
}
