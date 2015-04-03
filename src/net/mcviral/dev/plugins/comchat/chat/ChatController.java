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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import net.mcviral.dev.plugins.comchat.main.ComChat;
import net.mcviral.dev.plugins.comchat.main.ComChatChatEvent;
import net.mcviral.dev.plugins.comchat.util.ChatLog;
import net.mcviral.dev.plugins.comchat.util.FileManager;
import net.md_5.bungee.api.ChatColor;

public class ChatController {
	
	private ComChat chat = null;
	private Chat globalchat = null;
	private LinkedList <Chat> chats = new LinkedList <Chat> ();
	private LinkedList <Chatter> chatters = new LinkedList <Chatter> ();
	private LinkedList <Group> groups = new LinkedList <Group> ();
	private MessageManager messagemanager = null;
	
	public ChatController(ComChat chat){
		this.chat = chat;
		this.chats = new LinkedList <Chat> ();
		this.chatters = new LinkedList <Chatter> ();
		this.messagemanager = new MessageManager(chat);
		//globalchat = new Chat(0, "GLOBAL");
		//globalchat.setPrefix(colour("&f[&aGLOBAL&f]"));
		saveDefaultGlobalChat();
		loadChats();
		loadGroups();
		for (Player p : chat.getServer().getOnlinePlayers()){
			loadChatter(p.getUniqueId());
		}
	}
	
	public void saveDefaultGlobalChat(){
		chat.log.info("Checking for global.yml");
		File folder = new File(chat.getDataFolder() + "/chats/global.yml");
		if (!folder.exists()){
			chat.log.info("Saving default global.yml");
			FileManager fm = new FileManager(chat, "chats/", "global");
			if (!fm.exists()){
				chat.log.info("Created global.yml: " + fm.createFile());
			}
			String name = "GLOBAL";
			fm.getYAML().set("chatID", 0);
			fm.getYAML().set("name", name);
			fm.getYAML().set("prefix", null);
			fm.getYAML().set("suffix", null);
			fm.getYAML().set("messageColour", null);
			//LinkedList <String> list = new LinkedList <String> ();
			fm.getYAML().set("admins", null);
			fm.getYAML().set("moderators", null);
			fm.getYAML().set("displayRank", true);
			fm.getYAML().set("alias", "/gl");
			fm.getYAML().set("aliasApproved", true);
			fm.getYAML().set("joinable", true);
			fm.saveYAML();
		}else{
			chat.log.info("global.yml Found");
		}
		
		chat.log.info("Checking for next.yml");
		folder = new File(chat.getDataFolder() + "/chats/next.yml");
		if (!folder.exists()){
			chat.log.info("Saving default next.yml");
			FileManager fm = new FileManager(chat, "chats/", "next");
			if (!fm.exists()){
				chat.log.info("Created next.yml: " + fm.createFile());
			}
			fm.getYAML().set("next", 1);
			fm.saveYAML();
		}else{
			chat.log.info("next.yml Found");
		}
		
	}
	
	public MessageManager getMessageManager(){
		return messagemanager;
	}
	
	//Chat methods that chat in the right channel
	
