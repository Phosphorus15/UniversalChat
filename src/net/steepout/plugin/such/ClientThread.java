package net.steepout.plugin.such;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.jline.internal.Log;

import net.md_5.bungee.api.ChatColor;
import net.steepout.plugin.such.event.ClientAccidentallyDisconnectEvent;
import net.steepout.plugin.such.event.ClientDisconnectEvent;
import net.steepout.plugin.such.event.ClientEstablishedEvent;

public class ClientThread extends Thread {
	ChatServer creator;
	Socket connection;
	ClientProtocol protocol;
	Client client;
	boolean death = false;

	public ClientThread(ChatServer creator, Socket connection) {
		this.creator = creator;
		this.connection = connection;
		try {
			protocol = new ClientProtocol(connection);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			try {
				byte[] access = new byte[4];
				connection.getInputStream().read(access);
				if(!Arrays.equals(access, new byte[]{'m','u','c','p'})){ //fixed access code
					startRecycle(false);
					return;
				}
				ChatPacket packet = protocol.readPacket(); // try to read the
															// access packet
				String type = packet.get("type");
				if (type == null || !(type.equals("client_login") || type.equals("server_hook"))) {
					throw new SecurityException("incorrected protocol");
				}
				// then we should create a instance of client...
				// this is a important action
				setName("ChatClient_" + packet.get("username"));
				client = new Client(ClientType.parsePacket(packet), connection, packet.get("username"),
						packet.get("pwd"));
				creator.addClientUnsafe(client);// register it in client list
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				try {
					protocol.sendPacket(ChatPacket.wrapKick(e.getMessage())); // reject
																				// 'politely'
				} catch (IOException e1) {
					// TODO Auto-generated catch block
				}
				ClientAccidentallyDisconnectEvent event = new ClientAccidentallyDisconnectEvent(
						ClientAccidentallyDisconnectEvent.Reason.CLIENT_CREATION_FAILED, null, e); // the
																									// client
																									// isn't
																									// created
																									// yet,so
																									// it
																									// must
																									// be
																									// null
				PluginMain.server.getPluginManager().callEvent(event); // fire
																		// event
				startRecycle(false); // because of the client isn't created,we
										// don't needs to release it
				return;// end up the thread
			} catch (IOException e) {
				ClientAccidentallyDisconnectEvent event = new ClientAccidentallyDisconnectEvent(
						ClientAccidentallyDisconnectEvent.Reason.CLIENT_CREATION_FAILED, null, e); // the
																									// client
																									// isn't
																									// created
																									// yet,so
																									// it
																									// must
																									// be
																									// null
				PluginMain.server.getPluginManager().callEvent(event); // fire
																		// event
				startRecycle(false); // because of the client isn't created,we
										// don't needs to release it
				return;// end up the thread
			}
			try {
				try {
					protocol.sendPacket(ChatPacket.getVersionPacket()); // version
																		// exchange
					ChatPacket version = protocol.readPacket();
					boolean uns = false;
					if(Integer.parseInt(version.get("min_support"), 16)>ChatPacket.max_support){
						uns = true;
					}else if(Integer.parseInt(version.get("max_support"), 16)<ChatPacket.min_support){
						uns = true;
					}
					if(uns)
						throw new RuntimeException("unsupported protocol version "+version.get("version"));
				} catch (RuntimeException e) {
					try {
						protocol.sendPacket(ChatPacket.wrapKick(e.getMessage())); // reject
																					// 'politely'
					} catch (IOException e1) {
						// TODO Auto-generated catch block
					}
					ClientAccidentallyDisconnectEvent event = new ClientAccidentallyDisconnectEvent(
							ClientAccidentallyDisconnectEvent.Reason.UNSAFE_CLIENT, client, e);
					PluginMain.server.getPluginManager().callEvent(event); // fire
																			// event
					startRecycle(true);
					return;
				}
				try {
					//Log.info("starting to access...");
					client.accessAccount(creator.config);
				} catch (SecurityException e) {
					try {
						protocol.sendPacket(ChatPacket.wrapKick(e.getMessage())); // reject
																					// 'politely'
					} catch (IOException e1) {
						// TODO Auto-generated catch block
					}
					ClientAccidentallyDisconnectEvent event = new ClientAccidentallyDisconnectEvent(
							ClientAccidentallyDisconnectEvent.Reason.ACCESS_FAILED, client, e);
					PluginMain.server.getPluginManager().callEvent(event); // fire
																			// event
					startRecycle(true);
					return;
				}
				// after the information swap protocol , the chat client is
				// created successfully
				ClientEstablishedEvent event = new ClientEstablishedEvent(client);
				PluginMain.server.getPluginManager().callEvent(event); // fire
																		// event
				if (event.isCancelled()) { // although no one will cancel this
											// event normally,I'm insisted to do
											// this
					client.protocol.sendPacket(ChatPacket.wrapKick("Connection Cancelled by server"));
					ClientDisconnectEvent event2 = new ClientDisconnectEvent(client,
							ClientDisconnectEvent.Reason.CANCELLED_BY_EVENT_HANDLER);
					PluginMain.server.getPluginManager().callEvent(event2); // fire
																			// event
					startRecycle(true);
					return;
				}
				ChatPacket temp = new ChatPacket();
				boolean naturally = false;
				//Log.info("started to wait for next step");
				while (!this.isInterrupted()) {
					temp = client.waitForPacket();
					switch (temp.get("type")) {
					case "chat":
						Log.info("message recieved : " + temp.get("message"));
						ChatAgency.instance.appendChatMessage(temp, client);// append
																			// to
																			// pre-process
																			// queue
						break;
					case "private_chat":
						break;
					case "skip":
						continue;
					case "disconnect":
						naturally = true;
						Log.info("Client disconnected : "+temp.get("name")+" - "+temp.get("uuid")+" ("+temp.get("client_name")+")");
						Bukkit.getServer().broadcastMessage(ChatColor.GREEN+temp.get("name")+" diconnected from server");
						startRecycle(true);
						return; // leave the loop to the default exit case
					case "ping":
						death = false;
						break;
					default: // if the type cannot be recognized ,the client is
								// unsafe
						throw new SecurityException("unsafe client - unrecognized packet type");
					}
				}
				// if the thread was ended by server,run recycle system
				client.protocol.sendPacket(ChatPacket.wrapKick("Disconnect"));
				ClientDisconnectEvent event2 = new ClientDisconnectEvent(client, naturally
						? ClientDisconnectEvent.Reason.ENDED_BY_CLIENT : ClientDisconnectEvent.Reason.ENDED_BY_SERVER);
				PluginMain.server.getPluginManager().callEvent(event2); // fire
																		// event
				startRecycle(true);
				return;
			} catch (IOException e) {
				ClientAccidentallyDisconnectEvent event = new ClientAccidentallyDisconnectEvent(
						ClientAccidentallyDisconnectEvent.Reason.IO_ERROR, client, e);
				PluginMain.server.getPluginManager().callEvent(event); // fire
																		// event
				startRecycle(true);
				return;// end up the thread
			} catch (SecurityException e) {
				Log.error("connection ended due to unsafe condition : "+e.getMessage());
				ChatPacket packet = new ChatPacket();
				packet.put("type", "type");
				packet.put("reason", "unsafe");
				protocol.sendPacket(packet);
				ClientAccidentallyDisconnectEvent event = new ClientAccidentallyDisconnectEvent(
						ClientAccidentallyDisconnectEvent.Reason.UNSAFE_CLIENT, client, e);
				PluginMain.server.getPluginManager().callEvent(event); // fire
																		// event
				startRecycle(true);
				return;
			}
		} catch (Exception e) {
			Log.error("connection ended due to unknown exception", e);
			ClientAccidentallyDisconnectEvent event = new ClientAccidentallyDisconnectEvent(
					ClientAccidentallyDisconnectEvent.Reason.IO_ERROR, client, e);
			PluginMain.server.getPluginManager().callEvent(event); // fire event
			startRecycle(true);
			return;
		}
	}

	protected void startRecycle(boolean registed) {
		creator.removeThreadUnsafe(this);
		try {
			connection.close(); // even though the exception was thrown,it's no
								// necessity to solve it
		} catch (IOException e1) {
		} finally {
			connection = null;
			protocol = null; // after the connection was closed , we can release
								// the protocol controller without anxiety
		}
		if (registed) {
			creator.removeClientUnsafe(client); // remove the client from server
			client = null; // release the memory allocation
		}
		System.gc(); // call garbage collector
	}
}