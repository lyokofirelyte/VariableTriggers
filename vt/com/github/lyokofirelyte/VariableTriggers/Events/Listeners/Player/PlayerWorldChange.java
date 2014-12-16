package com.github.lyokofirelyte.VariableTriggers.Events.Listeners.Player;

import gnu.trove.map.hash.THashMap;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import com.github.lyokofirelyte.VariableTriggers.VTParser;
import com.github.lyokofirelyte.VariableTriggers.VariableTriggers;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.AR;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.VTMap;

public class PlayerWorldChange extends VTMap<Object, Object> implements AR {

	private VariableTriggers main;
	
	public PlayerWorldChange(VariableTriggers i){
		main = i;
		makePath("./plugins/VariableTriggers/events/player", "PlayerWorldChange.yml");
		load();
	}
	
	@EventHandler (ignoreCancelled = false)
	public void onHit(PlayerChangedWorldEvent e){
		
		if (getList("Worlds").contains(e.getPlayer().getWorld().getName())){
			if (getLong("ActiveCooldown") <= System.currentTimeMillis()){
				if (getList("main").size() > 0){
					new VTParser(main, "PlayerWorldChange.yml", "main", getList("main"), e.getPlayer().getLocation(), getCustoms(e), e.getPlayer().getName()).start();
					cooldown();
				}
			}
		}
	}
	
	private THashMap<String, String> getCustoms(PlayerChangedWorldEvent e){

		THashMap<String, String> map = new THashMap<String, String>();
		
		map.put("<worldto>", e.getPlayer().getWorld().getName());
		map.put("<worldfrom>", e.getFrom().getName());

		return map;
	}
	
	public void loadAll(){
		load();
	}
	
	public void saveAll(){
		save();
	}
}