package com.github.lyokofirelyte.VariableTriggers.Events.Listeners.Entity;

import gnu.trove.map.hash.THashMap;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityCreatePortalEvent;

import com.github.lyokofirelyte.VariableTriggers.VTParser;
import com.github.lyokofirelyte.VariableTriggers.VariableTriggers;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.AR;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.VTMap;

public class EntityCreatePortal extends VTMap<Object, Object> implements AR {

	private VariableTriggers main;
	
	public EntityCreatePortal(VariableTriggers i){
		main = i;
		makePath("./plugins/VariableTriggers/events/entity", "EntityCreatePortal.yml");
		load();
	}
	
	@EventHandler (ignoreCancelled = false)
	public void onPortal(EntityCreatePortalEvent e){
		
		if (getList("Worlds").contains(e.getEntity().getWorld().getName())){
			if (getLong("ActiveCooldown") <= System.currentTimeMillis()){
				if (getBool("Cancelled")){
					e.setCancelled(true);
				}
				if (getList("main").size() > 0){
					new VTParser(main, "EntityCreatePortal.yml", "main", getList("main"), e.getEntity().getLocation(), getCustoms(e), e.getEntity().getType().name().toLowerCase()).start();
					cooldown();
				}
			}
		}
	}
	
	private THashMap<String, String> getCustoms(EntityCreatePortalEvent e){

		THashMap<String, String> map = new THashMap<String, String>();
		String type = e.getEntity().getType().name();
		
		map.put("<entitytype>", type.substring(0, 1) + type.substring(1).toLowerCase());
		map.put("<portaltype>", e.getPortalType().name());

		return map;
	}
	
	public void loadAll(){
		load();
	}
	
	public void saveAll(){
		save();
	}
}