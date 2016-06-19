package io.github.gronnmann.gamecore;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import io.github.gronnmann.gamecore.events.GameStartEvent;

public class GameCore {
	public static boolean gameStarted = false;
	public enum StopReason {GAME_END_TIME, GAME_END, FORCE};
	FileConfiguration config, locs;
	File locations;
	int secondsremaining;
	private Location lobby, specspawn;
	
	private GameCore(){};
	private static GameCore gc = new GameCore();
	public static GameCore getGameCore(){
		return gc;
	}
	
	public void setup(Plugin p, FileConfiguration conf){
		
		config = conf;
		secondsremaining = config.getInt("waiting_time");
		
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(p, new Runnable() {
			public void run(){
				if (gameStarted == true)return;
				if (secondsremaining == 0){
					if (Bukkit.getOnlinePlayers().size() < config.getInt("min_players")){
							for (Player oPl : Bukkit.getOnlinePlayers()) {
								oPl.sendMessage(ChatColor.RED + "Not enough players. Restarting waiting time");
								secondsremaining = config.getInt("waiting_time");
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
		
		
		locations = new File(p.getDataFolder(), "locations.yml");
		if (!(locations.exists())){
			try {
				locations.createNewFile();
				locs = YamlConfiguration.loadConfiguration(locations);
				
				locs.set("lobby.w", "world");
				locs.set("lobby.x", "1");
				locs.set("lobby.y", "75");
				locs.set("lobby.z", "1");
				
				locs.set("spec.w", "world");
				locs.set("spec.x", "1");
				locs.set("spec.y", "75");
				locs.set("spec.z", "1");
				locs.save(locations);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		locs = YamlConfiguration.loadConfiguration(locations);
		
		lobby = new Location(Bukkit.getWorld(locs.getString("lobby.w")), locs.getDouble("lobby.x"), locs.getDouble("lobby.y"), locs.getDouble("lobby.z"));
		specspawn = new Location(Bukkit.getWorld(locs.getString("spec.w")), locs.getDouble("spec.x"), locs.getDouble("spec.y"), locs.getDouble("spec.z"));
	}
	
	public void startGame(){
		gameStarted = true;
		for (Player oPl : Bukkit.getOnlinePlayers()){
			oPl.sendMessage(ChatColor.RED + "The game has been started!");
			oPl.setLevel(0);
			oPl.setFlying(false);
			oPl.setAllowFlight(false);
			oPl.setGameMode(GameMode.valueOf(config.getString("gamestart_gamemode").toUpperCase()));
		}
		Bukkit.getPluginManager().callEvent(new GameStartEvent());
	}
	
	public Location getLobbyLocation(){
		return lobby;
	}
	
	public Location getSpectatorSpawn(){
		return specspawn;
	}
	
	public void saveLocs(){
		try{
			locs.save(locations);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	
}
