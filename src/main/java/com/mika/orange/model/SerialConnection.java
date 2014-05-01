package com.mika.orange.model;

public interface SerialConnection {
	
	public void connect();
	
	public void disconnect();
	
	public void send(String message);
	
	public void receive(String message);
	
	public void addMessageListener(MessageListener messageListener);

}
