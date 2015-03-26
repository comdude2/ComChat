package net.mcviral.dev.plugins.comchat.main;

import net.mcviral.dev.plugins.comchat.chat.ChatController;

import org.bukkit.plugin.java.JavaPlugin;

public class ComChat extends JavaPlugin{
	
	private Listeners listeners = null;
	private ChatController chatcontroller = null;
	
	private boolean loadedBefore = false;
	
	public void onEnable(){
		this.saveDefaultConfig();
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
	
	public ChatController getChatController(){
		return chatcontroller;
	}
	
}
