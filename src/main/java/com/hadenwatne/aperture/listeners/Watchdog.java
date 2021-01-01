package com.hadenwatne.aperture.listeners;

import com.hadenwatne.aperture.Aperture;
import com.hadenwatne.aperture.InstanceStream;
import com.hadenwatne.aperture.files.Camera;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class Watchdog implements Listener{
	private Aperture plugin;
	
	public Watchdog(Aperture c) {
		plugin=c;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if(e.getPlayer().hasPermission("aperture.admin")) {
			if(plugin.isUpdateAvailable()) {
				e.getPlayer().sendMessage(plugin.getMessages().updateAvailable());
			}
		}
	}
	
	@EventHandler
	public void onSneak(PlayerToggleSneakEvent e) {
		for(InstanceStream s : plugin.getStreams()) {
			if(s.getPlayer().equals(e.getPlayer())) {
				if(s.isActive()) {
					s.cancelStream();
					e.getPlayer().sendMessage(plugin.getMessages().streamFinished());
				}
				
				break;
			}
		}
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		for(InstanceStream s : plugin.getStreams()) {
			if(s.getPlayer().equals(e.getPlayer())) {
				if(s.isActive()) {
					if(e.getFrom().getWorld().getName().equals(e.getTo().getWorld().getName())) {
						if(s.getStream().getPlayerStreamLocation().distance(e.getTo()) >= 1d)
							e.setCancelled(true);
					}else {
						s.cancelStream();
					}
				}
				
				break;
			}
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(e.getAction()==Action.RIGHT_CLICK_BLOCK) {
			for(Location l : plugin.getCameras().getCameraLocations()) {
				if(e.getClickedBlock().getLocation().equals(l)) {
					e.setCancelled(true);
					break;
				}
			}
		}
	}
	
	@EventHandler
	public void onBreakCamera(BlockBreakEvent e) {
		if(!e.isCancelled()) {
			Location bl = e.getBlock().getLocation();
			String uuid = e.getPlayer().getUniqueId().toString();
			Camera cam = plugin.getCameras().getCameraByLocation(bl);
			
			if(cam != null) {
				if(e.getPlayer().hasPermission("aperture.break")) {
					if(cam.getOwner().equals(uuid) || e.getPlayer().hasPermission("aperture.break.other")) {
						e.getPlayer().sendMessage(plugin.getMessages().cameraRemoved());
						plugin.getCameras().removeCamera(bl);
					}else {
						e.setCancelled(true);
						e.getPlayer().sendMessage(plugin.getMessages().noPermission());
					}
				}else {
					e.setCancelled(true);
					e.getPlayer().sendMessage(plugin.getMessages().noPermission());
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onCreateCamera(BlockPlaceEvent e) {
		ItemStack hand = e.getPlayer().getInventory().getItemInMainHand();
		
		if(hand.isSimilar(plugin.getUserConf().getCameraItem())) {
			if(e.getPlayer().hasPermission("aperture.place")) {
				if(e.canBuild() && !e.isCancelled()) {
					if(plugin.getOwnedCameras(e.getPlayer().getUniqueId()).size() < plugin.getUserConf().getMaxCameras() || e.getPlayer().hasPermission("aperture.bypasslimit")) {
						if(!e.getPlayer().hasMetadata("cam_add")) {
							e.getPlayer().sendMessage(plugin.getMessages().typeName());
							e.getPlayer().setMetadata("cam_add", new FixedMetadataValue(plugin, true));
							
							String dir = e.getBlockAgainst().getFace(e.getBlock()).toString();
							
							if(dir.equals("UP") || dir.equals("DOWN")) 
								dir = getDirection(e.getPlayer().getLocation().getYaw());
							
							plugin.getServer().getPluginManager().registerEvents(new Add(dir, e.getBlock(), e.getPlayer(), this), plugin);
						}else {
							e.getPlayer().sendMessage(plugin.getMessages().mustFinishAdding());
							e.setCancelled(true);
						}
					}else {
						e.setCancelled(true);
						e.getPlayer().sendMessage(plugin.getMessages().limitReached());
					}
				}
			}else {
				e.getPlayer().sendMessage(plugin.getMessages().noPermission());
			}
		}
	}
	
	public Aperture getPlugin() {
		return plugin;
	}
	
	private String getDirection(float yaw) {
		BlockFace[] axis = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
        return axis[Math.round(yaw / 90f) & 0x3].toString();
    }
}
