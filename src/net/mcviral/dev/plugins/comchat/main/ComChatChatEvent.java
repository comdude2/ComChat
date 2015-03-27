package net.mcviral.dev.plugins.comchat.main;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ComChatChatEvent extends Event{
	
	private static final HandlerList handlers = new HandlerList();
	
	
	public ComChatChatEvent(){
		
	}
	
	public HandlerList getHandlers() {
        return handlers;
    }
 
    public static HandlerList getHandlerList() {
        return handlers;
    }

}
