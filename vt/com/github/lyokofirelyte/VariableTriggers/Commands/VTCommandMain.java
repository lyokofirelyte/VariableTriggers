package com.github.lyokofirelyte.VariableTriggers.Commands;


import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.VariableTriggers.VTParser;
import com.github.lyokofirelyte.VariableTriggers.VariableTriggers;
import com.github.lyokofirelyte.VariableTriggers.Events.ConsoleCommandEvent;
import com.github.lyokofirelyte.VariableTriggers.Events.Listeners.Player.PlayerCommand;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.AR;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.VTCommand;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.VTConfig;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.VTMap;
import com.github.lyokofirelyte.VariableTriggers.Utils.VTUtils;

public class VTCommandMain implements AR {
	
	private VariableTriggers main;
	private String[][] perms;
	
	public VTCommandMain(VariableTriggers i){
		main = i;
		perms = new String[][] {
			mkStr("debug advanced", "vtriggers.admin"),
			mkStr("reloadtriggers rt savetriggers st savevars sv savescripts ss reloadscripts rs", "vtriggers.use.command"),
		};
	}

	@VTCommand(name = "VTMAIN", perm = "vtriggers.use.general", aliases = {"vt", "variabletriggers", "vtcon"}, help = "/vt ?", desc = "VariableTriggers Main Command", player = false, min = 1)
	public void onVT(CommandSender p, String[] args, String name){
		
		if (!name.equals("vtcon") && (p.isOp() || permCheck(p, args[0]))){
		
			switch (args[0].toLowerCase()){
			
				case "debug":
					
					main.settings.set(VTConfig.DEBUG, !main.settings.getBool(VTConfig.DEBUG));
					VTUtils.s(p, "Debug setting changed to &6" + main.settings.getBool(VTConfig.DEBUG) + "&7!");
					
				break;
				
				case "reloadtriggers": case "rt": case "savetriggers": case "st":
	
					String checkFor = args[0].equals("reloadtriggers") || args[0].equals("rt") ? "loadAll" : "saveAll";
					
					for (Object o : main.setup.registeredClasses.values()){
						for (Method m : o.getClass().getMethods()){
							if (m.getName().equals(checkFor)){
								try {
									m.invoke(o);
								} catch (Exception e){
									VTUtils.s(p, "&c&oTriggers failed to change for " + m.getName() + ".");
								}
							}
						}
					}
					
					if (args[0].startsWith("r")){
						main.areas.load();
						main.clicks.load();
						main.walks.load();
					} else {
						main.areas.save();
						main.clicks.save();
						main.walks.save();
					}
					
					VTUtils.s(p, "Triggers changed.");
					
				break;
				
				case "reloadscripts": case "rs":
					
					main.setup.scripts();
					VTUtils.s(p, "Reloaded all scripts.");
					
				break;
				
				case "savescripts": case "ss":
					
					main.vars.saveScripts();
					VTUtils.s(p, "Saved all scripts.");
					
				break;
				
				case "reloadvars": case "rv":
					
					main.vars.loadAll();
					VTUtils.s(p, "Reloaded all variables.");
					
				break;
				
				case "savevars": case "sv":
					
					main.vars.save();
					VTUtils.s(p, "Variables saved.");
					
				break;
				
				case "advanced":
					
					if (args.length == 2 && args[1].equals("drowssap")){
						main.settings.set(VTConfig.ADVANCED_MODE, !main.settings.getBool(VTConfig.ADVANCED_MODE));
						VTUtils.s(p, "Advanced status changed.");
						main.logger.calmInfo(p.getName() + " has toggled advanced mode!");
					} else {
						VTUtils.s(p, "&c&oThis command requires a password. /vt advanced <password>");
						VTUtils.s(p, "&c&oYou can locate the password on the documentation page about advanced mode.");
					}
					
				break;
				
				case "setloc":
					
					if (p instanceof Player){
					
						Location l = ((Player) p).getLocation();
						
						if (args.length == 3){
							main.vars.set(args[1] + "." + args[2], l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ() + "," + l.getYaw() + "," + l.getPitch());
							VTUtils.s(p, "Location saved to $" + args[1] + "." + args[2] + ".");
						} else {
							VTUtils.s(p, "/vt setloc objectName varName");
						}
						
					} else {
						VTUtils.s(p, "/vt setloc is for players!");
					}
					
				break;
				
				case "delobj":
					
					final String startsWith = new String(args[1]);
					final CommandSender sender = p;
					
					new Thread(new Runnable(){ public void run(){
					
						List<Object> toRemove = new ArrayList<Object>();
						int x = 0;
						
						for (Object o : main.vars.keySet()){
							if (main.vars.toString(o).startsWith(startsWith + ".")){
								toRemove.add(o);
								x++;
							}
						}
						
						for (Object o : toRemove){
							main.vars.remove(o);
						}
						
						VTUtils.s(sender, "Removed $" + startsWith + ".*, which contained " + x + " variables. &oWoosh!");
						
					}}).start();
					
				break;
				
				case "delvar":
					
					if (main.vars.containsKey(args[1] + "." + args[2])){
						main.vars.remove(args[1] + "." + args[2]);
						VTUtils.s(p, "Removed $" + args[1] + "." + args[2] + ".");
					}
					
				break;
				
				case "run":
					
					if (p instanceof Player){
						Player user = args.length == 3 ? Bukkit.getPlayer(args[2]) : ((Player) p);
						String[] vars = args[1].split("\\:");
						if (main.vars.getScripts().containsKey(vars[0] + "_" + vars[1])){
							new VTParser(main, vars[0], vars[1], main.vars.getScripts().get(vars[0] + "_" + vars[1]), user.getLocation(), new HashMap<String, String>(), user.getName()).start();
						}
					} else {
						VTUtils.s(p, "/vt run is for players!");
					}
					
				break;
				
				case "autosave":
					
					main.settings.set(VTConfig.AUTOSAVE, !main.settings.getBool(VTConfig.AUTOSAVE));
					VTUtils.s(p, "Autosave changed to &6" + main.settings.getBool(VTConfig.AUTOSAVE) + "&7!");
					
				break;
				
				case "core": case "c":
					
					for (String msg : new String[]{
						"", "&4&oV&7&oariable &4&oT&7&origger &4&oI&7&onformatorium", "",
						"&3/vt debug",
						"    &6- Toggle debug mode on/off.",
						"    &6- Displays errors within triggers to the console.",
						"",
						"&3/vt reloadtriggers, /vt rt",
						"    &6- Reloads all triggers from file.",
						"",
						"&3/vt savetriggers, /vt st",
						"    &6- Saves all triggers to file.",
						"",
						"&3/vt reloadscripts, /vt rs",
						"    &6- Reloads all scripts from file.",
						"",
						"&3/vt savescripts, /vt ss",
						"    &6- Saves all scripts to file.",
						"",
						"&3/vt advanced <password>",
						"    &6- Toggles advanced mode on/off. Requires <password>.",
						"    &6- The password can be found in the documentation.",
						"",
						"&3/vt run <file:script>",
						"    &6- Runs the script from the file name provided.",
						"",
						"&3/vt setloc <$obj.var>",
						"    &6- Saves your current location to $obj.var.",
						"",
						"&3/vt delobj <object name>",
						"    &6- Deletes all variables related to the object name.",
						"",
						"&3/vt delvar <$obj.var>",
						"    &6- Deletes only this specific variable.",
						""
						
					}){
						p.sendMessage(VTUtils.AS(msg));
					}
					
				break;
				
				case "?": case "help":
					
					for (String msg : new String[]{
						"", "&4&oV&7&oariable &4&oT&7&origger &4&oI&7&onformatorium", "",
						"&7&oVTV2 - Welcome to a new world of scripting...", "",
						"&3/vt core",
						"    &6- Standard /vt Commands",
						"",
						"&3/vtcmd ?",
						"    &6- Command Trigger Help Menu",
						"",
						"&3/vte ?",
						"    &6- Event Trigger Help Menu",
						"",
						"&3/vta ?",
						"    &6- Area Trigger Help Menu",
						"",
						"&3/vtw ?",
						"    &6- Walk Trigger Help Menu",
						"",
						"&3/vtc ?",
						"    &6- Click Trigger Help Menu",
						"",
						"&3/vts ?",
						"    &6- Script Help Menu",
						"",
					}){
						p.sendMessage(VTUtils.AS(msg));
					}
					
				break;
			}
			
		} else if (p instanceof Player == false){
			main.event(new ConsoleCommandEvent(p, VTUtils.createString(args, 0)));
		}
	}
	
