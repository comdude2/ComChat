package net.mcviral.dev.plugins.comchat.chat;

import java.util.LinkedList;
import java.util.UUID;

public class Chat {
	
	private int chatID = 0;
	private String name = null;
	private String prefix = null;
	private String suffix = null;
	private String messageColour = null;
	private LinkedList <UUID> admins = new LinkedList <UUID> ();
	private LinkedList <UUID> moderators = new LinkedList <UUID> ();
	private boolean displayRank = false;
	
	public Chat(int chatID, String name){
		this.setChatID(chatID);
		this.setName(name);
	}

	public int getChatID() {
		return chatID;
	}

	public void setChatID(int chatID) {
		this.chatID = chatID;
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

	public String getMessageColour() {
		return messageColour;
	}

	public void setMessageColour(String messageColour) {
		this.messageColour = messageColour;
	}

	public LinkedList <UUID> getAdmins() {
		return admins;
	}

	public void setAdmins(LinkedList <UUID> admins) {
		this.admins = admins;
	}

	public LinkedList <UUID> getModerators() {
		return moderators;
	}

	public void setModerators(LinkedList <UUID> moderators) {
		this.moderators = moderators;
	}

	public boolean getDisplayRank() {
		return displayRank;
	}

	public void setDisplayRank(boolean displayRank) {
		this.displayRank = displayRank;
	}
	
}
