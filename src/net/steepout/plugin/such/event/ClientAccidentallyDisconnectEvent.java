package net.steepout.plugin.such.event;

import fr.xephi.authme.events.CustomEvent;
import net.steepout.plugin.such.Client;

public class ClientAccidentallyDisconnectEvent extends CustomEvent{
	public enum Reason{
		CLIENT_CREATION_FAILED,
		UNSAFE_CLIENT,
		IO_ERROR,
		ACCESS_FAILED
	}
	Reason reason;
	Client client;
	Object specifiedContent;
	public ClientAccidentallyDisconnectEvent(Reason reason,Client client,Object sp){
		this.reason = reason;
		this.client = client;
		this.specifiedContent = sp;
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
	public Object getSpecifiedContent() {
		return specifiedContent;
	}
	public void setSpecifiedContent(Object specifiedContent) {
		this.specifiedContent = specifiedContent;
	}
	@Deprecated
	public final void setCancelled(boolean cancelled){
		
	}
}