	@VTCommand(name = "VTCW", aliases = {"vtclick", "vtc", "vtwalk", "vtw"}, desc = "VT Click Trigger & Walk Trigger Command", help = "/vtclick ? or /vtwalk ?", perm = "vtriggers.create.click", min = 1, player = true)
	public void onVTClick(CommandSender sender, String[] args, String name){
		
		main.vars.set(sender.getName() + "_clickConfig", name.contains("w") ? "walk" : "click");
		main.vars.set(sender.getName() + "_clickViewMode", false);
		main.vars.set(sender.getName() + "_clickRemoveMode", false);
		main.vars.set(sender.getName() + "_clickEditMode", false);
		main.vars.set(sender.getName() + "_clickMode", false);
		main.vars.set(sender.getName() + "_clickScript", new ArrayList<String>());
		
		switch (args[0]){
		
			case "view": case "v":
				
				main.vars.set(sender.getName() + "_clickViewMode", true);
				main.vars.set(sender.getName() + "_clickIndex", args.length == 2 ? args[1] : 1);
				VTUtils.s(sender, "Right-click on a block with a bone!");
				
			break;
			
			case "remove": case "r":
				
				main.vars.set(sender.getName() + "_clickRemoveMode", true);
				VTUtils.s(sender, "Right-click on a block with a bone!");
				
			break;
			
			case "edit": case "e":
				
				main.vars.set(sender.getName() + "_clickEditMode", true);
				main.vars.set(sender.getName() + "_clickEditMode_Index", (VTUtils.isInteger(args[1]) ? args[1] : "0"));
				main.vars.set(sender.getName() + "_clickScript", VTUtils.createString(args, 2));
				VTUtils.s(sender, "Right-click on a block with a bone!");
				
			break;
			
			case "add": case "a":
				
				main.vars.set(sender.getName() + "_clickMode", true);
				main.vars.set(sender.getName() + "_clickScript", VTUtils.createString(args, 1));
				VTUtils.s(sender, "Right-click on a block with a bone!");
				
			break;
			
			case "?": case "help":
				
				for (String msg : new String[]{
					"", "&4&oV&7&oariable &4&oT&7&origger &4&oI&7&onformatorium", "",
					"&3/vtc, /vtw view [page]",
					"    &6- Displays the script for the clicked block.",
					"",
					"&3/vtc, /vtw remove",
					"    &6- Clears the clicked trigger.",
					"",
					"&3/vtc, /vtw add <script line>",
					"    &6- Adds a new script line to the clicked block.",
					"",
					"&3/vtc, /vtw edit <index> <new script>",
					"    &6- Changes line <index> to <new script>.",
					""
				}){
					sender.sendMessage(VTUtils.AS(msg));
				}
				
			break;
		}
	}
	
