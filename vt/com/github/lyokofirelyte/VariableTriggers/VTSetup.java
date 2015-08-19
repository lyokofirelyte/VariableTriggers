package com.github.lyokofirelyte.VariableTriggers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.github.lyokofirelyte.VariableTriggers.Identifiers.AR;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.VTConfig;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.VTData;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.VTMap;
import com.github.lyokofirelyte.VariableTriggers.Manager.VTRegistry;
import com.github.lyokofirelyte.VariableTriggers.Manager.VaultHook;
import com.github.lyokofirelyte.VariableTriggers.Manager.WorldEditHook;
import com.github.lyokofirelyte.VariableTriggers.Utils.FWTools;
import com.github.lyokofirelyte.VariableTriggers.Utils.VTUtils;
import com.google.common.io.Files;

public class VTSetup {

	private VariableTriggers main;
	private VTRegistry reg;
	
	public Map<String, Object> registeredClasses = new HashMap<String, Object>();
    public Map<List<String>, Object> commandMap = new HashMap<>();
    public List<String> syncTasks = new ArrayList<String>();
    public String base = "./plugins/VariableTriggers/";
	
	public VTSetup(VariableTriggers i){
		main = i;
		reg = new VTRegistry(main);
	}
	
	public VTSetup start(){
		
		main.settings = new VTSettings(main);
		main.vars = new VTVars(main);
		
		main.clicks = new VTMap<Object, Object>();
		main.clicks.makePath("./plugins/VariableTriggers/events/triggers", "ClickTriggers.yml");
		main.clicks.load();
		
		main.areas = new VTMap<Object, Object>();
		main.areas.makePath("./plugins/VariableTriggers/events/triggers", "AreaTriggers.yml");
		main.areas.load();
		
		main.walks = new VTMap<Object, Object>();
		main.walks.makePath("./plugins/VariableTriggers/events/triggers", "WalkTriggers.yml");
		main.walks.load();
		
		if (!main.settings.getBool(VTConfig.FIRST_RUN)){
			firstRun();
		} else if (!main.settings.getBool(VTConfig.FIRST_RUN)){
			new File(base + "scripts/").mkdirs();
			new File(base + "inventories/").mkdirs();
			main.settings.set(VTConfig.FIRST_RUN, true);
		}
		
		main.settings.set(VTData.PLACEHOLDERS, YamlConfiguration.loadConfiguration(main.getResource("placeholders.yml")).getStringList("placeholders"));
		
        List<Class<?>> allClasses = new ArrayList<Class<?>>();
        
        try {
        
	        List<String> classNames = new ArrayList<String>();
	        ZipInputStream zip = new ZipInputStream(new FileInputStream("./plugins/VariableTriggers.jar"));//TODO change this for users
	        boolean look = false;
	        
	        for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()){
	        	
	        	if (entry.isDirectory()){
	        		look = entry.getName().contains("VariableTriggers");
	        	}
	        	
	            if (entry.getName().endsWith(".class") && !entry.isDirectory() && look) {
	            	
	                StringBuilder className = new StringBuilder();
	                
	                for (String part : entry.getName().split("/")) {
	                	
	                    if (className.length() != 0){
	                        className.append(".");
	                    }
	                    
	                    className.append(part);
	                    
	                    if (part.endsWith(".class")){
	                        className.setLength(className.length()-".class".length());
	                    }
	                }
	                
	                classNames.add(className.toString());
	            }
	        }
	        
	        for (String clazz : classNames){
	        	allClasses.add(Class.forName(clazz));
	        }
	        
        } catch (Exception e){
        	e.printStackTrace();
        	main.logger.warning("You can't rename the VariableTriggers.jar, sorry!");
        	main.logger.warning("== VARIABLE TRIGGERS MAY NOT HAVE STARTED PROPERLY ==");
        }
        
		for (Class<?> clazz : allClasses){
			
			Object obj = null;

			try {
				Constructor<?> con = clazz.getConstructors()[0];
				con.setAccessible(true);
				obj = con.newInstance(main);
			} catch (Exception e1){
				continue;
			}
			
			if (obj instanceof AR && !clazz.toString().contains("\\$") && !registeredClasses.containsKey(clazz.toString())){
				registeredClasses.put(clazz.toString(), obj);
			}
		}
		
