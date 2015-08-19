package com.github.lyokofirelyte.VariableTriggers.Manager;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;

import com.github.lyokofirelyte.VariableTriggers.VariableTriggers;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.AR;

public class VaultHook implements AR {
	
	private VariableTriggers main;
	private ServicesManager services;
	
    public Economy econ = null;
    public Permission perms = null;
    public Chat chat = null;
    
    private Boolean[] status = new Boolean[4];
 
	public VaultHook(VariableTriggers i){
		main = i;
		services = main.getServer().getServicesManager();
	}
	
	public void hookSetup(){
		if (main.getServer().getPluginManager().getPlugin("Vault") != null){
			status = register();
		} else {
			status = new Boolean[]{ false, false, false, false};
		}
	}
	

    private Boolean[] register(){

        RegisteredServiceProvider<Economy> rspEcon = services.getRegistration(Economy.class);
        econ = rspEcon != null ? rspEcon.getProvider() : null;
        
    	RegisteredServiceProvider<Chat> rspChat = services.getRegistration(Chat.class);
    	chat = rspChat != null ? rspChat.getProvider() : null;
    	
        RegisteredServiceProvider<Permission> rspPerms = services.getRegistration(Permission.class);
        perms = rspPerms != null ? rspPerms.getProvider() : null;

        return new Boolean[]{ true, econ != null, chat != null, perms != null };
    }
    
    public boolean isVaultHooked(){
    	return status[0];
    }
    
    public boolean isEconHooked(){
    	return status[1];
    }
    
    public boolean isChatHooked(){
    	return status[2];
    }
    
    public boolean isPermsHooked(){
    	return status[3];
    }
}