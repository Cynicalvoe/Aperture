package tech.hadenw.aperture;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AP implements CommandExecutor{
	private Aperture plugin;
	private String version;
	
	public AP(Aperture c) {
		plugin=c;
		version = plugin.getDescription().getVersion();
		plugin.setUURL("&id=%%__NONCE__%%");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("ap") || cmd.getName().equalsIgnoreCase("aperture")) {
			if(plugin.isProtocolLib) {
				if(args.length == 0) {
					sender.sendMessage("§a§l>>>>>>>>>>>>>>> §2Aperture §8"+version+" §a§l<<<<<<<<<<<<<<<");
					sender.sendMessage("§2/ap getCamera §7- Get a new camera item");
					sender.sendMessage("§2/ap stream [player] §7- Begin streaming");
					sender.sendMessage("§2/ap share §7- Share a camera with others");
					sender.sendMessage("§2/ap reload §7- Reload the configuration");
					sender.sendMessage("\n§5Created by §lFireBreath15\n§5§ohttp://hadenw.tech");
					sender.sendMessage("§a§l>>>>>>>>>>>>>>> §2Aperture §8"+version+" §a§l<<<<<<<<<<<<<<<");
				}else{
					if(args[0].equalsIgnoreCase("getcamera")) {
						if(sender instanceof Player) {
							if(sender.hasPermission("aperture.getcamera")) {
								((Player)sender).getInventory().addItem(plugin.getUserConf().getCameraItem());
							}else {
								sender.sendMessage(plugin.getMessages().noPermission());
							}
						}else {
							sender.sendMessage(plugin.getMessages().mustBePlayer());
						}
					} else if(args[0].equalsIgnoreCase("reload")) {
						if(sender.hasPermission("aperture.reload")) {
							plugin.getUserConf().loadValues();
							sender.sendMessage(plugin.getMessages().pluginReloaded());
						}else {
							sender.sendMessage(plugin.getMessages().noPermission());
						}
					}else if(args[0].equalsIgnoreCase("share")) {
						if(sender.hasPermission("aperture.share")) {
							Bukkit.getServer().getPluginManager().registerEvents(new SelectCameraGUI(plugin, (Player)sender, true), plugin);
						}else {
							sender.sendMessage(plugin.getMessages().noPermission());
						}
					} else if(args[0].equalsIgnoreCase("stream")) {
						if(sender instanceof Player) {
							if(sender.hasPermission("aperture.stream")) {
								if(args.length == 2) {
									if(sender.hasPermission("aperture.stream.other")) {
										OfflinePlayer op = Bukkit.getOfflinePlayer(args[1]);
										
										if(op != null) {
											Bukkit.getServer().getPluginManager().registerEvents(new SelectCameraGUI(plugin, (Player)sender, op), plugin);
										}else {
											sender.sendMessage(plugin.getMessages().playerNotFound());
										}
									}else {
										sender.sendMessage(plugin.getMessages().noPermission());
									}
								}else if(args.length == 1){
									Bukkit.getServer().getPluginManager().registerEvents(new SelectCameraGUI(plugin, (Player)sender, false), plugin);
								} else {
									sender.sendMessage(plugin.getMessages().wrongCommand());
								}
							}else {
								sender.sendMessage(plugin.getMessages().noPermission());
							}
						}else {
							sender.sendMessage(plugin.getMessages().mustBePlayer());
						}
					} else {
						sender.sendMessage(plugin.getMessages().wrongCommand());
					}
				}
			}else {
				sender.sendMessage("§cAperture requires §e§lProtocolLib §cto function properly. Please install it and restart your server.");
			}
			
			return true;
		}
		return false;
	}
}
