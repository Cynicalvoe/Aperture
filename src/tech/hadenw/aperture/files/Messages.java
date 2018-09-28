package tech.hadenw.aperture.files;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import tech.hadenw.aperture.Aperture;

public class Messages {
	Aperture plugin;
	private FileConfiguration messages;
	private File messagesFile;
	private HashMap<String, String> vals;
	public Messages(Aperture c){
		plugin=c;
		this.messages = null;
	    this.messagesFile = null;
	    
	    vals = new HashMap<String, String>();
	    this.reloadMessages();
	    
	    if(this.getMessages().contains("plugin-title")){
	    	this.loadValues();
	    }else{
	    	this.saveDefaultValues();
	    }
	    
	    plugin.getLogger().log(Level.INFO, "Loaded Custom Messages");
	}
	
	public void saveDefaultValues(){
		this.getMessages().set("plugin-title", "&8[&2&lAperture&8]");
		this.getMessages().set("wrong-command", "&cWrong command or usage!");
		this.getMessages().set("no-permission", "&cYou don't have permission!");
		this.getMessages().set("must-be-player", "&cYou must be a player to run that command!");
		this.getMessages().set("player-not-found", "&cThat player wasn't found!");
		this.getMessages().set("already-streaming", "&cYou're already viewing a camera!");
		this.getMessages().set("plugin-reloaded", "&aPlugin was reloaded!");
		this.getMessages().set("camera-added", "&aCamera added!");
		this.getMessages().set("camera-removed", "&aCamera removed!");
		this.getMessages().set("limit-reached", "&cYou can't add any more cameras!");
		this.getMessages().set("type-cam-name", "&aPlease type the new camera's name.");
		this.getMessages().set("must-finish-adding", "&cYou must finish adding the camera!");
		this.getMessages().set("not-owner", "&cYou don't own that camera!");
		this.getMessages().set("share-self", "&cYou can't share with yourself!");
		this.getMessages().set("already-sharing", "&cYou are already sharing this camera with that player.");
		this.getMessages().set("cam-shared", "&aCamera shared!");
		this.getMessages().set("enter-name", "&aPlease enter the player's username.");
		this.getMessages().set("update-available", "&aAn update is available! Download it now on SpigotMC.org");
		
		// Added later
		this.getMessages().set("stream-finished", "&aCamera stream finished.");

		this.saveMessages();
		this.loadValues();
	}
	
	public void loadValues(){
		vals.clear();
		
		vals.put("plugin-title", getMessages().getString("plugin-title").replaceAll("&", "§")+" ");
		vals.put("wrong-command", getMessages().getString("wrong-command").replaceAll("&", "§"));
		vals.put("no-permission", getMessages().getString("no-permission").replaceAll("&", "§"));
		vals.put("must-be-player", getMessages().getString("must-be-player").replaceAll("&", "§"));
		vals.put("player-not-found", getMessages().getString("player-not-found").replaceAll("&", "§"));
		vals.put("already-streaming", getMessages().getString("already-streaming").replaceAll("&", "§"));
		vals.put("plugin-reloaded", getMessages().getString("plugin-reloaded").replaceAll("&", "§"));
		vals.put("camera-added", getMessages().getString("camera-added").replaceAll("&", "§"));
		vals.put("camera-removed", getMessages().getString("camera-removed").replaceAll("&", "§"));
		vals.put("limit-reached", getMessages().getString("limit-reached").replaceAll("&", "§"));
		vals.put("type-cam-name", getMessages().getString("type-cam-name").replaceAll("&", "§"));
		vals.put("must-finish-adding", getMessages().getString("must-finish-adding").replaceAll("&", "§"));
		vals.put("not-owner", getMessages().getString("not-owner").replaceAll("&", "§"));
		vals.put("share-self", getMessages().getString("share-self").replaceAll("&", "§"));
		vals.put("already-sharing", getMessages().getString("already-sharing").replaceAll("&", "§"));
		vals.put("cam-shared", getMessages().getString("cam-shared").replaceAll("&", "§"));
		vals.put("enter-name", getMessages().getString("enter-name").replaceAll("&", "§"));
		vals.put("update-available", getMessages().getString("update-available").replaceAll("&", "§"));
		
		// Added later
		if(getMessages().contains("stream-finished")) 
			vals.put("stream-finished", getMessages().getString("stream-finished").replaceAll("&", "§"));
		else {
			this.getMessages().set("stream-finished", "&aCamera stream finished.");
			this.saveMessages();
			
			vals.put("stream-finished", getMessages().getString("stream-finished").replaceAll("&", "§"));
		}
		
	}
	
	public String getTitle(){
		return vals.get("plugin-title");
	}
	
	public String wrongCommand(){
		return vals.get("plugin-title")+vals.get("wrong-command");
	}
	public String noPermission(){
		return vals.get("plugin-title")+vals.get("no-permission");
	}
	public String mustBePlayer(){
		return vals.get("plugin-title")+vals.get("must-be-player");
	}
	public String alreadyStreaming(){
		return vals.get("plugin-title")+vals.get("already-streaming");
	}
	public String pluginReloaded(){
		return vals.get("plugin-title")+vals.get("plugin-reloaded");
	}
	public String limitReached(){
		return vals.get("plugin-title")+vals.get("limit-reached");
	}
	public String cameraAdded(){
		return vals.get("plugin-title")+vals.get("camera-added");
	}
	public String cameraRemoved(){
		return vals.get("plugin-title")+vals.get("camera-removed");
	}
	public String playerNotFound(){
		return vals.get("plugin-title")+vals.get("player-not-found");
	}
	public String typeName(){
		return vals.get("plugin-title")+vals.get("type-cam-name");
	}
	public String mustFinishAdding(){
		return vals.get("plugin-title")+vals.get("must-finish-adding");
	}
	public String notOwner(){
		return vals.get("plugin-title")+vals.get("not-owner");
	}
	public String shareSelf(){
		return vals.get("plugin-title")+vals.get("share-self");
	}
	public String alreadySharing(){
		return vals.get("plugin-title")+vals.get("already-sharing");
	}
	public String camShared(){
		return vals.get("plugin-title")+vals.get("cam-shared");
	}
	public String enterName(){
		return vals.get("plugin-title")+vals.get("enter-name");
	}
	public String updateAvailable(){
		return vals.get("plugin-title")+vals.get("update-available");
	}
	public String streamFinished(){
		return vals.get("plugin-title")+vals.get("stream-finished");
	}
	
	public void reloadMessages(){
		if (this.messagesFile == null){
			this.messagesFile = new File(plugin.getDataFolder()+"/messages.yml");
			this.messages = YamlConfiguration.loadConfiguration(this.messagesFile);
		}
	}
	 
	public FileConfiguration getMessages(){
		if (this.messages == null) {
			reloadMessages();
		}
		return this.messages;
	}
	 
	public void saveMessages(){
		if ((this.messages == null) || (this.messagesFile == null)) {
			return;
		}
		
		try{
			getMessages().save(this.messagesFile);
		} catch (Exception ex){
			plugin.getLogger().log(Level.SEVERE, "Could not save config to " + this.messagesFile, ex);
		}
	}
}
