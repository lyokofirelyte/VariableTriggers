package com.github.lyokofirelyte.VariableTriggers.Identifiers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface VTCommand {
	public String[] aliases();
	public String name() default "none";
	public String desc() default "A VT Command";
	public String help() default "/vt ?";
	public String perm() default "vtriggers.use";
	public boolean player() default false;
	public int max() default 9999;
	public int min() default 0;
}