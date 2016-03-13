package net.steepout.plugin.such;

import java.net.InetAddress;

import org.bukkit.craftbukkit.libs.jline.internal.Log;

import fr.xephi.authme.api.API;

public class AuthAPI{
	public static synchronized boolean checkServerConnection(InetAddress address,String server,String password,ServerConfiguration config){
		Log.info("password should be "+config.getServers().getString(server));
		if(config.isAcceptHook()) //check feasiblity of hook action in configuration
			if(config.getServers().contains(server))
				if(config.getServers().getString(server).equals(password)){ //access the provided login information
					Log.info("chat server hooked : "+server);
					return true;
				}
		return false;
	}
	@SuppressWarnings("deprecation")
	public static synchronized boolean checkClientConnection(String name,String password,ServerConfiguration config){
		return API.checkPassword(name, password); //use Authme API
	}
}