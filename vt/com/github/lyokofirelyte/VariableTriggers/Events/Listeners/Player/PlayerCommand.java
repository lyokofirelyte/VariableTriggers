package com.github.lyokofirelyte.VariableTriggers.Events.Listeners.Player;

import gnu.trove.map.hash.THashMap;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.github.lyokofirelyte.VariableTriggers.VTParser;
import com.github.lyokofirelyte.VariableTriggers.VariableTriggers;
import com.github.lyokofirelyte.VariableTriggers.Events.CommandNotFoundEvent;
import com.github.lyokofirelyte.VariableTriggers.Events.Listeners.System.CommandNotFound;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.AR;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.VTMap;
import com.github.lyokofirelyte.VariableTriggers.Utils.VTUtils;

public class PlayerCommand extends VTMap<Object, Object> implements AR {

	private VariableTriggers main;
	
	public PlayerCommand(VariableTriggers i){
		main = i;
		makePath("./plugins/VariableTriggers/events/player", "PlayerCommand.yml");
		load();
	}
	
	@EventHandler (ignoreCancelled = false)
	public void onCmd(PlayerCommandPreprocessEvent e){
		
		String path = e.getMessage().replaceFirst("\\/", ""); 
		path = path.split(" ")[0];
		
		if (!containsKey(path + ".Script")){

			if (Bukkit.getHelpMap().getHelpTopic(path) == null && Bukkit.getHelpMap().getHelpTopic("/" + path) == null){
				if (((CommandNotFound) main.getInstance(CommandNotFound.class)).getList("Worlds").contains(e.getPlayer().getLocation().getWorld().getName())){
					main.event(new CommandNotFoundEvent(e.getPlayer(), path, e.getPlayer().getLocation()));
					e.setCancelled(true);
				}
			}
			
		} else {

			//if (getList("Worlds").contains(e.getPlayer().getWorld().getName())){
				if (getLong("ActiveCooldown") <= System.currentTimeMillis()){
					
					if (getBool("Cancelled")){
						e.setCancelled(true);
					}
					
					if (getLong(path + ".ActiveCooldown") <= System.currentTimeMillis()){
						
						if (getStr(path + ".Permission").equals("none") || e.getPlayer().hasPermission(getStr(path + ".Permission"))){
							
							if (getBool(path + ".Cancelled")){
								e.setCancelled(true);
							}
							
							new VTParser(main, "PlayerCommand.yml", path, getList(path + ".Script"), e.getPlayer().getLocation(), getCustoms(e), e.getPlayer().getName()).start();
							set(path + ".ActiveCooldown", (System.currentTimeMillis() + getLong(path + ".Cooldown")*1000L));
							cooldown();
							
						} else if (!getStr("NoPermissionMessage").equals("none")){
							VTUtils.s(e.getPlayer(), "&c&o" + getStr("NoPermissionMessage"));
						}
					}
				}
			//}
		}
	}

	private THashMap<String, String> getCustoms(PlayerCommandPreprocessEvent e){
		
		THashMap<String, String> map = new THashMap<String, String>();
		map.put("<cmdline>", e.getMessage().replace(e.getMessage().split(" ")[0], ""));
		map.put("<cmdargcount>", e.getMessage().split(" ").length + "");
		map.put("<cmdname>", e.getMessage().split(" ")[0].replace("\\/", ""));
		
		for (int i = 0; i < e.getMessage().split(" ").length; i++){
			map.put("<cmdarg:" + (i+1) + ">", e.getMessage().split(" ")[i]);
			map.put("<cmdarg" + (i+1) + ">", e.getMessage().split(" ")[i]);
		}
		
		return map;
	}
	
	public void loadAll(){
		load();
	}
	
	public void saveAll(){
		save();
	}
}