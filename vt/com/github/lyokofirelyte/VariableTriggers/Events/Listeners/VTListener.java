package com.github.lyokofirelyte.VariableTriggers.Events.Listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.lyokofirelyte.VariableTriggers.VTParser;
import com.github.lyokofirelyte.VariableTriggers.VariableTriggers;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.AR;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.VTMap;
import com.github.lyokofirelyte.VariableTriggers.Utils.VTUtils;

public class VTListener implements AR {
	
	private VariableTriggers main;
	
	public VTListener(VariableTriggers i){
		main = i;
	}

	@EventHandler(ignoreCancelled = false)
	public void onInteract(PlayerInteractEvent e){
		
		if (e.getAction() == Action.PHYSICAL || e.getClickedBlock() == null){
			return;
		}
		
		VTMap<Object, Object> obj = null;
		
		switch (main.vars.getStr(e.getPlayer().getName() + "_clickConfig")){
			case "walk":
				obj = main.walks;
			break;
			
			case "click":
				obj = main.clicks;
			break;
		}
		
		if (main.vars.getBool(e.getPlayer().getName() + "_areaMode")){
			
			if (e.getPlayer().getGameMode().equals(GameMode.SURVIVAL) && e.getPlayer().getItemInHand() != null && e.getPlayer().getItemInHand().getType().equals(Material.BONE)){
				
				Location l = e.getClickedBlock().getLocation();
				String loc = l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ();
				
				if (e.getAction() == Action.LEFT_CLICK_BLOCK){
					main.vars.set(e.getPlayer().getName() + "_editFirstPosition", loc);
					VTUtils.s(e.getPlayer(), "First position set! Right-click for the second one.");
				} else if (e.getAction() == Action.RIGHT_CLICK_BLOCK){
					main.vars.set(e.getPlayer().getName() + "_editSecondPosition", loc);
					VTUtils.s(e.getPlayer(), "Second position set!");
				}
				
			} else {
				VTUtils.s(e.getPlayer(), "You must be in survival mode & holding a bone.");
			}
			
		} else if (main.vars.getBool(e.getPlayer().getName() + "_clickMode") && e.getPlayer().getItemInHand() != null && e.getPlayer().getItemInHand().getType().equals(Material.BONE)){
			
			Location l = e.getClickedBlock().getLocation();
			String last = main.vars.getStr(e.getPlayer().getName() + "_clickScript");
			String loc = l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ();
			
			if (!obj.containsKey(l.getWorld().getName() + "." + loc + ".Script")){
				obj.set(l.getWorld().getName() + "." + loc + ".Script", new ArrayList<String>(Arrays.asList(last)));
				System.out.println("SCRIPT: " + last);
				obj.set(l.getWorld().getName() + "." + loc + ".Cooldown", 1);
			} else {
				obj.getList(l.getWorld().getName() + "." + loc + ".Script").add(last);
			}
			
			main.vars.set(e.getPlayer().getName() + "_clickMode", false);
			VTUtils.s(e.getPlayer(), "Trigger added!");
		
		} else if (main.vars.getBool(e.getPlayer().getName() + "_clickViewMode") && e.getPlayer().getItemInHand() != null && e.getPlayer().getItemInHand().getType().equals(Material.BONE)){

			int index = main.vars.getInt(e.getPlayer().getName() + "_clickIndex");
			Player sender = e.getPlayer();
			Location l = e.getClickedBlock().getLocation();
			String check = e.getPlayer().getWorld().getName() + "." + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ();
			
			if (obj.containsKey(check + ".Script")){
			
				List<String> script = obj.getList(check + ".Script");
				VTUtils.s(sender, "&6" + check + " &7[lines: " + script.size() + "]");
				sender.sendMessage("");
				
				for (int i = (index*5)-5; i < index*5 && i < script.size(); i++){
					sender.sendMessage(VTUtils.AS("&f> &6" + i + " &f<"));
					sender.sendMessage(VTUtils.AS("&3" + script.get(i)));
					sender.sendMessage("");
				}
				
			} else {
				VTUtils.s(sender, "&c&oThere's no trigger there.");
			}
			
			main.vars.set(e.getPlayer().getName() + "_clickViewMode", false);
			
		} else if (main.vars.getBool(e.getPlayer().getName() + "_clickRemoveMode") && e.getPlayer().getItemInHand() != null && e.getPlayer().getItemInHand().getType().equals(Material.BONE)){

			Location l = e.getClickedBlock().getLocation();
			String check = e.getPlayer().getWorld().getName() + "." + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ();
			
			if (obj.containsKey(check + ".Script")){
				obj.remove(check + ".Script");
				VTUtils.s(e.getPlayer(), "Script removed.");
			} else {
				VTUtils.s(e.getPlayer(), "&c&oThere's no trigger there.");
			}
			
			main.vars.set(e.getPlayer().getName() + "_clickRemoveMode", false);
			
		} else if (main.vars.getBool(e.getPlayer().getName() + "_clickEditMode") && e.getPlayer().getItemInHand() != null && e.getPlayer().getItemInHand().getType().equals(Material.BONE)){
		
			List<String> newScript = new ArrayList<String>();
			int x = 0;
			int repl = main.vars.getInt(e.getPlayer().getName() + "_clickEditMode_Index");
			
			Location l = e.getClickedBlock().getLocation();
			String check = e.getPlayer().getWorld().getName() + "." + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ();
			
			if (obj.containsKey(check + ".Script")){
				
				List<String> script = obj.getList(check + ".Script");
				
				if (script.size() > repl){
					
					for (String line : script){
						if (x == repl){
							newScript.add(main.vars.getStr(e.getPlayer().getName() + "_clickScript"));
						} else {
							newScript.add(line);
						}
					}
					
					obj.set(check + ".Script", newScript);
					VTUtils.s(e.getPlayer(), "Updated the script!");
					
				} else {
					VTUtils.s(e.getPlayer(), "&c&oThe script isn't that big.");
				}
				
			} else {
				VTUtils.s(e.getPlayer(), "&c&oThere's no trigger there.");
			}
			
			main.vars.set(e.getPlayer().getName() + "_clickEditMode", false);

		} else {

			Location l = e.getClickedBlock().getLocation();
			String check = e.getPlayer().getWorld().getName() + "." + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ();
			
			if (main.clicks.containsKey(check + ".Script") && main.clicks.getLong(check + ".ActiveCooldown") <= System.currentTimeMillis()){
				main.clicks.cooldown(check);
				new VTParser(main, "ClickTriggers.yml", check, main.clicks.getList(check + ".Script"), l, new HashMap<String, String>(), e.getPlayer().getName()).start();
			}
		}
		
		if (obj != null){
			try {
				obj.save();
			} catch (Exception ee){
				ee.printStackTrace();
			}
		}
	}
	
