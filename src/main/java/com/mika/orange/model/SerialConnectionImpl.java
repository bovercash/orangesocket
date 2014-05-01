package com.mika.orange.model;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Logger;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class SerialConnectionImpl implements SerialConnection, SerialPortEventListener {
	private static final Logger log = Logger.getLogger(SerialConnectionImpl.class);
	private RuntimeConfiguration config;
	private CommPortIdentifier portId;
	private SerialPort serialPort;
	private BufferedReader input;
	private OutputStream output;
	private List<MessageListener> messageListeners = new ArrayList<MessageListener>();
	
	public SerialConnectionImpl(RuntimeConfiguration config){
		this.config = config;
	}
	
	@Override
	public void connect() {
		log.info("checking ports...");
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			for (String portName : config.getComPortNames()) {
				if (currPortId.getName().equals(portName)) {
					portId = currPortId;
					break;
				}
			}
		}
		if (portId == null) {
			throw new RuntimeException("Could Not Establish COM Port Connection");
		}
		log.info(String.format("acquired port \"%s\", openning a connection...",portId.getName()));
		try {
			serialPort = (SerialPort) portId.open(this.getClass().getName(),
					config.getConnectionTimeout());

			serialPort.setSerialPortParams(config.getBaudeRate(),
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
			//output = serialPort.getOutputStream();

			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
			
		} catch (Exception e) {
			throw new RuntimeException("Could Not Open COM Port Connection",e);
		}
		log.info("connection acquired");
	}

	@Override
	public synchronized void disconnect() {
		log.info("disconnecting...");
		if (serialPort != null) {
			serialPort.removeEventListener();
			if(output != null){
				try{
					output.close();
				}catch(Exception e){
					//don't care
				}
			}
			serialPort.close();
		}
		log.info("disconnect complete");
	}
	
	@Override
	public void receive(String message) {
		for(MessageListener messageListener : messageListeners){
			messageListener.handleMessage(message);
		}		
	}

	@Override
	public void send(String message) {
		log.info(String.format("Sending \"%s\"",message));
		//add ETX - End of Line Character
		String msg = message + (char)3;		
		try{
			output = serialPort.getOutputStream();
			byte[] rawString = msg.getBytes();
			int rawStringSize = msg.length();
			output.write(rawString, 0, rawStringSize);
			log.info("Sending Complete");
			output.flush();
			log.info("Flush Complete");
		}catch(Exception e){
			log.warn("Could not write message to COM port",e);
		}
	}	

	@Override
	public void serialEvent(SerialPortEvent event) {
		if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				String inputLine=input.readLine();
				receive(inputLine);
			} catch (Exception e) {
				//log.debug("Could Not Interpret Serial Event",e);
			}
		}
	}
	
	@Override
	public void addMessageListener(MessageListener messageListener){
		messageListeners.add(messageListener);
	}

}
