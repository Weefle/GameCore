package me.gronnmann.gamecore;

import java.util.List;

import me.gronnmann.gamecore.events.GameEndEvent;
import me.gronnmann.gamecore.events.GameStartEvent;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
	int secondsremaining;
	public static boolean gameStarted = false;
	public enum StopReason {GAME_END_TIME, GAME_END, FORCE};
	FileConfiguration config;
	public void onEnable(){
		config = this.getConfig();
		config.options().copyDefaults(true);
		this.saveConfig();
		Bukkit.getPluginManager().registerEvents(this, this);
		secondsremaining = config.getInt("waiting_time");
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run(){
				if (gameStarted == true)return;
				if (secondsremaining == 0){
					if (Bukkit.getOnlinePlayers().size() < 2){
						secondsremaining = config.getInt("waiting_time");
							for (Player oPl : Bukkit.getOnlinePlayers()) {
								oPl.sendMessage(ChatColor.RED + "Not enough players. Restarting waiting time");
							}
							return;
						}
					startGame();
					
					return;
				}
				secondsremaining--;
				for (Player oPl : Bukkit.getOnlinePlayers()){
					oPl.setLevel(secondsremaining);
					if (secondsremaining == 300 || secondsremaining == 240 || secondsremaining == 180 || secondsremaining == 120){
						oPl.sendMessage(ChatColor.RED + "The game is starting in " + secondsremaining/60 + " minutes!");
					}
					else if (secondsremaining == 60){
						oPl.sendMessage(ChatColor.RED + "The game is starting in 1 minute");
					}
					else if (secondsremaining == 30 || secondsremaining == 15 || secondsremaining == 10 || secondsremaining < 6){
						oPl.sendMessage(ChatColor.RED + "The game is starting in " + secondsremaining + " seconds!");
						oPl.playEffect(oPl.getLocation(), Effect.GHAST_SHRIEK, 1);
					}
				}
			}
		}, 20, 20);
	}
	
	public void startGame(){
		gameStarted = true;
		for (Player oPl : Bukkit.getOnlinePlayers()){
			oPl.sendMessage(ChatColor.RED + "The game has been started!");
			oPl.setLevel(0);
			oPl.setFlying(false);
			oPl.setAllowFlight(false);
			oPl.setGameMode(GameMode.SURVIVAL);
		}
		Bukkit.getPluginManager().callEvent(new GameStartEvent());
	}
	
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		if (gameStarted == true){
			e.getPlayer().setGameMode(GameMode.SPECTATOR);
			return;
		}
		e.getPlayer().setAllowFlight(true);
		e.getPlayer().setFlying(true);
		
	}
	
	@EventHandler
	public void onGameStop(GameEndEvent e){
		if (e.getWinners() == null)return;
		StopReason reason = e.getReason();
		List<Player> winners = e.getWinners();
		
		StringBuilder builder = new StringBuilder();
		
		for (Player p : winners){
			builder.append(p.getDisplayName()).append(" ");
		}
		
		String winnersS = builder.toString().trim();
		
		for (Player oPl : Bukkit.getOnlinePlayers()){
			if (e.isAnnounced() == false)return;
			switch(reason){
			case GAME_END:
				oPl.sendMessage(ChatColor.GREEN + "The game has ended.");
				break;
			case GAME_END_TIME:
				oPl.sendMessage(ChatColor.GREEN + "The time has ran out");
				break;
			}
			if (config.getBoolean("announce_winners") == true){
				oPl.sendMessage(ChatColor.GREEN + "The winners are: " + winnersS);
			}
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
			public void run(){
				Bukkit.getServer().shutdown();
			}
		}, 100);
		
			
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String l, String args[]){
		if (l.equalsIgnoreCase("fstart")){
			if (!(sender.hasPermission("gamecore.fstart"))){
				sender.sendMessage(ChatColor.RED + "You don't have permission to do this.");
				return true;
			}
			startGame();
			sender.sendMessage(ChatColor.GREEN + "Forced the game to start");
			for (Player oPl : Bukkit.getOnlinePlayers()){
				oPl.sendMessage(ChatColor.RED + "The game has been forced to start by an operator");
			}
		}
		else if (l.equalsIgnoreCase("fstop")){
			if (!(sender.hasPermission("gamecore.fstart"))){
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