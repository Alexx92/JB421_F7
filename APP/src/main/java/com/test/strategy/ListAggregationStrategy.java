package com.test.strategy;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.builder.xml.XPathBuilder;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import com.test.object.Object_A;

public class ListAggregationStrategy implements AggregationStrategy{
	@Override
	public Exchange aggregate(Exchange oldExchange , Exchange newExchange) {
		List<Object_A> list = new ArrayList<Object_A>();
		
		String no = XPathBuilder.xpath("/order/id").evaluate(newExchange.getContext(), newExchange.getIn().getBody());
		String type = XPathBuilder.xpath("/order/name").evaluate(newExchange.getContext(), newExchange.getIn().getBody());
		String numero = XPathBuilder.xpath("/order/tel").evaluate(newExchange.getContext(), newExchange.getIn().getBody());

		if (no.isEmpty()) {
			no = XPathBuilder.xpath("/order/@id").evaluate(newExchange.getContext(), newExchange.getIn().getBody());
			type = XPathBuilder.xpath("/order/@name").evaluate(newExchange.getContext(), newExchange.getIn().getBody());
			numero = XPathBuilder.xpath("/order/@tel").evaluate(newExchange.getContext(), newExchange.getIn().getBody());
		}
		
		if (oldExchange == null){
			list.add(new Object_A(no,type,numero));
			newExchange.getIn().setBody(list);
			return newExchange;
        }
		list = (List<Object_A>) oldExchange.getIn().getBody();
		list.add(new Object_A(no, type, numero));		
		
		newExchange.getIn().setBody(list);
		return newExchange;
	}
}
