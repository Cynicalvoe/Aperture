package tech.hadenw.aperture;

import org.bukkit.block.BlockFace;

public class HeadRotationUtil {
	public static int getRotation(BlockFace face) {
		switch(face) {
			case NORTH:
				return 0;
			case NORTH_NORTH_EAST:
				return 1;
			case NORTH_EAST:
				return 2;
			case EAST_NORTH_EAST:
				return 3;
			case EAST:
				return 4;
			case EAST_SOUTH_EAST:
				return 5;
			case SOUTH_EAST:
				return 6;
			case SOUTH_SOUTH_EAST:
				return 7;
			case SOUTH:
				return 8;
			case SOUTH_SOUTH_WEST:
				return 9;
			case SOUTH_WEST:
				return 10;
			case WEST_SOUTH_WEST:
				return 11;
			case WEST:
				return 12;
			case WEST_NORTH_WEST:
				return 13;
			case NORTH_WEST:
				return 14;
			case NORTH_NORTH_WEST:
				return 15;
			default:
				return -1;
		}
	}
}
