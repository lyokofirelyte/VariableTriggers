package com.github.lyokofirelyte.VariableTriggers.Utils;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.lyokofirelyte.VariableTriggers.VTParser;
import com.github.lyokofirelyte.VariableTriggers.VariableTriggers;
import com.github.lyokofirelyte.VariableTriggers.Events.VTPluginMessageOutEvent;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.AR;

public class VTUtils implements AR {
	
	private static VariableTriggers main;
	
	public VTUtils(VariableTriggers i){
		main = i;
	}
	
	public static String AS(String msg){
		return ChatColor.translateAlternateColorCodes('&', msg);
	}

	public static List<String> AS(List<String> msg){
		
		List<String> newMsg = new ArrayList<String>();
		
		for (String s : msg){
			newMsg.add(AS(s));
		}
		
		return newMsg;
	}

	public static void s(CommandSender s, String type){
		main.event(new VTPluginMessageOutEvent(s, type));
	}
	
	public static void s(CommandSender s, List<String> type){
		for (String str : type){
			main.event(new VTPluginMessageOutEvent(s, str));
		}
	}
	
	public static Map<String, List<String>> getArea(Location l){
		
		int x = l.getBlockX();
		int y = l.getBlockY();
		int z = l.getBlockZ();
		
		Map<String, Integer> map = new HashMap<String, Integer>();
		Map<String, List<String>> otherMap = new HashMap<String, List<String>>();
		Map<String, List<String>> returnMap = new HashMap<String, List<String>>();
		String curr = "none";
		int max = -1;
		
		for (Object key : main.areas.keySet()){
			if (main.areas.toString(key).startsWith(l.getWorld().getName() + ".")){
				String[] split = main.areas.toString(key).split("\\.");
				String[] completeKey = split[1].split(",");
				int lowerBoundX = Integer.parseInt(completeKey[0]);
				int lowerBoundY = Integer.parseInt(completeKey[1]);
				int lowerBoundZ = Integer.parseInt(completeKey[2]);
				int upperBoundX = Integer.parseInt(completeKey[3]);
				int upperBoundY = Integer.parseInt(completeKey[4]);
				int upperBoundZ = Integer.parseInt(completeKey[5]);
				if (x > lowerBoundX && x < upperBoundX && y > lowerBoundY && y < upperBoundY && z > lowerBoundZ && z < upperBoundZ){
					map.put(main.areas.getStr(split[0] + "." + split[1] + ".Name"), main.areas.getInt(split[0] + "." + split[1] + ".Priority"));
					otherMap.put(main.areas.getStr(split[0] + "." + split[1] + ".Name") + "0", main.areas.getList(split[0] + "." + split[1] + ".Enter.Script"));
					otherMap.put(main.areas.getStr(split[0] + "." + split[1] + ".Name") + "1", main.areas.getList(split[0] + "." + split[1] + ".Exit.Script"));
				}
			}
		}
		
		if (map.size() > 0){
			for (String area : map.keySet()){
				if (map.get(area) > max){
					max = map.get(area);
					curr = area;
				}
			}
		}
		
		if (otherMap.size() > 0){
			returnMap.put(curr + "_Enter", new ArrayList<String>(otherMap.get(curr + "0")));
			returnMap.put(curr + "_Exit", new ArrayList<String>(otherMap.get(curr + "1")));
		} else {
			returnMap.put("none", new ArrayList<String>());
		}
		
		return returnMap;
	}
	
	public static String getTime(String format) {
		
		if (format.equals("default")){
			format = "EEEEE, MMMMM dd, H:mm";
		}
		
		Calendar cal = Calendar.getInstance();
	  	cal.setTimeInMillis(System.currentTimeMillis());
	  	SimpleDateFormat sdf = new SimpleDateFormat(format);
	  	return ( sdf.format(cal.getTime()) );
	}
	
	public static void s(CommandSender s, String type, String message){
		main.event(new VTPluginMessageOutEvent(s, type, new String[]{message}));
	}
	
	public static void s(CommandSender s, String type, String[] message){
		main.event(new VTPluginMessageOutEvent(s, type, message));
	}
	
    public static String createString(String[] args, int firstArg) {
    	
        String msg = args[firstArg];
        
        for (int x = firstArg+1; x < args.length; x++){
        	msg = msg + " " + args[x];
        }
        
        return msg;
    }
    
    public static boolean isBoolean(String thing){
    	return thing.equals("true") || thing.equals("false");
    }
    
    public static boolean isInteger(String thing){
    	
    	try {
    		int i = Integer.parseInt(thing);
    	} catch (Exception e){
    		return false;
    	}
    	
    	return true;
    }
    
	public static String getDirection(String type, float yaw){
		  
        yaw += (yaw > 0 ? 360 : 0);
        yaw += 180;
        yaw %= 360;
        
        int i = (int)((yaw+8) / 22.5);
        
        if (type.equalsIgnoreCase("int")){
        	return Integer.toString(i);
        }

        String dir;
        
        switch(i){
            case 0:dir =new String("NORTH");break;
            case 1:dir = new String("NORTH_NORTH_EAST");break;
            case 2:dir = new String("NORTH_EAST");break;
            case 3:dir = new String("EAST_NORTH_EAST");break;
            case 4:dir = new String("EAST");break;
            case 5:dir = new String("EAST_SOUTH_EAST");break;
            case 6:dir = new String("SOUTH_EAST");break;
            case 7:dir = new String("SOUTH_SOUTH_EAST");break;
            case 8:dir = new String("SOUTH");break;
            case 9:dir = new String("SOUTH_SOUTH_WEST");break;
            case 10:dir = new String("SOUTH_WEST");break;
            case 11:dir = new String("WEST_SOUTH_WEST");break;
            case 12:dir = new String("WEST");break;
            case 13:dir = new String("WEST_NORTH_WEST");break;
            case 14:dir = new String("NORTH_WEST");break;
            case 15:dir = new String("NORTH_NORTH_WEST");break;
            default :dir = new String("NORTH");
        }
        
        return dir;
	}
	
