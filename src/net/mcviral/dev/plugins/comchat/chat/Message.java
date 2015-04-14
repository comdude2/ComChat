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

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.mcviral.dev.plugins.comchat.main.ComChat;
import net.mcviral.dev.plugins.comchat.util.ChatLog;

public class Message extends Chat{
	
	private ComChat chat = null;
	private UUID chatterA = null;
	private UUID chatterB = null;
	
	public Message(ComChat chat, int chatID, String name, UUID chatterA, UUID chatterB) {
		super(chatID, name);
		this.chat = chat;
		this.setChatterA(chatterA);
		this.setChatterB(chatterB);
		this.setChatlog(new ChatLog(chat, this));
	}
	
	public UUID getChatterA() {
		return chatterA;
	}

	public void setChatterA(UUID chatterA) {
		this.chatterA = chatterA;
	}

	public UUID getChatterB() {
		return chatterB;
	}

	public void setChatterB(UUID chatterB) {
		this.chatterB = chatterB;
	}

	public boolean message(Player s, Player r, String message){
		if (r == null){
			//B is offline
			s.sendMessage(ChatColor.RED + "That player is offline.");
			return false;
		}else{
			//All is good
			if (s.hasPermission("chat.colour")){
				this.getChatlog().log(ChatColor.GREEN + s.getName() + ChatColor.LIGHT_PURPLE + " >> " + ChatColor.GREEN + r.getName() + ChatColor.WHITE + ": " + ChatColor.LIGHT_PURPLE + message);
				s.sendMessage(ChatColor.GREEN + "You" + ChatColor.LIGHT_PURPLE + " >> " + ChatColor.GREEN + r.getName() + ChatColor.WHITE + ": " + ChatColor.LIGHT_PURPLE + chat.getChatController().colour(message));
				r.sendMessage(ChatColor.GREEN + s.getName() + ChatColor.LIGHT_PURPLE + " >> " + ChatColor.GREEN + "You" + ChatColor.WHITE + ": " + ChatColor.LIGHT_PURPLE + chat.getChatController().colour(message));
			}else{
				this.getChatlog().log(ChatColor.GREEN + s.getName() + ChatColor.LIGHT_PURPLE + " >> " + ChatColor.GREEN + r.getName() + ChatColor.WHITE + ": " + ChatColor.LIGHT_PURPLE + message);
				s.sendMessage(ChatColor.GREEN + "You" + ChatColor.LIGHT_PURPLE + " >> " + ChatColor.GREEN + r.getName() + ChatColor.WHITE + ": " + ChatColor.LIGHT_PURPLE + message);
				r.sendMessage(ChatColor.GREEN + s.getName() + ChatColor.LIGHT_PURPLE + " >> " + ChatColor.GREEN + "You" + ChatColor.WHITE + ": " + ChatColor.LIGHT_PURPLE + message);
			}
			spy(s, r, message);
			return true;
		}
	}
	
	private void spy(Player s, Player r, String message){
		for (Player p : chat.getServer().getOnlinePlayers()){
			if ((p != s) && (p != r)){
				//spy
				Chatter chatter = chat.getChatController().getChatter(p.getUniqueId());
				if (chatter != null){
					if (chatter.getSpy()){
						p.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "SPY" + ChatColor.WHITE + "] " + ChatColor.GREEN + p.getName() + ChatColor.LIGHT_PURPLE + " >> " + ChatColor.GREEN + r.getName() + ChatColor.WHITE + ": " + ChatColor.LIGHT_PURPLE + message);
					}
				}
			}
		}
	}

}
