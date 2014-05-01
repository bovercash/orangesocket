package com.mika.orange.camel;

import org.apache.camel.ProducerTemplate;
import org.apache.log4j.Logger;

import com.mika.orange.model.MessageListener;

public class SerialMessageListener implements MessageListener {
	private static final Logger log = Logger.getLogger(SerialMessageListener.class);
	private Camel camel;
	
	public SerialMessageListener(Camel camel){
		this.camel = camel;
	}

	@Override
	public void handleMessage(String message) {
		if(camel.isRunning()){
			ProducerTemplate template = camel.getCamelContext().createProducerTemplate();
			template.sendBody(SerialWebsocketRouteBuilder.ROUTE_TO_WEBSOCKET, message);
			log.info(String.format("%s -> \"%s\"",SerialWebsocketRouteBuilder.ROUTE_TO_WEBSOCKET, message));
		}
	}

}
