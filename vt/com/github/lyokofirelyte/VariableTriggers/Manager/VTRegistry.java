package com.github.lyokofirelyte.VariableTriggers.Manager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.SimplePluginManager;

import com.github.lyokofirelyte.VariableTriggers.VariableTriggers;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.VTCommand;
import com.github.lyokofirelyte.VariableTriggers.Utils.VTUtils;


public class VTRegistry implements CommandExecutor {
	
	private VariableTriggers main;
	
	public VTRegistry(VariableTriggers i){
		main = i;
	}

	public void registerCommands(Object... classes){
                
		Field f = null;
		CommandMap scm = null;
	                
		try {
			f = SimplePluginManager.class.getDeclaredField("commandMap");
			f.setAccessible(true);
			scm = (CommandMap) f.get(Bukkit.getPluginManager());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	                
		for (Object obj : classes){
			for (Method method : obj.getClass().getMethods()) {
				if (method.getAnnotation(VTCommand.class) != null){
					VTCommand anno = method.getAnnotation(VTCommand.class);
					try {
						VTCmd command = new VTCmd(anno.aliases()[0]);
						command.setUsage(anno.help());
						command.setAliases(Arrays.asList(anno.aliases()));
						command.setDescription(anno.desc());
						scm.register("vt", command);
						command.setExecutor(this);
						main.commandMap.put(Arrays.asList(anno.aliases()), obj);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	
    	for (List<String> cmdList : main.commandMap.keySet()){
    		if (cmdList.contains(label)){
    			for (String command : cmdList){
    				if (command.equals(label)){
    					Object obj = main.commandMap.get(cmdList);
    					for (Method m : obj.getClass().getMethods()){
    						if (m.getAnnotation(VTCommand.class) != null && Arrays.asList(m.getAnnotation(VTCommand.class).aliases()).contains(command)){
    							try {
    								VTCommand anno = m.getAnnotation(VTCommand.class);
    								if ((sender instanceof Player && (((Player) sender).hasPermission(anno.perm())) || sender instanceof Player == false || sender.isOp())){
    									if (args.length > anno.max() || args.length < anno.min()){
    										VTUtils.s(sender, anno.help());
    										return true;
    									}     
    									if (anno.name().equals("none")){
    										if (anno.player()){
    											if (sender instanceof Player){
    												m.invoke(obj, (Player) sender, args);
    											} else {
    												VTUtils.s(sender, "&cConsole players cannot run this command!");
    											}
    										} else {
    											m.invoke(obj, sender, args);
    										}
    									} else {
    										if (anno.player()){
    											if (sender instanceof Player){
    												m.invoke(obj, (Player) sender, args, label);
    											} else {
    												VTUtils.s(sender, "&cConsole players cannot run this command!");
    											}
    										} else {
    											m.invoke(obj, sender, args, label);
    										}
    									}
    								} else {
    									VTUtils.s(sender, "&4No permission!");
    								}
    							} catch (Exception e) {
    								VTUtils.s(sender, "&c&oError parsing command! See /vt ? for help if necessary.");
    							}
    						}
    					}
    				}
    			}
    		}
    	}
    	return true;
    }
    
    public class VTCmd extends Command {

        public CommandExecutor exe = null;
        
        public VTCmd(String name) {
            super(name);
        }

        public boolean execute(CommandSender sender, String commandLabel, String[] args) {
            if(exe != null){
                exe.onCommand(sender, this, commandLabel,args);
            }
            return false;
        }
        
        public void setExecutor(CommandExecutor exe){
            this.exe = exe;
        }
    }
}