	public void chat(Player player, String message){
		Chatter chatter = getChatter(player.getUniqueId());
		if (chatter != null){
			if (chatter.isMuted()){
				Calendar cal = Calendar.getInstance();
				Calendar cal2 = Calendar.getInstance();
				if (chatter.getMutedUntil() == -1L){
					//Perm muted
					player.sendMessage(ChatColor.RED + "You are still permanently muted.");
					return;
				}else{
					cal2.setTimeInMillis(chatter.getMutedUntil());
					if (cal.after(cal2)){
						//unmute
						chat.log.info(player.getName() + " was unmuted as their mute time ran out.");
						chatter.setMuted(false);
						chatter.setMutedUntil(0L);
					}else{
						//still muted
						player.sendMessage(ChatColor.RED + "You are still muted.");
						return;
					}
				}
			}
			Chat chatToSend = chatter.getFocus();
			LinkedList <Player> recipients = new LinkedList <Player> ();
			if (chatToSend == globalchat){
				for (Chatter c : chatters){
					Player p = chat.getServer().getPlayer(c.getUuid());
					if (p != null){
						recipients.add(p);
					}
				}
				String msg = formatMessage(player, chatter, chatToSend, message);
				chatToSend.getChatlog().log(msg);
				chat.log.info(player.getName() + ": " + message);
				for (Player p : recipients){
					p.sendMessage(msg);
				}
			}else{
				for (Chatter c : chatters){
					if (c.getChats().contains(chatToSend)){
						Player p = chat.getServer().getPlayer(c.getUuid());
						if (p != null){
							recipients.add(p);
						}
					}
				}
				String msg = formatMessage(player, chatter, chatToSend, message);
				chatToSend.getChatlog().log(msg);
				chat.log.info(player.getName() + ": " + message);
				for (Player p : recipients){
					p.sendMessage(msg);
				}
			}
		}else{
			//Failed to send message.
		}
	}
	
	public void chatInDifferentToFocus(Player player, Chatter chatter, Chat targetChat, String message){
		if (chatter.isMuted()){
			Calendar cal = Calendar.getInstance();
			Calendar cal2 = Calendar.getInstance();
			if (chatter.getMutedUntil() == -1L){
				//Perm muted
				player.sendMessage(ChatColor.RED + "You are still permanently muted.");
				return;
			}else{
				cal2.setTimeInMillis(chatter.getMutedUntil());
				if (cal.after(cal2)){
					//unmute
					chat.log.info(player.getName() + " was unmuted as their mute time ran out.");
					chatter.setMuted(false);
					chatter.setMutedUntil(0L);
				}else{
					//still muted
					player.sendMessage(ChatColor.RED + "You are still muted.");
					return;
				}
			}
		}
		if (chatter.getChats().contains(targetChat)){
			LinkedList <Player> recipients = new LinkedList <Player> ();
			if (targetChat == globalchat){
				//Global
				for (Chatter c : chatters){
					Player p = chat.getServer().getPlayer(c.getUuid());
					if (p != null){
						recipients.add(p);
					}
				}
				String msg = formatMessage(player, chatter, targetChat, message);
				targetChat.getChatlog().log(msg);
				chat.log.info(player.getName() + ": " + message);
				for (Player p : recipients){
					p.sendMessage(msg);
				}
			}else{
				//Not global
				for (Chatter c : chatters){
					if ((c.getChats().contains(targetChat)) || (c.getSpy())){
						Player p = chat.getServer().getPlayer(c.getUuid());
						if (p != null){
							recipients.add(p);
						}
					}
				}
				String msg = formatMessage(player, chatter, targetChat, message);
				targetChat.getChatlog().log(msg);
				chat.log.info(player.getName() + ": " + message);
				for (Player p : recipients){
					p.sendMessage(msg);
				}
			}
		}else{
			//U wot m8
			player.sendMessage(ChatColor.RED + "You're not in " + targetChat.getName() + " chat, you need to be in it to talk in it.");
		}
	}
	
	public String fireEvent(UUID uuid, String prefix){
		ComChatChatEvent event = new ComChatChatEvent(uuid, prefix);
		chat.getServer().getPluginManager().callEvent(event);
		return event.getPrefix();
	}
	
