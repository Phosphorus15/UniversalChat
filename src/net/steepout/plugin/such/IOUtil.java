package net.steepout.plugin.such;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Deprecated
public class IOUtil{
	public static synchronized BufferedReader openReader(Socket socket) throws IOException{
		return new BufferedReader(new InputStreamReader(socket.getInputStream(),StandardCharsets.UTF_8));
	}
	public static synchronized PrintWriter openWriter(Socket socket) throws IOException{
		PrintWriter writer = new PrintWriter(socket.getOutputStream(),true);
		return writer;
	}
	public static synchronized void endSocketConnection(Socket socket) throws IOException{
		socket.close();
		socket = null;
		System.gc();//A wormhole to your brain hole
	}
}