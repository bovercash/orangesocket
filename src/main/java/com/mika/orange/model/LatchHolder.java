package com.mika.orange.model;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

public class LatchHolder {
	private static final Logger log = Logger.getLogger(LatchHolder.class);
	private CountDownLatch latch;
	
	public LatchHolder(CountDownLatch latch){
		this.latch = latch;
	}
	
	public void checkStop(String message){
		if(message == null || message == ""){
			return;
		}
		int c = (int)message.charAt(0);
		log.info("CharAt(0) is " + c);
		if(message.length() == 1 && message.charAt(0) == (char)4){
			
			//EOT
			dropLatch();
		}
	}
	
	protected void dropLatch(){
		latch.countDown();
	}

}
