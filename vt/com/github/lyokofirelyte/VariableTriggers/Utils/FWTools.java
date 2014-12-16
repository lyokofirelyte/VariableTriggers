package com.github.lyokofirelyte.VariableTriggers.Utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

// credit to codename_B for nms

public class FWTools {

    private Method world_getHandle = null;
    private Method nms_world_broadcastEntityEffect = null;
    private Method firework_getHandle = null;
	private Map<String, Color> colors = new HashMap<String, Color>();
	private Map<String, Type> types = new HashMap<String, Type>();
    
    public void playFirework(World world, Location loc, FireworkEffect fe) throws Exception {
    	
            Firework fw = (Firework) world.spawn(loc, Firework.class);
            Object nms_world = null;
            Object nms_firework = null;

            if(world_getHandle == null) {
              	world_getHandle = getMethod(world.getClass(), "getHandle");
                firework_getHandle = getMethod(fw.getClass(), "getHandle");
            }

            nms_world = world_getHandle.invoke(world, (Object[]) null);
            nms_firework = firework_getHandle.invoke(fw, (Object[]) null);

            if(nms_world_broadcastEntityEffect == null) {
            	nms_world_broadcastEntityEffect = getMethod(nms_world.getClass(), "broadcastEntityEffect");
            }

            FireworkMeta data = (FireworkMeta) fw.getFireworkMeta();
            data.clearEffects();
            data.setPower(1);
            data.addEffect(fe);
            data.addEnchant(Enchantment.DURABILITY, 10, false);
            fw.setFireworkMeta(data);
            nms_world_broadcastEntityEffect.invoke(nms_world, new Object[] {nms_firework, (byte) 17});
            fw.remove();
    }

    private static Method getMethod(Class<?> cl, String method) {
    	for(Method m : cl.getMethods()) {
    		if(m.getName().equals(method)) {
    			return m;
    		}
    	}
    	return null;
    }
    
    public FWTools init(){
		colors.put("red", Color.RED);
		colors.put("blue", Color.BLUE);
		colors.put("aqua", Color.AQUA);
		colors.put("black", Color.BLACK);
		colors.put("fuchsia", Color.FUCHSIA);
		colors.put("gray", Color.GRAY);
		colors.put("green", Color.GREEN);
		colors.put("lime", Color.LIME);
		colors.put("maroon", Color.MAROON);
		colors.put("navy", Color.NAVY);
		colors.put("olive", Color.OLIVE);
		colors.put("orange", Color.ORANGE);
		colors.put("purple", Color.PURPLE);
		colors.put("silver", Color.SILVER);
		colors.put("teal", Color.TEAL);
		colors.put("white", Color.WHITE);
		colors.put("yellow", Color.YELLOW);
		types.put("ball", Type.BALL);
		types.put("ball_large", Type.BALL_LARGE);
		types.put("burst", Type.BURST);
		types.put("creeper", Type.CREEPER);
		types.put("star", Type.STAR);
		return this;
    }
    
    public Color getRandomColor(){
    	
    	Random rand = new Random();
    	int pick = rand.nextInt(colors.keySet().size()-1);
    	
    	try {
    		List<Color> colorPicks = new ArrayList<Color>();
    		for (Color c : colors.values()){
    			colorPicks.add(c);
    		}
    		return colorPicks.get(pick);
    	} catch (Exception e){
    		return Color.WHITE;
    	}
    }
    
    public Type getRandomType(){
    	
    	Random rand = new Random();
    	int pick = rand.nextInt(types.keySet().size()-1);
    	
    	try {
    		List<Type> typePicks = new ArrayList<Type>();
    		for (Type c : types.values()){
    			typePicks.add(c);
    		}
    		return typePicks.get(pick);
    	} catch (Exception e){
    		return Type.BURST;
    	}
    }
    
    public Map<String, Color> getColors(){
    	return colors;
    }
    
    public Map<String, Type> getTypes(){
    	return types;
    }
}