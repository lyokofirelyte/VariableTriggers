package com.github.lyokofirelyte.VariableTriggers.Events.Listeners.Player;

import gnu.trove.map.hash.THashMap;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import com.github.lyokofirelyte.VariableTriggers.VTParser;
import com.github.lyokofirelyte.VariableTriggers.VariableTriggers;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.AR;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.VTMap;

public class PlayerJoin extends VTMap<Object, Object> implements AR {

	private VariableTriggers main;
	
	public PlayerJoin(VariableTriggers i){
		main = i;
		makePath("./plugins/VariableTriggers/events/player", "PlayerJoin.yml");
		load();
	}
	
	@EventHandler (ignoreCancelled = false)
	public void onJoin(PlayerJoinEvent e){
		
		if (getList("Worlds").contains(e.getPlayer().getWorld().getName())){
			if (getLong("ActiveCooldown") <= System.currentTimeMillis()){
				if (getList("main").size() > 0){
					if (getBool("Cancelled")){
						e.setJoinMessage(null);
					}
					new VTParser(main, "PlayerJoin.yml", "main", getList("main"), e.getPlayer().getLocation(), new THashMap<String, String>(), e.getPlayer().getName()).start();
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