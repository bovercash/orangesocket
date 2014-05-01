package com.mika.orange.model;

public class RuntimeConfiguration {
	private int baudeRate;
	private int connectionTimeout;
	private String[] comPortNames;
	private int websocketPort;
	public int getBaudeRate() {
		return baudeRate;
	}
	public void setBaudeRate(int baudeRate) {
		this.baudeRate = baudeRate;
	}
	public int getConnectionTimeout() {
		return connectionTimeout;
	}
	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}
	public String[] getComPortNames() {
		return comPortNames;
	}
	public void setComPortNames(String[] comPortNames) {
		this.comPortNames = comPortNames;
	}
	public int getWebsocketPort() {
		return websocketPort;
	}
	public void setWebsocketPort(int websocketPort) {
		this.websocketPort = websocketPort;
	}
}
