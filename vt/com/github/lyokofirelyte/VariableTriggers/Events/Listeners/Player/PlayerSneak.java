package com.github.lyokofirelyte.VariableTriggers.Events.Listeners.Player;

import gnu.trove.map.hash.THashMap;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.github.lyokofirelyte.VariableTriggers.VTParser;
import com.github.lyokofirelyte.VariableTriggers.VariableTriggers;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.AR;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.VTMap;

public class PlayerSneak extends VTMap<Object, Object> implements AR {

	private VariableTriggers main;
	
	public PlayerSneak(VariableTriggers i){
		main = i;
		makePath("./plugins/VariableTriggers/events/player", "PlayerSneak.yml");
		load();
	}
	
	@EventHandler (ignoreCancelled = false)
	public void onSneak(PlayerToggleSneakEvent e){
		
		if (getList("Worlds").contains(e.getPlayer().getWorld().getName())){
			if (getLong("ActiveCooldown") <= System.currentTimeMillis()){
				if (getList("main").size() > 0){
					if (getBool("Cancelled")){
						e.setCancelled(true);
					}
					new VTParser(main, "PlayerSneak.yml", "main", getList("main"), e.getPlayer().getLocation(), getCustoms(e), e.getPlayer().getName()).start();
					cooldown();
				}
			}
		}
	}
	
	private THashMap<String, String> getCustoms(PlayerToggleSneakEvent e){

		THashMap<String, String> map = new THashMap<String, String>();
		
		map.put("<sneaking>", e.isSneaking() + "");

		return map;
	}
	
	public void loadAll(){
		load();
	}
	
	public void saveAll(){
		save();
	}
}