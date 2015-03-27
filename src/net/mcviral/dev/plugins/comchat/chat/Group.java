package net.mcviral.dev.plugins.comchat.chat;

public class Group {
	
	private String name = null;
	private String prefix = null;
	private String suffix = null;
	private String chatColour = null;
	
	public Group(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getChatColour() {
		return chatColour;
	}

	public void setChatColour(String chatColour) {
		this.chatColour = chatColour;
	}
	
}
