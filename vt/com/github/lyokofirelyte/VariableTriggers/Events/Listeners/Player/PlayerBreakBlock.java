package com.github.lyokofirelyte.VariableTriggers.Events.Listeners.Player;

import gnu.trove.map.hash.THashMap;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.util.Vector;

import com.github.lyokofirelyte.VariableTriggers.VTParser;
import com.github.lyokofirelyte.VariableTriggers.VariableTriggers;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.AR;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.VTMap;

public class PlayerBreakBlock extends VTMap<Object, Object> implements AR {

	private VariableTriggers main;
	
	public PlayerBreakBlock(VariableTriggers i){
		main = i;
		makePath("./plugins/VariableTriggers/events/player", "PlayerBreakBlock.yml");
		load();
	}
	
	@EventHandler (ignoreCancelled = false)
	public void onBreak(BlockBreakEvent e){

		if (getList("Worlds").contains(e.getBlock().getWorld().getName())){
			if (getLong("ActiveCooldown") <= System.currentTimeMillis()){
				if (getBool("Cancelled")){
					e.setCancelled(true);
				}
				if (getList("main").size() > 0){
					new VTParser(main, "PlayerBreakBlock.yml", "main", getList("main"), e.getBlock().getLocation(), getCustoms(e), e.getPlayer().getName()).start();
					cooldown();
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	private THashMap<String, String> getCustoms(BlockBreakEvent e){
		
		Vector loc = e.getBlock().getLocation().toVector();
		THashMap<String, String> map = new THashMap<String, String>();

		map.put("<blockid>", e.getBlock().getType().getId() + "");
		map.put("<blockdata>", e.getBlock().getData() + "");
		map.put("<blocktype>", e.getBlock().getType().getId() + ":" + e.getBlock().getData());
		map.put("<blockmaterial>", e.getBlock().getType().name());
		map.put("<blocklocation>", e.getBlock().getLocation().getWorld().getName() + " " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
		map.put("<blockxp>", e.getExpToDrop() + "");
		
		return map;
	}
	
	public void loadAll(){
		load();
	}
	
	public void saveAll(){
		save();
	}
}