	public String formatMessage(Player player, Chatter chatter, Chat c, String message){
		String msg = "";
		if (c.getDisplayRank()){
			Group g = this.getGroup(player);
			if (g == null){
				g = new Group("DEFAULT");
				g.setPrefix("&bDEFAULT");
			}
			if (c.getPrefix() != null){
				msg += colour(c.getPrefix() + " ");
			}
			String prefix = "";
			if (chatter.getPrefix() != null){
				prefix += chatter.getPrefix() + " ";
			}else if(g.getPrefix() != null){
				prefix += g.getPrefix() + " ";
			}
			
			//Check for prestige
			prefix = fireEvent(chatter.getUuid(), prefix);
			msg += colour(prefix);
			//Name Colour
			String nameColour = c.getNameColour();
			if (nameColour == null){
				msg += colour("&f" + player.getDisplayName() + " ");
			}else{
				//chat.log.info("DEBUG: Name colour for " + c.getName() + " = " + nameColour);
				msg += colour("&f" + nameColour + (ChatColor.stripColor(player.getDisplayName())) + " ");
			}
			if (c.getSuffix() != null){
				msg += colour(c.getSuffix() + "");
			}
			if (chatter.getSuffix() != null){
				msg += colour(chatter.getSuffix() + "");
			}else if(g.getSuffix() != null){
				msg += colour(g.getSuffix() + "");
			}
			msg += colour("&f: ");
			if (c.getMessageColour() != null){
				msg += colour(c.getMessageColour() + message);
			}else{
				if (chatter.getChatColour() != null){
					msg += colour(chatter.getChatColour() + message);
				}else if(g.getChatColour() != null){
					msg += colour(g.getChatColour() + message);
				}else{
					if (player.hasPermission("chat.colour")){
						msg += colour(message);
					}else{
						msg += message;
					}
				}
			}
		}else{
			if (c.getPrefix() != null){
				msg += colour(c.getPrefix() + " ");
			}
			if (chatter.getPrefix() != null){
				msg += colour(chatter.getPrefix() + " ");
			}
			//Name Colour
			String nameColour = c.getNameColour();
			if (nameColour == null){
				msg += colour("&f" + player.getDisplayName() + " ");
			}else{
				//chat.log.info("DEBUG: Name colour for " + c.getName() + " = " + nameColour);
				msg += colour("&f" + nameColour + (ChatColor.stripColor(player.getDisplayName())) + " ");
			}
			if (c.getSuffix() != null){
				msg += colour(c.getSuffix() + " ");
			}
			if (chatter.getSuffix() != null){
				msg += colour(chatter.getSuffix() + " ");
			}
			msg += colour("&f: ");
			if (c.getMessageColour() != null){
				msg += colour(c.getMessageColour() + message);
			}else{
				if (chatter.getChatColour() != null){
					msg += colour(chatter.getChatColour() + message);
				}
			}
		}
		
		return msg;
	}
	
	public String colour(String msg){
		String coloredMsg = "";
		for(int i = 0; i < msg.length(); i++){
			if(msg.charAt(i) == '&'){
				coloredMsg += '§';
			}else{
				coloredMsg += msg.charAt(i);
			}
    	}
		//chat.getServer().getConsoleSender().sendMessage(coloredMsg);
    	return coloredMsg;
    }
	
	public boolean createChat(Chat c){
		for (Chat cht : chats){
			if (cht.getName().equalsIgnoreCase(c.getName())){
				return false;
			}
		}
		if (!c.getName().equalsIgnoreCase("global")){
			chats.add(c);
			return true;
		}else{
			return false;
		}
	}
	
	public boolean deleteChat(Chat c){
		for (Chat cht : chats){
			if (cht.equals(c)){
				File f = new File(chat.getDataFolder() + "/chats/" + c.getChatID() + ".yml");
				if (f.exists()){
					f.delete();
				}
				chats.remove(c);
				return true;
			}
		}
		return false;
	}
	
	public int getNextChatID(){
		FileManager fm = new FileManager(chat, "chats/", "next");
		if (!fm.exists()){
			fm.createFile();
			return -1;
		}
		return fm.getYAML().getInt("next");
	}
	
	public void setNextChatID(int value){
		FileManager fm = new FileManager(chat, "chats/", "next");
		if (!fm.exists()){
			fm.createFile();
		}
		fm.getYAML().set("next", value);
		fm.saveYAML();
	}
	
	public Chat getChat(String name){
		for (Chat c : chats){
			if (c.getName().equalsIgnoreCase(name)){
				return c;
			}
		}
		return null;
	}
	
	public Chatter getChatter(UUID uuid){
		for (Chatter c : chatters){
			if (c.getUuid().equals(uuid)){
				return c;
			}
		}
		return null;
	}

	public Chat getGlobalChat() {
		return globalchat;
	}

