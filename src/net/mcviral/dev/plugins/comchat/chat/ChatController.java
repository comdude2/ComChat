package net.mcviral.dev.plugins.comchat.chat;

import java.util.LinkedList;
import java.util.UUID;

import org.bukkit.entity.Player;

import net.mcviral.dev.plugins.comchat.main.ComChat;

public class ChatController {
	
	private ComChat chat = null;
	private Chat globalchat = null;
	private LinkedList <Chat> chats = new LinkedList <Chat> ();
	private LinkedList <Chatter> chatters = new LinkedList <Chatter> ();
	
	public ChatController(ComChat chat){
		this.chat = chat;
	}
	
	//Chat methods that chat in the right channel
	
	public void chat(Player player, String message){
		Chatter chatter = getChatter(player.getUniqueId());
		if (chatter != null){
			Chat chatToSend = chatter.getFocus();
			if (chatToSend == globalchat){
				
			}else{
				LinkedList <Player> recipients = new LinkedList <Player> ();
				for (Chatter c : chatters){
					if (c.getChats().contains(chatToSend)){
						Player p = chat.getServer().getPlayer(c.getUuid());
						if (p != null){
							recipients.add(p);
						}
					}
				}
				String msg = formatMessage(player, chatter, chatToSend, message);
				
				for (Player p : recipients){
					p.sendMessage(msg);
				}
			}
		}else{
			//Failed to send message.
		}
	}
	
	public String formatMessage(Player player, Chatter chatter, Chat chat, String message){
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

	public Chat getGlobalchat() {
		return globalchat;
	}

	public void setGlobalchat(Chat globalchat) {
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
	
}
