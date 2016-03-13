package net.steepout.plugin.such.event;

import fr.xephi.authme.events.CustomEvent;
import net.steepout.plugin.such.ChatPacket;
import net.steepout.plugin.such.Client;

public class ChatPacketRecievedEvent extends CustomEvent{
	ChatPacket packet;
	Client client;
	public ChatPacketRecievedEvent(ChatPacket packet,Client client){
		this.packet = packet;
		this.client = client;
	}
	public ChatPacket getPacket() {
		return packet;
	}
	public void setPacket(ChatPacket packet) {
		this.packet = packet;
	}
	public Client getClient() {
		return client;
	}
	public void setClient(Client client) {
		this.client = client;
	}
}