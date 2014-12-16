package com.github.lyokofirelyte.VariableTriggers.Identifiers;

import gnu.trove.map.hash.THashMap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

public class VTMap<K, V> extends THashMap<Object, Object> {
	
	public char getChar(Object i){
		
		if (containsKey(toString(i))){
			if (get(toString(i)) instanceof Character){
				return (Character) get(toString(i));
			}
		}
		
		return 'n';
	}

	public String getStr(Object i){

		if (containsKey(toString(i))){
			if (get(toString(i)) instanceof String){
				return (String) get(toString(i));
			}
			return get(toString(i)) + "";
		}
		return "none";
	}
	
	public int getInt(Object i){

		if (containsKey(toString(i))){
			if (get(toString(i)) instanceof Integer){
				return (Integer) get(toString(i));
			}
			try {
				return Integer.parseInt(get(toString(i)) + "");
			} catch (Exception e){
				return 0;
			}
		}
		return 0;
	}
	
	public long getLong(Object i){

		if (containsKey(toString(i))){
			if (get(toString(i)) instanceof Long){
				return (Long) get(toString(i));
			}
			try {
				return Long.parseLong(get(toString(i)) + "");
			} catch (Exception e){
				return 0L;
			}
		}
		return 0L;
	}
	
	public byte getByte(Object i){

		if (containsKey(toString(i))){
			if (get(toString(i)) instanceof Byte){
				return (Byte) get(toString(i));
			}
			try {
				return Byte.parseByte(get(toString(i)) + "");
			} catch (Exception e){
				return 0;
			}
		}
		return 0;
	}
	
	public float getFloat(Object i){
		
		if (containsKey(toString(i))){
			if (get(toString(i)) instanceof Float){
				return (Float) get(toString(i));
			}
			try {
				return Float.parseFloat(get(toString(i)) + "");
			} catch (Exception e){
				return 0F;
			}
		}
		return 0F;
	}
	
	public double getDouble(Object i){

		if (containsKey(toString(i))){
			if (get(toString(i)) instanceof Double){
				return (Double) get(toString(i));
			}
			try {
				return Double.parseDouble(get(toString(i)) + "");
			} catch (Exception e){
				return 0D;
			}
		}
		return 0D;
	}
	
