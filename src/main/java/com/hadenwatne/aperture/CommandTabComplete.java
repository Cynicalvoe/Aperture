package com.hadenwatne.aperture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

public class CommandTabComplete implements TabCompleter{
	private static final List<String> commands = Arrays.asList(new String[] {"getCamera", "stream", "share", "reload"});
	
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> matches = new ArrayList<String>();
		
		if(args.length > 0) {
			if(args.length == 1) {
				StringUtil.copyPartialMatches(args[0], commands, matches);
				
			} else if(args[0].equalsIgnoreCase("stream") && args.length <= 2) {
				matches.add("[player]");
			}
		}
		
		return matches;
	}
}
