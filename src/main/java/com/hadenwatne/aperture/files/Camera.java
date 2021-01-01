package com.hadenwatne.aperture.files;

import java.util.List;

import org.bukkit.Location;

public class Camera {
	private String owner;
	private String name;
	private Location location;
	private String direction;
	private List<String> shares;
	private int id;
	
	public Camera(int i, String o, String n, Location l, String d, List<String> s) {
		id=i;
		owner=o;
		name=n;
		location=l;
		direction=d;
		shares=s;
	}
	
	public int getID() {
		return id;
	}
	
	public String getOwner() {
		return owner;
	}
	
	public String getName() {
		return name;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public String getDirection() {
		return direction;
	}
	
	public List<String> getShares(){
		return shares;
	}
}
