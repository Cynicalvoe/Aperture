package com.hadenwatne.aperture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class Scene {
	
	private Location c;
	private HashMap<Location, List<Double>> blocks;
	private HashMap<Location, Material> types;
	private HashMap<WatchedEntity, List<Double>> entities;
	
	private int viewx;
	private int viewy;
	private int viewz;
	private boolean hasRun;
	private String direction;
	
	public Scene(String dir, Location cam, Aperture pl) {
		c=cam;
		direction=dir;
		viewx = pl.getUserConf().getViewX();
		viewy = pl.getUserConf().getViewY();
		viewz = pl.getUserConf().getViewZ();
		blocks = new HashMap<Location, List<Double>>();
		types = new HashMap<Location, Material>();
		entities = new HashMap<WatchedEntity, List<Double>>();
		hasRun = false;
	}
	
	/*
	 * When facing NORTH: Z gets smaller, X stays the same
	 * When facing SOUTH: Z gets larger, X stays the same
	 * When facing WEST: X gets smaller, Z stays the same
	 * When facing EAST: X gets larger, Z stays the same
	 */
	
	private void buildScene() {
		if(direction.equals("NORTH")) {
			for(double x=-viewx/2; x<viewx/2; x++) {
				for(double y=-viewy/2; y<viewy/2; y++) {
					for(double z=-viewz; z<0; z++) {
						addBlocks(false, x, y, z);
						addEntity(x, y, z);
					}
				}
			}
		} else if(direction.equals("SOUTH")) {
			for(double x=-viewx/2; x<viewx/2; x++) {
				for(double y=-viewy/2; y<viewy/2; y++) {
					for(double z=1; z<viewz+1; z++) {
						addBlocks(false, x, y, z);
						addEntity(x, y, z);
					}
				}
			}
		} else if(direction.equals("WEST")) {
			for(double x=-viewx; x<0; x++) {
				for(double y=-viewy/2; y<viewy/2; y++) {
					for(double z=-viewz/2; z<viewz/2; z++) {
						addBlocks(false, x, y, z);
						addEntity(x, y, z);
					}
				}
			}
		} else if(direction.equals("EAST")) {
			for(double x=1; x<viewx+1; x++) {
				for(double y=-viewy/2; y<viewy/2; y++) {
					for(double z=-viewz/2; z<viewz/2; z++) {
						addBlocks(false, x, y, z);
						addEntity(x, y, z);
					}
				}
			}
		}
		
		hasRun = true;
	}
	
	private void rebuildScene() {
		blocks.clear();
		
		if(direction.equals("NORTH")) {
			for(double x=-viewx/2; x<viewx/2; x++) {
				for(double y=-viewy/2; y<viewy/2; y++) {
					for(double z=-viewz; z<0; z++) {
						addBlocks(true, x, y, z);
						addEntity(x, y, z);
					}
				}
			}
		} else if(direction.equals("SOUTH")) {
			for(double x=-viewx/2; x<viewx/2; x++) {
				for(double y=-viewy/2; y<viewy/2; y++) {
					for(double z=1; z<viewz+1; z++) {
						addBlocks(true, x, y, z);
						addEntity(x, y, z);
					}
				}
			}
		} else if(direction.equals("WEST")) {
			for(double x=-viewx; x<0; x++) {
				for(double y=-viewy/2; y<viewy/2; y++) {
					for(double z=-viewz/2; z<viewz/2; z++) {
						addBlocks(true, x, y, z);
						addEntity(x, y, z);
					}
				}
			}
		} else if(direction.equals("EAST")) {
			for(double x=1; x<viewx+1; x++) {
				for(double y=-viewy/2; y<viewy/2; y++) {
					for(double z=-viewz/2; z<viewz/2; z++) {
						addBlocks(true, x, y, z);
						addEntity(x, y, z);
					}
				}
			}
		}
	}
	
	private void addBlocks(boolean rebuild, double x, double y, double z) {
		Location l = c.clone().add(x, y, z);
		
		if(!rebuild || types.get(l) != l.getBlock().getType()) {
			List<Double> loc = new ArrayList<Double>();
			loc.add(x);
			loc.add(y);
			loc.add(z);
			
			types.put(l, l.getBlock().getType());
			blocks.put(l, loc);
		}
	}
	
	private void addEntity(double x, double y, double z) {
		Location l = c.clone().add(x, y, z);
		List<Double> loc = new ArrayList<Double>();
		loc.add(x-0.5);
		loc.add(y);
		loc.add(z-0.5);
		
		for(Entity e : c.getWorld().getNearbyEntities(c, viewx, viewy, viewz)) {
			if(e instanceof LivingEntity) {
				if(e.getLocation().distance(l)<=1d) {
					for(WatchedEntity we : entities.keySet()) {
						if(we.getEntity().getEntityId()==e.getEntityId()) {
							entities.put(we, loc); // Re-assign relatives so we can "animate" the entity movement
							return;
						}
					}
					
					entities.put(new WatchedEntity((LivingEntity)e), loc);
				}
			}
		}
	}
	
	public Location getCamera() {
		return c;
	}
	
	public String getDirection() {
		return direction;
	}
	
	public void hardRefreshBlocks() {
		hasRun = false;
	}
	
	public HashMap<Location, List<Double>> getSceneBlocks(){
		if(hasRun)
			this.rebuildScene();
		else
			this.buildScene();
		
		return blocks;
	}
	
	public HashMap<WatchedEntity, List<Double>> getEntities(){
		return entities;
	}
}