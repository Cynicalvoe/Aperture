package tech.hadenw.aperture;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import tech.hadenw.aperture.files.Camera;
import tech.hadenw.aperture.listeners.Share;

public class SelectCameraGUI implements Listener{
	private Inventory gui;
	private Aperture plugin;
	private List<Camera> ownedCameras;
	private Player viewer;
	private OfflinePlayer owner;
	private boolean isRemoving;
	private boolean isSharing;
	
	public SelectCameraGUI(Aperture c, Player pl, boolean share){
		for(InstanceStream s : c.getStreams()) {
			if(s.getPlayer().equals(pl)) {
				if(s.isActive()) {
					this.unregister();
					// TODO send message "ya can't do that stairfax"
					return; // Break the loop and skip the following code, as this player is already streaming something
				}
			}
		}
		
		plugin=c;
		viewer=pl;
		owner=pl;
		isRemoving = false;
		isSharing = share;
		
		this.createMenu();
		this.beginStreaming();
		viewer.openInventory(gui);
	}
	
	public SelectCameraGUI(Aperture c, Player pl, OfflinePlayer other){
		for(InstanceStream s : c.getStreams()) {
			if(s.getPlayer().equals(pl)) {
				if(s.isActive()) {
					this.unregister();
					// TODO send message "ya can't do that stairfax"
					return; // Break the loop and skip the following code, as this player is already streaming something
				}
			}
		}
		
		plugin=c;
		viewer=pl;
		owner=other;
		isRemoving = false;
		isSharing = false;
		
		this.createMenu();
		this.beginStreaming();
		viewer.openInventory(gui);
	}
	
	private void createMenu() {
		ownedCameras = plugin.getOwnedCameras(owner.getUniqueId());
		int mc = ownedCameras.size();
		int size = mc % 9 > 0 ? mc + (9-mc%9) : mc;
		
		if(size > 45)
			size = 45;
		
		if(size == 0)
			size = 9;
		
		gui = Bukkit.createInventory(null, size, "§a§ki§8 Choose Camera §a§ki");
	}
	
	private void beginStreaming() {
		ownedCameras = plugin.getOwnedCameras(owner.getUniqueId());
		gui.clear();
		
		int added = 0;
		for(Camera c : ownedCameras) {
			if(added < plugin.getUserConf().getMaxCameras()) {
				added++;
				gui.addItem(cameraItem(c));
			} else break;
		}
	}
	
	@EventHandler
	public void clickGUI(InventoryClickEvent e){
		if(e.getClickedInventory() != null && e.getClickedInventory().equals(gui)){
			e.setCancelled(true);
			Player pl = (Player)e.getWhoClicked();
			
			if(e.getCurrentItem() != null) {
				if(e.getAction()==InventoryAction.PICKUP_ALL || e.getAction()==InventoryAction.SWAP_WITH_CURSOR) {
					pl.closeInventory();
					
					if(!isSharing) {
						Camera c = ownedCameras.get(e.getSlot());
						plugin.getStreams().add(new InstanceStream(pl, plugin, c));
					}else {
						Camera c = ownedCameras.get(e.getSlot());
						
						if(c.getOwner().equals(pl.getUniqueId().toString())) {
							Bukkit.getPluginManager().registerEvents(new Share(c, pl, plugin), plugin);
						}else {
							pl.sendMessage(plugin.getMessages().notOwner());
						}
					}
				} else if(e.getAction()==InventoryAction.PICKUP_HALF || e.getAction()==InventoryAction.PLACE_ONE) {
					if(isRemoving) {
						if(e.getCurrentItem().getType()==Material.BARRIER) {
							// Remove the camera in that slot
							Camera c = ownedCameras.get(e.getSlot());
							plugin.getCameras().removeCamera(c);
							pl.sendMessage(plugin.getMessages().cameraRemoved());
							isRemoving = false;
							
							this.beginStreaming();
						}
					}else {
						// Change item 
						isRemoving = true;
						gui.setItem(e.getSlot(), removeCamItem(ownedCameras.get(e.getSlot())));
					}
				}
			}
		}
	}
	
	private ItemStack cameraItem(Camera c){
		ItemStack item = new ItemStack(plugin.getUserConf().getCameraItem());
		ItemMeta hm = item.getItemMeta();
		
		List<String> lore = new ArrayList<String>();
		lore.add("§d"+c.getName());
		lore.add("§7x: "+c.getLocation().getX());
		lore.add("§7y: "+c.getLocation().getY());
		lore.add("§7z: "+c.getLocation().getZ());
		hm.setLore(lore);
		item.setItemMeta(hm);
		
		return item;
	}
	
	private ItemStack removeCamItem(Camera c){
		ItemStack item = new ItemStack(Material.BARRIER);
		ItemMeta hm = item.getItemMeta();
		
		hm.setDisplayName("§c§lRemove Camera?");
		
		List<String> lore = new ArrayList<String>();
		lore.add("§d"+c.getName());
		lore.add("§7x: "+c.getLocation().getX());
		lore.add("§7y: "+c.getLocation().getY());
		lore.add("§7z: "+c.getLocation().getZ());
		lore.add("§cRight-click again to remove");
		lore.add("§cthis camera");
		hm.setLore(lore);
		item.setItemMeta(hm);
		
		return item;
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e){
		if(e.getInventory().equals(gui)){
			this.unregister();
		}
	}
	
	private void unregister() {
		InventoryClickEvent.getHandlerList().unregister(this);
		InventoryCloseEvent.getHandlerList().unregister(this);
	}
}