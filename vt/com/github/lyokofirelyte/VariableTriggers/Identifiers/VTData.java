package com.github.lyokofirelyte.VariableTriggers.Identifiers;

public enum VTData {

	FILE_PATH("FILE_PATH"),
	SCRIPTS("SCRIPTS"),
	ENABLE("ENABLE"),
	DISABLE("DISABLE"),
	COOLED_SCRIPTS("COOLED_SCRIPTS"),
	TOGGLED_BLOCKS("TOGGLED_BLOCKS"),
	PLACEHOLDERS("PLACEHOLDERS"),
	AREAS("AREAS"),
	CLICKS("CLICKS");
	
	VTData(String name){
		this.name = name;
	}
	
	String name;
	
	public String s(){
		return name;
	}
}