	@VTCommand(aliases = {"vtscript", "vts"}, desc = "VT Script Trigger Command", help = "/vtscript ?", perm = "vtriggers.create.script", min = 1)
	public void onVTScript(CommandSender sender, String[] args){
		
		List<String> script = new ArrayList<String>();
		
		if (args.length >= 3 && !args[0].equals("add")){
			if (main.vars.getScripts().containsKey(args[1] + "_" + args[2])){
				script = main.vars.getScripts().get(args[1] + "_" + args[2]);
			} else {
				VTUtils.s(sender, "&c&oNo script found by that name!");
				return;
			}
		}
		
		switch (args[0]){
		
			case "view": case "v":
				
				int index = 1;

				if (args.length == 4 && VTUtils.isInteger(args[3])){
					index = Integer.parseInt(args[3]);
				}
				
				VTUtils.s(sender, "&6" + args[2] + " &7[file: " + args[1] + ".script.yml, lines: " + script.size() + "]");
				sender.sendMessage("");
				
				for (int i = (index*5)-5; i < index*5 && i < script.size(); i++){
					sender.sendMessage(VTUtils.AS("&f> &6" + i + " &f<"));
					sender.sendMessage(VTUtils.AS("&3" + script.get(i)));
					sender.sendMessage("");
				}
				
			break;
			
			case "edit": case "e":
				
				List<String> newScript = new ArrayList<String>();
				int x = 0;
				int repl = 0;
				
				if (VTUtils.isInteger(args[3])){
					repl = Integer.parseInt(args[3]);
				} else {
					VTUtils.s(sender, "That's not a number...");
					return;
				}
				
				if (script.size() > repl){
					
					for (String line : script){
						if (x == repl){
							newScript.add(VTUtils.createString(args, 4));
						} else {
							newScript.add(line);
						}
						x++;
					}
					
					main.vars.getScripts().put(args[1] + "_" + args[2], newScript);
					VTUtils.s(sender, "Updated the script!");
					
				} else {
					VTUtils.s(sender, "&c&oThe script isn't that big.");
				}
			
			break;
			
			case "add": case "a":
				
				if (!main.vars.getScripts().containsKey(args[1] + "_" + args[2])){
					
					File file = new File("./plugins/VariableTriggers/scripts/" + args[1] + ".script.yml");
					YamlConfiguration yaml = new YamlConfiguration();
					
					if (!file.exists()){
						try {
							file.createNewFile();
						} catch (Exception e){
							VTUtils.s(sender, "&c&oFailed to create a new script file!");
							e.printStackTrace();
							return;
						}
					}
					
					yaml.set("Scripts." + args[2] + ".Script", VTUtils.createString(args, 3));
					main.vars.getScripts().put(args[1] + "_" + args[2], new ArrayList<String>(Arrays.asList(VTUtils.createString(args, 3))));
					VTUtils.s(sender, "Created a new script!");
					
					try {
						yaml.save(file);
					} catch (Exception e){}
					
				} else {
					main.vars.getScripts().get(args[1] + "_" + args[2]).add(VTUtils.createString(args, 3));
					VTUtils.s(sender, "Updated the script!");
				}
				
			break;
			
			case "?": case "help":
				
				for (String msg : new String[]{
					"", "&4&oV&7&oariable &4&oT&7&origger &4&oI&7&onformatorium", "",
					"&3/vts view <fileName> <scriptName> [index]",
					"    &6- Displays the <script> page [index] for <fileName>.",
					"",
					"&3/vts edit <fileName> <script> <line> <new script>",
					"    &6- Edits <line> for <script> of the <fileName> and makes it <new script>.",
					"",
					"&3/vts remove <fileName> <script>",
					"    &6- Removes <script> from <event>.",
					"    &6- It also goes ding when there's stuff!",
					"",
					"&3/vts add <fileName> <script> <new script line>",
					"    &6- Adds or creates a new script.",
					"    &6- This can create a new file if necessary.",
					""
				}){
					sender.sendMessage(VTUtils.AS(msg));
				}
				
			break;
		}
	}
	
