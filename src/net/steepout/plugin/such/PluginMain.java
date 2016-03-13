package net.steepout.plugin.such;

import java.io.IOException;

import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.plugin.java.JavaPlugin;

import fr.xephi.authme.api.API;

public class PluginMain extends JavaPlugin{
	static boolean stopped = false;
	static Server server;
	static ChatServer chatserver;
	FileConfiguration config;
	static RemoteHook hook;
	@SuppressWarnings("deprecation")
	@Override
	public void onEnable(){
		API.hookAuthMe(); //hook on authme plugin
		server = this.getServer();
		config = this.getConfig(); //load config
		ServerConfiguration sconfig = new ServerConfiguration(config);
		Formatter.setConfiguration(sconfig); //set configuration for message formatter
		try {
			chatserver = new ChatServer(sconfig);//create server instance
			chatserver.start();//start server thread
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.info("chat server created successfully");
		this.getServer().getPluginManager().registerEvents(ChatAgency.initialAgency(sconfig),this);//I think this is a good idea to start chat agency and register listener in one step
		hook = new RemoteHook(sconfig);
		if(sconfig.isAutoHook())
			hook.attemptHook(); //start to hook
		Commands commands = new Commands(sconfig);
		this.getServer().getPluginCommand("ckick").setExecutor(commands);
		this.getServer().getPluginCommand("hook").setExecutor(commands);
		this.getServer().getScheduler().runTaskTimer(this, chatserver.getScheduleObject(), 100, config.getLong("ping_period",1000)); //schedule runnable
	}
	@Override
	public void onLoad(){
		this.saveDefaultConfig();
	}
	@Override
	public void onDisable(){
		//shutdown all services
		this.getServer().getScheduler().cancelTasks(this);
		stopped = true;
		chatserver.shutdown();
		ChatAgency.instance.close();
		if(hook.established)
			hook.close();
		Log.info("Chat server closed");
	}
}