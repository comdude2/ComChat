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

import java.io.File;

import net.mcviral.dev.plugins.comchat.chat.ChatController;
import net.mcviral.dev.plugins.comchat.util.Log;

import org.bukkit.plugin.java.JavaPlugin;

public class ComChat extends JavaPlugin{
	
	private Listeners listeners = null;
	private ChatController chatcontroller = null;
	public Log log = null;
	
	private boolean loadedBefore = false;
	
	public void onEnable(){
		this.saveDefaultConfig();
		setupFolders();
		log = new Log(this.getDescription().getName());
		listeners = new Listeners(this);
		chatcontroller = new ChatController(this);
		if(!loadedBefore){
			this.getServer().getPluginManager().registerEvents(listeners, this);
		}
		this.getLogger().info(this.getDescription().getName() + " Enabled!");
	}
	
	public void onDisable(){
		this.getLogger().info(this.getDescription().getName() + " Disabled!");
	}
	
	public void setupFolders(){
		File folder = new File(this.getDataFolder() + "/chats/");
		if (!folder.exists()){
			folder.mkdirs();
		}
		folder = new File(this.getDataFolder() + "/chatters/");
		if (!folder.exists()){
			folder.mkdirs();
		}
	}
	
	public ChatController getChatController(){
		return chatcontroller;
	}
	
}
