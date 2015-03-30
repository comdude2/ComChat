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

package net.mcviral.dev.plugins.comchat.main;

import net.mcviral.dev.plugins.comchat.chat.Chat;
import net.mcviral.dev.plugins.comchat.chat.Chatter;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Listeners implements Listener{
	
	private ComChat chat = null;
	
	public Listeners(ComChat chat){
		this.chat = chat;
	}
	
	public void register(){
		chat.getServer().getPluginManager().registerEvents(this, chat);
	}
	
	public void unregister(){
		//Unregister all events
		HandlerList.unregisterAll(chat);
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onCommandPreProcess(PlayerCommandPreprocessEvent event){
		for (Chat c : chat.getChatController().getChats()){
			if (event.getMessage().startsWith(c.getAlias() + " ")){
				//Forward to that chat
				event.setCancelled(true);
				Chatter chatter = chat.getChatController().getChatter(event.getPlayer().getUniqueId());
				if (chatter != null){
					String message = event.getMessage().substring(c.getAlias().length(), event.getMessage().length() - 1);//no -1 on alias length as we need to remove the space too.
					chat.getChatController().chatInDifferentToFocus(event.getPlayer(), chatter, c, message);
				}else{
					return;
				}
				break;
			}
		}
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
		if (!chat.getChatController().chatterIsOnFile(event.getPlayer().getUniqueId())){
			chat.getChatController().getChatters().add(new Chatter(event.getPlayer().getUniqueId(), chat.getChatController().getGlobalChat()));
		}else{
			chat.getChatController().loadChatter(event.getPlayer().getUniqueId());
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event){
		Chatter c = chat.getChatController().getChatter(event.getPlayer().getUniqueId());
		if (c != null){
			chat.getChatController().saveChatter(c);
			chat.getChatController().getChatters().remove(c);
		}//No chatter?
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerKick(PlayerKickEvent event){
		Chatter c = chat.getChatController().getChatter(event.getPlayer().getUniqueId());
		if (c != null){
			chat.getChatController().saveChatter(c);
			chat.getChatController().getChatters().remove(c);
		}//No chatter?
	}
	
}
