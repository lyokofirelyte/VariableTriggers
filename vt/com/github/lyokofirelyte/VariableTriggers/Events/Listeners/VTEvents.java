package com.github.lyokofirelyte.VariableTriggers.Events.Listeners;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.lyokofirelyte.VariableTriggers.VariableTriggers;
import com.github.lyokofirelyte.VariableTriggers.Events.VTPluginMessageOutEvent;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.AR;

public class VTEvents implements AR {
	
	private VariableTriggers main;
	
	public VTEvents(VariableTriggers i){
		main = i;
	}

	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onMessage(VTPluginMessageOutEvent e){
		
		CommandSender cs = e.getSender();
		Player p = null;
		
		if (cs instanceof Player){
			p = e.getPlayer();
		}
		
		switch (e.getType()){
		
			case "globalChat": break;
		
			case "noPerms":
				
				s(cs, "&4No permissions!");
				
			break;
			
			case "playerNotFound":
				
				s(cs, "&c&oThat player could not be found. Did you spell it correctly?");
				
			break;
			
			case "invalidNumber":
				
				s(cs, "&c&oThat number is invalid.");
				
			break;
			
			default:
				
				if (e.getExtras() == null){
					s(cs, e.getType());
				} else {
					for (String ss : e.getExtras()){
						s(cs, ss);
					}
				}
				
			break;
		}
	}
	
	public void s(CommandSender sender, String message){
		sender.sendMessage(main.AS("&7VT &8// &7" + message));
	}
}