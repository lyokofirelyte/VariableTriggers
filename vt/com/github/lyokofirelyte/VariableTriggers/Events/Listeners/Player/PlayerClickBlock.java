package com.github.lyokofirelyte.VariableTriggers.Events.Listeners.Player;

import java.util.HashMap;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import com.github.lyokofirelyte.VariableTriggers.VTParser;
import com.github.lyokofirelyte.VariableTriggers.VariableTriggers;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.AR;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.VTMap;

public class PlayerClickBlock extends VTMap<Object, Object> implements AR {

	private VariableTriggers main;
	
	public PlayerClickBlock(VariableTriggers i){
		main = i;
		makePath("./plugins/VariableTriggers/events/player", "PlayerClickBlock.yml");
		load();
	}
	
	@EventHandler (ignoreCancelled = false)
	public void onClick(PlayerInteractEvent e){
		
		if (getList("Worlds").contains(e.getPlayer().getWorld().getName())){
			if (getLong("ActiveCooldown") <= System.currentTimeMillis()){
				if (getBool("Cancelled")){
					e.setCancelled(true);
				}
				if (getList("main").size() > 0 && (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK)){
					new VTParser(main, "PlayerClickBlock.yml", "main", getList("main"), e.getClickedBlock().getLocation(), getCustoms(e), e.getPlayer().getName()).start();
					cooldown();
				}
			}
		}
	}
	
	private HashMap<String, String> getCustoms(PlayerInteractEvent e){
		
		Vector loc = e.getClickedBlock().getLocation().toVector();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("<clicktype>", e.getAction().name());
		map.put("<blockid>", e.getClickedBlock().getType().getId() + "");
		map.put("<blockdata>", e.getClickedBlock().getData() + "");
		map.put("<blocktype>", e.getClickedBlock().getType().getId() + ":" + e.getClickedBlock().getData());
		map.put("<blockmaterial>", e.getClickedBlock().getType().name());
		map.put("<blocklocation>", e.getClickedBlock().getLocation().getWorld().getName() + " " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
		
		return map;
	}
	
	public void loadAll(){
		load();
	}
	
	public void saveAll(){
		save();
	}
}