	public static String getUUID(String player){
		
		UUIDFetcher fetcher = new UUIDFetcher(Arrays.asList(player));
		Map<String, UUID> response = new HashMap<String, UUID>();
		
		try {
			response = fetcher.call();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (response.containsKey(player)){
			return response.get(player).toString();
		}
		
		return "null";
	}
	
	@SuppressWarnings("deprecation")
	public static Inventory parseNewInventory(YamlConfiguration yaml){
		
		try {

			VTParser parser = new VTParser(main);
			
			Inventory inv = Bukkit.createInventory(null, Integer.parseInt(parser.parseVars(yaml.getInt("slots") + "")), AS(parser.parseVars(yaml.getString("title"))));
			
			for (String key : yaml.getConfigurationSection("items").getKeys(false)){
				ItemStack i = new ItemStack(Material.valueOf(parser.parseVars(yaml.getString("items." + key + ".type")).toUpperCase()).getId(), Integer.parseInt(parser.parseVars("" + yaml.getInt("items." + key + ".amount"))), Byte.parseByte(parser.parseVars("" + yaml.get("items." + key + ".meta"))));
				ItemMeta im = i.getItemMeta();
				if (yaml.contains("items." + key + ".lore")){
					List<String> oldList = yaml.getStringList("items." + key + ".lore");
					List<String> newList = new ArrayList<String>();
					for (String thing : oldList){
						newList.add(parser.parseVars(thing));
					}
					im.setLore(AS(newList));
				}
				if (yaml.contains("items." + key + ".name")){
					im.setDisplayName(AS(parser.parseVars(yaml.getString("items." + key + ".name"))));
				}
				if (yaml.contains("items." + key + ".enchants")){
					for (String enchant : yaml.getStringList("items." + key + ".enchants")){
						String[] parse = enchant.split(" ");
						for (Enchantment e : Enchantment.values()){
							if (e.toString().equalsIgnoreCase(parse[0])){
								im.addEnchant(e, Integer.parseInt(parser.parseVars(parse[1])), true);
								break;
							}
						}
					}
				}
				i.setItemMeta(im);
				inv.setItem(Integer.parseInt(parser.parseVars(key)), i);
			}
			
			return inv;
			
		} catch (Exception e){
			main.logger.warning("Invalid inventory!");
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Inventory parseOldInventory(List<String> invList){
		
		Inventory inv = Bukkit.createInventory(null, 9, "Inventory");
		String title = "Inventory";
		String name = "none";
		int slots = 9;
		
		for (String s : invList){
			List<String> lore = new ArrayList<String>();
			List<Enchantment> enchants = new ArrayList<Enchantment>();
			int amount = 0;
			int slot = 0;
			byte meta = 0;
			if (s.startsWith("title: ")){
				title = AS(s.substring(7));
			} else if (s.startsWith("slots: ")){
				slots = Integer.parseInt(s.substring(7));
			} else if (s.startsWith("item: ")){
				String[] itemInfo = s.substring(6).split(", ");
				for (String info : itemInfo){
					if (info.startsWith("amount: ")){
						amount = Integer.parseInt(info.substring(8));
					} else if (info.startsWith("name: ")){
						name = AS(info.substring(6));
					} else if (info.startsWith("lore: ")){
						info = info.replaceFirst("lore: ", "");
						lore = AS(Arrays.asList(info.split(" % ")));
					} else if (info.startsWith("enchant: ")){
						for (String enchant : info.substring(9).split(" % ")){
							for (Enchantment e : Enchantment.values()){
								if (e.getName().equalsIgnoreCase(enchant)){
									enchants.add(e);
									break;
								}
							}
						}
					} else if (info.startsWith("slot: ")){
						slot = Integer.parseInt(info.substring(6));
					} else if (info.startsWith("meta: ")){
						meta = (byte) Integer.parseInt(info.substring(6));
					}
				}
				ItemStack i = new ItemStack(Material.valueOf(itemInfo[0].toUpperCase()), amount, meta);
				ItemMeta im = i.getItemMeta();
				if (inv.getTitle().equals("Inventory")){
					inv = Bukkit.createInventory(null, slots, AS(title));
				}
				try {
					for (Enchantment e : enchants){
						im.addEnchant(e, 10, true);
					}
					if (!name.equals("none")){
						im.setDisplayName(name);
					}
					if (lore.size() > 0){
						im.setLore(lore);
					}
					i.setItemMeta(im);
					inv.setItem(slot, i);
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		}
		
		return inv;
	}
	
	public static List<String> stripColor(List<String> list){
		
		List<String> newList = new ArrayList<String>();
		
		for (String s : list){
			newList.add(ChatColor.stripColor(s));
		}
		
		return newList;
	}
}