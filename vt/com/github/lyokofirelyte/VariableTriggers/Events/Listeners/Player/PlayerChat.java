package com.github.lyokofirelyte.VariableTriggers.Events.Listeners.Player;

import java.util.HashMap;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.github.lyokofirelyte.VariableTriggers.VTParser;
import com.github.lyokofirelyte.VariableTriggers.VariableTriggers;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.AR;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.VTMap;

public class PlayerChat extends VTMap<Object, Object> implements AR {

	private VariableTriggers main;
	
	public PlayerChat(VariableTriggers i){
		main = i;
		makePath("./plugins/VariableTriggers/events/player", "PlayerChat.yml");
		load();
	}
	
	@EventHandler (ignoreCancelled = false)
	public void onClick(AsyncPlayerChatEvent e){
		
		if (getList("Worlds").contains(e.getPlayer().getWorld().getName())){
			if (getLong("ActiveCooldown") <= System.currentTimeMillis()){
				if (getBool("Cancelled")){
					e.setCancelled(true);
				}
				if (getList("main").size() > 0){
					new VTParser(main, "PlayerChat.yml", "main", getList("main"), e.getPlayer().getLocation(), getCustoms(e), e.getPlayer().getName()).start();
					cooldown();
				}
			}
		}
	}
	
	private HashMap<String, String> getCustoms(AsyncPlayerChatEvent e){

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("<chatline>", e.getMessage());
		map.put("<chatwordcount>", e.getMessage().length() + "");
		
		return map;
	}
	
	public void loadAll(){
		load();
	}
	
	public void saveAll(){
		save();
	}
}