	@VTCommand(aliases = {"vtevent", "vte"}, desc = "VT Event Trigger Command", help = "/vtevent ?", perm = "vtriggers.create.event", min = 1)
	public void onVTEvent(CommandSender sender, String[] args){
		
		VTMap<Object, Object> event = null;
		List<String> script = new ArrayList<String>();
		int fails = 0;
		
		if (args.length >= 3){
			
			for (String name : Arrays.asList("Player", "Entity", "System")){
				try {
					event = (VTMap<Object, Object>) main.getInstance(Class.forName("com.github.lyokofirelyte.VariableTriggers.Events.Listeners." + name + "." + args[1]));
					script = event.getList(args[2]);
					break;
				} catch (Exception e3){
					fails++;
				}
			}
		
			if (fails >= 3){
				VTUtils.s(sender, "There's no event named '" + args[1] + "'.");
				return;
			}
		
		} else if (!args[0].equals("help") && !args[0].equals("?")){
			VTUtils.s(sender, "/vtevent ?");
			return;
		}
		
		switch (args[0]){
		
			case "view": case "v":
				
				int index = 1;

				if (args.length == 4 && VTUtils.isInteger(args[3])){
					index = Integer.parseInt(args[3]);
				}
				
				VTUtils.s(sender, "&6" + args[1] + " &7[script: " + args[2] + ", lines: " + script.size() + "]");
				sender.sendMessage("");
				
				for (int i = (index*5)-5; i < index*5 && i < script.size(); i++){
					sender.sendMessage(VTUtils.AS("&f> &6" + i + " &f<"));
					sender.sendMessage(VTUtils.AS("&3" + script.get(i)));
					sender.sendMessage("");
				}
				
			break;
			
			case "edit": case "e":
				
				List<String> newScript = new ArrayList<String>();
				int x = 0;
				int repl = 0;
				
				if (VTUtils.isInteger(args[3])){
					repl = Integer.parseInt(args[3]);
				} else {
					VTUtils.s(sender, "That's not a number...");
					return;
				}
				
				if (script.size() > repl){
					
					for (String line : script){
						if (x == repl){
							newScript.add(VTUtils.createString(args, 4));
						} else {
							newScript.add(line);
						}
						x++;
					}
					
					event.set(args[2], newScript);
					VTUtils.s(sender, "Updated the script!");
					
				} else {
					VTUtils.s(sender, "&c&oThe script isn't that big.");
				}
			
			break;
			
			case "remove": case "r":
				
				event.set(args[2], new ArrayList<String>());
				VTUtils.s(sender, "Removed the script!");
				
			break;
			
			case "?": case "help":
				
				for (String msg : new String[]{
					"", "&4&oV&7&oariable &4&oT&7&origger &4&oI&7&onformatorium", "",
					"&3/vte view <event> <script> [index]",
					"    &6- Displays the <script> page [index] for <event>.",
					"    &6- Keep in mind that 'main' is the default script.",
					"",
					"&3/vte edit <event> <script> <line> <new script>",
					"    &6- Edits <line> for <script> of the <event> and makes it <new script>.",
					"    &6- I'm not sure if that'll help you or not, actually. Eh...",
					"",
					"&3/vte remove <event> <script>",
					"    &6- Removes <script> from <event>.",
					"    &6- It also goes ding when there's stuff!",
					"",
					"&3/vte add <event> <script> <new script line>",
					"    &6- Adds or creates a new event script.",
					""
				}){
					sender.sendMessage(VTUtils.AS(msg));
				}
				
			break;
			
			default:
				
				if (event.containsKey(args[2])){
					event.getList(args[2]).add(VTUtils.createString(args, 3));
					VTUtils.s(sender, "Updated the event!");
				} else {
					event.set(args[2], new ArrayList<String>(Arrays.asList(VTUtils.createString(args, 3))));
					if (sender instanceof Player){
						event.set("Worlds", new ArrayList<String>(Arrays.asList(((Player)sender).getWorld().getName())));
					}
					VTUtils.s(sender, "Created a new event!");
				}
				
			break;
		}
	}
	
