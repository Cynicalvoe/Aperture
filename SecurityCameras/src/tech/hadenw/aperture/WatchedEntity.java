package tech.hadenw.aperture;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import com.comphenix.packetwrapper.WrapperPlayServerEntityHeadRotation;
import com.comphenix.packetwrapper.WrapperPlayServerNamedEntitySpawn;
import com.comphenix.packetwrapper.WrapperPlayServerRelEntityMoveLook;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntityLiving;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

public class WatchedEntity {
	private LivingEntity entity;
	private UUID playerUUID;
	private Random r;
	private int entityID;
	private Location lastLoc;
	private boolean hasSpawned;
	
	public WatchedEntity(LivingEntity le) {
		entity = le;
		r = new Random();
		entityID = r.nextInt(Integer.MAX_VALUE)*-1;
		lastLoc = le.getLocation();
		hasSpawned = false;
		
		if(le instanceof Player)
			playerUUID = ((Player)le).getUniqueId();
		else playerUUID = null;
	}
	
	public WrapperPlayServerSpawnEntityLiving spawnFakeMob(Location playerLoc, List<Double> rel, int xm, int zm) {
		WrapperPlayServerSpawnEntityLiving spawnPacket = new WrapperPlayServerSpawnEntityLiving();
		
		if(xm != zm)
			lastLoc = playerLoc.clone().add(rel.get(2)*xm,rel.get(1),rel.get(0)*zm);
		else
			lastLoc = playerLoc.clone().add(rel.get(0)*xm,rel.get(1),rel.get(2)*zm);
		
		lastLoc.setYaw(entity.getLocation().getYaw());
		lastLoc.setPitch(entity.getLocation().getPitch());
		
		spawnPacket.setType(entity.getType());
		spawnPacket.setX(lastLoc.getX());
		spawnPacket.setY(lastLoc.getY());
		spawnPacket.setZ(lastLoc.getZ());
		spawnPacket.setYaw(lastLoc.getYaw());
		spawnPacket.setPitch(lastLoc.getPitch());
		spawnPacket.setEntityID(entityID);
		
		hasSpawned = true;
		
		return spawnPacket;
	}
	
	public WrapperPlayServerNamedEntitySpawn spawnFakePlayer(Location playerLoc, List<Double> rel, int xm, int zm) {
		WrapperPlayServerNamedEntitySpawn spawnPacket = new WrapperPlayServerNamedEntitySpawn();
		
		if(xm != zm)
			lastLoc = playerLoc.clone().add(rel.get(2)*xm,rel.get(1),rel.get(0)*zm);
		else
			lastLoc = playerLoc.clone().add(rel.get(0)*xm,rel.get(1),rel.get(2)*zm);
		
		lastLoc.setYaw(entity.getLocation().getYaw());
		lastLoc.setPitch(entity.getLocation().getPitch());
		
		spawnPacket.setPosition(lastLoc.toVector());
		spawnPacket.setYaw(lastLoc.getYaw());
		spawnPacket.setPitch(lastLoc.getPitch());
		spawnPacket.setEntityID(entityID);
		spawnPacket.setPlayerUUID(((Player)entity).getUniqueId());
		
		spawnPacket.setMetadata(WrappedDataWatcher.getEntityWatcher((Player)entity));
		
		hasSpawned = true;
		
		return spawnPacket;
	}
	
	public WrapperPlayServerRelEntityMoveLook move(Location playerLoc, List<Double> rel, int xm, int zm) {
		WrapperPlayServerRelEntityMoveLook packet = new WrapperPlayServerRelEntityMoveLook();
		Location to;
		
		if(xm != zm)
			to = playerLoc.clone().add(rel.get(2)*xm,rel.get(1),rel.get(0)*zm);
		else
			to = playerLoc.clone().add(rel.get(0)*xm,rel.get(1),rel.get(2)*zm);
		
		packet.setEntityID(entityID);
		packet.setDx(to.getX() - lastLoc.getX());
		packet.setDy(to.getY() - lastLoc.getY());
		packet.setDz(to.getZ() - lastLoc.getZ());
		packet.setPitch(entity.getLocation().getPitch());
		packet.setYaw(entity.getLocation().getYaw());
		packet.setOnGround(entity.isOnGround());
		
		lastLoc = to;
		
		return packet;
	}
	
	public WrapperPlayServerEntityHeadRotation moveHead() {
		WrapperPlayServerEntityHeadRotation headpacket = new WrapperPlayServerEntityHeadRotation();
        headpacket.setEntityID(entityID);
        headpacket.setHeadYaw(getCompressedAngle(entity.getEyeLocation().getYaw()));
		
		return headpacket;
		
	}
	
	public WrapperPlayServerEntityDestroy destroyFakeEntity() {
		WrapperPlayServerEntityDestroy packet = new WrapperPlayServerEntityDestroy();
		packet.setEntityIds(new int[]{entityID});
		
		return packet;
	}
	
	protected byte getCompressedAngle(float value) {
        return (byte)(int) (value * 256.0F / 360.0F);
    }
	
	public boolean hasSpawned() {
		return hasSpawned;
	}
	
	public UUID getPlayerUUID() {
		return playerUUID;
	}
	
	public int getFakeEntityID() {
		return entityID;
	}
	
	public LivingEntity getEntity() {
		return entity;
	}
	
	public boolean isPlayer() {
		if(playerUUID != null)
			return true;
		
		return false;
	}
}
