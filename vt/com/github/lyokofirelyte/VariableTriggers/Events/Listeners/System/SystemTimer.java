package com.github.lyokofirelyte.VariableTriggers.Events.Listeners.System;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.github.lyokofirelyte.VariableTriggers.VTParser;
import com.github.lyokofirelyte.VariableTriggers.VariableTriggers;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.AR;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.VTMap;

public class SystemTimer extends VTMap<Object, Object> implements AR, Runnable {

	private VariableTriggers main;
	
	public SystemTimer(VariableTriggers i){
		main = i;
		makePath("./plugins/VariableTriggers/events/system", "SystemTimer.yml");
		load();
	}
	
	public void run(){
		if (getLong("ActiveCooldown") <= System.currentTimeMillis()){
			if (getList("main").size() > 0){
				new VTParser(main, "SystemTimer.yml", "main", getList("main"), new Location(Bukkit.getWorlds().get(0), 0, 0, 0), new HashMap<String, String>(), "VTSystem").start();
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