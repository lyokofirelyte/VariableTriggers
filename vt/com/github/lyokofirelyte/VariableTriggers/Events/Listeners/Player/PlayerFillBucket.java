package com.github.lyokofirelyte.VariableTriggers.Events.Listeners.Player;

import gnu.trove.map.hash.THashMap;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBucketFillEvent;

import com.github.lyokofirelyte.VariableTriggers.VTParser;
import com.github.lyokofirelyte.VariableTriggers.VariableTriggers;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.AR;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.VTMap;

public class PlayerFillBucket extends VTMap<Object, Object> implements AR {

	private VariableTriggers main;
	
	public PlayerFillBucket(VariableTriggers i){
		main = i;
		makePath("./plugins/VariableTriggers/events/player", "PlayerFillBucket.yml");
		load();
	}
	
	@EventHandler (ignoreCancelled = false)
	public void onFill(PlayerBucketFillEvent e){
		
		if (getList("Worlds").contains(e.getPlayer().getWorld().getName())){
			if (getLong("ActiveCooldown") <= System.currentTimeMillis()){
				if (getList("main").size() > 0){
					if (getBool("Cancelled")){
						e.setCancelled(true);
					}
					new VTParser(main, "PlayerFillBucket.yml", "main", getList("main"), e.getBlockClicked().getLocation(), getCustoms(e), e.getPlayer().getName()).start();
					cooldown();
				}
			}
		}
	}
	
	private THashMap<String, String> getCustoms(PlayerBucketFillEvent e){

		THashMap<String, String> map = new THashMap<String, String>();
		Location l = e.getBlockClicked().getLocation();
		map.put("<waterlocation>", l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ());

		return map;
	}
	
	public void loadAll(){
		load();
	}
	
	public void saveAll(){
		save();
	}
}