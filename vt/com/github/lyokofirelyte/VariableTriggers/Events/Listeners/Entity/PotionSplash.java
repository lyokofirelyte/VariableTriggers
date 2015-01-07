package com.github.lyokofirelyte.VariableTriggers.Events.Listeners.Entity;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.github.lyokofirelyte.VariableTriggers.VTParser;
import com.github.lyokofirelyte.VariableTriggers.VariableTriggers;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.AR;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.VTMap;

public class PotionSplash extends VTMap<Object, Object> implements AR {

	private VariableTriggers main;
	
	public PotionSplash(VariableTriggers i){
		main = i;
		makePath("./plugins/VariableTriggers/events/entity", "PotionSplash.yml");
		load();
	}
	
	@EventHandler (ignoreCancelled = false)
	public void onNote(PotionSplashEvent e){
		
		if (getList("Worlds").contains(e.getEntity().getWorld().getName())){
			if (getLong("ActiveCooldown") <= System.currentTimeMillis()){
				if (getBool("Cancelled")){
					e.setCancelled(true);
				}
				if (getList("main").size() > 0){
					new VTParser(main, "PotionSplash.yml", "main", getList("main"), e.getEntity().getLocation(), getCustoms(e), e.getEntityType().name()).start();
					cooldown();
				}
			}
		}
	}
	
	private HashMap<String, String> getCustoms(PotionSplashEvent e){

		HashMap<String, String> map = new HashMap<String, String>();
		ItemStack i = e.getPotion().getItem();
		int x = 0;
		
		map.put("<thrower>", e.getPotion().getShooter().getType().name().toLowerCase());
		map.put("<affectedentity:amount>", e.getAffectedEntities().size() + "");
		map.put("<potion:effect:amount>", e.getPotion().getEffects().size() + "");
		
		if (e.getPotion().getShooter() instanceof Player){
			map.put("<throwername>", ((Player)e.getPotion().getShooter()).getName());
		}
		
		for (LivingEntity eff : e.getAffectedEntities()){
			Location l = eff.getLocation();
			map.put("<affectedentity:" + x + ":type>", eff.getType().name().toLowerCase());
			map.put("<affectedentity:" + x + ":name>", eff instanceof Player ? ((Player) eff).getName() : "none");
			map.put("<affectedentity:" + x + ":location>", l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ());
			x++;
		}
		
		x = 0;
		
		for (PotionEffect peff : e.getPotion().getEffects()){
			map.put("<potion:effect:" + x + ":type>", peff.getType().getName());
			map.put("<potion:effect:" + x + ":amplifier>", peff.getAmplifier() + "");
			map.put("<potion:effect:" + x + ":duration>", peff.getDuration() + "");
			x++;
		}
		
		map.put("<potion:name>", i.getType().name().toLowerCase());
		map.put("<potion:displayname>", i.hasItemMeta() && i.getItemMeta().hasDisplayName() ? i.getItemMeta().getDisplayName() : "none");
		map.put("<potion:amount>", i.getAmount() + "");
		
		x = 0;
		
		if (i.hasItemMeta() && i.getItemMeta().hasLore()){
			map.put("<potion:lore:amount>", i.getItemMeta().getLore().size() + "");
			for (String lore : i.getItemMeta().getLore()){
				map.put("<potion:lore:" + x + ">", lore);
				x++;
			}
		} else {
			map.put("<potion:lore:amount>", "0");
		}
		
		x = 0;
		
		if (i.hasItemMeta() && i.getItemMeta().hasEnchants()){
			map.put("<potion:enchant:amount>", i.getItemMeta().getEnchants().size() + "");
			for (Enchantment ench : i.getItemMeta().getEnchants().keySet()){
				map.put("<potion:enchant:" + x + ">", ench.getName().toLowerCase() + "_" + i.getItemMeta().getEnchants().get(ench));	
				x++;
			}
		} else {
			map.put("<potion:enchant:amount>", "0");
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