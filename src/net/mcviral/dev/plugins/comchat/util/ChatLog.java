package net.mcviral.dev.plugins.comchat.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import net.mcviral.dev.plugins.comchat.chat.Chat;
import net.mcviral.dev.plugins.comchat.main.ComChat;

public class ChatLog {
	
	private ComChat chat = null;
	private FileManager fm = null;
	private Chat c = null;
	
	public ChatLog(ComChat chat, Chat c){
		this.chat = chat;
		this.c = c;
	}
	
	public void createDirectories(){
		File f = new File(chat.getDataFolder() + "logs/" + c.getName() + "/");
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
				fm.createFile();
			}
			int next = fm.getYAML().getInt("next");
			if (fm.getYAML().getInt("next") == 0){
				next = 1;
			}
			fm.getYAML().set(next + "", message);
			fm.saveYAML();
			fm = null;
		}catch (Exception e){
			chat.log.info("ERROR: Couldn't save chat log");
			e.printStackTrace();
		}
	}
	
	public String getDate(){
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd");
		String date = null;
		date = sdf.format(cal.getTime());
		return date;
	}
	
	public Chat getChat(){
		return c;
	}
	
	public void setChat(Chat c){
		this.c = c;
	}
	
}
