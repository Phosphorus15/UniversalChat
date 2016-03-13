package net.steepout.plugin.such;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class Commands implements CommandExecutor{
	
	ServerConfiguration config;
	
	public Commands(ServerConfiguration config){
		this.config = config;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] arg) {
		// TODO Auto-generated method stub
		if(command.getName().equals("hook")){
			if((sender instanceof ConsoleCommandSender)||sender.isOp()){
				if(config.isDoHook()){
					sender.sendMessage(ChatColor.RED+"The hook fuction is forbidden");
					return true;
				}else if(PluginMain.hook!=null&&PluginMain.hook.established){
					sender.sendMessage(ChatColor.RED+"The hook is established");
					return true;
				}else{
					sender.sendMessage(ChatColor.GOLD+"Hooking...");
					PluginMain.hook = new RemoteHook(config);
					PluginMain.hook.attemptHook(); //start to hook
				}
			}else{
				sender.sendMessage(ChatColor.RED+"you have no permission to do this");
				return true;
			}
		}else if(command.getName().equals("ckick")){
			if((sender instanceof ConsoleCommandSender)||sender.isOp()){
				String name="";
				String reason="Kicked by op";
				if(arg.length==1){
					name = arg[0];
				}else if(arg.length>1){
					name = arg[0];
					reason = arg[1];
				}else{
					sender.sendMessage(ChatColor.RED+"too few parameters provided");
					return true;
				}
			}else{
				sender.sendMessage(ChatColor.RED+"you have no permission to do this");
				return true;
			}
		}
		return true;
	}
	
}