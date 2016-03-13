package net.steepout.plugin.such;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;

public class ServerConfiguration{
	MemorySection servers;
	boolean enable;
	int port;
	boolean acceptHook;
	boolean doHook;
	String hookIP;
	int hookPort;
	String hookName;
	String hookPassword;
	String remoteFormat;
	String clientFormat;
	boolean autoHook;
	@SuppressWarnings("unchecked")
	public ServerConfiguration(FileConfiguration config){
		servers = (MemorySection) config.get("servers_map");
		enable = config.getBoolean("enable_chat_server",false);
		port = config.getInt("server_port",80);
		acceptHook = config.getBoolean("accept_server_hook",false);
		doHook = config.getBoolean("do_hook",false);
		hookIP = config.getString("hook_ip");
		hookPort = config.getInt("hook_port");
		hookName = config.getString("hook_name");
		hookPassword = config.getString("hook_password");
		remoteFormat = config.getString("remote_format","[{server}] <{display_name}> : {message}");
		clientFormat = config.getString("client_format","[{type}] [{date}] <{name}> : {message}");
		autoHook = config.getBoolean("auto_hook",true);
	}
	protected synchronized boolean isAutoHook() {
		return autoHook;
	}
	protected synchronized void setAutoHook(boolean autoHook) {
		this.autoHook = autoHook;
	}
	protected synchronized String getRemoteFormat() {
		return remoteFormat;
	}
	protected synchronized String getClientFormat() {
		return clientFormat;
	}
	protected synchronized MemorySection getServers() {
		return servers;
	}
	protected synchronized boolean isEnable() {
		return enable;
	}
	protected synchronized int getPort() {
		return port;
	}
	protected synchronized boolean isAcceptHook() {
		return acceptHook;
	}
	protected synchronized boolean isDoHook() {
		return doHook;
	}
	protected synchronized String getHookIP() {
		return hookIP;
	}
	protected synchronized int getHookPort() {
		return hookPort;
	}
	protected synchronized String getHookName() {
		return hookName;
	}
	protected synchronized String getHookPassword() {
		return hookPassword;
	}
}