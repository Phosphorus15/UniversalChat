package net.steepout.plugin.jd;

public class JavaDesktop implements Runnable{
	static String[] args;
	public static void main(String[] args){
		JavaDesktop.args = args;
		Thread thread = new Thread(new JavaDesktop());
		thread.setName("UniversalChatDesktop");
		thread.start();
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Main.main(args);
	}
}