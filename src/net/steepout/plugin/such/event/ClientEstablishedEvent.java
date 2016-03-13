package net.steepout.plugin.such.event;

import fr.xephi.authme.events.CustomEvent;
import net.steepout.plugin.such.Client;

public class ClientEstablishedEvent extends CustomEvent{
	Client client;
	public ClientEstablishedEvent(Client client){
		this.client = client;
	}
	public Client getClient() {
		return client;
	}
	public void setClient(Client client) {
		this.client = client;
	}
}