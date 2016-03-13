package net.steepout.plugin.such.api;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import net.steepout.plugin.such.ChatPacket;
import net.steepout.plugin.such.ClientProtocol;
import net.steepout.plugin.such.ClientType;

public class TempAuthoriztionLauncher{
	ClientType type;
	String username;
	String password;
	String address;
	Integer port;
	String uuid;
	Socket socket;
	String remote;
	public TempAuthoriztionLauncher(ClientType type,String username,String password,String address,Integer port){
		this.type = type;
		this.username = username;
		this.password = password;
		this.address = address;
		this.port = port;
	}
	public void attemptConnect() throws UnknownHostException, IOException{
		socket = new Socket(InetAddress.getByName(address),port);
		socket.getOutputStream().write(new byte[]{'m','u','c','p'}); //default access header
		ClientProtocol util = new ClientProtocol(socket);
		ChatPacket packet = new ChatPacket();
		packet.put("type", type.equals(ClientType.MINECRAFT_SERVER)?"server_hook":"client_login");
		packet.put("username", username);
		packet.put("pwd", password);
		packet.put("client_name", type.toString());
		util.sendPacket(packet);
		packet = util.readPacket();
		accessVersion(packet);
		util.sendPacket(ChatPacket.getVersionPacket()); //send a packet of current version
		packet = util.readPacket();
		if(packet.get("type").equalsIgnoreCase("kick")){
			
		}else{
			uuid = packet.get("content");
			remote = packet.get("server");
		}
	}
	public Socket getConnection(){
		return socket;
	}
	public String getUUID(){
		return uuid;
	}
	public String getServerName(){
		return remote;
	}
	public void accessVersion(ChatPacket packet){
		
	}
}