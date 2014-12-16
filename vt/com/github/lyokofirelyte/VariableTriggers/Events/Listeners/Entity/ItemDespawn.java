package com.github.lyokofirelyte.VariableTriggers.Events.Listeners.Entity;

import gnu.trove.map.hash.THashMap;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.inventory.ItemStack;

import com.github.lyokofirelyte.VariableTriggers.VTParser;
import com.github.lyokofirelyte.VariableTriggers.VariableTriggers;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.AR;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.VTMap;

public class ItemDespawn extends VTMap<Object, Object> implements AR {

	private VariableTriggers main;
	
	public ItemDespawn(VariableTriggers i){
		main = i;
		makePath("./plugins/VariableTriggers/events/entity", "ItemDespawn.yml");
		load();
	}
	
	@EventHandler (ignoreCancelled = false)
	public void onDespawn(ItemDespawnEvent e){
		
		if (getList("Worlds").contains(e.getEntity().getWorld().getName())){
			if (getLong("ActiveCooldown") <= System.currentTimeMillis()){
				if (getBool("Cancelled")){
					e.setCancelled(true);
				}
				if (getList("main").size() > 0){
					new VTParser(main, "ItemDespawn.yml", "main", getList("main"), e.getEntity().getLocation(), getCustoms(e), e.getEntityType().name()).start();
					cooldown();
				}
			}
		}
	}
	
	private THashMap<String, String> getCustoms(ItemDespawnEvent e){

		THashMap<String, String> map = new THashMap<String, String>();
		ItemStack i = e.getEntity().getItemStack();
		int x = 0;
		
		map.put("<despawneditem:name>", i.getType().name().toLowerCase());
		map.put("<despawneditem:displayname>", i.hasItemMeta() && i.getItemMeta().hasDisplayName() ? i.getItemMeta().getDisplayName() : "none");
		map.put("<despawneditem:amount>", i.getAmount() + "");
		
		if (i.hasItemMeta() && i.getItemMeta().hasLore()){
			map.put("<despawneditem:lore:amount>", i.getItemMeta().getLore().size() + "");
			for (String lore : i.getItemMeta().getLore()){
				map.put("<despawneditem:lore:" + x + ">", lore);
				x++;
			}
		} else {
			map.put("<despawneditem:lore:amount>", "0");
		}
		
		x = 0;
		
		if (i.hasItemMeta() && i.getItemMeta().hasEnchants()){
			map.put("<despawneditem:enchant:amount>", i.getItemMeta().getEnchants().size() + "");
			for (Enchantment ench : i.getItemMeta().getEnchants().keySet()){
				map.put("<despawneditem:enchant:" + x + ">", ench.getName().toLowerCase() + "_" + i.getItemMeta().getEnchants().get(ench));	
				x++;
			}
		} else {
			map.put("<despawneditem:enchant:amount>", "0");
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