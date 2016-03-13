package net.steepout.plugin.such;

import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Date;

import org.bukkit.Bukkit;

public class Testcase{
	public static void main(String[] args){
		ChatPacket packet = new ChatPacket();
		packet.put("type", "test");
		packet.put("message", "test");
		packet.put("sender", "wxx123");
		packet.put("display_name", "wxx123");
		packet.put("time", new Date().toString());
		packet.put("sender_type", "1"); //1=player,2=client,3=hook
		packet.put("world", "main");
		packet.put("positon", "1000:1000:10000");
		packet.put("server", "unknown");
		System.out.println(Arrays.toString(size00(packet.toString().length())));
	}
	private static int[] size00(int size){
		int data[] = new int[2];
		if(size>127*127){
			throw new RuntimeException("too big size");
		}
		if(size>0xff){
			data[0] = size/127;
			data[1] = size%127;
		}else{
			data[0] = 0;
			data[1] = size;
		}
		return data;
	}
}