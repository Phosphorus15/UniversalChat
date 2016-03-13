package net.steepout.plugin.such;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

public class ChatAgency extends ChatListener { // the agency of global chat
												// system

	ChatServer chatserver;

	Server gameserver;

	BroadcastThread broadcast;

	static ChatAgency instance;

	Formatter formatter;

	String name;
	
	RemoteHook hookObj;
	
	boolean hooked = false;

	private ChatAgency(ChatServer chatserver, Server gameserver, ServerConfiguration config) {
		this.chatserver = chatserver;
		this.gameserver = gameserver;
		broadcast = new BroadcastThread();
		formatter = new Formatter();
	}

	public static Listener initialAgency(ServerConfiguration config) {
		ChatAgency agency = new ChatAgency(PluginMain.chatserver, PluginMain.server, config);
		instance = agency;
		agency.broadcast.start();
		return agency;
	}

	@Override
	@EventHandler(priority = EventPriority.LOW) // low level to avoid 'tell&say'
												// command
	@SuppressWarnings("all")
	public void onPlayerChat(PlayerChatEvent event) {
		// TODO Auto-generated method stub
		if (event.isCancelled())
			return;
		appendChatMessage(event.getMessage(), event.getPlayer());
	}

	public void close() {
		broadcast.interrupt();
	}

	public void appendChatMessage(Object message, Object sender) {
		if (message instanceof ChatPacket) {
			if (((Client) sender).type.equals(ClientType.MINECRAFT_SERVER)) {
				if (((ChatPacket) message).get("server").equals(Bukkit.getServer().getName())) {
					return; // this message is send by current server,no need to
							// broadcast again
				}
			}
			appendBroadcastClientChatMessage(null, message, (Client) sender);
		} else if (sender instanceof Player) {
			appendBroadcastGamePlayerChatMessage(message.toString(), (Player) sender);
		} else {
			return;
		}
	}

	protected synchronized void appendBroadcastClientChatMessage(String message, Object specified, Client sender) {
		broadcast.addTask(new Task(1, message, specified, sender.username, sender, null));
	}
	
	protected synchronized void appendBroadcastHookChatMessage(String message, Object specified) {
		broadcast.addTask(new Task(1, message, specified, "", Client.virtualClient(), null));
	}

	protected synchronized void appendBroadcastGamePlayerChatMessage(String message, Player sender) {
		broadcast.addTask(
				new Task(2, message, ChatPacket.wrapBroadcast(message, sender), sender.getName(), null, sender));
	}

	protected void broadcast(ChatPacket packet, Client client, Player player) {
		gameserver.broadcastMessage(formatter.format(packet, client, player));
	}

	protected class Task {
		int workFor; // 1=client,2=player,3=hook
		String message;
		Object specifiedObject;
		String sender;
		Client client;
		Player player;

		protected synchronized Player getPlayer() {
			return player;
		}

		protected synchronized void setPlayer(Player player) {
			this.player = player;
		}

		public Task(int workFor, String message, Object specified, String sender, Client client, Player player) {
			this.workFor = workFor;
			this.message = message;
			this.specifiedObject = specified;
			this.sender = sender;
			this.client = client;
			this.player = player;
		}

		protected synchronized Client getClient() {
			return client;
		}

		protected synchronized void setClient(Client client) {
			this.client = client;
		}

		public String getSender() {
			return sender;
		}

		public void setSender(String sender) {
			this.sender = sender;
		}

		public int getWorkFor() {
			return workFor;
		}

		public void setWorkFor(int workFor) {
			this.workFor = workFor;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public Object getSpecifiedObject() {
			return specifiedObject;
		}

		public void setSpecifiedObject(Object specifiedObject) {
			this.specifiedObject = specifiedObject;
		}
	}

	private class BroadcastThread extends Thread {
		List<Task> tasks = Collections.synchronizedList(new LinkedList<Task>());

		public void run() {
			while (!this.isInterrupted()) {
				while (tasks.size() == 0) {
					if (this.isInterrupted()) {
						return;
					}
				}
				Log.info("task started");
				Task task = tasks.get(0);
				ChatPacket packet = (ChatPacket) task.getSpecifiedObject();
				/*
				 * if (packet == null || !(packet instanceof ChatPacket)) {
				 * packet = ChatPacket.wrapBroadcast(task.getMessage(),
				 * task.getSender()); } else { packet.remove("uuid");// leave
				 * the uuid in chat packet is a // dangerous action }
				 * if(task.workFor==1)
				 * gameserver.broadcastMessage(task.getMessage());
				 */ // deprecated: the broadcast task shouldn't be finish by such
					// method
				if (task.getWorkFor() == 1||task.getWorkFor()==3) {
					broadcast(packet, task.getClient(), task.getPlayer()); // don't
																			// mind
																			// whether
																			// client
																			// or
																			// player
																			// is
																			// null,because
																			// we
																			// can
																			// process
																			// it
																			// later
				}
				chatserver.sendToAllClientUnsafe(packet); // send chat packet to
															// client
				
				if(!(task.getWorkFor() == 3))
					if(hooked){
						try {
							hookObj.sendPacket(packet);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				tasks.remove(0);
				task = null;
			}
		}

		public synchronized void addTask(Task task) {
			tasks.add(task);
		}
	}

}