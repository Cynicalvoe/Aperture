package tech.hadenw.aperture.listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Add implements Listener{
	private Block block;
	private Player p;
	private Watchdog wd;
	private String face;
	
	public Add(String bf, Block b, Player pl, Watchdog dog) {
		face=bf;
		block=b;
		p=pl;
		wd=dog;
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		if(e.getPlayer().hasMetadata("cam_add") && e.getPlayer().equals(p)) {
			e.setCancelled(true);
			
			String name = e.getMessage().replace(" ", "_").replaceAll("[,\\.\\&\\*\\'\\$\\@\\!\\;\\:\"\\^\\%\\?]", "");
			wd.getPlugin().getCameras().addCamera(face, block.getLocation(), p.getUniqueId().toString(), name);
			p.sendMessage(wd.getPlugin().getMessages().cameraAdded());
			
			this.unregister();
		}
	}
	
	@EventHandler
	public void onBreakWhileAdding(BlockBreakEvent e) {
		if(e.getPlayer().hasMetadata("cam_add") && e.getPlayer().equals(p)) {
			if(e.getBlock().equals(block)) {
			
			p.sendMessage(wd.getPlugin().getMessages().cameraRemoved());
			p.removeMetadata("cam_add", wd.getPlugin());
			
			this.unregister();
			}
		}
	}
	
	@EventHandler
	public void onLogout(PlayerQuitEvent e) {
		if(e.getPlayer().equals(p)) {
			this.unregister();
		}
	}
	
	private void unregister() {
		p.removeMetadata("cam_add", wd.getPlugin());
		AsyncPlayerChatEvent.getHandlerList().unregister(this);
		BlockBreakEvent.getHandlerList().unregister(this);
		PlayerQuitEvent.getHandlerList().unregister(this);
	}
}
