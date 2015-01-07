package com.github.lyokofirelyte.VariableTriggers.Events.Listeners.Entity;

import java.util.HashMap;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;

import com.github.lyokofirelyte.VariableTriggers.VTParser;
import com.github.lyokofirelyte.VariableTriggers.VariableTriggers;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.AR;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.VTMap;

public class EntitySpawn extends VTMap<Object, Object> implements AR {

	private VariableTriggers main;
	
	public EntitySpawn(VariableTriggers i){
		main = i;
		makePath("./plugins/VariableTriggers/events/entity", "EntitySpawn.yml");
		load();
	}
	
	@EventHandler (ignoreCancelled = false)
	public void onDamage(CreatureSpawnEvent e){
		
		if (getList("Worlds").contains(e.getEntity().getWorld().getName())){
			if (getLong("ActiveCooldown") <= System.currentTimeMillis()){
				if (getBool("Cancelled")){
					e.setCancelled(true);
				}
				if (getList("main").size() > 0){
					new VTParser(main, "EntitySpawn.yml", "main", getList("main"), e.getEntity().getLocation(), getCustoms(e), e.getEntity().getType().name().toLowerCase()).start();
					cooldown();
				}
			}
		}
	}
	
	private HashMap<String, String> getCustoms(CreatureSpawnEvent e){

		HashMap<String, String> map = new HashMap<String, String>();
		String type = e.getEntity().getType().name();
		
		map.put("<entitytype>", type);
		map.put("<entityname>", type.substring(0, 1) + type.substring(1).toLowerCase());

		return map;
	}
	
	public void loadAll(){
		load();
	}
	
	public void saveAll(){
		save();
	}
}