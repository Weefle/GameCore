package me.gronnmann.gamecore.events;

import java.util.List;

import me.gronnmann.gamecore.Main.StopReason;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameEndEvent extends Event{
	private static HandlerList handler = new HandlerList();
	
	public static HandlerList getHandlerList(){
		return handler;
	}
	
	public HandlerList getHandlers(){
		return handler;
	}
	private StopReason rsn;
	private List<Player> winners, losers;
	private boolean announceEvent;
	
	public GameEndEvent(StopReason reason, List<Player> winners, List<Player> losers, boolean announceEvent){
		this.rsn = reason;
		this.winners = winners;
		this.losers = losers;
		this.announceEvent = announceEvent;
	}
	
	public GameEndEvent(StopReason reason, List<Player> winners, List<Player> losers){
		this.rsn = reason;
		this.winners = winners;
		this.losers = losers;
	}
	
	public GameEndEvent(StopReason reason){
		this.rsn = reason;
		this.winners = null;
		this.losers = null;
	}
	
	public GameEndEvent(StopReason reason, List<Player> winners){
		this.rsn = reason;
		this.winners = winners;
		this.losers = null;
	}
	
	public StopReason getReason(){
		return rsn;
	}
	public List<Player> getWinners(){
		return winners;
	}
	public List<Player> getLosers(){
		return losers;
	}
	public boolean isAnnounced(){
		return announceEvent;
	}
}
