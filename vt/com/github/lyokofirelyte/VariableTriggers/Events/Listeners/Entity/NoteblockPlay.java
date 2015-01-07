package com.github.lyokofirelyte.VariableTriggers.Events.Listeners.Entity;

import java.util.HashMap;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.NotePlayEvent;

import com.github.lyokofirelyte.VariableTriggers.VTParser;
import com.github.lyokofirelyte.VariableTriggers.VariableTriggers;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.AR;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.VTMap;

public class NoteblockPlay extends VTMap<Object, Object> implements AR {

	private VariableTriggers main;
	
	public NoteblockPlay(VariableTriggers i){
		main = i;
		makePath("./plugins/VariableTriggers/events/entity", "NoteblockPlay.yml");
		load();
	}
	
	@EventHandler (ignoreCancelled = false)
	public void onNote(NotePlayEvent e){
		
		if (getList("Worlds").contains(e.getBlock().getWorld().getName())){
			if (getLong("ActiveCooldown") <= System.currentTimeMillis()){
				if (getBool("Cancelled")){
					e.setCancelled(true);
				}
				if (getList("main").size() > 0){
					new VTParser(main, "NoteblockPlay.yml", "main", getList("main"), e.getBlock().getLocation(), getCustoms(e), "noteblock").start();
					cooldown();
				}
			}
		}
	}
	
	private HashMap<String, String> getCustoms(NotePlayEvent e){

		HashMap<String, String> map = new HashMap<String, String>();

		map.put("<note:octave>", e.getNote().getOctave() + "");
		map.put("<note:tone>", e.getNote().getTone().getId() + "");
		map.put("<note:id>", e.getNote().getId() + "");

		return map;
	}
	
	public void loadAll(){
		load();
	}
	
	public void saveAll(){
		save();
	}
}