package net.steepout.plugin.such.event;

import fr.xephi.authme.events.CustomEvent;
import net.steepout.plugin.such.Client;

public class ClientDisconnectEvent extends CustomEvent{
	Client client;
	Reason reason;
	public static enum Reason{
		CANCELLED_BY_EVENT_HANDLER,
		ENDED_BY_SERVER,
		ENDED_BY_CLIENT
	}
	public ClientDisconnectEvent(Client client,Reason reason){
		this.client = client;
		this.reason = reason;
	}
	public Reason getReason() {
		return reason;
	}
	public void setReason(Reason reason) {
		this.reason = reason;
	}
	public Client getClient() {
		return client;
	}
	public void setClient(Client client) {
		this.client = client;
	}
	@Deprecated
	public final void setCancelled(boolean cancelled){
		
	}
}