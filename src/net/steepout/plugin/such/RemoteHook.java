package net.steepout.plugin.such;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.jline.internal.Log;

import net.steepout.plugin.such.api.TempAuthoriztionLauncher;

public class RemoteHook {
	boolean enable = false;
	String address;
	Integer port;
	String name;
	String password;
	ClientProtocol protocol;
	Thread thread;
	String uuid;
	String remoteName;
	Socket conn;
	boolean established; // to protect the connection ,it's very necessary to
							// check if the hook is established

	public RemoteHook(ServerConfiguration config) {
		enable = config.isDoHook();
		address = config.getHookIP();
		port = config.getHookPort();
		name = config.getHookName();
		password = config.getHookPassword();
	}

	public void attemptHook() {
		if (!enable)
			return;
		Log.info("Trying to hook on another chat server...");
		TempAuthoriztionLauncher launcher = new TempAuthoriztionLauncher(ClientType.MINECRAFT_SERVER, name, password,
				address, port);
		try {
			launcher.attemptConnect();
			protocol = new ClientProtocol(launcher.getConnection());
			ChatAgency.instance.hookObj = this;
			ChatAgency.instance.hooked = true; // register in chat agency
		} catch (Exception e) {
			Log.error("Failed to hook on server : " + e.toString(), "Server info[" + address + ":" + port + "]");
			return;
		}
		uuid = launcher.getUUID();
		remoteName = launcher.getServerName();
		Log.info("Hooked!", "Server info[" + address + ":" + port + "] uuid:"+uuid);
		established = true;
		startListening();
	}

	public boolean isEstablished() {
		return this.established;
	}

	public synchronized void sendPacket(ChatPacket packet) throws IOException {
		if(packet.get("sender_type")!=null&&packet.get("sender_type").equals("1")){
			if(packet.get("server").equals(remoteName)){
				return;
			}
		}
		packet.put("uuid", uuid);
		protocol.sendPacket(packet);
	}

	public void close() {
		established = false;
		try {
			protocol.in.close(); // close input stream means close the
									// connection (not very exact)?
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		protocol = null;
		thread.interrupt();
	}

	public void startListening() {
		thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					while ((!Thread.interrupted())&&established) {
						ChatPacket ping = new ChatPacket();
						ping.put("type", "ping");
						ping.put("uuid", uuid);
						ChatPacket packet = protocol.readPacket();
						switch (packet.get("type")) {
						case "chat":
							Log.info("reading packet of chat ["+packet.get("sender_type")+":"+packet.get("server")+"]");
							if(packet.get("sender_type")!=null&&packet.get("sender_type").equals("1")){
								if(packet.get("server").equalsIgnoreCase(Bukkit.getServer().getServerName())) continue;
							}
							ChatAgency.instance.appendBroadcastHookChatMessage(packet.get("message"), packet);
							break;
						case "kick":
							Log.info("kicked by remote server : "+packet.get("reason"));
							close();
							break;
						case "ping":
							protocol.sendPacket(ping);
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					established = false;
					return;
				}
				close();
			}

		});
		thread.start();
	}
}