	@EventHandler(ignoreCancelled = false)
	public void onMove(PlayerMoveEvent e){

		Map<String, List<String>> results = VTUtils.getArea(e.getTo());
		String name = ((String) results.keySet().toArray()[0]).replace("_Enter", "").replace("_Exit", "");
		String p = e.getPlayer().getName();

		if (!name.equals("none")){
			
			if (!main.vars.getBool(p + "_isInArea_" + name)){
				main.vars.set(p + "_isInArea_" + name, true);
				new VTParser(main, "AreaTriggers.yml", name, results.get(name + "_Enter"), e.getTo(), getAreaCustoms(name, "ENTER"), p).start();
			}
			
			main.vars.set(p + "_areaLast", name);
			
		} else if (!main.vars.getStr(p + "_areaLast").equals("none")){
			results = VTUtils.getArea(e.getFrom());
			name = ((String) results.keySet().toArray()[0]).replace("_Enter", "").replace("_Exit", "");
			new VTParser(main, "AreaTriggers.yml", main.vars.getStr(p + "_areaLast"), results.get(name + "_Exit"), e.getTo(), getAreaCustoms(name, "EXIT"), p).start();
			main.vars.set(p + "_areaLast", "none");
			main.vars.set(p + "_isInArea_" + name, false);
		}
		
		Location l = e.getPlayer().getLocation();
		int x = l.getBlockX();
		int y = l.getBlockY()-1;
		int z = l.getBlockZ();
		
		for (Object thing : main.walks.keySet()){
			String key = main.walks.toString(thing);
			if (key.startsWith(e.getPlayer().getWorld().getName() + ".")){
				String[] keySplit = key.split("\\.");
				String[] coords = keySplit[1].split(",");
				int cX = Integer.parseInt(coords[0]);
				int cY = Integer.parseInt(coords[1]);
				int cZ = Integer.parseInt(coords[2]);
				if (x == cX && y == cY && z == cZ){
					if (main.walks.getLong(keySplit[0] + "." + keySplit[1] + ".ActiveCooldown") <= System.currentTimeMillis()){
						main.walks.cooldown(keySplit[0] + "." + keySplit[1]);
						new VTParser(main, "WalkTriggers.yml", keySplit[1], main.walks.getList(keySplit[0] + "." + keySplit[1] + ".Script"), l, new HashMap<String, String>(), e.getPlayer().getName()).start();
					}
					break;
				}
			}
		}
	}
	
	public HashMap<String, String> getAreaCustoms(String name, String type){
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("<areaentered>", name);
		map.put("<areaexited>", name);
		map.put("<movetype>", type);
		
		return map;
	}
}