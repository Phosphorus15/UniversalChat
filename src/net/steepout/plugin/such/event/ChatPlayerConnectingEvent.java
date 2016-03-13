package net.steepout.plugin.such.event;

import java.net.InetAddress;
import java.net.Socket;

import fr.xephi.authme.events.CustomEvent;

public class ChatPlayerConnectingEvent extends CustomEvent{
	Socket connection;
	InetAddress remoteAddress;
	public ChatPlayerConnectingEvent(Socket conn){
		super();
		this.connection = conn;
		this.remoteAddress = conn.getInetAddress();
	}
	public Socket getConnection() {
		return connection;
	}
	public void setConnection(Socket connection) {
		this.connection = connection;
	}
	public InetAddress getRemoteAddress() {
		return remoteAddress;
	}
	public void setRemoteAddress(InetAddress remoteAddress) {
		this.remoteAddress = remoteAddress;
	}
}