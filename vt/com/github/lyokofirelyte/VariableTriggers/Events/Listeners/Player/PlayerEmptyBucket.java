package com.github.lyokofirelyte.VariableTriggers.Events.Listeners.Player;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

import com.github.lyokofirelyte.VariableTriggers.VTParser;
import com.github.lyokofirelyte.VariableTriggers.VariableTriggers;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.AR;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.VTMap;

public class PlayerEmptyBucket extends VTMap<Object, Object> implements AR {

	private VariableTriggers main;
	
	public PlayerEmptyBucket(VariableTriggers i){
		main = i;
		makePath("./plugins/VariableTriggers/events/player", "PlayerEmptyBucket.yml");
		load();
	}
	
	@EventHandler (ignoreCancelled = false)
	public void onFill(PlayerBucketEmptyEvent e){
		
		if (getList("Worlds").contains(e.getPlayer().getWorld().getName())){
			if (getLong("ActiveCooldown") <= System.currentTimeMillis()){
				if (getList("main").size() > 0){
					if (getBool("Cancelled")){
						e.setCancelled(true);
					}
					new VTParser(main, "PlayerEmptyBucket.yml", "main", getList("main"), e.getBlockClicked().getLocation(), getCustoms(e), e.getPlayer().getName()).start();
					cooldown();
				}
			}
		}
	}
	
	private HashMap<String, String> getCustoms(PlayerBucketEmptyEvent e){

		HashMap<String, String> map = new HashMap<String, String>();
		Location l = e.getBlockClicked().getLocation();
		map.put("<emptylocation>", l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ());

		return map;
	}
	
	public void loadAll(){
		load();
	}
	
	public void saveAll(){
		save();
	}
}