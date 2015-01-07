package com.github.lyokofirelyte.VariableTriggers.Events.Listeners.System;

import java.util.HashMap;

import org.bukkit.event.EventHandler;

import com.github.lyokofirelyte.VariableTriggers.VTParser;
import com.github.lyokofirelyte.VariableTriggers.VariableTriggers;
import com.github.lyokofirelyte.VariableTriggers.Events.CommandNotFoundEvent;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.AR;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.VTMap;

public class CommandNotFound extends VTMap<Object, Object> implements AR {

	private VariableTriggers main;
	
	public CommandNotFound(VariableTriggers i){
		main = i;
		makePath("./plugins/VariableTriggers/events/system", "CommandNotFound.yml");
		load();
	}
	
	@EventHandler (ignoreCancelled = false)
	public void onBadCommand(CommandNotFoundEvent e){
		
		if (getList("Worlds").contains(e.getLocation().getWorld().getName())){
			if (getLong("ActiveCooldown") <= System.currentTimeMillis()){
				if (getList("main").size() > 0){
					new VTParser(main, "CommandNotFound.yml", "main", getList("main"), e.getLocation(), getCustoms(e), e.getSender().getName()).start();
					cooldown();
				}
			}
		}
	}

	private HashMap<String, String> getCustoms(CommandNotFoundEvent e){
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("<cmdname>", e.getType());
		return map;
	}
	
	public void loadAll(){
		load();
	}
	
	public void saveAll(){
		save();
	}
}