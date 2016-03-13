package net.steepout.plugin.such;

import java.util.Date;

import org.bukkit.entity.Player;

public class Formatter { // message formation system
	static ServerConfiguration config;

	public static void setConfiguration(ServerConfiguration config) {
		Formatter.config = config;
	}

	public String format(ChatPacket packet, Client client, Player player) {
		boolean isRemote = ((client == null)
				|| (Client.isVirtualClient(client)) && (packet.get("sender_type").equals("1")));
		if(client!=null&&(!Client.isVirtualClient(client))){
			isRemote = client.type.equals(ClientType.MINECRAFT_SERVER);
		}
		if (isRemote) {
			return formatPosition(packet, config.getRemoteFormat().replace("{server}", packet.get("server"))
					.replace("{time}", packet.get("time")).replace("{name}", packet.get("sender"))
					.replace("{display_name}", packet.get("display_name")).replace("{world}", packet.get("world")).replace("{message}", packet.get("message")));
		} else {
			return config.getClientFormat().replace("{type}", ClientType.parsePacket(packet).toLocaleString())
					.replace("{time}", new Date().toString()).replace("{name}", packet.get("name")).replace("{message}", packet.get("message"));
		}
	}

	public String formatPosition(ChatPacket packet, String str) {
		String arg[] = packet.get("position").split(":");
		return str.replace("{position}", packet.get("position")).replace("{position_x}", arg[0])
				.replace("{position_y}", arg[1]).replace("{position_z}", arg[2]);
	}

}