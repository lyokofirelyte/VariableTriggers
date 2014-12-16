package com.github.lyokofirelyte.VariableTriggers.Identifiers;

public enum VTConfig {

	DEBUG("DEBUG", false),
	ADVANCED_MODE("ADVANCED_MODE", false),
	FIRST_RUN("FIRST_RUN", false),
	MYSQL_DATABASE("MYSQL.DATABASE", "db"),
	MYSQL_USERNAME("MYSQL.USERNAME", "admin"),
	MYSQL_PASSWORD("MYSQL.PASSWORD", "drowssap"),
	MYSQL_IP("MYSQL.IP", "localhost"),
	MYSQL_PORT("MYSQL.PORT", "3306"),
	ASYNC_WARNING("ASYNC_WARNING", false),
	MOTD("MOTD", "none"),
	AUTOSAVE("AUTOSAVE", true),
	TIMER_INTERVAL("TIMER_INTERVAL", 60);
	
	VTConfig(String type, Object def){
		this.type = type;
		this.def = def;
	}
	
	String type;
	Object def;
	
	public String s(){
		return type;
	}
	
	public Object d(){
		return def;
	}
}