	public void setGlobalChat(Chat globalchat) {
		this.globalchat = globalchat;
	}

	public LinkedList <Chat> getChats() {
		return chats;
	}

	public void setChats(LinkedList <Chat> chats) {
		this.chats = chats;
	}

	public LinkedList <Chatter> getChatters() {
		return chatters;
	}

	public void setChatters(LinkedList <Chatter> chatters) {
		this.chatters = chatters;
	}
	
	public void loadChats(){
		try{
			chats = new LinkedList <Chat> ();
			File folder = new File(chat.getDataFolder() + "/chats/");
			if (!folder.exists()){
				folder.mkdirs();
			}
			List <File> files = Arrays.asList(folder.listFiles());
			FileManager fm = null;
			if (files.size() > 0){
				chats = new LinkedList <Chat> ();
				for (File f : files){
					if (f.isFile()){
						if (!f.getName().equalsIgnoreCase("next.yml")){
							chat.log.info("Loading chat with id: " + f.getName());
							fm = new FileManager(chat, "chats/", f.getName().substring(0, f.getName().length() - 4));
							chat.log.info("Loading .yml: " + f.getName().substring(0, f.getName().length() - 4));
							int chatID = fm.getYAML().getInt("chatID");
							String name = fm.getYAML().getString("name");
							String prefix = fm.getYAML().getString("prefix");
							String nameColour = fm.getYAML().getString("nameColour");
							String suffix = fm.getYAML().getString("suffix");
							String messageColour = fm.getYAML().getString("messageColour");
							String listnull = null;
							try{
								listnull = fm.getYAML().getString("admins");
							}catch(Exception e){
								e.printStackTrace();
							}
							UUID uuid = null;
							LinkedList <UUID> admins = new LinkedList <UUID> ();
							if (listnull != null){
								List <String> tempadmins = fm.getYAML().getStringList("admins");
								for (String s : tempadmins){
									uuid = UUID.fromString(s);
									if (uuid != null){
										admins.add(uuid);
									}
								}
							}else{
								
							}
							LinkedList <UUID> mods = new LinkedList <UUID> ();
							if (listnull != null){
								List <String> tempmods = fm.getYAML().getStringList("moderators");
								for (String s : tempmods){
									uuid = UUID.fromString(s);
									if (uuid != null){
										mods.add(uuid);
									}
								}
							}else{
								
							}
							boolean displayRank =  fm.getYAML().getBoolean("displayRank");
							String alias = fm.getYAML().getString("alias");
							boolean aliasApproved = fm.getYAML().getBoolean("aliasApproved");
							boolean joinable = fm.getYAML().getBoolean("joinable");
							Chat c = new Chat(chatID, name);
							c.setPrefix(prefix);
							c.setNameColour(nameColour);
							c.setSuffix(suffix);
							c.setMessageColour(messageColour);
							c.setAdmins(admins);
							c.setModerators(mods);
							c.setDisplayRank(displayRank);
							c.setAlias(alias);
							c.setAliasApproved(aliasApproved);
							c.setJoinable(joinable);
							c.setChatlog(new ChatLog(chat, c));
							chat.log.info(f.getName());
							if (name.equals("GLOBAL")){
								globalchat = c;
								chat.log.info("Global chat loaded.");
							}else{
								chats.add(c);
								chat.log.info("Chat: " + c.getName() + " loaded.");
							}
						}else{
							//Next chat id
						}
					}else if (f.isDirectory()){
						//Wut
						chat.log.info("Directory: " + f.getAbsolutePath() + f.getName() + " is a directory and in the chats folder, why is it there?");
					}
				}
			}else{
				//No chats to load
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void saveChats(){
		FileManager fm = null;
		for (Chat c : chats){
			if (c.getName() != "GLOBAL"){
				chat.log.info("Saving chat: " + c.getChatID() + " with name: " + c.getName());
				fm = new FileManager(chat, "chats/", c.getChatID() + "");
			}else{
				chat.log.info("Saving chat: GLOBAL");
				fm = new FileManager(chat, "chats/", "global");
			}
			if (!fm.exists()){
				boolean created = fm.createFile();
				if (created){
					chat.log.info("Created file for chat.");
				}else{
					chat.log.info("Failed to create file for chat.");
				}
			}
			try{
				fm.getYAML().set("name", c.getName());
				fm.getYAML().set("prefix", c.getPrefix());
				fm.getYAML().set("nameColour", c.getNameColour());
				fm.getYAML().set("suffix", c.getSuffix());
				fm.getYAML().set("messageColour", c.getMessageColour());
				LinkedList <String> list = new LinkedList <String> ();
				for (UUID uuid : c.getAdmins()){
					list.add(uuid.toString());
				}
				if (list.size() > 0){
					fm.getYAML().set("admins", list);
				}else{
					fm.getYAML().set("admins", null);
				}
				for (UUID uuid : c.getModerators()){
					list.add(uuid.toString());
				}
				if (list.size() > 0){
					fm.getYAML().set("moderators", list);
				}else{
					fm.getYAML().set("moderators", null);
				}
				fm.getYAML().set("displayRank", c.getDisplayRank());
				fm.getYAML().set("alias", c.getAlias());
				fm.getYAML().set("aliasApproved", c.isAliasApproved());
				fm.getYAML().set("joinable", c.isJoinable());
				fm.saveYAML();
				chat.log.info("Chat saved.");
			}catch(Exception e){
				chat.log.severe("FAILED TO SAVE CHAT: " + c.getName() + " with ID: " + c.getChatID());
				e.printStackTrace();
			}
		}
	}
	
	public void saveChat(Chat c){
		FileManager fm = null;
		if (c.getName() != "GLOBAL"){
			fm = new FileManager(chat, "chats/", c.getChatID() + "");
		}else{
			fm = new FileManager(chat, "chats/", "global");
		}
		if (!fm.exists()){
			fm.createFile();
		}
		fm.getYAML().set("name", c.getName());
		fm.getYAML().set("prefix", c.getPrefix());
		fm.getYAML().set("suffix", c.getSuffix());
		fm.getYAML().set("messageColour", c.getMessageColour());
		LinkedList <String> list = new LinkedList <String> ();
		for (UUID uuid : c.getAdmins()){
			list.add(uuid.toString());
		}
		if (list.size() > 0){
			fm.getYAML().set("admins", list);
		}else{
			fm.getYAML().set("admins", null);
		}
		for (UUID uuid : c.getModerators()){
			list.add(uuid.toString());
		}
		if (list.size() > 0){
			fm.getYAML().set("moderators", list);
		}else{
			fm.getYAML().set("moderators", null);
		}
		fm.getYAML().set("displayRank", c.getDisplayRank());
		fm.getYAML().set("alias", c.getAlias());
		fm.getYAML().set("aliasApproved", c.isAliasApproved());
		fm.getYAML().set("joinable", c.isJoinable());
		fm.saveYAML();
	}
	
	public void loadChatter(UUID uuid){
		chat.log.info("Loading chatter: " + uuid.toString());
		FileManager fm = new FileManager(chat, "chatters/", uuid.toString());
		if (fm.exists()){
			List <Integer> chatids = fm.getYAML().getIntegerList("chats");
			LinkedList <Chat> chatterchats = new LinkedList <Chat> ();
			for (Chat c : chats){
				for (Integer i : chatids){
					if (c.getChatID() == i){
						chatterchats.add(c);
						break;
					}
				}
			}
			String prefix = fm.getYAML().getString("prefix");
			String suffix = fm.getYAML().getString("suffix");
			String chatColour = fm.getYAML().getString("chatColour");
			boolean muted = fm.getYAML().getBoolean("muted");
			long mutedUntil = fm.getYAML().getLong("mutedUntil");
			boolean spy = fm.getYAML().getBoolean("spy");
			Chatter chatter = new Chatter(uuid, globalchat);
			chatter.setChats(chatterchats);
			chatter.setPrefix(prefix);
			chatter.setSuffix(suffix);
			chatter.setChatColour(chatColour);
			chatter.setMuted(muted);
			chatter.setMutedUntil(mutedUntil);
			chatter.setSpy(spy);
			chatters.add(chatter);
		}else{
			Chatter chatter = new Chatter(uuid, globalchat);
			chatter.setPrefix(null);
			chatter.setSuffix(null);
			chatter.setMuted(false);
			chatter.setMutedUntil(0L);
			chatter.setSpy(false);
			chatters.add(chatter);
		}
		chat.log.info("Loaded chatter.");
	}
	
	public void saveChatter(Chatter chatter){
		chat.log.info("Saving chatter: " + chatter.getUuid().toString());
		File folder = new File(chat.getDataFolder() + "/chatters/" + chatter.getUuid().toString() + ".yml");
		if (!folder.exists()){
			try {
				folder.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
		FileManager fm = new FileManager(chat, "chatters/", chatter.getUuid().toString());
		LinkedList <Integer> chatids = new LinkedList <Integer> ();
		for (Chat c : chatter.getChats()){
			chatids.add(c.getChatID());
		}
		fm.getYAML().set("chats", chatids);
		fm.getYAML().set("prefix", chatter.getPrefix());
		fm.getYAML().set("suffix", chatter.getSuffix());
		fm.getYAML().set("chatColour", chatter.getChatColour());
		fm.getYAML().set("muted", chatter.isMuted());
		fm.getYAML().set("mutedUntil", chatter.getMutedUntil());
		fm.getYAML().set("spy", chatter.getSpy());
		fm.saveYAML();
		chat.log.info("Saved chatter.");
	}
	
	public void saveChatters(){
		for (Chatter c : chatters){
			try{
				saveChatter(c);
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public boolean chatterIsOnFile(UUID uuid){
		File file = new File(chat.getDataFolder() + "/chatters/" + uuid.toString() + ".yml");
		if (!file.exists()){
			return false;
		}
		return true;
	}
	
	public void loadChatters(){
		try{
			for (Player p : chat.getServer().getOnlinePlayers()){
				if (chatterIsOnFile(p.getUniqueId())){
					this.loadChatter(p.getUniqueId());
				}else{
					chatters.add(new Chatter(p.getUniqueId(), globalchat));
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public LinkedList <Group> getGroups() {
		return groups;
	}

	public void setGroups(LinkedList <Group> groups) {
		this.groups = groups;
	}
	
	public void loadGroups(){
		groups = new LinkedList <Group> ();
		FileManager fm = new FileManager(chat, "", "groups");
		if (fm.exists()){
			int i = 1;
			boolean done = false;
			do{
				String name = fm.getYAML().getString(i + ".name");
				if (name == null){
					done = true;
				}else{
					String prefix = fm.getYAML().getString(i + ".prefix");
					String suffix = fm.getYAML().getString(i + ".suffix");
					String chatColour = fm.getYAML().getString(i + ".chatColour");
					Group g = new Group(name);
					g.setPrefix(prefix);
					g.setSuffix(suffix);
					g.setChatColour(chatColour);
					groups.add(g);
				}
				i++;
			}while(done == false);
		}else{
			fm.createFile();
			chat.log.warning("No groups file found, I've created one, please edit it.");
		}
	}
	
	public void saveGroups(){
		FileManager fm = new FileManager(chat, "", "groups");
		if (!fm.exists()){
			fm.createFile();
		}
		int i = 1;
		for (Group g : groups){
			chat.log.info("Saving group: " + g.getName());
			fm.getYAML().set(i + ".name", g.getName());
			fm.getYAML().set(i + ".prefix", g.getPrefix());
			fm.getYAML().set(i + ".suffix", g.getSuffix());
			fm.getYAML().set(i + ".chatColour", g.getChatColour());
			i++;
		}
		fm.saveYAML();
	}
	
	public Group getGroup(Player p){
		for (Group g : groups){
			//chat.log.info("Does user have chat.group." + g.getName() + "?");
			if (p.hasPermission("chat.group." + g.getName())){
				//chat.log.info("Yes");
				return g;
			}
			//chat.log.info("No");
		}
		return null;
	}
	
}
