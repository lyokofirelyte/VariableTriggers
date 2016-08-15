package com.github.lyokofirelyte.VariableTriggers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.lyokofirelyte.VariableTriggers.Events.VTSystemEvent;
import com.github.lyokofirelyte.VariableTriggers.Events.Listeners.System.SystemAutoSave;
import com.github.lyokofirelyte.VariableTriggers.Events.Listeners.System.SystemTimer;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.VTConfig;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.VTData;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.VTMap;
import com.github.lyokofirelyte.VariableTriggers.Manager.VaultHook;
import com.github.lyokofirelyte.VariableTriggers.Utils.FWTools;
import com.github.lyokofirelyte.VariableTriggers.Utils.FancyLogging;

public class VariableTriggers extends JavaPlugin {
	
	public VTSetup setup;
	public VTVars vars;
	public VTSettings settings;
	public FWTools fw;
	public FancyLogging logger;
	public VaultHook vault;
	//public WorldEditHook we;
	public VTMap<Object, Object> clicks;
	public VTMap<Object, Object> areas;
	public VTMap<Object, Object> walks;
	public int timerTask = 0;
	public int saveTask = 0;
	
	static final String WRITE_OBJECT_SQL = "INSERT INTO java_objects(name, object_value) VALUES (?, ?)";
	
    public Map<List<String>, Object> commandMap = new HashMap<>();

	@Override
	public void onEnable(){
		
		try {
			getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		} catch (Exception e){}
		
		logger = new FancyLogging(this);
		logger.safeWarning("VariableTriggers is preparing files...");
		setup = new VTSetup(this).start();
		
		SystemTimer time = (SystemTimer) getInstance(SystemTimer.class);
		SystemAutoSave save = (SystemAutoSave) getInstance(SystemAutoSave.class);
		
		if (settings.getLong(VTConfig.TIMER_INTERVAL) == 0){
			settings.set(VTConfig.TIMER_INTERVAL, 60);
		}
		
		timerTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, time, settings.getLong(VTConfig.TIMER_INTERVAL)*20L, settings.getLong(VTConfig.TIMER_INTERVAL)*20L);
		saveTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, save, 12000L, 12000L);
		event(new VTSystemEvent(VTData.ENABLE));
		hook();
		logger.safeWarning("VTV2 is ready.");
	}
	
	@Override
	public void onDisable(){
		settings.set(VTData.PLACEHOLDERS, null);
		event(new VTSystemEvent(VTData.DISABLE));
		vars.saveScripts();
		vars.save();
		clicks.save();
		areas.save();
		walks.save();
		settings.save();
		Bukkit.getScheduler().cancelTasks(this);
		logger.safeWarning("I don't want to go!");
	}
	
	private void hook(){}
		
		/*if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
			PlaceholderAPI.registerPlaceholderHook(this, new PlaceholderHook() {
	            @Override
	            public String onPlaceholderRequest(Player p, String identifier) {
	                try {
	                	if (vars.containsKey(identifier)){
	                		return vars.getStr(identifier);
	                	}
	                } catch (Exception noMatch){}
	                
	                return "none";
	            }
	        });
			logger.safeWarning("Hooked with PlaceholderAPI!");
		}
	}*/
	
	public void event(Event e){
		Bukkit.getPluginManager().callEvent((Event) e);
	}
	
	public boolean perms(CommandSender cs, String perm){
		return cs.hasPermission(perm);
	}
	
	public Object getInstance(Class<?> clazz){
		return setup.registeredClasses.containsKey(clazz.toString()) ? setup.registeredClasses.get(clazz.toString()) : this;
	}
	
	public String AS(String msg){
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
	
	public void debug(String message, String scriptName, int line, String fileName){
		if (settings.getBool(VTConfig.DEBUG)){
			logger.safeWarning("> " + message + " @ " + scriptName + " line " + line + " in " + fileName + ".");
		}
	}
}