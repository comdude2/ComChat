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

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ComChatChatEvent extends Event{
	
	private static final HandlerList handlers = new HandlerList();
	private String prefix = null;
	
	public ComChatChatEvent(String prefix){
		this.prefix = prefix;
	}
	
	public String getPrefix(){
		return prefix;
	}
	
	public void setPrefix(String prefix){
		this.prefix = prefix;
	}
	
	public HandlerList getHandlers() {
        return handlers;
    }
 
    public static HandlerList getHandlerList() {
        return handlers;
    }

}
