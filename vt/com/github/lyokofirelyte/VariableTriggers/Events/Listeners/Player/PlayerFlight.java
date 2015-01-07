package com.github.lyokofirelyte.VariableTriggers.Events.Listeners.Player;


import java.util.HashMap;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import com.github.lyokofirelyte.VariableTriggers.VTParser;
import com.github.lyokofirelyte.VariableTriggers.VariableTriggers;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.AR;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.VTMap;

public class PlayerFlight extends VTMap<Object, Object> implements AR {

	private VariableTriggers main;
	
	public PlayerFlight(VariableTriggers i){
		main = i;
		makePath("./plugins/VariableTriggers/events/player", "PlayerFlight.yml");
		load();
	}
	
	@EventHandler (ignoreCancelled = false)
	public void onSneak(PlayerToggleFlightEvent e){
		
		if (getList("Worlds").contains(e.getPlayer().getWorld().getName())){
			if (getLong("ActiveCooldown") <= System.currentTimeMillis()){
				if (getList("main").size() > 0){
					if (getBool("Cancelled")){
						e.setCancelled(true);
					}
					new VTParser(main, "PlayerFlight.yml", "main", getList("main"), e.getPlayer().getLocation(), getCustoms(e), e.getPlayer().getName()).start();
					cooldown();
				}
			}
		}
	}
	
	private HashMap<String, String> getCustoms(PlayerToggleFlightEvent e){

		HashMap<String, String> map = new HashMap<String, String>();
		
		map.put("<flying>", e.isFlying() + "");

		return map;
	}
	
	public void loadAll(){
		load();
	}
	
	public void saveAll(){
		save();
	}
}