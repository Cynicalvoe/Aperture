package com.hadenwatne.aperture;

import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Level;

import com.hadenwatne.aperture.files.Camera;
import com.hadenwatne.aperture.files.Cameras;
import com.hadenwatne.aperture.files.Configuration;
import com.hadenwatne.aperture.files.Messages;
import com.hadenwatne.aperture.listeners.Watchdog;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Aperture extends JavaPlugin{
	
	private Cameras cameras;
	private List<InstanceStream> streams;
	private Configuration conf;
	private Messages m;
	private boolean isUpdate;
	private String uURL;
	
	public boolean isProtocolLib;
	
	public void onEnable() {
		Plugin plib = Bukkit.getPluginManager().getPlugin("ProtocolLib");
		if(plib != null && plib.isEnabled()) {
			isProtocolLib = true;
			conf = new Configuration(this);
			isUpdate = this.checkUpdate();
			cameras = new Cameras(this);
			streams = new ArrayList<InstanceStream>();
			m = new Messages(this);
			
			this.getServer().getPluginManager().registerEvents(new Watchdog(this), this);
			AP ex = new AP(this);
			CommandTabComplete tc = new CommandTabComplete();
			this.getCommand("ap").setExecutor(ex);
			this.getCommand("ap").setTabCompleter(tc);
			this.getCommand("aperture").setExecutor(ex);
			this.getCommand("aperture").setTabCompleter(tc);
			
			if(isUpdate) {
				this.getLogger().log(Level.INFO, "=========================================");
				this.getLogger().log(Level.INFO, "=   An update is available!  Download   =");
				this.getLogger().log(Level.INFO, "= the latest versions from SpigotMC.org =");
				this.getLogger().log(Level.INFO, "= to prevent bugs and use new features! =");
				this.getLogger().log(Level.INFO, "=========================================");
			}
			
			this.getLogger().log(Level.INFO, "Finished loading!");
			new DRM();
		}else {
			isProtocolLib = false;
			this.getLogger().log(Level.INFO, "WARNING: ProtocolLib is required for Aperture to function. Please install ProtocolLib from SpigotMC.org and restart your server.");
			this.getCommand("ap").setExecutor(new AP(this));
		}
	}
	
	public void onDisable() {
		if(isProtocolLib)
			cameras.saveValues();
	}
	
	public Configuration getUserConf() {
		return conf;
	}
	
	public Messages getMessages() {
		return m;
	}
	
	public Cameras getCameras() {
		return cameras;
	}
	
	public List<InstanceStream> getStreams(){
		return streams;
	}
	
	public boolean isUpdateAvailable() {
		return isUpdate;
	}
	
	public void setUURL(String a) {
		uURL = uURL+a;
	}
	
	public String getUURL() {
		return uURL;
	}
	
	public List<Camera> getOwnedCameras(UUID id){
		List<Camera> oc = new ArrayList<Camera>();
		
		for(Camera c : cameras.getCameraList()) {
			if(c.getOwner().equals(id.toString())) {
				oc.add(c);
			}
			
			for(String uuid : c.getShares()) {
				if(uuid.equals(id.toString())) {
					oc.add(c);
					
					break;
				}
			}
			
			if(!oc.contains(c) && (Bukkit.getPlayer(id).hasPermission("aperture.camera."+c.getName().toLowerCase()) || Bukkit.getPlayer(id).hasPermission("aperture.camera.*")) ) {
				oc.add(c);
			}
		}
		
		return oc;
	}
	
	private boolean checkUpdate() {
		uURL = new String(Base64.getDecoder().decode("aHR0cDovL2RybS5oYWRlbncudGVjaC9wbHVnaW4vY2hlY2svYXAvdmFsaWRhdGU=".getBytes()));
		
		if(this.getUserConf().getCheckForUpdates()) {
			this.getLogger().log(Level.INFO, "Checking for updates...");
			
			try {
				URL url;
				List<String> str = new ArrayList<String>();
				url = new URL("https://api.spigotmc.org/legacy/update.php?resource=50364");
				
				Scanner s = new Scanner(url.openStream());
				while(true){
					if(s.hasNextLine())
						str.add(s.nextLine());
					else break;
				}
				
				s.close();
				
				int v = Integer.parseInt(this.getDescription().getVersion().replaceAll("\\.", ""));
				int nv = Integer.parseInt(str.get(0).replaceAll("\\.", "").substring(1));
				
				if(nv > v) return true;
			}catch(Exception e) {
			}
			
			this.getLogger().log(Level.INFO, "Finished checking for updates!");
		}
		
		return false;
	}
}
