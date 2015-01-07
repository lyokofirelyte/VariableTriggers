package com.github.lyokofirelyte.VariableTriggers.Events.Listeners.System;

import java.util.HashMap;

import org.bukkit.event.EventHandler;

import com.github.lyokofirelyte.VariableTriggers.VTParser;
import com.github.lyokofirelyte.VariableTriggers.VariableTriggers;
import com.github.lyokofirelyte.VariableTriggers.Events.VTSystemEvent;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.AR;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.VTData;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.VTMap;

public class SystemEnable extends VTMap<Object, Object> implements AR {

	private VariableTriggers main;
	
	public SystemEnable(VariableTriggers i){
		main = i;
		makePath("./plugins/VariableTriggers/events/system", "SystemEnable.yml");
		load();
	}
	
	@EventHandler (ignoreCancelled = false)
	public void onSystemEnable(VTSystemEvent e){
		
		if (getLong("ActiveCooldown") <= System.currentTimeMillis() && e.getType().equals(VTData.ENABLE)){
			if (getList("main").size() > 0){
				new VTParser(main, "SystemEnable.yml", "main", getList("main"), e.getLocation(), new HashMap<String, String>(), e.getSender()).start();
				cooldown();
			}
		}
	}
	
	public void loadAll(){
		load();
	}
	
	public void saveAll(){
		save();
	}
}