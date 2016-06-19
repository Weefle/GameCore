package io.github.gronnmann.gamecore;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.gronnmann.gamecore.GameCore.StopReason;
import io.github.gronnmann.gamecore.events.GameEndEvent;
import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin implements Listener{
	FileConfiguration config;
	public void onEnable(){
		config = this.getConfig();
		config.options().copyDefaults(true);
		this.saveConfig();
		
		Bukkit.getPluginManager().registerEvents(this, this);
		
		this.getCommand("fstart").setExecutor(Commands.getInstance());
		this.getCommand("fstop").setExecutor(Commands.getInstance());
		this.getCommand("setloc").setExecutor(Commands.getInstance());
		
		GameCore.getGameCore().setup(this, config);
	}
	
	
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		if (GameCore.gameStarted == true){
			e.getPlayer().setGameMode(GameMode.SPECTATOR);
			e.getPlayer().teleport(GameCore.getGameCore().getSpectatorSpawn());
			return;
		}
		if (config.getBoolean("lobby_flight") == true){
			e.getPlayer().setAllowFlight(true);
			e.getPlayer().setFlying(true);
		}
		if (config.getBoolean("lobby_enabled") == true){
			e.getPlayer().teleport(GameCore.getGameCore().getLobbyLocation());
		}
		
		
	}
	
	@EventHandler
	public void onGameStop(GameEndEvent e){
		if (e.getWinners() == null){
			Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
				public void run(){
					Bukkit.getServer().shutdown();
				}
			}, 100);
		}
		StopReason reason = e.getReason();
		List<Player> winners = e.getWinners();
		
		StringBuilder builder = new StringBuilder();
		
		for (Player p : winners){
			builder.append(p.getDisplayName()).append(" ");
		}
		
		String winnersS = builder.toString().trim();
		
		for (Player oPl : Bukkit.getOnlinePlayers()){
			if (e.isAnnounced() == false){
				Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
					public void run(){
						Bukkit.getServer().shutdown();
					}
				}, 100);
				return;
			}
			switch(reason){
			case GAME_END:
				oPl.sendMessage(ChatColor.GREEN + "The game has ended.");
				break;
			case GAME_END_TIME:
				oPl.sendMessage(ChatColor.GREEN + "The time has ran out");
				break;
			}
			if (config.getBoolean("announce_winners") == true){
				oPl.sendMessage(ChatColor.GREEN + "The winners are: " + ChatColor.GOLD + winnersS);
			}
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
			public void run(){
				Bukkit.getServer().shutdown();
			}
		}, 100);
		
			
	}
}