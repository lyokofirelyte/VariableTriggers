package com.github.lyokofirelyte.VariableTriggers.Events;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CommandNotFoundEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private CommandSender commandSender;
    private Location where;
    private String type;
    
    public CommandNotFoundEvent(CommandSender p, String command, Location loc) {
        commandSender = p;
        type = command;
        setLocation(loc);
    }

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
    public static HandlerList getHandlerList() {
        return handlers;
    }

	public CommandSender getSender(){
		return commandSender;
	}
	
	public String getType(){
		return type;
	}
	
	public void setType(String t){
		type = t;
	}

	public Location getLocation(){
		return where;
	}

	public void setLocation(Location where){
		this.where = where;
	}
}