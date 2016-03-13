package net.steepout.plugin.such;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.jline.internal.Log;

import net.steepout.plugin.such.event.ChatPacketRecievedEvent;

public class Client{
	ClientType type;
	Socket connection;
	String username;
	String password;
	String uuid;
	ClientProtocol protocol;
	private Client(){
		
	}
	public Client(ClientType type,Socket connection,String username,String password) throws IOException{
		this.type = type;
		this.connection = connection;
		this.username = username;
		this.password = password;
		protocol = new ClientProtocol(connection); //create protocol controller
		uuid = UUID.randomUUID().toString().replace("-", ""); //random uuid
	}
	public String getUUID(){
		return uuid;
	}
	public void accessAccount(ServerConfiguration config) throws SecurityException, IOException{//access the user account in this method
		Log.info("accessing client "+type.toString());
		if(type.equals(ClientType.MINECRAFT_SERVER)){ //when the connected object is a server.authme is unable to access it
			if(!AuthAPI.checkServerConnection(connection.getInetAddress(), username, password,config)){
				throw new SecurityException("account check failed");
			}
		}else{
			if(!AuthAPI.checkClientConnection(username, password,config)){
				throw new SecurityException("account check failed");
			}
		}
		ChatPacket packet = new ChatPacket(); //send the access uuid to client
		packet.put("type", "uuid");
		packet.put("content", getUUID());
		packet.put("server", Bukkit.getServer().getServerName());
		protocol.sendPacket(packet); //send packet out
	}
	public ChatPacket waitForPacket() throws IOException,SecurityException{
		boolean flag = true;//use this to control the loop instead of 'goto' which is not existed
		ChatPacket packet = null;
		while(flag){
			flag = false;
			packet = protocol.readPacket(); //read a raw packet for upper level to parse
			if(packet.get("uuid")==null||!packet.get("uuid").equals(getUUID())){//if the uuid is incorrect.the connection must have some exception
				throw new SecurityException("UUID Access failed");
			}
			ChatPacketRecievedEvent event = new ChatPacketRecievedEvent(packet,this);
			PluginMain.server.getPluginManager().callEvent(event); //fire event
			if(event.isCancelled()){
				flag = true; //abandon current packet and wait for next
			}
		}
		return packet;
	}
	public boolean isNormalClient(){
		return !(type.equals(ClientType.MINECRAFT_SERVER));
	}
	public static Client virtualClient(){
		return new VirtualClient(); //a almost void 'client'
	}
	private static class VirtualClient extends Client{}
	public static synchronized boolean isVirtualClient(Client client){
		return (client instanceof VirtualClient);
	}
}