package net.steepout.plugin.such;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sun.xml.internal.messaging.saaj.util.Base64;

public class ChatPacket extends HashMap<String,String>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4533170417791705290L;
	
	static int version = 0x1;
	
	static int max_support = 0x10; //Temporarily support to this version
	
	static int min_support = 0x1;
	
	public String get(Object key){
		if(key==null)
			return null;
		String value = super.get(key);
		if(value==null){
			return null;
		}else{
			try {
				return URLDecoder.decode(value,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				return null;
			}
		}
	}
	
	public String put(String key,String value){ 
		if(value==null){
			value = null;
		}else{
			try {
				value = URLEncoder.encode(value, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return super.put(key,value);
	}
	
	private void putUnsafe(String key,String value){
		super.put(key, value);
	}
	
	public String toString(){
		Set<Map.Entry<String,String>> entrys;
		entrys = entrySet(); //get entries
		String result = entrys.size()+":";
		for(Map.Entry<String, String> x:entrys){ //iterate and save in string
			result+=x.getKey()+":"+x.getValue()+":";
		}
		return new String(Base64.encode(result.replace(":", "cafebabe").getBytes(StandardCharsets.UTF_8)),StandardCharsets.UTF_8).replace('\n', '*');
	}
	
	public static synchronized ChatPacket fromString(String string){
		ChatPacket packet = new ChatPacket();
		string = Base64.base64Decode(string.replace('*', '\n'));
		string = string.replace("cafebabe", ":");
		int pairs = Integer.parseInt(string.split(":")[0]);
		String arrays[] = Arrays.copyOfRange(string.split(":"),1 , string.split(":").length);
		for(int x=0;x!=pairs;x++){
			packet.putUnsafe(arrays[x*2], arrays[x*2+1]);
		}
		return packet;
	}
	
	public static synchronized ChatPacket skip(){
		ChatPacket packet = new ChatPacket();
		packet.put("type", "skip");
		return packet;
	}
	
	public static synchronized ChatPacket wrapBroadcast(String message,Player sender){
		ChatPacket packet = new ChatPacket();
		packet.put("type", "chat");
		packet.put("message", message);
		packet.put("sender", sender.getName());
		packet.put("display_name", sender.getDisplayName());
		packet.put("time", new Date().toString());
		packet.put("sender_type", "1"); //1=player,2=client,3=hook
		packet.put("world", sender.getWorld().getName());
		packet.put("position", convertPosition(sender.getLocation()));
		packet.put("server", Bukkit.getServer().getServerName());
		packet.put("name", sender.getName()); //compromise to the android client (I am too lazy to edit the protocol QwQ)
		return packet;
	}//我这边完全正常了
	
	private static String convertPosition(Location location){
		return location.getX()+":"+location.getY()+":"+location.getZ();
	}
	
	public static synchronized ChatPacket wrapKick(String reason){
		ChatPacket packet = new ChatPacket();
		packet.put("type", "kick");
		packet.put("reason", reason);
		return packet;
	}
	
	public void listProperty(){
		for(Map.Entry<String, String> x : this.entrySet()){
			System.out.println(x.getKey()+" : "+x.getValue());
		}
	}
	
	public static synchronized ChatPacket getVersionPacket(){
		ChatPacket packet = new ChatPacket();
		packet.put("type", "version");
		packet.put("version", Integer.toHexString(version));
		packet.put("max_support", Integer.toHexString(max_support));
		packet.put("min_support", Integer.toHexString(min_support));
		return packet;
	}
	
}