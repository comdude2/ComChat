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
