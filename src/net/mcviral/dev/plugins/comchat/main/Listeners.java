package net.mcviral.dev.plugins.comchat.main;

import net.mcviral.dev.plugins.comchat.chat.Chatter;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Listeners implements Listener{
	
	private ComChat chat = null;
	
	public Listeners(ComChat chat){
		this.chat = chat;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent event){
		if (!event.getMessage().startsWith("/")){
			event.setCancelled(true);
			chat.getChatController().chat(event.getPlayer(), event.getMessage());
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		chat.getChatController().getChatters().add(new Chatter(event.getPlayer().getUniqueId(), chat.getChatController().getGlobalChat()));
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event){
		
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerKick(PlayerKickEvent event){
		
	}
	
}
