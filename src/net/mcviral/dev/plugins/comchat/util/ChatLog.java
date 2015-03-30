package net.mcviral.dev.plugins.comchat.util;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import net.mcviral.dev.plugins.comchat.chat.Chat;
import net.mcviral.dev.plugins.comchat.main.ComChat;
import net.md_5.bungee.api.ChatColor;

public class ChatLog {
	
	private ComChat chat = null;
	private FileManager fm = null;
	private Chat c = null;
	
	public ChatLog(ComChat chat, Chat c){
		this.chat = chat;
		this.c = c;
		createDirectories();
	}
	
	public void createDirectories(){
		File f = new File(chat.getDataFolder() + "/logs/" + c.getName() + "/");
		if (!f.exists()){
			f.mkdirs();
		}
	}
	
	public void log(String message){
		String date = getDate();
		if (date == null){
			date = "dump";
		}
		try{
			fm = new FileManager(chat, "logs/" + c.getName() + "/", date);
			if (!fm.exists()){
				boolean created = fm.createFile();
				if (!created){
					
				}
			}
			fm.getYAML().set("" + getTimestamp(), ChatColor.stripColor(message));
			fm.saveYAML();
		}catch (Exception e){
			chat.log.info("ERROR: Couldn't save chat log");
			e.printStackTrace();
		}
	}
	
	public Long getTimestamp(){
		return Calendar.getInstance().getTimeInMillis();
	}
	
	public String getDate(){
		Calendar cal = Calendar.getInstance();
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = null;
		date = sdf.format(cal.getTime());
		chat.log.info(date);
		return date;
	}
	
	public Chat getChat(){
		return c;
	}
	
	public void setChat(Chat c){
		this.c = c;
	}
	
}
