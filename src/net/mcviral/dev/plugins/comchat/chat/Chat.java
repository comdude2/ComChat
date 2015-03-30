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

import net.mcviral.dev.plugins.comchat.util.ChatLog;

public class Chat {
	
	private int chatID = 0;
	private String name = null;
	private String prefix = null;
	private String suffix = null;
	private String messageColour = null;
	private LinkedList <UUID> admins = new LinkedList <UUID> ();
	private LinkedList <UUID> moderators = new LinkedList <UUID> ();
	private boolean displayRank = false;
	private String alias = null;
	private boolean aliasApproved = false;
	private boolean joinable = false;
	private ChatLog chatlog = null;
	
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

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public boolean isAliasApproved() {
		return aliasApproved;
	}

	public void setAliasApproved(boolean aliasApproved) {
		this.aliasApproved = aliasApproved;
	}

	public boolean isJoinable() {
		return joinable;
	}

	public void setJoinable(boolean joinable) {
		this.joinable = joinable;
	}

	public ChatLog getChatlog() {
		return chatlog;
	}

	public void setChatlog(ChatLog chatlog) {
		this.chatlog = chatlog;
	}
	
}