		for (Object obj : registeredClasses.values()){
			
			if (obj instanceof Listener){
				Bukkit.getPluginManager().registerEvents((Listener) obj, main);
			}
			
			reg.registerCommands(obj);
		}
		
		scripts();
		helpFiles();
		
		main.vault = (VaultHook) registeredClasses.get(VaultHook.class.toString());
		main.we = (WorldEditHook) registeredClasses.get(WorldEditHook.class.toString());
		main.fw = new FWTools().init();
		
		main.vault.hookSetup();
		main.we.hookSetup();

		return this;
	}
	
	public void helpFiles(){
		
		File file = new File(base + "system/enums.yml");
		
		if (!file.exists()){
			
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			YamlConfiguration yaml = new YamlConfiguration();
			List<String> mats = new ArrayList<String>();
			
			for (Material m : Material.values()){
				mats.add(m.name());
			}
			
			yaml.set("MaterialEnums", new ArrayList<String>(mats));
			mats = new ArrayList<String>();
			
			for (Enchantment e : Enchantment.values()){
				mats.add(e.getName());
			}
			
			yaml.set("EnchantEnums", new ArrayList<String>(mats));
			mats = new ArrayList<String>();
			
			for (EntityType e : EntityType.values()){
				mats.add(e.name());
			}
			
			yaml.set("EntityEnums", new ArrayList<String>(mats));
			
			mats = new ArrayList<String>();
			
			for (Action a : Action.values()){
				mats.add(a.name());
			}
			
			yaml.set("Actions", mats);
			
			try {
				yaml.save(file);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public void scripts(){
		
		Map<String, List<String>> scriptMap = new HashMap<String, List<String>>();
		
		for (String file : new File(base + "scripts/").list()){
			if (file.endsWith(".script.yml")){
				YamlConfiguration yaml = YamlConfiguration.loadConfiguration(new File(base + "scripts/" + file));
				if (yaml.contains("Scripts")){
					for (String key : yaml.getConfigurationSection("Scripts").getKeys(false)){
						if (yaml.contains("Scripts." + key + ".Script")){
							scriptMap.put(file.replace(".script.yml", "") + "_" + key, yaml.getStringList("Scripts." + key + ".Script"));
						} else {
							main.logger.warning("Invalid script key (" + file + ":" + key + ")!");
						}
					}
				} else {
					main.logger.warning("Invalid script file (" + file + ")! Moved to /scripts/invalid/");
					new File(base + "scripts/invalid/").mkdirs();
					try {
						Files.move(new File(base + "scripts/" + file), new File(base + "scripts/invalid/" + file));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		main.vars.set(VTData.SCRIPTS, scriptMap);
	}

	private void firstRun(){
		
		main.logger.calmInfo("An older version of VT was detected on file.");
		main.logger.calmInfo("Let's convert your files into VTV2 format.");
		
		new File(base + "deprecated/").mkdirs();
		new File(base + "scripts/").mkdirs();
		new File(base + "inventories/").mkdirs();
		
		for (String file : new File(base).list()){
			
			if (new File(base + file).isDirectory()){
				switch (file){
					case "inventories": case "events": case "scripts": case "system": case "deprecated": continue;
				}
			}
			
			try {
				if (file.endsWith(".script.yml")){
					Files.move(new File(base + file), new File(base + "scripts/" + file));
				} else if (!file.endsWith("~")){
					Files.move(new File(base + file), new File(base + "deprecated/" + file));
				} else {
					new File(base + file).delete();
				}
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		
		/*====================
				INVENTORY
		======================*/
		
		File oldInvTrigger = new File(base + "deprecated/InventoryTriggers.yml");
		
		if (oldInvTrigger.exists()){
			
			YamlConfiguration triggerYaml = YamlConfiguration.loadConfiguration(oldInvTrigger);
			
			for (String key : triggerYaml.getKeys(false)){
				
				File newInv = new File(base + "inventories/" + key + ".yml");
				YamlConfiguration newYaml = new YamlConfiguration();
				int x = 0;
				
				try {
					newInv.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				Inventory inv = VTUtils.parseOldInventory(triggerYaml.getStringList(key));
				newYaml.set("title", inv.getTitle());
				newYaml.set("slots", inv.getSize());
				
				for (ItemStack i : inv.getContents()){
					if (i != null){
						List<String> enchants = new ArrayList<String>();
						if (i.hasItemMeta() && i.getItemMeta().hasEnchants()){
							for (Enchantment e : i.getItemMeta().getEnchants().keySet()){
								enchants.add(e.getName() + " " + i.getItemMeta().getEnchants().get(e));
							}
						}
						for (int ix = 0; ix < inv.getContents().length; ix++){
							if (inv.getItem(ix) != null && inv.getItem(ix).equals(i)){
								x = ix;
								break;
							}
						}
						newYaml.set("items." + x + ".type", i.getType().toString().toLowerCase());
						newYaml.set("items." + x + ".meta", i.getData().getData());
						newYaml.set("items." + x + ".amount", i.getAmount());
						newYaml.set("items." + x + ".name", i.hasItemMeta() && i.getItemMeta().hasDisplayName() ? ChatColor.stripColor(i.getItemMeta().getDisplayName()) : "null");
						newYaml.set("items." + x + ".lore", i.hasItemMeta() && i.getItemMeta().hasLore() ? VTUtils.stripColor(i.getItemMeta().getLore()) : new ArrayList<String>());
						newYaml.set("items." + x + ".enchants", enchants);
					}
				}
				
				try {
					newYaml.save(newInv);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		/*====================
				VAR DATA
		======================*/
		File varDataFile = new File(base + "deprecated/vardata.yml");
		File newDataFile = new File(base + "system/vars.yml");
		
		if (varDataFile.exists()){
			try {
				YamlConfiguration config = YamlConfiguration.loadConfiguration(varDataFile);
				YamlConfiguration newConfig = new YamlConfiguration();
				
				for (String key : config.getConfigurationSection("objects").getKeys(false)){
					String[] types = new String[]{ "Strings", "Integers", "StringLists", "Booleans" };
					for (String type : types){
						if (config.contains("objects." + key + "." + type)){
							for (String innerKey : config.getConfigurationSection("objects." + key + "." + type).getKeys(false)){
								newConfig.set(key + "." + innerKey, config.get("objects." + key + "." + type + "." + innerKey));
							}
						}
					}
				}
				
				try {
					new File(base + "system/").mkdirs();
					new File(base + "events/player/").mkdirs();
					new File(base + "events/system/").mkdirs();
					new File(base + "events/entity/").mkdirs();
					new File(base + "events/triggers/").mkdirs();
					newDataFile.createNewFile();
					newConfig.save(newDataFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (Exception e){}
		}
		
		/*====================
				COMMANDS
		======================*/
		File commandFile = new File(base + "deprecated/CommandTriggers.yml");
		
		if (commandFile.exists()){
			File newCommandFile = new File(base + "events/player/PlayerCommand.yml");
			YamlConfiguration newConfig = new YamlConfiguration();
			YamlConfiguration config = YamlConfiguration.loadConfiguration(commandFile);
			
			try {
				
				newCommandFile.createNewFile();
				
				for (String sec : config.getConfigurationSection("Commands").getKeys(false)){
					if (config.contains("Commands." + sec + ".Override")){
						newConfig.set(sec + ".Cancelled", config.getBoolean("Commands." + sec + ".Override"));
					}
					if (config.contains("Commands." + sec + ".Script")){
						newConfig.set(sec + ".Script", config.getStringList("Commands." + sec + ".Script"));
					}
					if (config.contains("Commands." + sec + ".Permission")){
						newConfig.set(sec + ".Permission", config.getStringList("Commands." + sec + ".Permission"));
					}
				}
				
				newConfig.save(newCommandFile);
				
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		
		/*====================
				EVENTS
		======================*/
		File eventFile = new File(base + "deprecated/EventTriggers.yml");
		
		if (eventFile.exists()){
			YamlConfiguration config = YamlConfiguration.loadConfiguration(eventFile);
			
			Map<String, YamlConfiguration> eventsMap = new HashMap<String, YamlConfiguration>();
			
			String[][] events = new String[][]{
				new String[]{ "PlayerDeath", base + "events/player/PlayerDeath.yml" },
				new String[]{ "EntityDeath", base + "events/entity/EntityDeath.yml" },
				new String[]{ "Respawn", base + "events/player/PlayerRespawn.yml" },
				new String[]{ "Join", base + "events/player/PlayerJoin.yml" },
				new String[]{ "Quit", base + "events/player/PlayerQuit.yml" },
				new String[]{ "Interact", base + "events/player/PlayerInteract.yml" },
				new String[]{ "EntitySpawn", base + "events/entity/EntitySpawn.yml" },
				new String[]{ "Chat", base + "events/player/PlayerChat.yml" },
				new String[]{ "Timer", base + "events/system/SystemTimer.yml" },
				new String[]{ "InventoryClick", base + "events/player/PlayerClickInventory.yml" },
				new String[]{ "BlockBreak", base + "events/player/PlayerBreakBlock.yml" },
				new String[]{ "BlockPlaced", base + "events/player/PlayerPlaceBlock.yml" },
				new String[]{ "WorldChange", base + "events/player/PlayerWorldChange.yml" },
				new String[]{ "Sneak", base + "events/player/PlayerSneak.yml" },
				new String[]{ "Flight", base + "events/player/PlayerFlight.yml" },
				new String[]{ "Sprint", base + "events/player/PlayerSprint.yml" },
				new String[]{ "Enable", base + "events/system/SystemEnable.yml" },
				new String[]{ "Disable", base + "events/system/SystemDisable.yml" },
			};
			
			for (String[] str : events){
				
				File file = new File(str[1]);
				
				try {
					file.createNewFile();
				} catch (Exception e){
					e.printStackTrace();
				}
				
				eventsMap.put(str[0], new YamlConfiguration());
				eventsMap.get(str[0]).set("main", new ArrayList<String>());
				eventsMap.get(str[0]).set("Worlds", new ArrayList<String>());
			}
			
			for (String world : config.getKeys(false)){
				for (String event : config.getConfigurationSection(world).getKeys(false)){
					if (eventsMap.containsKey(event)){
						List<String> script = eventsMap.get(event).getStringList("main");
						script.add("@IF s <worldname> = " + world);
						for (String line : config.getStringList(world + "." + event + ".Script")){
							script.add(line);
						}
						script.add("@ENDIF");
						eventsMap.get(event).set("main", script);
						eventsMap.get(event).set("Cooldown", config.getInt(world + "." + event + ".CoolDown"));
						eventsMap.get(event).getStringList("Worlds").add(world);
					}
				}
			}
			
			for (String[] str : events){
				try {
					eventsMap.get(str[0]).save(new File(str[1]));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		/*====================
				MYSQL
		======================*/
		File mySqlFile = new File(base + "deprecated/MYSQL.yml");
		
		if (mySqlFile.exists()){
			YamlConfiguration config = YamlConfiguration.loadConfiguration(mySqlFile);
			
			for (String key : config.getKeys(false)){
				main.settings.set("MYSQL." + key, config.get(key));
			}
		}
		
		/*====================
			INV/AREA/CLICK/WALK TRIGGERS
		======================*/
		try {
			Files.move(new File(base + "deprecated/AreaTriggers.yml"), new File(base + "/events/triggers/AreaTriggers.yml"));
			Files.move(new File(base + "deprecated/ClickTriggers.yml"), new File(base + "/events/triggers/ClickTriggers.yml"));
			Files.move(new File(base + "deprecated/WalkTriggers.yml"), new File(base + "/events/triggers/WalkTriggers.yml"));
		} catch (Exception e){}
		
		main.settings.set(VTConfig.FIRST_RUN, true);
		main.settings.set(VTConfig.DEBUG, true);
		main.settings.save();
		
		try {
			Thread.sleep(2000);
		} catch (Exception e){}
		
		main.logger.calmInfo("All done.");
	}
}