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

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.mcviral.dev.plugins.comchat.main.ComChat;

public class MessageManager {
	
	private ComChat chat = null;
	private Chat c = null;
	private int nextID = 0;
	private LinkedList <Message> messages = new LinkedList <Message> ();
	
	//s.sendMessage(ChatColor.RED + "");
	
	public MessageManager(ComChat chat){
		this.chat = chat;
	}
	
	public void message(Player s, Player r, String message){
		Message m = null;
		if (!hasMessageChannel(s, r)){
			m = new Message(chat, nextID, s.getUniqueId().toString() + "-message-" + r.getUniqueId().toString(), s.getUniqueId(), r.getUniqueId());
			Chatter cs = chat.getChatController().getChatter(s.getUniqueId());
			if (cs != null){
				if (cs.getLastMessage() != null){
					Chatter cr = null;
					if (cs.getLastMessage().getChatterB() == cs.getUuid()){
						cr = chat.getChatController().getChatter(cs.getLastMessage().getChatterA());
					}else{
						cr = chat.getChatController().getChatter(cs.getLastMessage().getChatterB());
					}
					if (cr.getLastMessage() != null){
						if (cr.getLastMessage() != cs.getLastMessage()){
							cr.setLastMessage(null);
							messages.remove(cs.getLastMessage());
						}//else ignore it
					}else{
						//leave it alone but remove message channel
					}
				}
			}else{
				
			}
		}else{
			m = getMessageChannel(s, r);
		}
		if (m != null){
			Chatter cs = chat.getChatController().getChatter(s.getUniqueId());
			Chatter cr = chat.getChatController().getChatter(r.getUniqueId());
			if ((cs != null) && (cr != null)){
				cs.setLastMessage(m);
				cr.setLastMessage(m);
				m.message(s, r, message);
			}else{
				s.sendMessage(ChatColor.RED + "Couldn't find you / the target as a chatter, try relogging or asking them to relog.");
			}
		}else{
			//cry
			s.sendMessage(ChatColor.RED + "An unexpected error ocurred, contact a warden. Error code: 1");
		}
	}
	
	public void reply(Player s, String message){
		Chatter chatter = chat.getChatController().getChatter(s.getUniqueId());
		if (c != null){
			Message m = chatter.getLastMessage();
			if (m != null){
				Player r = null;
				if (m.getChatterB() == s.getUniqueId()){
					//a
					r = chat.getServer().getPlayer(m.getChatterA());
				}else if (m.getChatterA() == s.getUniqueId()){
					//b
					r = chat.getServer().getPlayer(m.getChatterB());
				}else{
					//big problem
					s.sendMessage(ChatColor.RED + "An unexpected error ocurred, contact a warden. Error code: 2");
					return;
				}
				if (r != null){
					m.message(s, r, message);
				}else{
					//R is null, they're offline
					s.sendMessage(ChatColor.RED + "That player is offline.");
				}
			}else{
				//no last message
				s.sendMessage(ChatColor.RED + "You're not in a conversation with anyone.");
			}
		}else{
			//error
			s.sendMessage(ChatColor.RED + "Couldn't find you as a chatter, try relogging.");
		}
	}
	
	public boolean hasMessageChannel(Player s, Player r){
		String aCombo = s.getUniqueId().toString() + "-message-" + r.getUniqueId().toString();
		String bCombo = r.getUniqueId().toString() + "-message-" + s.getUniqueId().toString();
		for (Message m : messages){
			if (m.getName().equalsIgnoreCase(aCombo)){
				return true;
			}else if (m.getName().equalsIgnoreCase(bCombo)){
				return true;
			}
		}
		return false;
	}
	
	public Message getMessageChannel(Player s, Player r){
		String aCombo = s.getUniqueId().toString() + "-message-" + r.getUniqueId().toString();
		String bCombo = r.getUniqueId().toString() + "-message-" + s.getUniqueId().toString();
		for (Message m : messages){
			if (m.getName().equalsIgnoreCase(aCombo)){
				return m;
			}else if (m.getName().equalsIgnoreCase(bCombo)){
				return m;
			}
		}
		return null;
	}
	
	@Deprecated
	public void messageStandard(Player p, Player r, String message){
		if (p.hasPermission("chat.colour")){
			c.getChatlog().log(ChatColor.GREEN + p.getName() + ChatColor.LIGHT_PURPLE + " >> " + ChatColor.GREEN + r.getName() + ChatColor.WHITE + ": " + ChatColor.LIGHT_PURPLE + message);
			p.sendMessage(ChatColor.GREEN + "You" + ChatColor.LIGHT_PURPLE + " >> " + ChatColor.GREEN + r.getName() + ChatColor.WHITE + ": " + ChatColor.LIGHT_PURPLE + chat.getChatController().colour(message));
			r.sendMessage(ChatColor.GREEN + p.getName() + ChatColor.LIGHT_PURPLE + " >> " + ChatColor.GREEN + "You" + ChatColor.WHITE + ": " + ChatColor.LIGHT_PURPLE + chat.getChatController().colour(message));
			for (Player ex : chat.getServer().getOnlinePlayers()){
				Chatter chatter = chat.getChatController().getChatter(ex.getUniqueId());
				if (chatter != null){
					if (chatter.getSpy()){
						ex.sendMessage(ChatColor.GREEN + p.getName() + ChatColor.LIGHT_PURPLE + " >> " + ChatColor.GREEN + r.getName() + ChatColor.WHITE + ": " + ChatColor.LIGHT_PURPLE + chat.getChatController().colour(message));
					}
				}
			}
		}else{
			c.getChatlog().log(ChatColor.GREEN + p.getName() + ChatColor.LIGHT_PURPLE + " >> " + ChatColor.GREEN + r.getName() + ChatColor.WHITE + ": " + ChatColor.LIGHT_PURPLE + message);
			p.sendMessage(ChatColor.GREEN + "You" + ChatColor.LIGHT_PURPLE + " >> " + ChatColor.GREEN + r.getName() + ChatColor.WHITE + ": " + ChatColor.LIGHT_PURPLE + message);
			r.sendMessage(ChatColor.GREEN + p.getName() + ChatColor.LIGHT_PURPLE + " >> " + ChatColor.GREEN + "You" + ChatColor.WHITE + ": " + ChatColor.LIGHT_PURPLE + message);
			for (Player ex : chat.getServer().getOnlinePlayers()){
				Chatter chatter = chat.getChatController().getChatter(ex.getUniqueId());
				if (chatter != null){
					if (chatter.getSpy()){
						ex.sendMessage(ChatColor.GREEN + p.getName() + ChatColor.LIGHT_PURPLE + " >> " + ChatColor.GREEN + r.getName() + ChatColor.WHITE + ": " + ChatColor.LIGHT_PURPLE + message);
					}
				}
			}
		}
	}
	
}
