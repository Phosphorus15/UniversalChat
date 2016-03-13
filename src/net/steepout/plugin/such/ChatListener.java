package net.steepout.plugin.such;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

public abstract class ChatListener implements Listener{
	@EventHandler(priority = EventPriority.MONITOR)
	public abstract void onPlayerChat(PlayerChatEvent event);
}