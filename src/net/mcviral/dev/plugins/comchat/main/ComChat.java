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
import java.util.Calendar;
import java.util.LinkedList;
import java.util.UUID;

import net.mcviral.dev.plugins.comchat.chat.Chat;
import net.mcviral.dev.plugins.comchat.chat.ChatController;
import net.mcviral.dev.plugins.comchat.chat.Chatter;
import net.mcviral.dev.plugins.comchat.util.Log;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ComChat extends JavaPlugin{
	
	private Listeners listeners = null;
	private ChatController chatcontroller = null;
	public Log log = null;
	
	public void onEnable(){
		this.saveDefaultConfig();
		setupFolders();
		log = new Log(this.getDescription().getName());
		listeners = new Listeners(this);
		listeners.register();
		chatcontroller = new ChatController(this);
		this.getLogger().info(this.getDescription().getName() + " Enabled!");
	}
	
	public void onDisable(){
		this.getChatController().saveChats();
		this.getChatController().saveChatters();
		this.getChatController().saveGroups();
		listeners.unregister();
		this.getLogger().info(this.getDescription().getName() + " Disabled!");
	}
	
	public boolean reload(){
		try{
			this.getChatController().saveChats();
			this.getChatController().saveChatters();
			this.getChatController().saveGroups();
			listeners.unregister();
			this.getLogger().info(this.getDescription().getName() + " Disabled!");
			this.saveDefaultConfig();
			setupFolders();
			log = new Log(this.getDescription().getName());
			listeners = new Listeners(this);
			listeners.register();
			chatcontroller = new ChatController(this);
			this.getLogger().info(this.getDescription().getName() + " Enabled!");
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
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
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//sender.sendMessage("");
		//sender.sendMessage(ChatColor.RED + "Couldn't find you on file! What's going on? (Contact a warden)");
		if (sender instanceof Player){
			Player p = (Player) sender;
			if (cmd.getName().equalsIgnoreCase("chat")) {
				if (args.length > 0){
					if (args.length == 1){
						if (args[0].equalsIgnoreCase("restart")){
							if (sender.hasPermission("chat.admin")){
								boolean done = reload();
								if (done){
									sender.sendMessage(ChatColor.GREEN + "Reloaded!");
								}else{
									sender.sendMessage(ChatColor.RED + "Reload failed, see console for more details.");
								}
							}else{
								//no perms
								noPerms(sender);
							}
						}else if (args[0].equalsIgnoreCase("reload")){
							if (sender.hasPermission("chat.admin")){
								this.getChatController().loadChats();
								this.getChatController().loadChatters();
								sender.sendMessage(ChatColor.GREEN + "Reloaded!");
							}else{
								//no perms
								noPerms(sender);
							}
						}else if (args[0].equalsIgnoreCase("save")){
							if (sender.hasPermission("chat.admin")){
								this.getChatController().saveChats();
								this.getChatController().saveChatters();
								sender.sendMessage(ChatColor.GREEN + "Saved all objects!");
							}else{
								noPerms(sender);
							}
						}else if (args[0].equalsIgnoreCase("spy")){
							if (sender.hasPermission("chat.spy")){
								Chatter chatter = this.getChatController().getChatter(p.getUniqueId());
								if (chatter != null){
									if (chatter.getSpy()){
										chatter.setSpy(false);
										sender.sendMessage(ChatColor.YELLOW + "Your chat spy is now disabled.");
									}else{
										chatter.setSpy(true);
										sender.sendMessage(ChatColor.YELLOW + "Your chat spy is now enabled.");
									}
								}else{
									//Can't find them
									sender.sendMessage(ChatColor.RED + "Couldn't find you on file! What's going on? (Contact a warden)");
								}
							}else{
								noPerms(sender);
							}
						}else if (args[0].equalsIgnoreCase("toggle")){
							Chatter chatter = this.getChatController().getChatter(p.getUniqueId());
							if (chatter != null){
								chatter.setFocus(this.getChatController().getGlobalChat());
								sender.sendMessage(ChatColor.YELLOW + "Your chat focus is now the global chat channel.");
							}else{
								//can't find them.
								sender.sendMessage(ChatColor.RED + "Couldn't find you on file! What's going on? (Contact a warden)");
							}
						}else if (args[0].equalsIgnoreCase("help")){
							help(sender);
						}else{
							help(sender);
						}
					}else if (args.length == 2){
						if (args[0].equalsIgnoreCase("join")){
							Chatter chatter = this.getChatController().getChatter(p.getUniqueId());
							if (chatter != null){
								Chat c = this.getChatController().getChat(args[1]);
								if (c != null){
									if (!chatter.getChats().contains(c)){
										if (c.isJoinable()){
											LinkedList <Chat> chats = chatter.getChats();
											chats.add(c);
											chatter.setChats(chats);
											//Added - Notify members
											p.sendMessage(ChatColor.GREEN + "You have joined " + c.getName());
											LinkedList <Player> recipients = new LinkedList <Player> ();
											for (Chatter cter : this.getChatController().getChatters()){
												if (cter.getChats().contains(c)){
													Player player = this.getServer().getPlayer(cter.getUuid());
													if (player != null){
														recipients.add(player);
													}
												}
											}
											String msg = ChatColor.YELLOW + p.getName() + ChatColor.GREEN + " joined the chat (" + c.getName() + ")";
											log.info(p.getName() + " joined " + c.getName());
											for (Player player : recipients){
												player.sendMessage(msg);
											}
										}else{
											//They need an invite
											sender.sendMessage(ChatColor.RED + "You can only join this chat if you're invited to do so.");
										}
									}else{
										//What are they thinking...
										sender.sendMessage(ChatColor.RED + "You're already in that chat you doofus.");
									}
								}else{
									//Can't find chat.
									sender.sendMessage(ChatColor.RED + "Couldn't find the specified chat, are you sure it exists?");
								}
							}else{
								//can't find them.
								sender.sendMessage(ChatColor.RED + "Couldn't find you on file! What's going on? (Contact a warden)");
							}
						}else if (args[0].equalsIgnoreCase("leave")){
							Chatter chatter = this.getChatController().getChatter(p.getUniqueId());
							if (chatter != null){
								Chat c = this.getChatController().getChat(args[1]);
								if (c != null){
									if (chatter.getChats().contains(c)){
										if (!c.getName().equals("GLOBAL")){
											LinkedList <Chat> chats = chatter.getChats();
											chats.remove(c);
											chatter.setChats(chats);
											//Left - Notify members
											p.sendMessage(ChatColor.RED + "You have left " + c.getName());
											LinkedList <Player> recipients = new LinkedList <Player> ();
											for (Chatter cter : this.getChatController().getChatters()){
												if (cter.getChats().contains(c)){
													Player player = this.getServer().getPlayer(cter.getUuid());
													if (player != null){
														recipients.add(player);
													}
												}
											}
											String msg = ChatColor.YELLOW + p.getName() + ChatColor.RED + " left the chat (" + c.getName() + ")";
											log.info(p.getName() + " left " + c.getName());
											for (Player player : recipients){
												player.sendMessage(msg);
											}
										}else{
											//They can't leave global chat
											sender.sendMessage(ChatColor.RED + "You cannot leave the global chat channel.");
										}
									}else{
										//What are they thinking...
										sender.sendMessage(ChatColor.RED + "You're not in that chat, therefore you cannot leave it.");
									}
								}else{
									//Can't find chat.
									sender.sendMessage(ChatColor.RED + "Couldn't find chat, are you sure it exists?");
								}
							}else{
								//can't find them.
								sender.sendMessage(ChatColor.RED + "Couldn't find you on file! What's going on? (Contact a warden)");
							}
						}else if (args[0].equalsIgnoreCase("toggle")){
							Chatter chatter = this.getChatController().getChatter(p.getUniqueId());
							if (chatter != null){
								Chat c = this.getChatController().getChat(args[1]);
								if (c != null){
									if (chatter.getChats().contains(c)){
										chatter.setFocus(c);
										sender.sendMessage(ChatColor.YELLOW + "Your chat focus is now " + c.getName() + " chat.");
									}else{
										//What are they thinking...
										sender.sendMessage(ChatColor.RED + "You're not in that chat, therefore you can't toggle to it.");
									}
								}else{
									//Can't find chat.
									sender.sendMessage(ChatColor.RED + "Couldn't find chat, are you sure it exists?");
								}
							}else{
								//can't find them.
								sender.sendMessage(ChatColor.RED + "Couldn't find you on file! What's going on? (Contact a warden)");
							}
						}else if (args[0].equalsIgnoreCase("create")){
							if (sender.hasPermission("chat.create")){
								int id = this.getChatController().getNextChatID();
								Chat c = new Chat(id, args[1]);
								LinkedList <UUID> list = new LinkedList <UUID> ();
								list.add(p.getUniqueId());
								c.setAdmins(list);
								c.setDisplayRank(false);
								c.setJoinable(false);
								c.setMessageColour("&b");
								c.setPrefix("&f(&b" + c.getName() + "&f)");
								c.setAliasApproved(false);
								boolean created = this.getChatController().createChat(c);
								if (created){
									this.getChatController().setNextChatID(this.getChatController().getNextChatID() + 1);
									sender.sendMessage(ChatColor.GREEN + "Chat created.");
								}else{
									sender.sendMessage(ChatColor.RED + "Chat not created, the name you picked is already in use.");
								}
							}else{
								noPerms(sender);
							}
						}else if (args[0].equalsIgnoreCase("delete")){
							//TODO Add this
							sender.sendMessage(ChatColor.RED + "This is not implemented yet.");
						}else if (args[0].equalsIgnoreCase("admin")){
							//admin <chat> (Makes this person an admin of the chat, needs chat.admin)
							if (p.hasPermission("chat.admin")){
								Chat c = this.getChatController().getChat(args[1]);
								if (c != null){
									if (c.getAdmins().contains(p.getUniqueId())){
										LinkedList <UUID> admins = c.getAdmins();
										admins.remove(p.getUniqueId());
										c.setAdmins(admins);
										p.sendMessage(ChatColor.YELLOW + "You're no longer an admin of " + c.getName() + " chat.");
										this.getChatController().saveChat(c);
									}else{
										LinkedList <UUID> admins = c.getAdmins();
										admins.add(p.getUniqueId());
										c.setAdmins(admins);
										p.sendMessage(ChatColor.YELLOW + "You're now an admin of " + c.getName() + " chat.");
										this.getChatController().saveChat(c);
									}
								}else{
									//Null chat
									p.sendMessage(ChatColor.RED + "Couldn't find chat, are you sure it exists?");
								}
							}else{
								noPerms(sender);
							}
						}else{
							help(sender);
						}
					}else if (args.length == 3){
						if (args[0].equalsIgnoreCase("kick")){
							//<player> <chat>
							Chatter chatter = this.getChatController().getChatter(p.getUniqueId());
							if (chatter != null){
								Chat c = this.getChatController().getChat(args[2]);
								if (c != null){
									if (p.hasPermission("chat.admin")){
										//Admin of server
									}else if (c.getAdmins().contains(chatter.getUuid())){
										//Admin of that chat
									}else{
										noPerms(sender);
										return true;
									}
									if (c != this.getChatController().getGlobalChat()){
										Player player = this.getServer().getPlayer(args[1]);
										if (player != null){
											Chatter target = this.getChatController().getChatter(player.getUniqueId());
											if (target != null){
												if (target.getChats().contains(c)){
													LinkedList <Chat> chats = target.getChats();
													chats.remove(c);
													target.setChats(chats);
													this.getChatController().saveChatter(chatter);
													p.sendMessage(ChatColor.RED + "You have been kicked from " + c.getName());
													LinkedList <Player> recipients = new LinkedList <Player> ();
													for (Chatter cter : this.getChatController().getChatters()){
														if (cter.getChats().contains(c)){
															player = this.getServer().getPlayer(cter.getUuid());
															if (player != null){
																recipients.add(player);
															}
														}
													}
													String msg = ChatColor.YELLOW + p.getName() + ChatColor.RED + " was kicked from the chat (" + c.getName() + ")";
													log.info(p.getName() + " was kicked from " + c.getName());
													for (Player p2 : recipients){
														p2.sendMessage(msg);
													}
												}else{
													//They're not in that chat.
													sender.sendMessage(ChatColor.RED + "That person is not in the specified chat.");
												}
											}else{
												//Couldn't find target's chatter object
												sender.sendMessage(ChatColor.RED + "Couldn't find player.");
											}
										}else{
											//Couldn't find target
											sender.sendMessage(ChatColor.RED + "Couldn't find player.");
										}
									}else{
										sender.sendMessage(ChatColor.RED + "You cannot kick someone from the global chat channel.");
									}
								}
							}
						}else if (args[0].equalsIgnoreCase("admin")){
							//admin <chat> <player>
							Chat c = this.getChatController().getChat(args[1]);
							if (c != null){
								if ((c.getAdmins().contains(p.getUniqueId())) || (p.hasPermission("chat.admin"))){
									Player t = this.getServer().getPlayer(args[2]);
									if (t != null){
										if (c.getAdmins().contains(t.getUniqueId())){
											LinkedList <UUID> admins = c.getAdmins();
											admins.remove(t.getUniqueId());
											c.setAdmins(admins);
											t.sendMessage(ChatColor.YELLOW + "You're no longer an admin of " + c.getName() + " chat.");
											p.sendMessage(ChatColor.YELLOW + t.getName() + " is no longer an admin of " + c.getName() + " chat.");
											this.getChatController().saveChat(c);
										}else{
											LinkedList <UUID> admins = c.getAdmins();
											admins.add(t.getUniqueId());
											c.setAdmins(admins);
											p.sendMessage(ChatColor.YELLOW + "You're now an admin of " + c.getName() + " chat.");
											p.sendMessage(ChatColor.YELLOW + t.getName() + " is now an admin of " + c.getName() + " chat.");
											this.getChatController().saveChat(c);
										}
									}else{
										//Couldn't find target
										sender.sendMessage(ChatColor.RED + "Couldn't find player.");
									}
								}else{
									sender.sendMessage(ChatColor.RED + "You need to be an admin of this chat or a global chat admin to manage admins for this chat.");
								}
							}else{
								p.sendMessage(ChatColor.RED + "Couldn't find chat.");
							}
						}else if (args[0].equalsIgnoreCase("mute")){
							//<player> <time>
							Player p2 = this.getServer().getPlayer(args[1]);
							if (p2 != null){
								Chatter c = this.getChatController().getChatter(p2.getUniqueId());
								if (c != null){
									if (p.hasPermission("chat.moderate")){
										if (!p2.hasPermission("chat.moderate")){
											String time = args[2];
											Calendar cal = Calendar.getInstance();
											if (time.contains("s")){
												//seconds
												time = time.replaceAll("[^\\d.]", "");
												try{
													int ntime = Integer.parseInt(time);
													cal.add(Calendar.SECOND, ntime);
													c.setMuted(true);
													c.setMutedUntil(cal.getTimeInMillis());
													this.getChatController().saveChatter(c);
													this.getChatController().getGlobalChat().getChatlog().log(p.getName() + " muted " + p2.getName() + " for " + time);
													p.sendMessage(ChatColor.GREEN + "Player muted.");
													p2.sendMessage(ChatColor.RED + "You have been muted by " + ChatColor.YELLOW + p.getName() + ChatColor.RED + " for " + ChatColor.YELLOW + time + " second(s)");
													for (Player p3 : this.getServer().getOnlinePlayers()){
														if (p3.hasPermission("chat.moderate")){
															p3.sendMessage(ChatColor.RED + p2.getName() + ChatColor.YELLOW + " was muted for " + ChatColor.AQUA + time + " second(s) by " + ChatColor.GREEN + p.getName());
														}
													}
												}catch (Exception e){
													p.sendMessage(ChatColor.DARK_RED + "Error: " + e.getMessage() + " check console for full details.");
													e.printStackTrace();
												}
											}else if (time.contains("m")){
												//minutes
												time = time.replaceAll("[^\\d.]", "");
												try{
													int ntime = Integer.parseInt(time);
													cal.add(Calendar.MINUTE, ntime);
													c.setMuted(true);
													c.setMutedUntil(cal.getTimeInMillis());
													this.getChatController().saveChatter(c);
													this.getChatController().getGlobalChat().getChatlog().log(p.getName() + " muted " + p2.getName() + " for " + time);
													p.sendMessage(ChatColor.GREEN + "Player muted.");
													p2.sendMessage(ChatColor.RED + "You have been muted by " + ChatColor.YELLOW + p.getName() + ChatColor.RED + " for " + ChatColor.YELLOW + time + " minute(s)");
													for (Player p3 : this.getServer().getOnlinePlayers()){
														if (p3.hasPermission("chat.moderate")){
															p3.sendMessage(ChatColor.RED + p2.getName() + ChatColor.YELLOW + " was muted for " + ChatColor.AQUA + time + " minute(s) by " + ChatColor.GREEN + p.getName());
														}
													}
												}catch (Exception e){
													p.sendMessage(ChatColor.DARK_RED + "Error: " + e.getMessage() + " check console for full details.");
													e.printStackTrace();
												}
											}else if (time.contains("h")){
												//hours
												time = time.replaceAll("[^\\d.]", "");
												try{
													int ntime = Integer.parseInt(time);
													cal.add(Calendar.HOUR, ntime);
													c.setMuted(true);
													c.setMutedUntil(cal.getTimeInMillis());
													this.getChatController().saveChatter(c);
													this.getChatController().getGlobalChat().getChatlog().log(p.getName() + " muted " + p2.getName() + " for " + time);
													p.sendMessage(ChatColor.GREEN + "Player muted.");
													p2.sendMessage(ChatColor.RED + "You have been muted by " + ChatColor.YELLOW + p.getName() + ChatColor.RED + " for " + ChatColor.YELLOW + time + " hours(s)");
													for (Player p3 : this.getServer().getOnlinePlayers()){
														if (p3.hasPermission("chat.moderate")){
															p3.sendMessage(ChatColor.RED + p2.getName() + ChatColor.YELLOW + " was muted for " + ChatColor.AQUA + time + " hour(s) by " + ChatColor.GREEN + p.getName());
														}
													}
												}catch (Exception e){
													p.sendMessage(ChatColor.DARK_RED + "Error: " + e.getMessage() + " check console for full details.");
													e.printStackTrace();
												}
											}else if (time.contains("d")){
												//days
												time = time.replaceAll("[^\\d.]", "");
												try{
													int ntime = Integer.parseInt(time);
													cal.add(Calendar.DAY_OF_YEAR, ntime);
													c.setMuted(true);
													c.setMutedUntil(cal.getTimeInMillis());
													this.getChatController().saveChatter(c);
													this.getChatController().getGlobalChat().getChatlog().log(p.getName() + " muted " + p2.getName() + " for " + time);
													p.sendMessage(ChatColor.GREEN + "Player muted.");
													p2.sendMessage(ChatColor.RED + "You have been muted by " + ChatColor.YELLOW + p.getName() + ChatColor.RED + " for " + ChatColor.YELLOW + time + " days(s)");
													for (Player p3 : this.getServer().getOnlinePlayers()){
														if (p3.hasPermission("chat.moderate")){
															p3.sendMessage(ChatColor.RED + p2.getName() + ChatColor.YELLOW + " was muted for " + ChatColor.AQUA + time + " day(s) by " + ChatColor.GREEN + p.getName());
														}
													}
												}catch (Exception e){
													p.sendMessage(ChatColor.DARK_RED + "Error: " + e.getMessage() + " check console for full details.");
													e.printStackTrace();
												}
											}else if (time.contains("p")){
												//perm
												if (p.hasPermission("chat.admin")){
													c.setMuted(true);
													c.setMutedUntil(-1L);
													this.getChatController().saveChatter(c);
													this.getChatController().getGlobalChat().getChatlog().log(p.getName() + " muted " + p2.getName() + " permanently.");
													p.sendMessage(ChatColor.GREEN + "Player muted.");
													p2.sendMessage(ChatColor.RED + "You have been muted by " + ChatColor.YELLOW + p.getName() + ChatColor.RED + " for " + ChatColor.DARK_RED+ "PERMANENTLY");
													for (Player p3 : this.getServer().getOnlinePlayers()){
														if (p3.hasPermission("chat.moderate")){
															p3.sendMessage(ChatColor.RED + p2.getName() + ChatColor.YELLOW + " was muted permanently by " + ChatColor.GREEN + p.getName());
														}
													}
												}else{
													//Need to be admin to perm mute
													p.sendMessage(ChatColor.RED + "You need to be an admin to mute permanently.");
												}
											}else{
												//Unknown time format
												p.sendMessage(ChatColor.RED + "Unknown time format, formats are: s, m, h, d, p (p = permanent)");
											}
										}else{
											//Muting a mod or admin
											p.sendMessage(ChatColor.RED + "You can't mute a mod or admin.");
										}
									}else{
										//Not a mod
										p.sendMessage(ChatColor.RED + "You need to be a moderator to mute someone.");
									}
								}else{
									//chatter null
									p.sendMessage(ChatColor.RED + "Couldn't find chatter.");
								}
							}else{
								//player null
								p.sendMessage(ChatColor.RED + "Couldn't find player, are they online?");
							}
						}else if (args[0].equalsIgnoreCase("join")){
							Chatter chatter = this.getChatController().getChatter(p.getUniqueId());
							if (chatter != null){
								Chat c = this.getChatController().getChat(args[1]);
								String password = args[2];
								if (c != null){
									if (!chatter.getChats().contains(c)){
										if (c.isJoinable()){
											LinkedList <Chat> chats = chatter.getChats();
											chats.add(c);
											chatter.setChats(chats);
											this.getChatController().saveChatter(chatter);
											//Added - Notify members
											p.sendMessage(ChatColor.GREEN + "You have joined " + c.getName());
											LinkedList <Player> recipients = new LinkedList <Player> ();
											for (Chatter cter : this.getChatController().getChatters()){
												if (cter.getChats().contains(c)){
													Player player = this.getServer().getPlayer(cter.getUuid());
													if (player != null){
														recipients.add(player);
													}
												}
											}
											String msg = ChatColor.YELLOW + p.getName() + ChatColor.GREEN + " joined the chat (" + c.getName() + ")";
											log.info(p.getName() + " joined " + c.getName());
											for (Player player : recipients){
												player.sendMessage(msg);
											}
										}else{
											//They need to get password correct
											password = ChatController.generateMD5(password);
											if (password != null){
												if (password == c.getPassword()){
													LinkedList <Chat> chats = chatter.getChats();
													chats.add(c);
													chatter.setChats(chats);
													this.getChatController().saveChatter(chatter);
													//Added - Notify members
													p.sendMessage(ChatColor.GREEN + "You have joined " + c.getName());
													LinkedList <Player> recipients = new LinkedList <Player> ();
													for (Chatter cter : this.getChatController().getChatters()){
														if (cter.getChats().contains(c)){
															Player player = this.getServer().getPlayer(cter.getUuid());
															if (player != null){
																recipients.add(player);
															}
														}
													}
													String msg = ChatColor.YELLOW + p.getName() + ChatColor.GREEN + " joined the chat (" + c.getName() + ")";
													log.info(p.getName() + " joined " + c.getName());
													for (Player player : recipients){
														player.sendMessage(msg);
													}
												}else{
													//Password incorrect
													p.sendMessage(ChatColor.RED + "Password incorrect.");
												}
											}else{
												p.sendMessage(ChatColor.RED + "Failed to convert password to MD5 hash, please contact a warden.");
											}
										}
									}else{
										//What are they thinking...
										sender.sendMessage(ChatColor.RED + "You're already in that chat you doofus.");
									}
								}else{
									//Can't find chat.
									sender.sendMessage(ChatColor.RED + "Couldn't find the specified chat, are you sure it exists?");
								}
							}else{
								//can't find them.
								sender.sendMessage(ChatColor.RED + "Couldn't find you on file! What's going on? (Contact a warden)");
							}
						}else{
							help(sender);
						}
					}else if (args.length == 4){
						//TODO add assignment support (e.g. joinable, password etc)
						if (args[0].equalsIgnoreCase("set")){
							if (args[1].equalsIgnoreCase("password")){
								Chat c = this.getChatController().getChat(args[2]);
								if (c!= null){
									if (c.getAdmins().contains(p.getUniqueId())){
										String value = null;
										value = args[3];
										c.setPassword(ChatController.generateMD5(value));
										p.sendMessage(ChatColor.YELLOW + "Password changed.");
									}else{
										sender.sendMessage(ChatColor.RED + "You need to be an admin of this chat to modify it's settings.");
									}
								}else{
									sender.sendMessage(ChatColor.RED + "Couldn't find the specified chat, are you sure it exists?");
								}
							}else if (args[1].equalsIgnoreCase("joinable")){
								Chat c = this.getChatController().getChat(args[2]);
								if (c!= null){
									boolean value = false;
									value = Boolean.getBoolean(args[3]);
									c.setJoinable(value);
									if (value){
										p.sendMessage(ChatColor.YELLOW + "Joinable set to true.");
									}else{
										p.sendMessage(ChatColor.YELLOW + "Joinable set to false.");
									}
								}else{
									sender.sendMessage(ChatColor.RED + "Couldn't find the specified chat, are you sure it exists?");
								}
							}else if (args[1].equalsIgnoreCase("name")){
								Chat c = this.getChatController().getChat(args[2]);
								if (c!= null){
									if (c.getAdmins().contains(p.getUniqueId())){
										String value = null;
										value = args[3];
										c.setName(value);
										p.sendMessage(ChatColor.YELLOW + "Name changed.");
									}else{
										sender.sendMessage(ChatColor.RED + "You need to be an admin of this chat to modify it's settings.");
									}
								}else{
									sender.sendMessage(ChatColor.RED + "Couldn't find the specified chat, are you sure it exists?");
								}
							}else if (args[1].equalsIgnoreCase("prefix")){
								Chat c = this.getChatController().getChat(args[2]);
								if (c!= null){
									if (c.getAdmins().contains(p.getUniqueId())){
										String value = null;
										value = args[3];
										c.setPrefix(value);
										p.sendMessage(ChatColor.YELLOW + "Prefix changed.");
									}else{
										sender.sendMessage(ChatColor.RED + "You need to be an admin of this chat to modify it's settings.");
									}
								}else{
									sender.sendMessage(ChatColor.RED + "Couldn't find the specified chat, are you sure it exists?");
								}
							}else if ((args[1].equalsIgnoreCase("namecolour")) || (args[1].equalsIgnoreCase("namecolor"))){
								Chat c = this.getChatController().getChat(args[2]);
								if (c!= null){
									if (c.getAdmins().contains(p.getUniqueId())){
										String value = null;
										value = args[3];
										c.setNameColour(value);
										p.sendMessage(ChatColor.YELLOW + "Name colour changed.");
									}else{
										sender.sendMessage(ChatColor.RED + "You need to be an admin of this chat to modify it's settings.");
									}
								}else{
									sender.sendMessage(ChatColor.RED + "Couldn't find the specified chat, are you sure it exists?");
								}
							}else if (args[1].equalsIgnoreCase("suffix")){
								Chat c = this.getChatController().getChat(args[2]);
								if (c!= null){
									if (c.getAdmins().contains(p.getUniqueId())){
										String value = null;
										value = args[3];
										c.setSuffix(value);
										p.sendMessage(ChatColor.YELLOW + "Suffix changed.");
									}else{
										sender.sendMessage(ChatColor.RED + "You need to be an admin of this chat to modify it's settings.");
									}
								}else{
									sender.sendMessage(ChatColor.RED + "Couldn't find the specified chat, are you sure it exists?");
								}
							}else if ((args[1].equalsIgnoreCase("messagecolour")) || (args[1].equalsIgnoreCase("messagecolor"))){
								Chat c = this.getChatController().getChat(args[2]);
								if (c!= null){
									if (c.getAdmins().contains(p.getUniqueId())){
										String value = null;
										value = args[3];
										c.setMessageColour(value);
										p.sendMessage(ChatColor.YELLOW + "Message colour changed.");
									}else{
										sender.sendMessage(ChatColor.RED + "You need to be an admin of this chat to modify it's settings.");
									}
								}else{
									sender.sendMessage(ChatColor.RED + "Couldn't find the specified chat, are you sure it exists?");
								}
							}else if (args[1].equalsIgnoreCase("displayrank")){
								Chat c = this.getChatController().getChat(args[2]);
								if (c!= null){
									boolean value = false;
									value = Boolean.getBoolean(args[3]);
									c.setJoinable(value);
									if (value){
										p.sendMessage(ChatColor.YELLOW + "Display rank set to true.");
									}else{
										p.sendMessage(ChatColor.YELLOW + "Display rank set to false.");
									}
								}else{
									sender.sendMessage(ChatColor.RED + "Couldn't find the specified chat, are you sure it exists?");
								}
							}else if (args[1].equalsIgnoreCase("alias")){
								Chat c = this.getChatController().getChat(args[2]);
								if (c!= null){
									if (c.getAdmins().contains(p.getUniqueId())){
										String value = null;
										value = args[3];
										c.setAlias(value);
										p.sendMessage(ChatColor.YELLOW + "Alias changed.");
									}else{
										sender.sendMessage(ChatColor.RED + "You need to be an admin of this chat to modify it's settings.");
									}
								}else{
									sender.sendMessage(ChatColor.RED + "Couldn't find the specified chat, are you sure it exists?");
								}
							}else{
								help(sender);
							}
						}else if (args[0].equalsIgnoreCase("approve")){
							if (args[1].equalsIgnoreCase("alias")){
								Chat c = this.getChatController().getChat(args[2]);
								if (c!= null){
									boolean value = false;
									value = Boolean.getBoolean(args[3]);
									c.setAliasApproved(value);
									if (value){
										p.sendMessage(ChatColor.YELLOW + "Alias approved.");
									}else{
										p.sendMessage(ChatColor.YELLOW + "Alias revoked.");
									}
								}else{
									sender.sendMessage(ChatColor.RED + "Couldn't find the specified chat, are you sure it exists?");
								}
							}else{
								help(sender);
							}
						}else{
							help(sender);
						}
					}else{
						help(sender);
					}
				}else{
					help(sender);
				}
				return true;
			}else if (cmd.getName().equalsIgnoreCase("mute")){
				if (args.length > 0){
					if (args.length == 2){
						mute(p, sender, args);
					}else if (args.length < 2){
						p.sendMessage(ChatColor.RED + "Too few arguments.");
					}else if (args.length > 2){
						p.sendMessage(ChatColor.RED + "Too many arguments.");
					}else{
						//err....
						p.sendMessage(ChatColor.RED + "I have no idea how many arguments you just did...");
					}
				}else{
					p.sendMessage(ChatColor.YELLOW + "/mute <player> <time [s/m/h/d/p]>");
				}
				return true;
			}else if ((cmd.getName().equalsIgnoreCase("message")) || (cmd.getName().equalsIgnoreCase("msg")) || (cmd.getName().equalsIgnoreCase("m"))){
				if (args.length > 0){
					if (args.length == 2){
						Player r = this.getServer().getPlayer(args[0]);
						String message = args[1];
						if (r != null){
							this.getChatController().getMessageManager().message(p, r, message);
						}else{
							p.sendMessage(ChatColor.RED + "Couldn't find player.");
						}
					}else{
						p.sendMessage(ChatColor.YELLOW + "/msg <player> <message>");
					}
				}else{
					help(sender);
				}
				return true;
			}else{
				//Not my command
			}
		}
		return false;
	}
	
	public void help(CommandSender sender){
		//sender.sendMessage(ChatColor.RED + "");
		// + arg("")
		// + parameter("")
		sender.sendMessage(ChatColor.AQUA + "ComChat Help:");
		sender.sendMessage(ChatColor.YELLOW + "/chat toggle " + arg("chat"));
		sender.sendMessage(ChatColor.YELLOW + "/caht join" + parameter("chat") + arg("password"));
		sender.sendMessage(ChatColor.YELLOW + "/chat leave" + parameter("chat"));
		sender.sendMessage(ChatColor.YELLOW + "/chat create" + parameter("name"));
		sender.sendMessage(ChatColor.YELLOW + "/chat delete" + parameter("chat"));
		sender.sendMessage(ChatColor.YELLOW + "/chat kick" + parameter("player") + parameter("chat"));
		sender.sendMessage(ChatColor.YELLOW + "/chat admin" + parameter("chat") + parameter("player"));
		sender.sendMessage(ChatColor.YELLOW + "/chat set" + parameter("chat") + parameter("name|password|joinable|prefix|namecolour|suffix|messagecolour|displayrank|alias") + parameter("value"));
		sender.sendMessage(ChatColor.YELLOW + "/chat approve" + parameter("alias") + parameter("chat") + parameter("value"));
		if (sender.hasPermission("chat.moderate")){
			sender.sendMessage(ChatColor.GOLD + "/chat mute" + parameter("player") + parameter("time [s|m|h|d|p]"));
		}
		if (sender.hasPermission("chat.admin")){
			sender.sendMessage(ChatColor.DARK_RED + "/chat spy");
			sender.sendMessage(ChatColor.DARK_RED + "/chat save");
			sender.sendMessage(ChatColor.DARK_RED + "/chat reload");
			sender.sendMessage(ChatColor.DARK_RED + "/chat restart");
		}
	}
	
	private String arg(String arg){
		return ChatColor.BLUE + " [" + arg + "]";
	}
	
	private String parameter(String arg){
		return ChatColor.GREEN + " <" + arg + ">";
	}
	
	public void noPerms(CommandSender sender){
		sender.sendMessage(ChatColor.RED + "You don't have permission to perform this command.");
	}
	
	public void mute(Player p, CommandSender sender, String args[]){
		Player p2 = this.getServer().getPlayer(args[0]);
		if (p2 != null){
			Chatter c = this.getChatController().getChatter(p2.getUniqueId());
			if (c != null){
				if (p.hasPermission("chat.moderate")){
					if (!p2.hasPermission("chat.moderate")){
						String time = args[1];
						Calendar cal = Calendar.getInstance();
						if (time.contains("s")){
							//seconds
							time = time.replaceAll("[^\\d.]", "");
							try{
								int ntime = Integer.parseInt(time);
								cal.add(Calendar.SECOND, ntime);
								c.setMuted(true);
								c.setMutedUntil(cal.getTimeInMillis());
								this.getChatController().getGlobalChat().getChatlog().log(p.getName() + " muted " + p2.getName() + " for " + time);
								p.sendMessage(ChatColor.GREEN + "Player muted.");
								p2.sendMessage(ChatColor.RED + "You have been muted by " + ChatColor.YELLOW + p.getName() + ChatColor.RED + " for " + ChatColor.YELLOW + time + " second(s)");
								for (Player p3 : this.getServer().getOnlinePlayers()){
									if (p3.hasPermission("chat.moderate")){
										p3.sendMessage(ChatColor.RED + p2.getName() + ChatColor.YELLOW + " was muted for " + ChatColor.AQUA + time + " second(s) by " + ChatColor.GREEN + p.getName());
									}
								}
							}catch (Exception e){
								p.sendMessage(ChatColor.DARK_RED + "Error: " + e.getMessage() + " check console for full details.");
								e.printStackTrace();
							}
						}else if (time.contains("m")){
							//minutes
							time = time.replaceAll("[^\\d.]", "");
							try{
								int ntime = Integer.parseInt(time);
								cal.add(Calendar.MINUTE, ntime);
								c.setMuted(true);
								c.setMutedUntil(cal.getTimeInMillis());
								this.getChatController().getGlobalChat().getChatlog().log(p.getName() + " muted " + p2.getName() + " for " + time);
								p.sendMessage(ChatColor.GREEN + "Player muted.");
								p2.sendMessage(ChatColor.RED + "You have been muted by " + ChatColor.YELLOW + p.getName() + ChatColor.RED + " for " + ChatColor.YELLOW + time + " minute(s)");
								for (Player p3 : this.getServer().getOnlinePlayers()){
									if (p3.hasPermission("chat.moderate")){
										p3.sendMessage(ChatColor.RED + p2.getName() + ChatColor.YELLOW + " was muted for " + ChatColor.AQUA + time + " minute(s) by " + ChatColor.GREEN + p.getName());
									}
								}
							}catch (Exception e){
								p.sendMessage(ChatColor.DARK_RED + "Error: " + e.getMessage() + " check console for full details.");
								e.printStackTrace();
							}
						}else if (time.contains("h")){
							//hours
							time = time.replaceAll("[^\\d.]", "");
							try{
								int ntime = Integer.parseInt(time);
								cal.add(Calendar.HOUR, ntime);
								c.setMuted(true);
								c.setMutedUntil(cal.getTimeInMillis());
								this.getChatController().getGlobalChat().getChatlog().log(p.getName() + " muted " + p2.getName() + " for " + time);
								p.sendMessage(ChatColor.GREEN + "Player muted.");
								p2.sendMessage(ChatColor.RED + "You have been muted by " + ChatColor.YELLOW + p.getName() + ChatColor.RED + " for " + ChatColor.YELLOW + time + " hours(s)");
								for (Player p3 : this.getServer().getOnlinePlayers()){
									if (p3.hasPermission("chat.moderate")){
										p3.sendMessage(ChatColor.RED + p2.getName() + ChatColor.YELLOW + " was muted for " + ChatColor.AQUA + time + " hour(s) by " + ChatColor.GREEN + p.getName());
									}
								}
							}catch (Exception e){
								p.sendMessage(ChatColor.DARK_RED + "Error: " + e.getMessage() + " check console for full details.");
								e.printStackTrace();
							}
						}else if (time.contains("d")){
							//days
							time = time.replaceAll("[^\\d.]", "");
							try{
								int ntime = Integer.parseInt(time);
								cal.add(Calendar.DAY_OF_YEAR, ntime);
								c.setMuted(true);
								c.setMutedUntil(cal.getTimeInMillis());
								this.getChatController().getGlobalChat().getChatlog().log(p.getName() + " muted " + p2.getName() + " for " + time);
								p.sendMessage(ChatColor.GREEN + "Player muted.");
								p2.sendMessage(ChatColor.RED + "You have been muted by " + ChatColor.YELLOW + p.getName() + ChatColor.RED + " for " + ChatColor.YELLOW + time + " days(s)");
								for (Player p3 : this.getServer().getOnlinePlayers()){
									if (p3.hasPermission("chat.moderate")){
										p3.sendMessage(ChatColor.RED + p2.getName() + ChatColor.YELLOW + " was muted for " + ChatColor.AQUA + time + " day(s) by " + ChatColor.GREEN + p.getName());
									}
								}
							}catch (Exception e){
								p.sendMessage(ChatColor.DARK_RED + "Error: " + e.getMessage() + " check console for full details.");
								e.printStackTrace();
							}
						}else if (time.contains("p")){
							//perm
							if (p.hasPermission("chat.admin")){
								c.setMuted(true);
								c.setMutedUntil(-1L);
								this.getChatController().getGlobalChat().getChatlog().log(p.getName() + " muted " + p2.getName() + " permanently.");
								p.sendMessage(ChatColor.GREEN + "Player muted.");
								p2.sendMessage(ChatColor.RED + "You have been muted by " + ChatColor.YELLOW + p.getName() + ChatColor.RED + " for " + ChatColor.DARK_RED+ "PERMANENTLY");
								for (Player p3 : this.getServer().getOnlinePlayers()){
									if (p3.hasPermission("chat.moderate")){
										p3.sendMessage(ChatColor.RED + p2.getName() + ChatColor.YELLOW + " was muted permanently by " + ChatColor.GREEN + p.getName());
									}
								}
							}else{
								//Need to be admin to perm mute
								p.sendMessage(ChatColor.RED + "You need to be an admin to mute permanently.");
							}
						}else{
							//Unknown time format
							p.sendMessage(ChatColor.RED + "Unknown time format, formats are: s, m, h, d, p (p = permanent)");
						}
					}else{
						//Muting a mod or admin
						p.sendMessage(ChatColor.RED + "You can't mute a mod or admin.");
					}
				}else{
					//Not a mod
					p.sendMessage(ChatColor.RED + "You need to be a moderator to mute someone.");
				}
			}else{
				//chatter null
				p.sendMessage(ChatColor.RED + "Couldn't find chatter.");
			}
		}else{
			//player null
			p.sendMessage(ChatColor.RED + "Couldn't find player, are they online?");
		}
	}
	
}
