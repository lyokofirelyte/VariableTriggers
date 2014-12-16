package com.github.lyokofirelyte.VariableTriggers.Events;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class VTPluginMessageOutEvent extends Event implements Cancellable {

	private boolean cancelled = false;
    private static final HandlerList handlers = new HandlerList();
    private CommandSender commandSender;
    private Player p;
    private String type;
    private String[] extras;

    public VTPluginMessageOutEvent(CommandSender p, String type) {
        commandSender = p;
        if (commandSender instanceof Player){
        	this.p = (Player) p;
        }
        this.type = type;
    }
    
    public VTPluginMessageOutEvent(CommandSender p, String type, String[] extras) {
        commandSender = p;
        if (commandSender instanceof Player){
        	this.p = (Player) p;
        }
        this.type = type;
        this.extras = extras;
    }

	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
    public static HandlerList getHandlerList() {
        return handlers;
    }
	
	public String[] getExtras(){
		return extras;
	}
	
	public Player getPlayer(){
		return p;
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