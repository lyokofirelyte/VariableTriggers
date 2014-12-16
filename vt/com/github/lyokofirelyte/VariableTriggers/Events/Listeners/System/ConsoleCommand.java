package com.github.lyokofirelyte.VariableTriggers.Events.Listeners.System;

import gnu.trove.map.hash.THashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;

import com.github.lyokofirelyte.VariableTriggers.VTParser;
import com.github.lyokofirelyte.VariableTriggers.VariableTriggers;
import com.github.lyokofirelyte.VariableTriggers.Events.ConsoleCommandEvent;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.AR;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.VTMap;
import com.github.lyokofirelyte.VariableTriggers.Utils.VTUtils;

public class ConsoleCommand extends VTMap<Object, Object> implements AR {

	private VariableTriggers main;
	
	public ConsoleCommand(VariableTriggers i){
		main = i;
		makePath("./plugins/VariableTriggers/events/system", "ConsoleCommand.yml");
		load();
	}
	
	@EventHandler (ignoreCancelled = false)
	public void onCmd(ConsoleCommandEvent e){
		
		String path = e.getType().split(" ")[0];
		
		if (!containsKey(path + ".Script")){
			VTUtils.s(e.getSender(), "You haven't defined that console command in your script file!");
		} else {
			if (getLong("ActiveCooldown") <= System.currentTimeMillis()){
				if (getLong(path + ".ActiveCooldown") <= System.currentTimeMillis()){
					new VTParser(main, "ConsoleCommand.yml", path, getList(path + ".Script"), new Location(Bukkit.getWorlds().get(0), 0, 0, 0), getCustoms(e), "Console").start();
					set(path + ".ActiveCooldown", (System.currentTimeMillis() + getLong(path + ".Cooldown")*1000L));
					cooldown();
				}
			}
		}
	}
	
	private THashMap<String, String> getCustoms(ConsoleCommandEvent e){
		
		THashMap<String, String> map = new THashMap<String, String>();
		map.put("<cmdline>", e.getType().replace(e.getType().split(" ")[0], ""));
		map.put("<cmdargcount>", e.getType().split(" ").length + "");
		map.put("<cmdname>", e.getType().split(" ")[0]);
		
		for (int i = 0; i < e.getType().split(" ").length; i++){
			map.put("<cmdarg:" + (i+1) + ">", e.getType().split(" ")[i]);
			map.put("<cmdarg" + (i+1) + ">", e.getType().split(" ")[i]);
		}
		
		return map;
	}
	
	public void loadAll(){
		load();
	}
	
	public void saveAll(){
		save();
	}
}