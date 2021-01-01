package com.hadenwatne.aperture;

import java.util.ArrayList;
import java.util.List;

import com.hadenwatne.aperture.files.Camera;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntityLiving;

public class InstanceStream {
	private Stream stream;
	private Scene scene;
	private Player p;
	private Camera cam;
	private boolean isActive;
	private Aperture plugin;
	private List<LivingEntity> hiddenEntities;
	
	public InstanceStream(Player pl, Aperture c, Camera camera) {
		p=pl;
		cam=camera;
		isActive = false;
		plugin=c;
		hiddenEntities = new ArrayList<LivingEntity>();
		
		scene = new Scene(cam.getDirection(), cam.getLocation(), plugin);
		stream = new Stream(this);
		
		hideNearbyEntities();
		
		// Begin streaming
		stream.runTaskTimer(c, 15, 15); // TODO configurable refresh rate (lower = more lag)
		isActive = true;
	}
	
	private void hideNearbyEntities() {
		for(Entity e : p.getWorld().getNearbyEntities(p.getLocation(), plugin.getUserConf().getViewX(), plugin.getUserConf().getViewY(), plugin.getUserConf().getViewZ())) {
			if(e instanceof LivingEntity && !(e instanceof Player)) {
				LivingEntity le = (LivingEntity)e;
				WrapperPlayServerEntityDestroy packet = new WrapperPlayServerEntityDestroy();
				hiddenEntities.add(le);
				packet.setEntityIds(new int[]{le.getEntityId()});
				packet.sendPacket(p);
			}
		}
	}
	
	private void showHiddenEntities() {
		for(LivingEntity e : hiddenEntities) {
			WrapperPlayServerSpawnEntityLiving packet = new WrapperPlayServerSpawnEntityLiving();
			packet.setEntityID(e.getEntityId());
			packet.setX(e.getLocation().getX());
			packet.setY(e.getLocation().getY());
			packet.setZ(e.getLocation().getZ());
			packet.setType(e.getType());
			packet.sendPacket(p);
			
		}
		
		hiddenEntities.clear();
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public boolean isActive() {
		return isActive;
	}
	
	public void cancelStream() {
		isActive = false;
		plugin.getStreams().remove(this);
		
		this.showHiddenEntities();
	}
	
	public Scene getScene() {
		return scene;
	}
	
	public Stream getStream() {
		return stream;
	}
}
