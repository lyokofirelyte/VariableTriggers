package com.github.lyokofirelyte.VariableTriggers.Events.Listeners.System;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerListPingEvent;

import com.github.lyokofirelyte.VariableTriggers.VTParser;
import com.github.lyokofirelyte.VariableTriggers.VariableTriggers;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.AR;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.VTConfig;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.VTMap;

public class ServerListPing extends VTMap<Object, Object> implements AR {

	private VariableTriggers main;
	
	public ServerListPing(VariableTriggers i){
		main = i;
		makePath("./plugins/VariableTriggers/events/system", "ServerListPing.yml");
		load();
	}
	
	@EventHandler (ignoreCancelled = false)
	public void onPing(ServerListPingEvent e){
		
		if (!main.settings.getStr(VTConfig.MOTD).equals("none")){
			e.setMotd(main.settings.getStr(VTConfig.MOTD));
		}
		
		if (getLong("ActiveCooldown") <= System.currentTimeMillis()){
			if (getList("main").size() > 0){
				new VTParser(main, "ServerListPing.yml", "main", getList("main"), new Location(Bukkit.getWorlds().get(0), 0, 0, 0), getCustoms(e), "VTSystem").start();
				cooldown();
			}
		}
	}
	
	private HashMap<String, String> getCustoms(ServerListPingEvent e){
		
		HashMap<String, String> map = new HashMap<String, String>();
		
		map.put("<motd>", e.getMotd());
		map.put("<hostip>", e.getAddress().getHostAddress());
		map.put("<maxplayers>", e.getMaxPlayers() + "");
		map.put("<onlineplayers>", e.getNumPlayers() + "");
		
		return map;
	}
	
	public void loadAll(){
		load();
	}
	
	public void saveAll(){
		save();
	}
}