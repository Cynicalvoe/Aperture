package com.hadenwatne.aperture.files;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.hadenwatne.aperture.Aperture;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class Configuration {
	private int viewx;
	private int viewy;
	private int viewz;
	private int maxCameras;
	private ItemStack camItem;
	private boolean checkUpdates;
	private final String colorChar = Character.toString(ChatColor.COLOR_CHAR);
	
	// SQL data
	private boolean useMySQL;
	private String SQLAddress;
	private String SQLDB;
	private String SQLUser;
	private String SQLPass;
	
	private Aperture plugin;
	
	public Configuration(Aperture c) {
		plugin=c;
		plugin.getLogger().log(Level.INFO, "Loading configuration...");
		plugin.saveDefaultConfig();
		this.loadValues();
		plugin.getLogger().log(Level.INFO, "Loaded configuration!");
	}
	
	public void loadValues() {
		plugin.reloadConfig();
		
		try {
			viewx = plugin.getConfig().getInt("camera.viewport.x");
		}catch(Exception e){
			viewx = 20;
			plugin.getLogger().log(Level.WARNING, "Error loading camera viewport settings. Using default value.");
		}
		
		try {
			viewy = plugin.getConfig().getInt("camera.viewport.y");
		}catch(Exception e){
			viewy = 10;
			plugin.getLogger().log(Level.WARNING, "Error loading camera viewport settings. Using default value.");
		}
		
		try {
			viewz = plugin.getConfig().getInt("camera.viewport.z");
		}catch(Exception e){
			viewz = 20;
			plugin.getLogger().log(Level.WARNING, "Error loading camera viewport settings. Using default value.");
		}
		
		try {
			maxCameras = plugin.getConfig().getInt("camera.max");
			if(maxCameras > 45)
				maxCameras = 45;
		}catch(Exception e){
			maxCameras = 18;
			plugin.getLogger().log(Level.WARNING, "Error loading camera settings. Using default value.");
		}
		
		try {
			camItem = new ItemStack(Material.getMaterial(plugin.getConfig().getString("camera.item.type")));
			
			if(!camItem.getType().isBlock())
				camItem.setType(Material.OBSERVER);
			
			if(plugin.getConfig().contains("camera.item.data")) 
				camItem.setDurability((short)plugin.getConfig().getInt("camera.item.data"));
			
			if(camItem.getType()==Material.SKELETON_SKULL) {
				SkullMeta sm = (SkullMeta)camItem.getItemMeta();
				
				if(plugin.getConfig().contains("camera.item.owner")) 
					sm.setOwningPlayer(Bukkit.getOfflinePlayer(plugin.getConfig().getString("camera.item.owner")));
				
				camItem.setItemMeta(sm);
			}
			
			if(plugin.getConfig().contains("camera.item.lore")) {
				ItemMeta im = camItem.getItemMeta();
				List<String> lore = new ArrayList<String>();
				
				for(String s : plugin.getConfig().getStringList("camera.item.lore")) {
					lore.add(s.replaceAll("&", colorChar));
				}
				
				im.setLore(lore);
				camItem.setItemMeta(im);
			}
			
			if(plugin.getConfig().contains("camera.item.name")) { 
				ItemMeta im = camItem.getItemMeta();
				im.setDisplayName(plugin.getConfig().getString("camera.item.name").replaceAll("&", colorChar));
				camItem.setItemMeta(im);
			}
		}catch(Exception e){
			camItem = new ItemStack(Material.SKELETON_SKULL, 1, (byte)3);
			SkullMeta hm = (SkullMeta)camItem.getItemMeta();
			hm.setOwningPlayer(Bukkit.getOfflinePlayer("Camera"));
			hm.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e&lCamera"));
			camItem.setItemMeta(hm);
			
			plugin.getLogger().log(Level.WARNING, "Error loading camera item. Using default value.");
		}
		
		try {
			checkUpdates = plugin.getConfig().getBoolean("checkForUpdates");
		}catch(Exception e){
			plugin.getConfig().set("checkForUpdates", true);
			plugin.saveConfig();
			
			checkUpdates = true;
			plugin.getLogger().log(Level.WARNING, "Error loading update check settings. Using default value.");
		}
		
		// Added later
		try {
			useMySQL = plugin.getConfig().getBoolean("mysql.enable");
			SQLAddress = plugin.getConfig().getString("mysql.address");
			SQLDB = plugin.getConfig().getString("mysql.db_name");
			SQLUser = plugin.getConfig().getString("mysql.db_user");
			SQLPass = plugin.getConfig().getString("mysql.db_pass");
		}catch(Exception e){
			plugin.getConfig().set("mysql.enable", false);
			plugin.getConfig().set("mysql.address", "localhost:3306");
			plugin.getConfig().set("mysql.db_name", "myDatabase");
			plugin.getConfig().set("mysql.db_user", "username");
			plugin.getConfig().set("mysql.db_pass", "password");
			plugin.saveConfig();
			plugin.reloadConfig();
			
			useMySQL = false;
			plugin.getLogger().log(Level.WARNING, "Error loading MySQL settings. Using default values (disabled).");
		}
	}
	
	public ItemStack getCameraItem() {
		return camItem;
	}
	
	public int getViewX() {
		return viewx;
	}
	
	public int getViewY() {
		return viewy;
	}
	
	public int getViewZ() {
		return viewz;
	}
	
	public int getMaxCameras() {
		return maxCameras;
	}
	
	public boolean getCheckForUpdates() {
		return checkUpdates;
	}
	
	public boolean useMySQL() {
		return useMySQL;
	}
	
	public String SQLAddress() {
		return SQLAddress;
	}
	
	public String SQLDB() {
		return SQLDB;
	}
	
	public String SQLUser() {
		return SQLUser;
	}
	
	public String SQLPass() {
		return SQLPass;
	}
}
