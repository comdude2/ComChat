package net.mcviral.dev.plugins.comchat.main;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class Listeners implements Listener{
	
	@SuppressWarnings("unused")
	private ComChat chat = null;
	
	public Listeners(ComChat chat){
		this.chat = chat;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent event){
		if (!event.getMessage().startsWith("/")){
			event.setCancelled(true);
			
		}
	}
	
}
