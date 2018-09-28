package tech.hadenw.aperture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.packetwrapper.WrapperPlayServerBlockChange;
import com.comphenix.packetwrapper.WrapperPlayServerTileEntityData;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.comphenix.protocol.wrappers.nbt.NbtBase;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;

public class Stream extends BukkitRunnable{
	
	private Location playerLoc;
	private HashMap<Location, List<Double>> currentScene;
	private HashMap<WatchedEntity, List<Double>> currentSceneEnt;
	private InstanceStream is;
	private int xm;
	private int zm;
	private String pd;
	private String cd;
	
	public Stream(InstanceStream instance) {
		playerLoc=instance.getPlayer().getLocation().getBlock().getLocation();
		is=instance;
		xm=1;
		zm=1;
		
		// Determine direction before the stream begins
		pd = getDirection(is.getPlayer().getLocation().getYaw());
		cd = is.getScene().getDirection();
		
		if(pd != cd) {
			// North
			if(cd.equals("NORTH") && pd.equals("WEST"))
				zm = -1;
			else if(cd.equals("NORTH") && pd.equals("SOUTH")) {
				xm = -1;
				zm = -1;
			} else if(cd.equals("NORTH") && pd.equals("EAST"))
				xm = -1;
			
			//East
			if(cd.equals("EAST") && pd.equals("WEST")) {
				xm = -1;
				zm = -1;
			}else if(cd.equals("EAST") && pd.equals("SOUTH")) 
				xm = -1;
			 else if(cd.equals("EAST") && pd.equals("NORTH"))
				zm = -1;
			
			//West
			if(cd.equals("WEST") && pd.equals("EAST")) {
				xm = -1;
				zm = -1;
			}else if(cd.equals("WEST") && pd.equals("SOUTH")) 
				zm = -1;
			 else if(cd.equals("WEST") && pd.equals("NORTH"))
				xm = -1;
			
			//South
			if(cd.equals("SOUTH") && pd.equals("NORTH")) {
				xm = -1;
				zm = -1;
			}else if(cd.equals("SOUTH") && pd.equals("WEST")) 
				xm = -1;
			 else if(cd.equals("SOUTH") && pd.equals("EAST"))
				zm = -1;
		}
	}
	
	public Location getPlayerStreamLocation() {
		return playerLoc;
	}
	
