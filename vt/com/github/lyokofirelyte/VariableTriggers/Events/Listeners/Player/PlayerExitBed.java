package com.github.lyokofirelyte.VariableTriggers.Events.Listeners.Player;

import java.util.HashMap;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBedLeaveEvent;

import com.github.lyokofirelyte.VariableTriggers.VTParser;
import com.github.lyokofirelyte.VariableTriggers.VariableTriggers;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.AR;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.VTMap;

public class PlayerExitBed extends VTMap<Object, Object> implements AR {

	private VariableTriggers main;
	
	public PlayerExitBed(VariableTriggers i){
		main = i;
		makePath("./plugins/VariableTriggers/events/player", "PlayerExitBed.yml");
		load();
	}
	
	@EventHandler (ignoreCancelled = false)
	public void onBed(PlayerBedLeaveEvent e){
		
		if (getList("Worlds").contains(e.getPlayer().getWorld().getName())){
			if (getLong("ActiveCooldown") <= System.currentTimeMillis()){
				if (getList("main").size() > 0){
					new VTParser(main, "PlayerExitBed.yml", "main", getList("main"), e.getBed().getLocation(), new HashMap<String, String>(), e.getPlayer().getName()).start();
					cooldown();
				}
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