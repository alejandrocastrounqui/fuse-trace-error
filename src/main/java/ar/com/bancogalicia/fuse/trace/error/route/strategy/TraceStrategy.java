package ar.com.bancogalicia.fuse.trace.error.route.strategy;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import java.util.ArrayList;
import java.util.List;

public class TraceStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        Message newExchangeIn = newExchange.getIn();
        Object newExchangeInBody = newExchangeIn.getBody();
        List collectorList = null;
        if (oldExchange == null) {
            collectorList = new ArrayList<String>();
            collectorList.add(newExchangeInBody);
            newExchangeIn.setBody(collectorList);
            return newExchange;
        } else {
            Message oldExchangeIn = oldExchange.getIn();
            collectorList = oldExchangeIn.getBody(List.class);
            collectorList.add(newExchangeInBody);
            return oldExchange;
        }
    }

}