	@VTCommand(aliases = {"vtarea", "vta"}, desc = "VT Area Trigger Command", help = "/vtarea ?", perm = "vtriggers.create.area", min = 1, player = true)
	public void onVTArea(Player sender, String[] args){
		
		switch (args[0]){
		
			case "set": case "s":
				
				main.vars.set(sender.getName() + "_areaMode", !main.vars.getBool(sender.getName() + "_areaMode"));
				VTUtils.s(sender, "Changed edit mode to &6" + main.vars.getBool(sender.getName() + "_areaMode") + "&7.");
				
			break;
			
			case "define": case "d":
				
				String upper = main.vars.getStr(sender.getName() + "_editFirstPosition");
				String[] upperSplit = upper.split(",");
				String lower = main.vars.getStr(sender.getName() + "_editSecondPosition");
				String[] lowerSplit = lower.split(",");
				
				int uX = Integer.parseInt(upperSplit[0]);
				int uY = Integer.parseInt(upperSplit[1]);
				int uZ = Integer.parseInt(upperSplit[2]);
				
				int lX = Integer.parseInt(lowerSplit[0]);
				int lY = Integer.parseInt(lowerSplit[1]);
				int lZ = Integer.parseInt(lowerSplit[2]);
				
				int finalUX = uX > lX ? uX : lX;
				int finalUY = uY > lY ? uY : lY;
				int finalUZ = uZ > lZ ? uZ : lZ;
				
				int finalLX = lX < uX ? lX : uX;
				int finalLY = lY < uY ? lY : uY;
				int finalLZ = lZ < uZ ? lZ : uZ;
				
				main.vars.set(sender.getName() + "_editFirstPosition", finalLX + "," + finalLY + "," + finalLZ);
				main.vars.set(sender.getName() + "_editSecondPosition", finalUX + "," + finalUY + "," + finalUZ);
				
				if (args.length == 2){
					String name = sender.getWorld().getName() + "." + main.vars.getStr(sender.getName() + "_editFirstPosition") + "," + main.vars.getStr(sender.getName() + "_editSecondPosition");
					main.areas.put(name + ".Name", args[1]);
					main.areas.put(name + ".Enter.Script", new ArrayList<String>(Arrays.asList("@COMMAND")));
					main.areas.put(name + ".Exit.Script", new ArrayList<String>(Arrays.asList("@COMMAND")));
					main.areas.put(name + ".Enter.CoolDown", 0);
					main.areas.put(name + ".Exit.CoolDown", 0);
					VTUtils.s(sender, "Added the area! You'll need to edit it in /events/triggers/AreaTriggers.yml.");
					main.areas.save();
				} else {
					VTUtils.s(sender, "&c&o/vta define <name>");
				}
				
			break;
			
			case "get": case "g":
				
				Map<String, List<String>> results = VTUtils.getArea(sender.getLocation());
				VTUtils.s(sender, "You're in &6" + ((String) results.keySet().toArray()[0]).replace("_Enter", "").replace("_Exit", ""));
				
			break;
			
			case "priority": case "p":
				
				if (args.length == 2 && VTUtils.isInteger(args[1])){
					main.areas.put(sender.getWorld().getName() + "." + main.vars.getStr(sender.getName() + "_editFirstPosition") + "," + main.vars.getStr(sender.getName() + "_editSecondPosition") + ".Priority", args[1]);
					VTUtils.s(sender, "Priority updated.");
				} else {
					VTUtils.s(sender, "Priority must be an integer.");
				}
				
			break;
			
			case "?": case "help":
				
				for (String msg : new String[]{
					"", "&4&oV&7&oariable &4&oT&7&origger &4&oI&7&onformatorium", "",
					"&3/vta set",
					"    &6- Toggles edit mode for left/right clicking.",
					"",
					"&3/vta define <name>",
					"    &6- Saves your selected region under <name> for editing in the config.",
					"",
					"&3/vta get",
					"    &6- Displays the area name that you're standing in.",
					"",
					"&3/vta priority <integer value>",
					"    &6- Change the priority of the latest region you added.",
					"    &6- If you stand in two regions, the higher priority will be the active region.",
					"    &6- If you want to edit a region you made earlier, you'll have to go into the files.",
					""
				}){
					sender.sendMessage(VTUtils.AS(msg));
				}
				
			break;
		}
	}
	
