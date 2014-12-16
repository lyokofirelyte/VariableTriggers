package com.github.lyokofirelyte.VariableTriggers.Events.Listeners.Entity;

import gnu.trove.map.hash.THashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.vehicle.VehicleExitEvent;

import com.github.lyokofirelyte.VariableTriggers.VTParser;
import com.github.lyokofirelyte.VariableTriggers.VariableTriggers;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.AR;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.VTMap;

public class VehicleExit extends VTMap<Object, Object> implements AR {

	private VariableTriggers main;
	
	public VehicleExit(VariableTriggers i){
		main = i;
		makePath("./plugins/VariableTriggers/events/entity", "VehicleExit.yml");
		load();
	}
	
	@EventHandler (ignoreCancelled = false)
	public void onDamage(VehicleExitEvent e){
		
		if (getList("Worlds").contains(e.getExited().getWorld().getName())){
			if (getLong("ActiveCooldown") <= System.currentTimeMillis()){
				if (getBool("Cancelled")){
					e.setCancelled(true);
				}
				if (getList("main").size() > 0){
					new VTParser(main, "VehicleExit.yml", "main", getList("main"), e.getVehicle().getLocation(), getCustoms(e), e.getExited().getType().name().toLowerCase()).start();
					cooldown();
				}
			}
		}
	}
	
	private THashMap<String, String> getCustoms(VehicleExitEvent e){

		THashMap<String, String> map = new THashMap<String, String>();
		String type = e.getExited().getType().name();
		
		map.put("<entitytype>", type);
		
		if (e.getExited() instanceof Player){
			map.put("<entityname>", ((Player) e.getExited()).getName());
		} else {
			map.put("<entityname>", type.substring(0, 1) + type.substring(1).toLowerCase());
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