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
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import net.mcviral.dev.plugins.comchat.main.ComChat;
import net.mcviral.dev.plugins.comchat.util.FileManager;

public class ChatController {
	
	private ComChat chat = null;
	private Chat globalchat = null;
	private LinkedList <Chat> chats = new LinkedList <Chat> ();
	private LinkedList <Chatter> chatters = new LinkedList <Chatter> ();
	
	public ChatController(ComChat chat){
		this.chat = chat;
		this.chats = new LinkedList <Chat> ();
		this.chatters = new LinkedList <Chatter> ();
		//globalchat = new Chat(0, "GLOBAL");
		//globalchat.setPrefix(colour("&f[&aGLOBAL&f]"));
		saveDefaultGlobalChat();
		loadChats();
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
			fm.getYAML().set("name", nullToString(name));
			fm.getYAML().set("prefix", nullToString("&aGLOBAL"));
			fm.getYAML().set("suffix", nullToString(null));
			fm.getYAML().set("messageColour", nullToString(null));
			//LinkedList <String> list = new LinkedList <String> ();
			fm.getYAML().set("admins", nullToString(null));
			fm.getYAML().set("moderators", nullToString(null));
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
	
	//Chat methods that chat in the right channel
	
	public void chat(Player player, String message){
		Chatter chatter = getChatter(player.getUniqueId());
		if (chatter != null){
			if (!chatter.isMuted()){
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
					chat.log.info(player.getName() + ": " + message);
					for (Player p : recipients){
						p.sendMessage(msg);
					}
				}
			}else{
				//Muted
			}
		}else{
			//Failed to send message.
		}
	}
	
	public void chatInDifferentToFocus(Player player, Chatter chatter, Chat targetChat, String message){
		if (!chatter.isMuted()){
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
					chat.log.info(player.getName() + ": " + message);
					for (Player p : recipients){
						p.sendMessage(msg);
					}
				}else{
					//Not global
					for (Chatter c : chatters){
						if (c.getChats().contains(targetChat)){
							Player p = chat.getServer().getPlayer(c.getUuid());
							if (p != null){
								recipients.add(p);
							}
						}
					}
					String msg = formatMessage(player, chatter, targetChat, message);
					chat.log.info(player.getName() + ": " + message);
					for (Player p : recipients){
						p.sendMessage(msg);
					}
				}
			}else{
				//U wot m8
				
			}
		}else{
			//Muted
		}
	}
	
	public String formatMessage(Player player, Chatter chatter, Chat c, String message){
		String msg = "";
		if ((c.getPrefix() != null) && (!c.getPrefix().contains("null"))){
			msg = msg + colour(c.getPrefix()) + " ";
		}
		//if (c.getDisplayRank()){
			//msg = msg + c.getPrefix();
		//}
		msg = msg + player.getDisplayName() + colour("&f");
		if ((c.getSuffix() != null) && (!c.getSuffix().contains("null"))){
			msg = msg + colour(" " + c.getSuffix());
		}
		msg = msg + colour("&f: ");
		if ((c.getMessageColour() != null) && (!c.getMessageColour().contains("null"))){
			//Testing lines:
			//chat.log.info("chat message colour wasn't null");
			//chat.log.info("" + c.getMessageColour());
			msg = msg + c.getMessageColour() + message;
		}else{
			if ((chatter.getChatColour() != null) && (!chatter.getChatColour().contains("null"))){
				msg = msg + colour(chatter.getChatColour());
			}
			if (player.hasPermission("chat.colour")){
				msg = msg + colour(message);
			}else{
				msg = msg + message;
			}
		}
		return msg;
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
	
	public String colour(String msg){
		String coloredMsg = "";
		for(int i = 0; i < msg.length(); i++){
			if(msg.charAt(i) == '&'){
				coloredMsg += '§';
			}else{
				coloredMsg += msg.charAt(i);
			}
    	}
    	return coloredMsg;
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
							String prefix = stringToNull(fm.getYAML().getString("prefix"));
							String suffix = stringToNull(fm.getYAML().getString("suffix"));
							String messageColour = stringToNull(fm.getYAML().getString("messageColour"));
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
							prefix = stringToNull(prefix);
							suffix = stringToNull(suffix);
							messageColour = stringToNull(messageColour);
							Chat c = new Chat(chatID, name);
							c.setPrefix(prefix);
							c.setSuffix(suffix);
							c.setMessageColour(messageColour);
							c.setAdmins(admins);
							c.setModerators(mods);
							c.setDisplayRank(displayRank);
							c.setAlias(alias);
							c.setAliasApproved(aliasApproved);
							c.setJoinable(joinable);
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
	
	private String stringToNull(String s){
		if (s == "null"){
			s = null;
		}
		return s;
	}
	
	private String nullToString(String s){
		if (s == null){
			s = "null";
		}
		return s;
	}
	
	public void saveChats(){
		//nullToString("");
		FileManager fm = null;
		for (Chat c : chats){
			if (c.getName() != "GLOBAL"){
				fm = new FileManager(chat, "chats/", c.getChatID() + "");
			}else{
				fm = new FileManager(chat, "chats/", "global");
			}
			if (!fm.exists()){
				fm.createFile();
			}
			fm.getYAML().set("name", c.getName());
			fm.getYAML().set("prefix", nullToString(c.getPrefix()));
			fm.getYAML().set("suffix", nullToString(c.getSuffix()));
			fm.getYAML().set("messageColour", nullToString(c.getMessageColour()));
			LinkedList <String> list = new LinkedList <String> ();
			for (UUID uuid : c.getAdmins()){
				list.add(uuid.toString());
			}
			if (list.size() > 0){
				fm.getYAML().set("admins", list);
			}else{
				fm.getYAML().set("admins", nullToString(null));
			}
			for (UUID uuid : c.getModerators()){
				list.add(uuid.toString());
			}
			if (list.size() > 0){
				fm.getYAML().set("moderators", list);
			}else{
				fm.getYAML().set("moderators", nullToString(null));
			}
			fm.getYAML().set("displayRank", c.getDisplayRank());
			fm.getYAML().set("alias", c.getAlias());
			fm.getYAML().set("aliasApproved", c.isAliasApproved());
			fm.getYAML().set("joinable", c.isJoinable());
			fm.saveYAML();
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
		fm.getYAML().set("prefix", nullToString(c.getPrefix()));
		fm.getYAML().set("suffix", nullToString(c.getSuffix()));
		fm.getYAML().set("messageColour", nullToString(c.getMessageColour()));
		LinkedList <String> list = new LinkedList <String> ();
		for (UUID uuid : c.getAdmins()){
			list.add(uuid.toString());
		}
		if (list.size() > 0){
			fm.getYAML().set("admins", list);
		}else{
			fm.getYAML().set("admins", nullToString(null));
		}
		for (UUID uuid : c.getModerators()){
			list.add(uuid.toString());
		}
		if (list.size() > 0){
			fm.getYAML().set("moderators", list);
		}else{
			fm.getYAML().set("moderators", nullToString(null));
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
			String prefix = stringToNull(fm.getYAML().getString("prefix"));
			String suffix = stringToNull(fm.getYAML().getString("suffix"));
			String chatColour = stringToNull(fm.getYAML().getString("chatColour"));
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
				// TODO Auto-generated catch block
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
		fm.getYAML().set("prefix", nullToString(chatter.getPrefix()));
		fm.getYAML().set("suffix", nullToString(chatter.getSuffix()));
		fm.getYAML().set("chatColour", nullToString(chatter.getChatColour()));
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
	
}
