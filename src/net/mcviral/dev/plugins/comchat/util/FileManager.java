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

package net.mcviral.dev.plugins.comchat.util;

import java.io.File;
import java.io.IOException;
//import java.io.InputStream;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class FileManager {

	private Plugin plugin;
	private FileConfiguration customConfig = null;
	private File customConfigFile = null;
	private String path = null;;
	private String filename = null;
	
	public FileManager(Plugin mplugin, String mpath, String file){
		plugin = mplugin;
		path = mpath;
		filename = file;
		customConfigFile = new File(plugin.getDataFolder() + "/" + path, filename + ".yml");
	}
	
	//@SuppressWarnings("deprecation")
	public void reloadYAML(){
		customConfigFile = new File(plugin.getDataFolder() + "/" + path, filename + ".yml");
		if (!customConfigFile.exists()){
			try {
				customConfigFile.mkdirs();
				customConfigFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		customConfig = YamlConfiguration.loadConfiguration(customConfigFile);
		// Look for defaults in the jar
		//InputStream defConfigStream = plugin.getResource(path + ".yml");
		//if (defConfigStream != null) {
			//YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			//customConfig.setDefaults(defConfig);
		//}
	}
	
	public FileConfiguration getYAML() {
	    if (customConfig == null) {
	        reloadYAML();
	    }
	    return customConfig;
	}
	
	public void saveYAML() {
	    if (customConfig == null || customConfigFile == null) {
	        return;
	    }
	    try {
	        getYAML().save(customConfigFile);
	    } catch (IOException ex) {
	        plugin.getLogger().log(Level.SEVERE, "Could not save config to " + customConfigFile.getAbsolutePath() + customConfigFile.getName(), ex);
	    }
	}
	
	public boolean exists(){
		if (customConfigFile.exists()){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean createFile(){
		try{
			return customConfigFile.createNewFile();
		}catch(Exception e){
			return false;
		}
	}
	
	public void delete(){
		customConfigFile.delete();
	}
	
}
