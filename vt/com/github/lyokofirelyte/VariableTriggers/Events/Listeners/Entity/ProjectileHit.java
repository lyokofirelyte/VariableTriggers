package com.github.lyokofirelyte.VariableTriggers.Events.Listeners.Entity;

import java.util.HashMap;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;

import com.github.lyokofirelyte.VariableTriggers.VTParser;
import com.github.lyokofirelyte.VariableTriggers.VariableTriggers;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.AR;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.VTMap;

public class ProjectileHit extends VTMap<Object, Object> implements AR {

	private VariableTriggers main;
	
	public ProjectileHit(VariableTriggers i){
		main = i;
		makePath("./plugins/VariableTriggers/events/entity", "ProjectileHit.yml");
		load();
	}
	
	@EventHandler (ignoreCancelled = false)
	public void onHit(ProjectileHitEvent e){
		
		if (getList("Worlds").contains(e.getEntity().getWorld().getName())){
			if (getLong("ActiveCooldown") <= System.currentTimeMillis()){
				if (getList("main").size() > 0){
					new VTParser(main, "ProjectileHit.yml", "main", getList("main"), e.getEntity().getLocation(), getCustoms(e), e.getEntity().getType().name().toLowerCase()).start();
					cooldown();
				}
			}
		}
	}
	
	private HashMap<String, String> getCustoms(ProjectileHitEvent e){

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("<projectile>", e.getEntity().getType().name().toLowerCase());
		map.put("<projectileshooter>", ((Entity) e.getEntity().getShooter()).getType().name().toLowerCase());
		
		if (((Entity) e.getEntity().getShooter()).getType().equals(EntityType.PLAYER)){
			map.put("<projectileshootername>", ((Player) e.getEntity().getShooter()).getName());
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