	public void run() {
		if(is.getPlayer().isOnline()) {
			if(is.isActive()) {
				currentScene = is.getScene().getSceneBlocks();
				currentSceneEnt = is.getScene().getEntities();
				
				for(Location l : currentScene.keySet()) {
					WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange();
					Location b;
					
					if(zm != xm) {
						b = playerLoc.clone().add(currentScene.get(l).get(2)*xm, currentScene.get(l).get(1), currentScene.get(l).get(0)*zm);
					} else {
						b = playerLoc.clone().add(currentScene.get(l).get(0)*xm, currentScene.get(l).get(1), currentScene.get(l).get(2)*zm);
					}
					
					packet.setLocation(new BlockPosition(b.getBlockX(), b.getBlockY(), b.getBlockZ())); 
					packet.setBlockData(WrappedBlockData.createData(l.getBlock().getType()));
					packet.sendPacket(is.getPlayer());
					
					// Support sign data and player skulls
					if(l.getBlock().getType()==Material.SIGN || l.getBlock().getType()==Material.WALL_SIGN) {
						Sign s = (Sign)l.getBlock().getState();
						
						WrapperPlayServerTileEntityData ted = new WrapperPlayServerTileEntityData();
						ted.setLocation(new BlockPosition(b.getBlockX(), b.getBlockY(), b.getBlockZ()));

						ArrayList<NbtBase<?>> tags = new ArrayList<NbtBase<?>>();
						for(int i=0; i<4; i++) {
							tags.add(NbtFactory.of("Text"+(i+1), "{\"extra\":[{\"text\":\""+s.getLine((i))+"\"}],\"text\":\"\"}"));
						}
						
						tags.add(NbtFactory.of("id", "minecraft:sign"));
						tags.add(NbtFactory.of("x", b.getBlockX()));
						tags.add(NbtFactory.of("y", b.getBlockY()));
						tags.add(NbtFactory.of("z", b.getBlockZ()));
						
						ted.setNbtData(NbtFactory.ofCompound("", tags));
						ted.setAction(9);
						
						ted.sendPacket(is.getPlayer());
					}else if(l.getBlock().getState() instanceof Skull) {
						Skull s = (Skull)l.getBlock().getState();
						WrapperPlayServerTileEntityData ted = new WrapperPlayServerTileEntityData();
						ArrayList<NbtBase<?>> tags = new ArrayList<NbtBase<?>>();
						
						ted.setLocation(new BlockPosition(b.getBlockX(), b.getBlockY(), b.getBlockZ()));
						tags.add(NbtFactory.of("id", "minecraft:skull"));
						tags.add(NbtFactory.of("x", b.getBlockX()));
						tags.add(NbtFactory.of("y", b.getBlockY()));
						tags.add(NbtFactory.of("z", b.getBlockZ()));
						tags.add(NbtFactory.of("Rot", HeadRotationUtil.getRotation(s.getRotation())));
						
						if(s.getType() == Material.PLAYER_HEAD || s.getType() == Material.PLAYER_WALL_HEAD) {
							tags.add(NbtFactory.of("SkullType", 3));
							
							ArrayList<NbtBase<?>> otags = new ArrayList<NbtBase<?>>();
							if(s.hasOwner()) {
								otags.add(NbtFactory.of("Id", s.getOwningPlayer().getUniqueId().toString()));
								otags.add(NbtFactory.of("Name", s.getOwningPlayer().getName()));
							}
							
							tags.add(NbtFactory.ofCompound("Owner", otags));
						} else if(s.getType() == Material.CREEPER_HEAD || s.getType() == Material.CREEPER_WALL_HEAD) {
							tags.add(NbtFactory.of("SkullType", 4));
						} else if(s.getType() == Material.WITHER_SKELETON_SKULL || s.getType() == Material.WITHER_SKELETON_WALL_SKULL) {
							tags.add(NbtFactory.of("SkullType", 1));
						} else if(s.getType() == Material.ZOMBIE_HEAD || s.getType() == Material.ZOMBIE_WALL_HEAD) {
							tags.add(NbtFactory.of("SkullType", 2));
						} else if(s.getType() == Material.DRAGON_HEAD || s.getType() == Material.DRAGON_WALL_HEAD) {
							tags.add(NbtFactory.of("SkullType", 5));
						}
						
						ted.setNbtData(NbtFactory.ofCompound("", tags));
						ted.setAction(4);
						
						// We just gotta send it
						ted.sendPacket(is.getPlayer());
					}
				}
				
				for(WatchedEntity we : currentSceneEnt.keySet()) {
					if(we.getEntity() == null || we.getEntity().isDead()) {
						we.destroyFakeEntity().sendPacket(is.getPlayer());
						is.getScene().getEntities().remove(we);
						break;
					}
					
					if(!we.hasSpawned())
						if(!we.isPlayer())
							we.spawnFakeMob(playerLoc, currentSceneEnt.get(we), xm, zm).sendPacket(is.getPlayer());
						else
							we.spawnFakePlayer(playerLoc, currentSceneEnt.get(we), xm, zm).sendPacket(is.getPlayer());
					else {
						we.move(playerLoc, currentSceneEnt.get(we), xm, zm).sendPacket(is.getPlayer());
						we.moveHead().sendPacket(is.getPlayer());
					}
				}
			} else {
				is.getScene().hardRefreshBlocks();
				currentScene = is.getScene().getSceneBlocks();
				
				for(Location l : currentScene.keySet()) {
					WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange();
					Location b;
					
					if(zm != xm) {
						b = playerLoc.clone().add(currentScene.get(l).get(2)*xm, currentScene.get(l).get(1), currentScene.get(l).get(0)*zm);
					} else {
						b = playerLoc.clone().add(currentScene.get(l).get(0)*xm, currentScene.get(l).get(1), currentScene.get(l).get(2)*zm);
					}
					
					packet.setLocation(new BlockPosition(b.getBlockX(), b.getBlockY(), b.getBlockZ())); 
					packet.setBlockData(WrappedBlockData.createData(b.getBlock().getType()));
					packet.sendPacket(is.getPlayer());
					
					// Support sign data and player skulls
					if(b.getBlock().getType()==Material.SIGN || b.getBlock().getType()==Material.WALL_SIGN) {
						Sign s = (Sign)b.getBlock().getState();
						
						WrapperPlayServerTileEntityData ted = new WrapperPlayServerTileEntityData();
						ted.setLocation(new BlockPosition(b.getBlockX(), b.getBlockY(), b.getBlockZ()));

						ArrayList<NbtBase<?>> tags = new ArrayList<NbtBase<?>>();
						for(int i=0; i<4; i++) {
							tags.add(NbtFactory.of("Text"+(i+1), "{\"extra\":[{\"text\":\""+s.getLine((i))+"\"}],\"text\":\"\"}"));
						}
						
						tags.add(NbtFactory.of("id", "minecraft:sign"));
						tags.add(NbtFactory.of("x", b.getBlockX()));
						tags.add(NbtFactory.of("y", b.getBlockY()));
						tags.add(NbtFactory.of("z", b.getBlockZ()));
						
						ted.setNbtData(NbtFactory.ofCompound("", tags));
						ted.setAction(9);
						
						ted.sendPacket(is.getPlayer());
					}else if(b.getBlock().getType()==Material.SKELETON_SKULL || b.getBlock().getType()==Material.SKELETON_WALL_SKULL) {
						Skull s = (Skull)b.getBlock().getState();
						
						if(s.getType() == Material.PLAYER_HEAD || s.getType() == Material.PLAYER_WALL_HEAD) {
							WrapperPlayServerTileEntityData ted = new WrapperPlayServerTileEntityData();
							ted.setLocation(new BlockPosition(b.getBlockX(), b.getBlockY(), b.getBlockZ()));
							
							ArrayList<NbtBase<?>> tags = new ArrayList<NbtBase<?>>();
							tags.add(NbtFactory.of("id", "minecraft:skull"));
							tags.add(NbtFactory.of("SkullType", 3));
							tags.add(NbtFactory.of("x", b.getBlockX()));
							tags.add(NbtFactory.of("y", b.getBlockY()));
							tags.add(NbtFactory.of("z", b.getBlockZ()));
							tags.add(NbtFactory.of("Rot", HeadRotationUtil.getRotation(s.getRotation())));
							
							ArrayList<NbtBase<?>> otags = new ArrayList<NbtBase<?>>();
							otags.add(NbtFactory.of("Id", s.getOwningPlayer().getUniqueId().toString()));
							otags.add(NbtFactory.of("Name", s.getOwningPlayer().getName()));
							
							tags.add(NbtFactory.ofCompound("Owner", otags));
							
							ted.setNbtData(NbtFactory.ofCompound("", tags));
							ted.setAction(4);
							
							ted.sendPacket(is.getPlayer());
						}
					}
				}
				
				for(WatchedEntity we : currentSceneEnt.keySet()) {
					if(we.hasSpawned())
						we.destroyFakeEntity().sendPacket(is.getPlayer());
				}
				
				this.cancel();
			}
		} else {
			this.cancel();
			is.cancelStream();
		}
	}
	
	private String getDirection(float yaw) {
		BlockFace[] axis = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
        return axis[Math.round(yaw / 90f) & 0x3].getOppositeFace().toString();
    }
}
