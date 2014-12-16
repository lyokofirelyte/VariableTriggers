package com.github.lyokofirelyte.VariableTriggers.Events.Listeners.System;

import gnu.trove.map.hash.THashMap;

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

	private THashMap<String, String> getCustoms(CommandNotFoundEvent e){
		
		THashMap<String, String> map = new THashMap<String, String>();
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