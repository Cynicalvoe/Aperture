package com.hadenwatne.aperture.files;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.hadenwatne.aperture.Aperture;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Cameras {	
	
	private boolean useSQL;
	private String dbName;
	private String dbUser;
	private String dbPass;
	private String dbURL;
	
	private FileConfiguration cConf;
	private File cFile;
	private Aperture plugin;
	private List<Camera> cameras; 
	private List<Location> cameraLocations;
	
	private int newID;
	
	public Cameras(Aperture c){
		cConf = null;
		cFile = null;
		plugin=c;
		
		newID = -1;
		
		useSQL = plugin.getUserConf().useMySQL();
		dbName = plugin.getUserConf().SQLDB();
		dbUser = plugin.getUserConf().SQLUser();
		dbPass = plugin.getUserConf().SQLPass();
		dbURL = "jdbc:mysql://"+plugin.getUserConf().SQLAddress()+"/";
		
		plugin.getLogger().log(Level.INFO, "Loading cameras...");
		
		cameras = new ArrayList<Camera>();
		cameraLocations = new ArrayList<Location>();
		
		if(!useSQL) {
			reloadCamerasFile();
		} else {
			try(Connection con = DriverManager.getConnection(dbURL+dbName, dbUser, dbPass)){
				Statement s = con.createStatement();
				s.execute("CREATE TABLE IF NOT EXISTS Ap_Cameras ("
						+ "cameraID int,"
						+ "owner varchar(36),"
						+ "name varchar(255),"
						+ "location_x decimal,"
						+ "location_y int,"
						+ "location_z decimal,"
						+ "location_w varchar(255),"
						+ "location_d varchar(5),"
						+ "CONSTRAINT PK_CameraID PRIMARY KEY (cameraID)"
						+ ")");
				
				s.execute("CREATE TABLE IF NOT EXISTS Ap_Shares ("
						+ "cameraID int,"
						+ "player varchar(36),"
						+ "CONSTRAINT FK_CameraID FOREIGN KEY (cameraID) REFERENCES Ap_Cameras(cameraID),"
						+ "CONSTRAINT PK_Share PRIMARY KEY (cameraID, player)"
						+ ")");
				
				s.close();
				con.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		loadValues();
		
		plugin.getLogger().log(Level.INFO, "Loaded cameras!");
	}
	
	public List<Camera> getCameraList(){
		return cameras;
	}
	
	public List<Location> getCameraLocations(){
		return cameraLocations;
	}
	
	public void removeCamera(Camera c) {
		cameras.remove(c);
		cameraLocations.remove(c.getLocation());
		c.getLocation().getBlock().setType(Material.AIR);
	}
	
	public void removeCamera(Location l) {
		cameraLocations.remove(l);
		l.getBlock().setType(Material.AIR);
		
		for(Camera c : cameras) {
			if(c.getLocation().getX()==l.getX() && c.getLocation().getY()==l.getY() && c.getLocation().getZ()==l.getZ()) {
				cameras.remove(c);
				break;
			}
		}
	}
	
	public Camera getCameraByLocation(Location l) {
		for(Camera c : cameras) {
			if(c.getLocation().getX()==l.getX() && c.getLocation().getY()==l.getY() && c.getLocation().getZ()==l.getZ()) {
				return c;
			}
		}
		
		return null;
	}
	
	public void addCamera(String direction, Location loc, String uuid, String name) {
		Camera c = new Camera(this.createNewID(), uuid, name, loc, direction, new ArrayList<String>());
		
		cameras.add(c);
		cameraLocations.add(c.getLocation());
	}
	
	private void loadValues(){
		if(!useSQL) {
			for(String key : this.cConf.getKeys(false)) {
				ConfigurationSection cam = this.cConf.getConfigurationSection(key);
				Camera c = new Camera(cameras.size(), cam.getString("owner"), cam.getString("name"), new Location(Bukkit.getWorld(cam.getString("location.w")), cam.getDouble("location.x"), cam.getDouble("location.y"), cam.getDouble("location.z")), cam.getString("location.d"), cam.getStringList("share"));
				cameras.add(c);
				cameraLocations.add(c.getLocation());
			}
		} else {
			try(Connection con = DriverManager.getConnection(dbURL+dbName, dbUser, dbPass)){
				Statement s = con.createStatement();
				Statement s2 = con.createStatement();
				ResultSet camData = s.executeQuery("SELECT * FROM Ap_Cameras");
				
				while(camData.next()) {
					Camera c = new Camera(camData.getInt("cameraID"), camData.getString("owner"), camData.getString("name"), new Location(Bukkit.getWorld(camData.getString("location_w")), camData.getDouble("location_x"), camData.getDouble("location_y"), camData.getDouble("location_z")), camData.getString("location_d"), new ArrayList<String>());
					cameras.add(c);
					cameraLocations.add(c.getLocation());
					
					// Assign share data
					ResultSet shareData = s2.executeQuery("SELECT * FROM Ap_Shares WHERE cameraID = "+camData.getInt("cameraID"));
					
					while(shareData.next()) {
						c.getShares().add(shareData.getString("player"));
					}
					
					shareData.close();
				}
				
				camData.close();
				s.close();
				con.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void saveValues() {
		if(!useSQL) {
			for(String key : cConf.getKeys(false)) {
				cConf.set(key, null);
			}
			
			for(Camera c : cameras) {
				cConf.set("c"+c.getID()+".id", c.getID());
				cConf.set("c"+c.getID()+".owner", c.getOwner());
				cConf.set("c"+c.getID()+".name", c.getName());
				cConf.set("c"+c.getID()+".share", c.getShares());
				cConf.set("c"+c.getID()+".location.w", c.getLocation().getWorld().getName());
				cConf.set("c"+c.getID()+".location.x", c.getLocation().getX());
				cConf.set("c"+c.getID()+".location.y", c.getLocation().getY());
				cConf.set("c"+c.getID()+".location.z", c.getLocation().getZ());
				cConf.set("c"+c.getID()+".location.d", c.getDirection());
			}
			
			this.saveCamerasFile();
		} else {
			try(Connection con = DriverManager.getConnection(dbURL+dbName, dbUser, dbPass)){
				Statement s = con.createStatement();
				Statement s2 = con.createStatement();
				ResultSet ids = s.executeQuery("SELECT cameraID FROM Ap_Cameras");
				List<Integer> cameraIDs = new ArrayList<Integer>(); // All SQL IDs that match up with Memory IDs
				
				while(ids.next()) {
					for(Camera c : cameras) {
						if(c.getID()==ids.getInt("cameraID")) {
							// cameras contains same camera
							cameraIDs.add(ids.getInt("cameraID"));
							
							break;
						}
					}
				}
				
				ids.beforeFirst();
				while(ids.next()) {
					if(!cameraIDs.contains(ids.getInt("cameraID"))) {
						s2.execute("DELETE FROM Ap_Shares WHERE cameraID = "+ids.getInt("cameraID"));
						s2.execute("DELETE FROM Ap_Cameras WHERE cameraID = "+ids.getInt("cameraID"));
					}
				}
				
				for(Camera c : cameras) {
					if(!cameraIDs.contains(c.getID())) {
						s.execute("INSERT INTO Ap_Cameras VALUES ("+c.getID()+", '"+c.getOwner()+"', '"+c.getName()+"', "+c.getLocation().getX()+", "+c.getLocation().getY()+", "+c.getLocation().getZ()+", '"+c.getLocation().getWorld().getName()+"', '"+c.getDirection()+"')");
						
						for(String share : c.getShares()) {
							s.execute("INSERT INTO Ap_Shares (cameraID, player) VALUES ("+c.getID()+", '"+share+"')");
						}
					}else {
						ResultSet sh = s.executeQuery("SELECT player FROM Ap_Shares WHERE cameraID = "+c.getID());
						
						List<String> sharedPlayers = new ArrayList<String>();
						while(sh.next()) {
							sharedPlayers.add(sh.getString("player"));
						}
						
						for(String share : c.getShares()) {
							if(!sharedPlayers.contains(share))
								s.execute("INSERT INTO Ap_Shares (cameraID, player) VALUES ("+c.getID()+", '"+share+"')");
						}
						
						sh.close();
					}
				}
				
				ids.close();
				s.close();
				s2.close();
				con.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private int createNewID() {
		newID++;
		
		for(Camera c : cameras) {
			if(c.getID()>=newID)
				newID=c.getID()+1;
		}
		
		return newID;
	}
	
	public void reloadCamerasFile(){
		if(this.cFile == null){
			this.cFile = new File(plugin.getDataFolder(), "cameras.yml");
		    this.cConf = YamlConfiguration.loadConfiguration(this.cFile);
		}
	}
	
	public FileConfiguration getCamerasFile(){
		if(this.cConf == null){
			reloadCamerasFile();
		}
		
	   return this.cConf;
	}
		 
	public void saveCamerasFile(){
		if ((this.cConf == null) || (this.cFile == null)) {
			return;
		}
		try{
			getCamerasFile().save(this.cFile);
		}catch(Exception ex){
			plugin.getLogger().log(Level.SEVERE, "Could not save config to " + this.cFile, ex);
		}
	}
}
