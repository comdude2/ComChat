/*
ComChat - A chat plugin for Minecraft servers
Copyright (C) 2015  comdude2 (Matt Armer)

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

Contact: admin@mcviral.net
*/

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
	private long mutedUntil = 0L;
	
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

	public long getMutedUntil() {
		return mutedUntil;
	}

	public void setMutedUntil(long mutedUntil) {
		this.mutedUntil = mutedUntil;
	}
	
}
