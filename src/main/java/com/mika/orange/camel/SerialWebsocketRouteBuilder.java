package com.mika.orange.camel;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.websocket.WebsocketComponent;

import com.mika.orange.model.LatchHolder;
import com.mika.orange.model.SerialConnection;

public class SerialWebsocketRouteBuilder extends RouteBuilder {
	private int port;
	private SerialConnection serialConnection;
	private LatchHolder latchHolder;
	
	public SerialWebsocketRouteBuilder(int port, SerialConnection serialConnection, LatchHolder latchHolder){
		this.port = port;
		this.serialConnection = serialConnection;
		this.latchHolder = latchHolder;
	}

	@Override
	public void configure() throws Exception {
		// setup Camel web-socket component on the port we have defined
        WebsocketComponent wc = getContext().getComponent("websocket", WebsocketComponent.class);
        wc.setPort(port);
        wc.setStaticResources("classpath:.");
        
        //websocket to serial
        fromF("websocket://0.0.0.0:%d/arduino",port).routeId("WS.TO.ARDUINO")
        .log(LoggingLevel.INFO,">> From WS : ${body}")
        .bean(latchHolder, "checkStop")
        .bean(serialConnection, "send");
                
       
        //serial to websocket
        fromF("direct:out").routeId("ARDUINO.TO.WS")        		
            .log(LoggingLevel.INFO,">> From Arduino : ${body}")
            .toF("websocket://0.0.0.0:%d/arduino?sendToAll=true",port);
		
	}
	
	protected static final String ROUTE_TO_WEBSOCKET = "direct:out";

}
