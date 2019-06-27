package com.test.bean;

import org.apache.camel.Exchange;

import com.test.object.Order;

public class BeanOrder {
	
	public Order  createOrderObject(Exchange exchange) {		
		Order order = new Order();
		order.setNo(exchange.getProperty("NO", String.class));
		order.setNumero(exchange.getProperty("NRO", Integer.class));
		order.setTipo(exchange.getProperty("TYPE", String.class));
		order.setDescription(exchange.getProperty("DESCRIPTION", String.class));		
		return order;
		
	}
}
