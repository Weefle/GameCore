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
		return true;
	}
}
