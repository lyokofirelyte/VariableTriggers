package com.github.lyokofirelyte.VariableTriggers.Events.Listeners.Player;

import gnu.trove.map.hash.THashMap;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;

import com.github.lyokofirelyte.VariableTriggers.VTParser;
import com.github.lyokofirelyte.VariableTriggers.VariableTriggers;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.AR;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.VTMap;

public class PlayerEnchant extends VTMap<Object, Object> implements AR {

	private VariableTriggers main;
	
	public PlayerEnchant(VariableTriggers i){
		main = i;
		makePath("./plugins/VariableTriggers/events/player", "PlayerEnchant.yml");
		load();
	}
	
	@EventHandler (ignoreCancelled = false)
	public void onDeath(EnchantItemEvent e){
		
		if (getList("Worlds").contains(e.getEnchanter().getWorld().getName())){
			if (getLong("ActiveCooldown") <= System.currentTimeMillis()){
				if (getList("main").size() > 0){
					if (getBool("Cancelled")){
						e.setCancelled(true);
					}
					new VTParser(main, "PlayerEnchant.yml", "main", getList("main"), e.getEnchantBlock().getLocation(), getCustoms(e), e.getEnchanter().getName()).start();
					cooldown();
				}
			}
		}
	}
	
	private THashMap<String, String> getCustoms(EnchantItemEvent e){
		
		THashMap<String, String> map = new THashMap<String, String>();
		ItemStack i = e.getItem();
		int x = 0;

		map.put("<levelcost>", (e.getExpLevelCost() + ""));
		map.put("<enchanteditem:name>", i.getType().name().toLowerCase());
		map.put("<enchanteditem:displayname>", i.hasItemMeta() && i.getItemMeta().hasDisplayName() ? i.getItemMeta().getDisplayName() : "none");
		map.put("<enchanteditem:amount>", i.getAmount() + "");
		
		if (i.hasItemMeta() && i.getItemMeta().hasLore()){
			map.put("<enchanteditem:lore:amount>", i.getItemMeta().getLore().size() + "");
			for (String lore : i.getItemMeta().getLore()){
				map.put("<enchanteditem:lore:" + x + ">", lore);
				x++;
			}
		} else {
			map.put("<enchanteditem:lore:amount>", "0");
		}
		
		x = 0;
		
		if (i.hasItemMeta() && i.getItemMeta().hasEnchants()){
			map.put("<enchanteditem:enchant:amount>", i.getItemMeta().getEnchants().size() + "");
			for (Enchantment ench : i.getItemMeta().getEnchants().keySet()){
				map.put("<enchanteditem:enchant:" + x + ">", ench.getName().toLowerCase() + "_" + i.getItemMeta().getEnchants().get(ench));	
				x++;
			}
		} else {
			map.put("<enchanteditem:enchant:amount>", "0");
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