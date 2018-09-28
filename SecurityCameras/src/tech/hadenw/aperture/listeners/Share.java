package tech.hadenw.aperture.listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import tech.hadenw.aperture.Aperture;
import tech.hadenw.aperture.files.Camera;

public class Share implements Listener{
	private Player p;
	private Camera cam;
	private Aperture plugin;
	
	public Share(Camera cs, Player pl, Aperture c) {
		p=pl;
		cam=cs;
		plugin=c;
		
		p.sendMessage(plugin.getMessages().enterName());
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		if(e.getPlayer().equals(p)) {
			e.setCancelled(true);
			
			OfflinePlayer op = Bukkit.getOfflinePlayer(e.getMessage()) ;
			
			if(op != null) {
				List<String> uuids = cam.getShares();
				
				if(!uuids.contains(op.getUniqueId().toString())) {
					if(!op.getUniqueId().toString().equals(p.getUniqueId().toString())) {
						cam.getShares().add(op.getUniqueId().toString());
						
						p.sendMessage(plugin.getMessages().camShared());
					}else {
						p.sendMessage(plugin.getMessages().shareSelf());
					}
				}else {
					p.sendMessage(plugin.getMessages().alreadySharing());
				}
			}else {
				p.sendMessage(plugin.getMessages().playerNotFound());
			}
			
			this.unregister();
		}
	}
	
	@EventHandler
	public void onLogout(PlayerQuitEvent e) {
		if(e.getPlayer().equals(p)) {
			this.unregister();
		}
	}
	
	private void unregister() {
		AsyncPlayerChatEvent.getHandlerList().unregister(this);
		PlayerQuitEvent.getHandlerList().unregister(this);
	}
}
