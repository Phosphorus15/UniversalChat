package net.steepout.plugin.such;

public enum ClientType{
	ANDROID ,
	JAVA_DESKTOP ,
	@Deprecated
	C_SHARP_DESKTOP ,
	@Deprecated
	IOS ,
	MINECRAFT_SERVER ,
	@Deprecated
	MINECRAFT_MOD;
	public static ClientType parsePacket(ChatPacket packet){
		String string = packet.get("client_name");
		switch(string){
		case "android":
			return ANDROID;
		case "jd":
			return JAVA_DESKTOP;
		case "server":
			return MINECRAFT_SERVER;
		case "csharp":
			return C_SHARP_DESKTOP;
			default:
				throw new RuntimeException("no such client type");
		}
	}
	public String toLocaleString(){
		if(this.equals(ANDROID))
			return "android";
		else if(this.equals(JAVA_DESKTOP))
			return "jd";
		else if(this.equals(MINECRAFT_SERVER))
			return "server";
		else if(this.equals(C_SHARP_DESKTOP))
			return "csharp";
		return "unknown";
	}
	public String toString(){
		if(this.equals(ANDROID))
			return "android";
		else if(this.equals(JAVA_DESKTOP))
			return "jd";
		else if(this.equals(MINECRAFT_SERVER))
			return "server";
		else if(this.equals(C_SHARP_DESKTOP))
			return "csharp";
		return "unknown";
	}
}