	public boolean getBool(Object i){

		if (containsKey(toString(i))){
			if (get(toString(i)) instanceof Boolean){
				return (Boolean) get(toString(i));
			}
			try {
				return Boolean.valueOf(get(toString(i)) + "");
			} catch (Exception e){
				return false;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public List<String> getList(Object i){
		
		if (containsKey(toString(i))){
			try {
				if (get(toString(i)) instanceof List){
					return (List<String>) get(toString(i));
				}
				set(i, new ArrayList<String>());
			} catch (Exception e){
				set(i, new ArrayList<String>());
			}
		} else {
			set(i, new ArrayList<String>());
		}
		
		return (List<String>) get(toString(i));
	}
	
	public THashMap<String, List<String>> getScripts(){
		return (THashMap<String, List<String>>) get(toString(VTData.SCRIPTS));
	}
	
	public String valuesToString(){
		
		String str = "";
		
		for (Object obj : values()){
			str = str.equals("") ? toString(obj) : str + ", " + toString(obj);
		}
		
		return str;
	}
	
	public String valuesToSortedString(){
		
		List<String> str = new ArrayList<String>();
		String newStr = "";
		
		for (Object obj : values()){
			str.add(toString(obj));
		}
		
		Collections.sort(str);
		
		for (String string : str){
			newStr = newStr.equals("") ? string : newStr + ", " + string;
		}
		
		return newStr;
	}
	
	public boolean editValue(Object oldValue, Object newValue){
		
		List<Object> oldKeys = new ArrayList<Object>(keySet());
	
		for (Object o : oldKeys){
			if (get(toString(o)).equals(oldValue)){
				put(toString(o), newValue);
				return true;
			}
		}
		
		return false;
	}
	
	public boolean editKey(Object oldKey, Object newKey){
		
		if (containsKey(toString(oldKey)) && !containsKey(toString(newKey))){
			set(newKey, get(toString(oldKey)));
			remove(toString(oldKey));
			return true;
		}
		
		return false;
	}
	
	public String toString(Object i){
		
		if (i instanceof String){
			return (String) i;
		} else if (i instanceof Enum){
			try {
				return (String) i.getClass().getMethod("s").invoke(null);
			} catch (Exception e){
				return ((Enum) i).toString();
			}
		}
		return i.toString();
	}
	
	public boolean isEmpty(){
		return size() <= 0;
	}
	
	public boolean isSize(int size){
		return size() >= size;
	}
	
	public void set(Object i, Object infos){

		if (infos instanceof Location){
			Location l = (Location) infos;
			put(toString(i), l.getWorld().getName() + " " + l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ() + " " + l.getYaw() + " " + l.getPitch());
		} else {
			put(toString(i), infos);
		}
	}
	
	public void makePath(String folder, String name){
		
		set(VTData.FILE_PATH, folder + "/" + name);
		
		File folderFile = new File(folder);
		File file = new File(folderFile + "/" + name);
		
		if (!folderFile.exists()){
			folderFile.mkdirs();
		}
		
		if (!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void cooldown(){
		int mult = getInt("Cooldown");
		if (mult == 0){
			mult = getInt("CoolDown");
		}
		long currTime = System.currentTimeMillis();
		long extraTime = mult*1000L;
		long finalTime = currTime + extraTime;
		set("ActiveCooldown", finalTime);
	}
	
	public void cooldown(String path){
		
		int mult = getInt(path + ".Cooldown");
		if (mult == 0){
			mult = getInt(path + ".CoolDown");
		}
		long currTime = System.currentTimeMillis();
		long extraTime = mult*1000L;
		long finalTime = currTime + extraTime;
		set(path + ".ActiveCooldown", finalTime);
	}
	
	public void saveScripts(){
		
		File file = null;
		YamlConfiguration scriptYaml = null;
		Map<String, YamlConfiguration> yamls = new THashMap<String, YamlConfiguration>();
		Map<String, File> files = new THashMap<String, File>();
		
		try {
			
			if (getScripts() != null && getScripts().size() > 0){
				for (String script : getScripts().keySet()){
					
					if (file == null || !yamls.containsKey(script.split("_")[0])){
						file = new File("./plugins/VariableTriggers/scripts/" + script.split("_")[0] + ".script.yml");
						scriptYaml = YamlConfiguration.loadConfiguration(file);
						yamls.put(script.split("_")[0], scriptYaml);
						files.put(script.split("_")[0], file);
					} else {
						scriptYaml = yamls.get(script.split("_")[0]);
					}
					
					scriptYaml.set("Scripts." + script.split("_")[1] + ".Script", getScripts().get(script));
				}
				
				for (String yamlName : yamls.keySet()){
					yamls.get(yamlName).save(files.get(yamlName));
				}
			}
			
		} catch (Exception e){}
	}
	
	public void save(){
		
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(new File(getStr(VTData.FILE_PATH)));
		remove(toString(VTData.SCRIPTS));
		
		for (Object o : keySet()){
			yaml.set(toString(o), get(toString(o)));
		}
		
		yaml.set("FILE_PATH", null);
		
		try {
			yaml.save(new File(getStr(VTData.FILE_PATH)));
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void load(){
		
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(new File(getStr(VTData.FILE_PATH)));
		List<String> noDefault = Arrays.asList("settings.yml", "vars.yml");
		boolean go = true;
		
		for (String s : noDefault){
			if (getStr(VTData.FILE_PATH).contains(s)){
				go = false;
				break;
			}
		}
		
		if (go){
			
			if (!yaml.contains("Worlds")){
				yaml.set("Worlds", new ArrayList<String>(Arrays.asList("some_world_here", "some_other_world_here")));
			}
				
			if (!yaml.contains("Cooldown")){
				yaml.set("Cooldown", 0);
			}
				
			if (!yaml.contains("Cancelled")){
				yaml.set("Cancelled", false);
			}
			
			if (getStr(VTData.FILE_PATH).contains("ConsoleCommand") || getStr(VTData.FILE_PATH).contains("PlayerCommand")){
				if (yaml.getKeys(false).size() == 3){
					yaml.set("SomeCommand.Script", new ArrayList<String>(Arrays.asList("@COMMAND", "@COMMAND")));
					yaml.set("SomeCommand.Cooldown", 0);
				}
			}
				
			if (!yaml.contains("main")){
				yaml.set("main", new ArrayList<String>(Arrays.asList("@COMMAND")));
			}
			
			try {
				yaml.save(new File(getStr(VTData.FILE_PATH)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		for (String sec : yaml.getKeys(true)){
			set(sec, yaml.get(sec));
		}
	}
}