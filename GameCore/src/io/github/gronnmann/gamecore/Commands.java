package io.github.gronnmann.gamecore;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.gronnmann.gamecore.GameCore.StopReason;
import io.github.gronnmann.gamecore.events.GameEndEvent;

public class Commands implements CommandExecutor{
	private Commands(){}
	private static Commands executor = new Commands();
	public static Commands getInstance(){
		return executor;
	}
	public boolean onCommand(CommandSender sender, Command cmd, String l, String args[]){
		if (l.equalsIgnoreCase("fstart")){
			if (!(sender.hasPermission("gamecore.fstart"))){
				sender.sendMessage(ChatColor.RED + "You don't have permission to do this.");
				return true;
			}
			GameCore.getGameCore().startGame();
			sender.sendMessage(ChatColor.GREEN + "Forced the game to start");
			for (Player oPl : Bukkit.getOnlinePlayers()){
				oPl.sendMessage(ChatColor.RED + "The game has been forced to start by an operator");
			}
		}
		else if (l.equalsIgnoreCase("fstop")){
			if (!(sender.hasPermission("gamecore.fstop"))){
				sender.sendMessage(ChatColor.RED + "You don't have permission to do this.");
				return true;
			}
			Bukkit.getPluginManager().callEvent(new GameEndEvent(StopReason.FORCE));
			sender.sendMessage(ChatColor.GREEN + "Forced the game to stop");
			for (Player oPl : Bukkit.getOnlinePlayers()){
				oPl.sendMessage(ChatColor.RED + "The game has been forced to stop by an operator");
			}
		}
		
		else if (l.equalsIgnoreCase("setloc")){
			if (!(sender instanceof Player)){
				sender.sendMessage("Only players can use this command.");
				return true;
			}
			Player p = (Player)sender;
			if (!(sender.hasPermission("gamecore.setloc"))){
				p.sendMessage(ChatColor.RED + "You don't have permission to do this.");
				return true;
			}
			if (args.length != 1){
				p.sendMessage(ChatColor.RED + "Usage: /setloc [spectators/lobby]");
				return true;
			}
			if (args[0].equalsIgnoreCase("spectators")){
				GameCore.getGameCore().locs.set("spec.w", p.getWorld().getName());
				GameCore.getGameCore().locs.set("spec.x", p.getLocation().getX());
				GameCore.getGameCore().locs.set("spec.y", p.getLocation().getY());
				GameCore.getGameCore().locs.set("spec.z", p.getLocation().getZ());
				p.sendMessage(ChatColor.GREEN + "The location of " + ChatColor.YELLOW + "SPECTATOR SPAWN " + ChatColor.GREEN + "has been set.");
				GameCore.getGameCore().saveLocs();
				return true;
			}else if (args[0].equalsIgnoreCase("lobby")){
				GameCore.getGameCore().locs.set("lobby.w", p.getWorld().getName());
				GameCore.getGameCore().locs.set("lobby.x", p.getLocation().getX());
				GameCore.getGameCore().locs.set("lobby.y", p.getLocation().getY());
				GameCore.getGameCore().locs.set("lobby.z", p.getLocation().getZ());
				p.sendMessage(ChatColor.GREEN + "The location of " + ChatColor.YELLOW + "LOBBY " + ChatColor.GREEN + "has been set.");
				GameCore.getGameCore().saveLocs();
				return true;
			}else{
				p.sendMessage(ChatColor.RED + "Usage: /setloc [spectators/lobby]");
				return true;
			}
		}
		return true;
	}
}