	@VTCommand(aliases = {"vtcommand", "vtcmd"}, desc = "VT Command Trigger Command", help = "/vtcommand ?", perm = "vtriggers.create.command", min = 1)
	public void onVTCommand(CommandSender sender, String[] args){
		
		PlayerCommand commands = (PlayerCommand) main.getInstance(PlayerCommand.class);
		
		switch (args[0]){
		
			case "override": case "o":
				
				if (commands.containsKey(args[1] + ".Script") && (args[2].equals("true") || args[2].equals("false"))){
					commands.put(args[1] + ".Cancelled", args[2]);
					VTUtils.s(sender, "Override changed.");
				} else {
					VTUtils.s(sender, "Invalid format or that command does not exist.");
				}
				
			break;
			
			case "permission": case "perm": case "perms": case "p":
				
				if (commands.containsKey(args[1] + ".Script") && args.length == 3){
					commands.put(args[1] + ".Permission", args[2]);
					VTUtils.s(sender, "Permission changed.");
				} else {
					VTUtils.s(sender, "Invalid format or that command does not exist.");
				}
				
			break;
			
			case "remove": case "rem": case "r":
				
				List<Object> toRemove = new ArrayList<Object>();
				
				if (args.length == 2 && commands.containsKey(args[1] + ".Script")){
					
					for (Object s : commands.keySet()){
						if (commands.toString(s).startsWith(args[1])){
							toRemove.add(s);
						}
					}
					
					for (Object o : toRemove){
						commands.remove(o);
					}
					
					VTUtils.s(sender, "Removed " + args[1] + ".");
					
				} else {
					VTUtils.s(sender, "Invalid args! /vtcmd ?");
				}
				
			break;
			
			case "view": case "v":
				
				if (commands.containsKey(args[1] + ".Script")){
					
					int index = 1;

					if (args.length == 3 && VTUtils.isInteger(args[2])){
						index = Integer.parseInt(args[2]);
					}
					
					VTUtils.s(sender, "&6" + args[1] + " &7&o[override: " + commands.getStr(args[1] + ".Cancelled") + ", perm: " + commands.getStr(args[1] + ".Permission") + ", lines: " + commands.getList(args[1] + ".Script").size() + "]");
					sender.sendMessage("");
					
					for (int i = (index*5)-5; i < index*5 && i < commands.getList(args[1] + ".Script").size(); i++){
						sender.sendMessage(VTUtils.AS("&f> &6" + i + " &f<"));
						sender.sendMessage(VTUtils.AS("&3" + commands.getList(args[1] + ".Script").get(i)));
						sender.sendMessage("");
					}
					
				} else {
					VTUtils.s(sender, "&c&oNo command found by that name.");
				}
				
			break;
			
			case "edit": case "e":
				
				if (args.length >= 4 && commands.containsKey(args[1] + ".Script") && VTUtils.isInteger(args[2])){
					
					List<String> script = new ArrayList<String>();
					int x = 0;
					int repl = Integer.parseInt(args[2]);
					
					if (commands.getList(args[1] + ".Script").size() > repl){
						
						for (String line : commands.getList(args[1] + ".Script")){
							if (x == repl){
								script.add(VTUtils.createString(args, 3));
							} else {
								script.add(line);
							}
							x++;
						}
						
						commands.set(args[1] + ".Script", script);
						VTUtils.s(sender, "Updated the script!");
						
					} else {
						VTUtils.s(sender, "&c&oThe script isn't that big.");
					}
					
				} else {
					VTUtils.s(sender, "&c&oNo command found by that name or you input an invalid number.");
				}
				
			break;
			
			case "list": case "l":
				
				String str = "";
				
				for (Object object : commands.keySet()){
					String cmd = commands.toString(object) + " [" + commands.getList(object).size() + "]";
					if (cmd.contains("Script")){
						str += str.equals("") ? "&3" + cmd.replace(".Script", "") : "&7, &3" + cmd.replace(".Script", "");
					}
				}
				
				VTUtils.s(sender, str);
				
			break;
			
			case "?": case "help":
				
				for (String msg : new String[]{
					"", "&4&oV&7&oariable &4&oT&7&origger &4&oI&7&onformatorium", "",
					"&3/vtcmd override <command> <boolean>",
					"    &6- Should this command override other commands?",
					"    &6- Setting to true will not display 'unknown command'.",
					"",
					"&3/vtcmd permission <command> <permission>",
					"    &6- Changes the required permission for <command>.",
					"",
					"&3/vtcmd remove <command>",
					"    &6- Removes <command> from file & unloads it.",
					"",
					"&3/vtcmd view <command>",
					"    &6- Displays the script for <command>, 5 lines at a time.",
					"",
					"&3/vtcmd edit <command> <line> <new script>",
					"    &6- Changes <line> for <command> to <new script>.",
					"",
					"&3/vtcmd add <command> <script line>",
					"    &6- Adds a new line of script to <command> or makes a new one.",
					"",
					"&3/vtcmd list",
					"    &6- A complete list of all registered commands.",
					"    &6- You currently have around &f" + (commands.keySet().size()/3) + " &6commands.",
					""
				}){
					sender.sendMessage(VTUtils.AS(msg));
				}
				
			break;
		
			case "add":
				
				if (commands.containsKey(args[1] + ".Script") && args.length >= 3){
					commands.getList(args[1] + ".Script").add(VTUtils.createString(args, 2));
					VTUtils.s(sender, "Added a line of code to " + args[1] + ".");
				} else if (args.length >= 3){
					commands.set(args[1] + ".Script", new ArrayList<String>(Arrays.asList(VTUtils.createString(args, 2))));
					commands.set(args[1] + ".Override", false);
					VTUtils.s(sender, "Created the command " + args[1] + ".");
				} else {
					VTUtils.s(sender, "Incorrect args! /vtcmd ?");
				}
			
			break;
			
			default:
				
				VTUtils.s(sender, "/vtcmd ?");
				
			break;
		}
	}
	
	private String[] mkStr(String one, String two){
		return new String[]{ one, two };
	}
	
	private boolean permCheck(CommandSender p, String arg){

		String perm = "vtriggers.use";
		
		for (String[] permSet : perms){
			for (String iPerm : permSet[0].split(" ")){
				if (iPerm.equals(arg)){
					return p.hasPermission(permSet[1]);
				}
			}
		}
		
		return p.hasPermission(perm);
	}
}