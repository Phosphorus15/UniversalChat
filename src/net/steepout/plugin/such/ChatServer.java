package net.steepout.plugin.such;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.craftbukkit.libs.jline.internal.Log;

import net.steepout.plugin.such.event.ChatPlayerConnectingEvent;

public class ChatServer extends Thread {
	Set<Client> clients; // All connected clients
	Set<ClientThread> clientThreads; // All running client threads
	ServerSocket server; // Server interface object
	public static Object global_sync = new Object();
	ServerConfiguration config;

	private ChatServer() { // default constructor
		clients = Collections.synchronizedSet(new HashSet<Client>()); // synchronized
																		// set
																		// for
																		// safety
		clientThreads = Collections.synchronizedSet(new HashSet<ClientThread>());
	}

	public ChatServer(ServerConfiguration config) throws IOException {
		this();
		server = new ServerSocket(config.getPort()); // listen at specified port
		Log.info("Chat server created on " + config.getPort());
		this.config = config;// save configuration
		this.setName("ChatServer_thread");
	}

	@Override
	public void run() {
		while (!(PluginMain.stopped || this.isInterrupted())) {
			try {
				// Log.info("New client connecting...");
				Socket socket = server.accept(); // listen for connect
				ChatPlayerConnectingEvent event = new ChatPlayerConnectingEvent(socket); // create
																							// event
				PluginMain.server.getPluginManager().callEvent(event); // fire
																		// event
				if (event.isCancelled()) {
					socket.close();
					continue;
				}
				ClientThread thread = new ClientThread(this, socket);
				thread.start(); // Gosh ... I called "run" method there at
								// first..that's why the bug emerge..
				clientThreads.add(thread);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				if (e.getMessage().startsWith("Socket closed"))
					return;
				e.printStackTrace();
			}
		}
	}

	protected synchronized void addClientUnsafe(Client client) {
		synchronized (global_sync) {
			clients.add(client);
		}
	}

	protected synchronized void removeClientUnsafe(Client client) {
		synchronized (global_sync) {
			if (clients.contains(client)) // if contains this object
				clients.remove(client);
		}
	}

	protected synchronized void sendToAllClientUnsafe(ChatPacket packet) {
		synchronized (global_sync) { // not allows any modification when sending
										// message
			for (Client x : clients) {
				try {
					x.protocol.sendPacket(packet);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					// do not mind any exception
				}
			}
		}
	}

	protected synchronized void kickUnsafe(String username, final String reason) {
		synchronized (global_sync) {
			for (final ClientThread x : clientThreads) {
				if (x.client.username.equals(username)) {
					new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							ChatPacket packet = new ChatPacket();
							packet.put("type", "kick");
							packet.put("reason", reason);
							try {
								x.protocol.sendPacket(packet);
							} catch (IOException e) {
								// TODO Auto-generated catch block
							}
							x.startRecycle(true);
						}
					}).start();
				}
			}
		}
	}

	public void shutdown() {// shutdown chat server
		this.interrupt();
		synchronized (global_sync) {
			for (ClientThread x : clientThreads) {
				x.interrupt(); // stop client threads
				x.startRecycle(true);
			}
		}
		try {
			server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		server = null;
	}

	public Runnable getScheduleObject() {
		return new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.info("sending ping for response...");
				ping();
			}

		};
	}
	
	protected synchronized void removeThreadUnsafe(Thread thread){
		clientThreads.remove(thread);
	}

	public synchronized void ping() {
		synchronized (global_sync) {
			ChatPacket packet = new ChatPacket();
			packet.put("type", "ping");// a packet require the response of
										// client
			ClientThread x = null;
			for (int size=0;size!=clientThreads.size();size++) {
				x = (ClientThread) clientThreads.toArray()[size];
				if (x != null && x.death) {
					try {
						if (x.client != null)
							Log.info(x.client.username + " connection timed out");

						x.interrupt(); // clear death client
						x.startRecycle(true);
					} catch (Exception e) {
						
					}
				} else {
					x.death = true;
					try {
						x.protocol.sendPacket(packet);
					} catch (Exception e) {
						// TODO Auto-generated catch block
					}
				}
			}
		}
	}
}