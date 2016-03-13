package net.steepout.plugin.such;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.bukkit.craftbukkit.libs.jline.internal.Log;

public class ClientProtocol{
	BufferedReader in;
	PrintWriter out;
	public static final byte[] PACKET_TAIL = {'b','y','e'};
	public ClientProtocol(Socket socket) throws IOException{
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(),true);
	}
	private byte[] size00(int size){
		byte data[] = new byte[2];
		if(size>127*127){
			throw new RuntimeException("too big size");
		}
		if(size>127){
			data[0] = (byte) (size/127);
			data[1] = (byte) (size%127);
		}else{
			data[0] = 0;
			data[1] = (byte) size;
		}
		return data;
	}
	byte tmp[];
	private int size01(byte[] data) throws IOException{
		tmp = data;
		return data[0]*127 + data[1];
	}
	public void sendPacket(ChatPacket packet) throws IOException{
		/*Log.info("sending packet : "+packet.get("type"));
		Log.info();
		packet.listProperty();*/
		StringBuilder builder = new StringBuilder();
		String data = packet.toString();
		builder.append(new String(size00(data.length())));
		builder.append(new String(data));
		builder.append(new String(PACKET_TAIL));
		out.println(builder.toString());
		//Log.info("successful sent!");
	}
	public ChatPacket readPacket() throws IOException{
		//Log.info("reading packet");
		String str = in.readLine();
		if(str==null)
			throw new IOException("connection error!");
		byte[] _size = str.substring(0, 2).getBytes();
		int size = size01(_size);
		byte[] tail = str.substring(str.length()-3,str.length()).getBytes();
		String data = str.substring(2, str.length()-3);
		for(char x : data.toCharArray()){
			if(Character.isAlphabetic(x)||Character.isDigit(x)||x=='='||x=='*'||x=='+'){ //check illegal
				
			}else{
				Log.info("Illegal Character [code:"+(int)x+"]"); 
			}
		}
		data = data.replace('*', '\n');
		//Log.info("packet length : "+size+" : "+Arrays.toString(tmp));
		if(!Arrays.equals(tail, PACKET_TAIL)){
			throw new RuntimeException("Malformed protocol!");
		}
		//System.out.println(data);
		ChatPacket packet = ChatPacket.fromString(data);
		//Log.info("packet type "+packet.get("type"));
		//packet.listProperty();
		return packet;
	}
}