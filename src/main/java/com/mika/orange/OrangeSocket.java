package com.mika.orange;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.mika.orange.camel.Camel;
import com.mika.orange.camel.SerialMessageListener;
import com.mika.orange.camel.SerialWebsocketRouteBuilder;
import com.mika.orange.model.LatchHolder;
import com.mika.orange.model.RuntimeConfiguration;
import com.mika.orange.model.SerialConnection;
import com.mika.orange.model.SerialConnectionImpl;

public class OrangeSocket {
	private static final Logger log = Logger.getLogger(OrangeSocket.class);
	private Camel camel;
	private SerialConnection serialConnection;
	
	public OrangeSocket(CountDownLatch countDownLatch) throws Exception{
		log.info("Starting Up...");
		RuntimeConfiguration config = new RuntimeConfiguration();
		config.setConnectionTimeout(2000);
		config.setBaudeRate(9600);
		config.setComPortNames(new String[]{"COM5"});
		config.setWebsocketPort(8080);

		serialConnection = new SerialConnectionImpl(config);
		serialConnection.connect();
		
		LatchHolder latchHolder = new LatchHolder(countDownLatch);

		SerialWebsocketRouteBuilder websocketRoute = 
				new SerialWebsocketRouteBuilder(config.getWebsocketPort(),serialConnection,latchHolder);

		camel = new Camel(websocketRoute);
		camel.start();
		SerialMessageListener messageListener = new SerialMessageListener(camel);
		serialConnection.addMessageListener(messageListener);
		log.info("Ready");
	}
	
	public void shutdown(){
		log.info("Shutting Down...");
		camel.stop();
		serialConnection.disconnect();		
	}

	public static void main(String[] args) throws Exception {
		BasicConfigurator.configure();
		log.getRootLogger().setLevel(Level.DEBUG);
		CountDownLatch countDownLatch = new CountDownLatch(1);
		OrangeSocket orange = new OrangeSocket(countDownLatch);
		countDownLatch.await(10l, TimeUnit.MINUTES);  //use countDownLatch.await(); to wait forever		
		orange.shutdown();
		log.info("Program Haulting Now");
	}

}
