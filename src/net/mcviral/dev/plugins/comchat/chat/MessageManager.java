package net.mcviral.dev.plugins.comchat.chat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.mcviral.dev.plugins.comchat.main.ComChat;
import net.mcviral.dev.plugins.comchat.util.ChatLog;

public class MessageManager {
	
	private ComChat chat = null;
	private Chat c = null;
	
	public MessageManager(ComChat chat){
		this.chat = chat;
		c = new Chat(60000, "MESSAGE");
		c.setChatlog(new ChatLog(chat, c));
	}
	
	public void message(Player p, Player r, String message){
		if (p.hasPermission("chat.colour")){
			c.getChatlog().log(ChatColor.GREEN + p.getName() + ChatColor.LIGHT_PURPLE + " > " + ChatColor.GREEN + r.getName() + ChatColor.WHITE + ": " + ChatColor.YELLOW + message);
			p.sendMessage(ChatColor.GREEN + "You" + ChatColor.LIGHT_PURPLE + " > " + ChatColor.GREEN + r.getName() + ChatColor.WHITE + ": " + ChatColor.YELLOW + chat.getChatController().colour(message));
			r.sendMessage(ChatColor.GREEN + p.getName() + ChatColor.LIGHT_PURPLE + " > " + ChatColor.GREEN + "You" + ChatColor.WHITE + ": " + ChatColor.YELLOW + chat.getChatController().colour(message));
			for (Player ex : chat.getServer().getOnlinePlayers()){
				Chatter chatter = chat.getChatController().getChatter(ex.getUniqueId());
				if (chatter != null){
					if (chatter.getSpy()){
						ex.sendMessage(ChatColor.GREEN + p.getName() + ChatColor.LIGHT_PURPLE + " > " + ChatColor.GREEN + r.getName() + ChatColor.WHITE + ": " + ChatColor.YELLOW + chat.getChatController().colour(message));
					}
				}
			}
		}else{
			c.getChatlog().log(ChatColor.GREEN + p.getName() + ChatColor.LIGHT_PURPLE + " > " + ChatColor.GREEN + r.getName() + ChatColor.WHITE + ": " + ChatColor.YELLOW + message);
			p.sendMessage(ChatColor.GREEN + "You" + ChatColor.LIGHT_PURPLE + " > " + ChatColor.GREEN + r.getName() + ChatColor.WHITE + ": " + ChatColor.YELLOW + message);
			r.sendMessage(ChatColor.GREEN + p.getName() + ChatColor.LIGHT_PURPLE + " > " + ChatColor.GREEN + "You" + ChatColor.WHITE + ": " + ChatColor.YELLOW + message);
			for (Player ex : chat.getServer().getOnlinePlayers()){
				Chatter chatter = chat.getChatController().getChatter(ex.getUniqueId());
				if (chatter != null){
					if (chatter.getSpy()){
						ex.sendMessage(ChatColor.GREEN + p.getName() + ChatColor.LIGHT_PURPLE + " > " + ChatColor.GREEN + r.getName() + ChatColor.WHITE + ": " + ChatColor.YELLOW + message);
					}
				}
			}
		}
	}
	
}
