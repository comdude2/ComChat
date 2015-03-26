package net.mcviral.dev.plugins.comchat.chat;

import java.util.LinkedList;
import java.util.UUID;

public class Chatter {
	
	private UUID uuid = null;
	private Chat focus = null;
	private LinkedList <Chat> chats = new LinkedList <Chat> ();
	private String prefix = null;
	private String suffix = null;
	private boolean muted = false;
	
	public Chatter(UUID uuid, Chat focus){
		this.uuid = uuid;
		this.focus = focus;
	}
	
	public UUID getUuid() {
		return uuid;
	}
	
	public Chat getFocus(){
		return focus;
	}
	
	public void setFocus(Chat c){
		focus = c;
	}

	public LinkedList <Chat> getChats() {
		return chats;
	}

	public void setChats(LinkedList <Chat> chats) {
		this.chats = chats;
	}

	public boolean isMuted() {
		return muted;
	}

	public void setMuted(boolean muted) {
		this.muted = muted;
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
	
}
