package com.github.lyokofirelyte.VariableTriggers.Events;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ConsoleCommandEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private CommandSender commandSender;
    private String type;
    
    public ConsoleCommandEvent(CommandSender p, String command) {
        commandSender = p;
        type = command;
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
}