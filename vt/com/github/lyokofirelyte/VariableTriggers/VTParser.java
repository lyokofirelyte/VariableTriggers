package com.github.lyokofirelyte.VariableTriggers;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Lever;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.github.lyokofirelyte.VariableTriggers.Identifiers.VTConfig;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.VTData;
import com.github.lyokofirelyte.VariableTriggers.Identifiers.VTMap;
import com.github.lyokofirelyte.VariableTriggers.Utils.VTUtils;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import net.milkbowl.vault.economy.EconomyResponse;

public class VTParser {

	private VariableTriggers main;
	
	private String fileName;
	private String scriptName;
	private String sender;
	private Location triggerLoc;
	
	private Player p;
	private List<String> script;
	private HashMap<String, String> customPlaceHolders;
	private boolean async = true;
	private boolean prevSucc = true;
	private int ifLevel = 0;
	private int whileLevel = 0;
	private int line = 0;
	
	public VTParser(VariableTriggers i){
		main = i;
	}
	
	public VTParser(VariableTriggers i, String fileName, String scriptName, List<String> script, Location triggerLoc, HashMap<String, String> customPlaceHolders, String sender){
		main = i;
		this.fileName = fileName;
		this.scriptName = scriptName;
		this.script = script;
		this.customPlaceHolders = customPlaceHolders;
		this.sender = sender;
		this.triggerLoc = triggerLoc;
		p = Bukkit.getPlayer(sender) != null ? Bukkit.getPlayer(sender) : null;
		for (String garbleGarble : script){
			garbleGarble = garbleGarble.trim();
		}
	}
	
	public void start(){
					
		if (main.vars.containsKey("@COOLDOWN " + scriptName)){
			if (main.vars.getLong("@COOLDOWN " + scriptName) > System.currentTimeMillis()){
				return;
			}
			main.vars.remove("@COOLDOWN " + scriptName);
		}
		
		for (int i = 0; i < script.size(); i++){
			if (line >= script.size()){
				return;
			}
			try {
				parse();
			} catch (Exception e){
				e.printStackTrace();
				main.logger.warning("Failed to parse the entire line @ " + scriptName + " (" + line + ")");
				line++;
			}
		}
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	public void parse(){
		
		String currentLine = parseHolders(script.get(line));
		currentLine = parseVars(currentLine);
		currentLine = parseFunctionalHolders(currentLine);
		final String[] args = currentLine.split(" ");
		
		if (!currentLine.startsWith("\\//")){

			switch (args[0]){
			
				case "@DEBUG": case "@!":
					
					VTUtils.s(p, "You're running VTV2. Type /vt debug to enable console messages in the event that something fails.");
					VTUtils.s(p, "Lyoko_Firelyte thanks you for using this plugin! :D");
					
				break;
				
				case "@QUIET":

					main.debug("@QUIET has been deprecated! (It never really worked anyway)", scriptName, line, fileName);
					
				break;
				
				case "@CLEARCHAT":
					
					if (args.length >= 1){
						
						Player sendTo = Bukkit.getPlayer(args[1]);
						
						if (sendTo != null){
							for (int i = 0; i < 100; i++){
								sendTo.sendMessage("");
							}
						}
						
					} else {
						for (int i = 0; i < 100; i++){
							p.sendMessage("");
						}
					}
					
				break;
				
				// BEGIN https://dev.bukkit.org/profiles/soliddanii/ CODE
				
					case "@ITEMFRAMESET":
		                 
		                    Block b3 = getLocFromString(args[2]).getBlock();
		                    int type0 = Integer.parseInt(args[1].split("\\:")[0]);
		                    Byte id0 = Byte.parseByte(args[1].split("\\:")[1]);
		                    short damage0 = 0;
	
		                    for (Entity entity : b3.getWorld().getNearbyEntities(b3.getLocation(), 2, 2, 2)) {
		                        if (entity instanceof ItemFrame) {
		                            ItemFrame itemFrame = (ItemFrame) entity;
		                            itemFrame.setItem(new ItemStack(type0, 1, damage0, id0));
		                        }
		                    }
		                    
	                break;
	                
					case "@ITEMFRAMEROTATE":
		                 
		                    Block b4 = getLocFromString(args[2]).getBlock();
	
		                    for (Entity entity : b4.getWorld().getNearbyEntities(b4.getLocation(), 2, 2, 2)) {
		                        if (entity instanceof ItemFrame) {
		                            ItemFrame itemFrame = (ItemFrame) entity;
		                            itemFrame.setRotation(Rotation.valueOf(args[1]));
		                        }
		                    }
		                    
	                break;
	                
					case "@TOGGLELEVER":
	                    
						//Get the lever block
                        Block b0 = getLocFromString(args[1]).getBlock();

                        if (b0.getType() == Material.LEVER) {

                            //Get lever state and Material Data
                            BlockState b0S = b0.getState();
                            MaterialData b0M = b0S.getData();

                            //Get lever, change power and update state
                            Lever lever = (Lever) b0M;
                            lever.setPowered(!lever.isPowered());
                            b0S.setData(lever);
                            b0S.update(true);

                            //Update Redstone Circuit:
                            //Get the block that holds the lever, change to air and revert
                            Block b1 = b0.getRelative(lever.getAttachedFace());
                            BlockState initialSupportState = b1.getState();
                            BlockState supportState = b1.getState();
                            supportState.setType(Material.AIR);
                            supportState.update(true, false);
                            initialSupportState.update(true);

                        }
	                    
	                break;
	                
	            // END https://dev.bukkit.org/profiles/soliddanii/ CODE
				
				case "@BROADCAST":
					
					Bukkit.broadcastMessage(VTUtils.AS(VTUtils.createString(args, 1)));
					
				break;
				
				case "@PLAYER":
					
					if (args.length >= 1){
						p.sendMessage(VTUtils.AS(VTUtils.createString(args, 1)));
					}
					
				break;
					
				case "@TELL":
					
					if (args.length >= 2){
						
						if (Bukkit.getPlayer(args[1]) != null){
							Bukkit.getPlayer(args[1]).sendMessage(VTUtils.createString(args, 2));
						} else {
							main.debug("The player " + args[1] + " is not online!", scriptName, line, fileName);
						}
						
					} else {
						main.debug("@TELL requires more than 1 argument!", scriptName, line, fileName);
					}
					
				break;
				
				case "@SCOREBOARD":
					
					//TODO
					
				break;
				
				case "@MODIFYINV":
					
					//TODO
					
				break;
				
				case "@PRINT":
					
					main.logger.calmInfo(VTUtils.createString(args, 1));
					
				break;
				
				case "@CALL":
					
					List<String> scripts = new ArrayList<String>();
					int fails = 0;
					
					if (args[1].startsWith("this:")){
						
						for (String name : Arrays.asList("Player", "Entity", "System")){
							try {
								scripts = new ArrayList<String>(((VTMap<Object, Object>) main.getInstance(Class.forName("com.github.lyokofirelyte.VariableTriggers.Events.Listeners." + name + "." + fileName.replace(".yml", "")))).getList(args[1].split("\\:")[1] + ".Script"));
								break;
							} catch (Exception e3){
								fails++;
							}
						}
					
						if (fails >= 3){
							main.debug("Could not find script!", scriptName, line, fileName);
							return;
						}
						
					} else {
						scripts = new ArrayList<String>(main.vars.getScripts().get(args[1].split("\\:")[0] + "_" + args[1].split("\\:")[1]));
					}
					
					try {
						new VTParser(main, args[1].split("\\:")[0], args[1].split("\\:")[1], scripts, p != null ? p.getLocation() : new Location(Bukkit.getWorlds().get(0), 0, 0, 0), customPlaceHolders, sender).start();
					} catch (Exception e){
						main.debug("Error calling script!", scriptName, line, fileName);
					}
					
				break;
				
				case "@SETMOTD":
					
					main.settings.set(VTConfig.MOTD, VTUtils.createString(args, 1));
					
				break;
				
				case "@EXIT":
					
					line = Integer.MAX_VALUE;
					
				return;
					
				case "@PAUSE":
					
					if (async){
						try {
							Thread.sleep(Integer.parseInt(args[1])*1000L);
						} catch (Exception e){
							main.debug("@PAUSE - thread locking failed. WE HAVE NO IDEA WHY.", scriptName, line, fileName);
						}
					} else {
						main.debug("@PAUSE - can't lock a sync Bukkit thread!", scriptName, line, fileName);
					}
					
				break;
				
				case "@COOLDOWN":
					
					main.vars.set("@COOLDOWN " + scriptName, new Long(System.currentTimeMillis() + (Long.parseLong(args[1])*1000L)));
					
				break;
				
				case "@SENDTOSERVER":
					
					if (Bukkit.getOnlinePlayers().size() > 0 && args.length == 3){
						try {
							ByteArrayDataOutput out = ByteStreams.newDataOutput();
							out.writeUTF("ConnectOther");
							out.writeUTF(args[1]);
							out.writeUTF(args[2]);
							Iterables.getFirst(Bukkit.getOnlinePlayers(), null).sendPluginMessage(main, "BungeeCord", out.toByteArray());
						} catch (Exception e){
							e.printStackTrace();
						}
					}
					
				break;

				case "@CMD":
					
					p.performCommand(VTUtils.createString(args, 1));
					
				break;
				
				case "@CMDOP":
					
					boolean removeOp = new Boolean(p.isOp());
					
					p.setOp(true);
					p.performCommand(VTUtils.createString(args, 1));
					p.setOp(removeOp);
					
				break;
				
				case "@CMDCON":
					
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), VTUtils.createString(args, 1));
					
				break;
				
				case "@SETBLOCK": case "@SETBLOCKSAFE":

					getLocFromString(args[2]).getBlock().setTypeIdAndData(Integer.parseInt(args[1].split("\\:")[0]), Byte.parseByte(args[1].split("\\:")[1]), true);
					
				break;
				
				case "@TOGGLEBLOCK":
					
					Block b = getLocFromString(args[2]).getBlock();
					
					if (main.vars.getBool("@TOGGLEBLOCK " + args[2])){
						b.setType(Material.AIR);
					} else {
						b.setTypeIdAndData(Integer.parseInt(args[1].split("\\:")[0]), Byte.parseByte(args[1].split("\\:")[1]), true);
					}
					
					main.vars.set("@TOGGLEBLOCK " + args[2], !main.vars.getBool("@TOGGLEBLOCK " + args[2]));
					
				break;
				
				case "@DROPITEM":
					
					try {
					
						ItemStack i = new ItemStack(Material.valueOf(args[1].toUpperCase()), Integer.parseInt(args[2]));
						ItemMeta im = i.getItemMeta();
						
						if (!args[3].equalsIgnoreCase("none")){
							String[] enchs = args[3].split("\\,");
							for (String en : enchs){
								for (Enchantment e : Enchantment.values()){
									if (e.getName().equalsIgnoreCase(en.split("\\:")[0])){
										im.addEnchant(e, Integer.parseInt(en.split("\\:")[1]), true);
										break;
									}
								}
							}
						}
						
						i.setItemMeta(im);
						triggerLoc.getWorld().dropItem(getLocFromString(args[4]), i);
						
					} catch (Exception e){
						main.debug("@DROPITEM - invalid args!", scriptName, line, fileName);
					}
					
				break;
				
				case "@SIGNTEXT":
					
					b = getLocFromString(args[1]).getBlock();
					
					if (b.getType().name().toLowerCase().contains("sign")){
						Sign sign = (Sign) b.getState();
						sign.setLine(Integer.parseInt(args[2]), VTUtils.createString(args, 3));
						sign.update();
					} else {
						main.debug("@SIGNTEXT - that's not a sign!", scriptName, line, fileName);
					}
					
				break;
				
				case "@FALLINGBLOCK":
					
					try {
						triggerLoc.getWorld().spawnFallingBlock(getLocFromString(args[2]), Material.valueOf(args[1].toUpperCase()), (byte) 0);
					} catch (Exception e){
						main.debug("@FALLINGBLOCK - invalid args!", scriptName, line, fileName);
					}
					
				break;
				
				case "@ENTITY":
					
					try {
						
						for (int amt = 0; amt < Integer.parseInt(args[2]); amt++){
							triggerLoc.getWorld().spawnEntity(getLocFromString(args[3]), EntityType.valueOf(args[1]));
						}
						
					} catch (Exception e){
						main.debug("@ENTITY - invalid args!", scriptName, line, fileName);
					}
				
				break;
				
				case "@TP":
					
					p.teleport(getLocFromString(args[1]));
					
				break;
				
				case "@WORLDTP":
					
					if (Bukkit.getPlayer(args[1]) != null && Bukkit.getWorld(args[2]) != null){
						Location send = Bukkit.getPlayer(args[1]).getLocation();
						Bukkit.getPlayer(args[1]).teleport(new Location(Bukkit.getWorld(args[2]), send.getX(), send.getY(), send.getZ(), send.getYaw(), send.getPitch()));
					} else {
						main.debug("@WORLDTP - invalid args!", scriptName, line, fileName);
					}
					
				break;
				
				case "@OPENINV":
					
					File invFile = new File("./plugins/VariableTriggers/inventories/" + args[1] + ".yml");
					
					if (invFile.exists()){
						
						if (args.length == 3){
							getPlayerFromArg(args, 2).openInventory(VTUtils.parseNewInventory(YamlConfiguration.loadConfiguration(invFile)));
						} else {
							p.openInventory(VTUtils.parseNewInventory(YamlConfiguration.loadConfiguration(invFile)));
						}
						
					} else {
						main.debug("Inventory file " + args[1] + " was not found.", scriptName, line, fileName);
					}
					
				break;
				
				case "@CLOSEINV":
					
					getPlayerFromArg(args, 1).closeInventory();
					
				break;
				
				case "@MODIFYPLAYER":
					
					Player toModify = getPlayerFromArg(args, 1);
					
					switch (args[2]){
					
						case "HEALTH":
							
							try {
								toModify.setHealth(Double.parseDouble(args[3]));
							} catch (Exception e){
								main.debug("@MODIFYPLAYER - invalid health!", scriptName, line, fileName);
							}
							
						break;
						
						case "FOOD":
							
							try {
								toModify.setFoodLevel(Integer.parseInt(args[3]));
							} catch (Exception e){
								main.debug("@MODIFYPLAYER - invalid food level!", scriptName, line, fileName);
							}
							
						break;
						
						case "SATURATION":
							
							try {
								toModify.setSaturation(Float.parseFloat(args[3]));
							} catch (Exception e){
								main.debug("@MODIFYPLAYER - invalid saturation level!", scriptName, line, fileName);
							}
							
						break;
						
						case "EXP": case "XP":
							
							try {
								toModify.setLevel(0);
								toModify.giveExp(Integer.parseInt(args[3]));
							} catch (Exception e){
								main.debug("@MODIFYPLAYER - invalid xp level!", scriptName, line, fileName);
							}
							
						break;
						
						case "WALKSPEED":
							
							try {
								toModify.setWalkSpeed(Float.parseFloat(args[3]));
							} catch (Exception e){
								main.debug("@MODIFYPLAYER - invalid walk speed!", scriptName, line, fileName);
							}
							
						break;
						
						case "FLYSPEED":
							
							try {
								toModify.setFlySpeed(Float.parseFloat(args[3]));
							} catch (Exception e){
								main.debug("@MODIFYPLAYER - invalid fly speed!", scriptName, line, fileName);
							}
							
						break;
						
						case "DISPLAYNAME":
							
							try {
								toModify.setDisplayName(main.AS(args[3].replaceAll("__", " ")));
							} catch (Exception e){
								main.debug("@MODIFYPLAYER - invalid display name length!", scriptName, line, fileName);
							}
							
						break;
						
						case "LISTNAME":
							
							try {
								toModify.setPlayerListName(main.AS(args[3].replaceAll("__", " ")));
							} catch (Exception e){
								main.debug("@MODIFYPLAYER - invalid list name length!", scriptName, line, fileName);
							}
							
						break;
						
						case "FLYING":
							
							try {
								
								boolean fly = Boolean.valueOf(args[3]);
								
								if (fly){
									toModify.setAllowFlight(true);
									toModify.setFlying(true);
								} else {
									toModify.setAllowFlight(false);
									toModify.setFlying(false);
								}
	
							} catch (Exception e){
								main.debug("@MODIFYPLAYER - flying is true or false!", scriptName, line, fileName);
							}
							
						break;
						
						case "GAMEMODE":
							
							try {
								toModify.setGameMode(org.bukkit.GameMode.valueOf(args[3].toUpperCase()));
							} catch (Exception e){
								main.debug("@MODIFYPLAYER - invalid game mode!", scriptName, line, fileName);
							}
							
						break;
						
						case "MAXHEALTH":
							
							try {
								toModify.setMaxHealth(Double.parseDouble(args[3]));
							} catch (Exception e){
								main.debug("@MODIFYPLAYER - invalid health!", scriptName, line, fileName);
							}
							
						break;
						
						case "HELDITEM":
							
							try {
								toModify.setItemInHand(new ItemStack(Material.valueOf(args[3].toUpperCase()), 1));
							} catch (Exception e){
								main.debug("@MODIFYPLAYER - invalid material!", scriptName, line, fileName);
							}
							
						break;
						
						case "HELDITEM:MATERIAL":
							
							try {
								toModify.getItemInHand().setType(Material.valueOf(args[3].toUpperCase()));
							} catch (Exception e){
								main.debug("@MODIFYPLAYER - invalid material!", scriptName, line, fileName);
							}
							
						break;
						
						case "HELDITEM:ID":
							
							try {
								toModify.getItemInHand().setTypeId(Integer.parseInt(args[3]));
							} catch (Exception e){
								main.debug("@MODIFYPLAYER - invalid id!", scriptName, line, fileName);
							}
							
						break;
						
						case "HELDITEM:META":
							
							try {
								ItemMeta im = toModify.getItemInHand().getItemMeta();
								ItemStack i = new ItemStack(toModify.getItemInHand().getType(), toModify.getItemInHand().getAmount(), Byte.parseByte(args[3]));
								i.setItemMeta(im);
								toModify.setItemInHand(i);
							} catch (Exception e){
								main.debug("@MODIFYPLAYER - invalid meta!", scriptName, line, fileName);
							}
							
						break;
						
						case "HELDITEM:AMOUNT":
							
							try {
								if (Integer.parseInt(args[3]) == 0){
									toModify.setItemInHand(new ItemStack(Material.AIR));
								} else {
									toModify.getItemInHand().setAmount(Integer.parseInt(args[3]));
								}
							} catch (Exception e){
								main.debug("@MODIFYPLAYER - invalid number!", scriptName, line, fileName);
							}
							
						break;
						
						case "HELDITEM:ENCHANT":
							
							try {
								for (Enchantment e : Enchantment.values()){
									if (e.getName().equalsIgnoreCase(args[3])){
										toModify.getItemInHand().addUnsafeEnchantment(e, 1);
									}
								}
							} catch (Exception e){
								main.debug("@MODIFYPLAYER - invalid enchant type!", scriptName, line, fileName);
							}
							
						break;
						
						case "HELDITEM:DISPLAYNAME":
							
							try {
								ItemStack i = toModify.getItemInHand();
								ItemMeta im = i.getItemMeta();
								im.setDisplayName(main.AS(VTUtils.createString(args, 3)));
								i.setItemMeta(im);
								toModify.setItemInHand(i);
							} catch (Exception e){
								main.debug("@MODIFYPLAYER - display name failed somehow!", scriptName, line, fileName);
							}
							
						break;
						
						case "HELDITEM:LORE:SET":
							
							try {
								ItemStack i = toModify.getItemInHand();
								ItemMeta im = i.getItemMeta();
								im.setLore(Arrays.asList(main.AS(args[3].replaceAll("__",  " "))));
								i.setItemMeta(im);
								toModify.setItemInHand(i);
							} catch (Exception e){
								main.debug("@MODIFYPLAYER - invalid lore!", scriptName, line, fileName);
							}
							
						break;
						
						case "HELDITEM:LORE:ADD":
							
							try {
								ItemStack i = toModify.getItemInHand();
								ItemMeta im = i.getItemMeta();
								im.getLore().add(main.AS(args[3]));
								i.setItemMeta(im);
								toModify.setItemInHand(i);
							} catch (Exception e){
								main.debug("@MODIFYPLAYER - no lore found to add to!", scriptName, line, fileName);
							}
							
						break;
						
						case "HELDITEM:LORE:REMOVE":
							
							try {
								ItemStack i = toModify.getItemInHand();
								ItemMeta im = i.getItemMeta();
								im.setLore(new ArrayList<String>());
								i.setItemMeta(im);
								toModify.setItemInHand(i);
							} catch (Exception e){
								main.debug("@MODIFYPLAYER - no lore found to remove!", scriptName, line, fileName);
							}
							
						break;
						
						case "HIDDEN":
							
							try {
								
								boolean hide = Boolean.valueOf(args[3]);
								
								if (hide){
									for (Player pp : Bukkit.getOnlinePlayers()){
										pp.hidePlayer(toModify);
									}
								} else {
									for (Player pp : Bukkit.getOnlinePlayers()){
										pp.showPlayer(toModify);
									}
								}
								
							} catch (Exception e){
								main.debug("@MODIFYPLAYER - HIDDEN is true or false!", scriptName, line, fileName);
							}
							
						break;
						
						case "BANNED":
							
							if (main.settings.getBool(VTConfig.ADVANCED_MODE)){
								
								try {
									if (Boolean.valueOf(args[3])) {
                                		Bukkit.getBanList(org.bukkit.BanList.Type.NAME).addBan(toModify.getName(), "Banned by VT Operator", null, "Console");
                                	} else {
                                		Bukkit.getBanList(org.bukkit.BanList.Type.NAME).pardon(toModify.getName());
                                	}
									main.logger.safeWarning(args[2] + " changed banned status via a script!");
								} catch (Exception e){
									main.debug("@MODIFYPLAYER - BANNED is true or false!", scriptName, line, fileName);
								}
								
							} else {
								main.debug("@BANNED - advanced mode is required!", currentLine, line, fileName);
							}
							
						break;
						
						case "OPERATOR":
							
							if (main.settings.getBool(VTConfig.ADVANCED_MODE)){
							
								try {
									toModify.setOp(Boolean.valueOf(args[3]));
									main.logger.safeWarning(args[2] + " changed OP status via a script!");
								} catch (Exception e){
									main.debug("@MODIFYPLAYER - OPERATOR is true or false!", scriptName, line, fileName);
								}
								
							} else {
								main.debug("@OPERATOR - advanced mode is required!", currentLine, line, fileName);
							}
							
						break;
						
						case "DAMAGE":
							
							try {
								toModify.damage(Double.parseDouble(args[3]));
							} catch (Exception e){
								main.debug("@MODIFYPLAYER - DAMAGE format is @MODIFYPLAYER <player> DAMAGE <amount as an number>", scriptName, line, fileName);
							}
							
						break;
						
						case "PLAYERTIME":
							
							try {
								toModify.setPlayerTime(Long.parseLong(args[3]), false);
							} catch (Exception e){
								main.debug("@MODIFYPLAYER - PLAYERTIME format is @MODIFYPLAYER <player> PLAYERTIME <number>", scriptName, line, fileName);
							}
							
						break;
						
						
					}
					
				break;
				
				case "@FIREWORK":
					
					if (args.length == 4){
						if (main.fw.getColors().containsKey(args[1]) || args[1].equalsIgnoreCase("random")){
							if (main.fw.getTypes().containsKey(args[2]) || args[2].equalsIgnoreCase("random")){	
								
								Type type;
								Color color;
								Location location = getLocFromString(args[3]);
											
								if (args[1].equalsIgnoreCase("random")){
									color = main.fw.getRandomColor();
								} else {
									color = main.fw.getColors().get(args[1]);
								}
											
								if (args[2].equalsIgnoreCase("random")){
									type = main.fw.getRandomType();
								} else {
									type = main.fw.getTypes().get(args[2]);
								}
											
								try {
									main.fw.playFirework(location.getWorld(), location, FireworkEffect.builder().with(type).withColor(color).build());
								} catch (Exception e) {}
							
							} else {
								main.debug("@FIREWORK - invalid type!", scriptName, line, fileName);
							}
							
						} else {
							main.debug("@FIREWORK - invalid color!", scriptName, line, fileName);
						}
						
					} else {
						main.debug("@FIREWORK - invalid args!", scriptName, line, fileName);
					}
					
				break;
				
				case "@SMOKE":
					
					try {
						triggerLoc.getWorld().playEffect(getLocFromString(args[2]), Effect.SMOKE, Float.parseFloat(args[1]));
					} catch (Exception e){
						main.debug("@POOF - Should be @FLAMES <volume as a number> <location>", scriptName, line, fileName);
					}
					
				break;
				
				case "@PARTICLE":
					
					triggerLoc.getWorld().playEffect(getLocFromString(args[2]), Effect.STEP_SOUND, Material.valueOf(args[1].toUpperCase()).getId());
					
				break;
				
				case "@SOUND":
					
					triggerLoc.getWorld().playSound(getLocFromString(args[2]), Sound.valueOf(args[1].toUpperCase()), 5F, 5F);
					
				break;
				
				case "@SOUNDEX":
					
					triggerLoc.getWorld().playSound(getLocFromString(args[4]), Sound.valueOf(args[1].toUpperCase()), Float.parseFloat(args[2]), Float.parseFloat(args[3]));
					
				break;
				
				case "@SETVELOCITY":
					
					if (args.length >= 5){
						getPlayerFromArg(args, 1).setVelocity(new Vector(Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4])));
					} else {
						main.debug("@SETVELOCITY - Should be @SETVELOCITY <player> <x> <y> <z>", currentLine, line, fileName);
					}
					
				break;
				
				case "@FLAMES":
					
					try {
						triggerLoc.getWorld().playEffect(getLocFromString(args[2]), Effect.MOBSPAWNER_FLAMES, Float.parseFloat(args[1]));
					} catch (Exception e){
						main.debug("@FLAMES - Should be @FLAMES <volume as a number> <location>", scriptName, line, fileName);
					}
					
				break;
				
				case "@POOF":
					
					try {
						triggerLoc.getWorld().playEffect(getLocFromString(args[2]), Effect.EXTINGUISH, Float.parseFloat(args[1]));
					} catch (Exception e){
						main.debug("@POOF - Should be @FLAMES <volume as a number> <location>", scriptName, line, fileName);
					}
					
				break;
				
				case "@LIGHTNING":
					
					boolean damage = VTUtils.isBoolean(args[1]) ? Boolean.valueOf(args[1]) : false;
					
					if (damage){
						triggerLoc.getWorld().strikeLightning(getLocFromString(args[2]));
					} else {
						triggerLoc.getWorld().strikeLightningEffect(getLocFromString(args[2]));
					}
					
				break;
				
				case "@EXPLOSION":
					
					triggerLoc.getWorld().createExplosion(getLocFromString(args[2]), Float.parseFloat(args[1]));
					
				break;
				
				case "@GETBLOCK":

					main.vars.set(args[1].replaceFirst("\\$", ""), getLocFromString(args[2]).getBlock().getTypeId() + ":" + getLocFromString(args[2]).getBlock().getData());
					
				break;
				
				case "@GETENTITYCOUNT":
					
					int amt = 0;
					double rad = Double.parseDouble(args[3]);
					
					for (Entity e : p.getNearbyEntities(rad, rad, rad)){
						if (e.getType().getName().equalsIgnoreCase(args[2])){
							amt++;
						}
					}
					
					main.vars.set(args[1].replaceFirst("\\$", ""), amt);
					
				break;
				
				case "@GETLIGHT":
					
					Location loc = getLocFromString(args[2]);
					main.vars.set(args[1].replaceFirst("\\$", ""), loc.getBlock().getLightLevel());
					
				break;
				
				case "@SETCANCELLED":
					
					try {
						VTMap map = (VTMap) main.getInstance(Class.forName(args[1]));
						map.set("Cancelled", Boolean.parseBoolean(args[2]));
					} catch (Exception e){
						main.debug("Could not find the event class " + args[1] + "!", scriptName, line, fileName);
					}
					
				break;
				
				case "@SET": case "@SETINT": case "@SETSTR": case "@SETBOOL":

					main.vars.set(args[1].replaceFirst("\\$", ""), VTUtils.createString(args, 2));
					
				break;
				
				case "@ADDINT": case "@SUBINT":

					int result = 0;
					
					try {
						result = main.vars.getInt(args[1].replaceFirst("\\$", ""));
						main.vars.set(args[1].replaceFirst("\\$", ""), new Integer(result + Integer.parseInt(args[2])*(args[0].equals("@ADDINT") ? 1 : -1)));
					} catch (Exception e){
						main.debug("@ADDINT / @SUBINT - Invalid number parsing!", scriptName, line, fileName);
					}
					
				break;
				
				case "@MULTINT": case "@DIVINT":

					try {
						result = main.vars.getInt(args[1].replaceFirst("\\$", ""));
						result = args[0].equals("@MULTINT") ? result*Integer.parseInt(args[2]) : result/Integer.parseInt(args[2]);
						main.vars.set(args[1].replaceFirst("\\$", ""), result);
					} catch (Exception e){
						main.debug("@MULTINT / @DIVINT - Invalid number parsing!", scriptName, line, fileName);
					}
					
				break;
				
				case "@STRBUILD":
					
					main.vars.set(args[1].replaceFirst("\\$", ""), VTUtils.createString(args, new Integer(Integer.parseInt(args[2]) + 3)));
					
				break;
				
				case "@ADDSTR":
					
					String res = main.vars.getStr(args[1].replaceFirst("\\$", ""));
					res += VTUtils.createString(args, 2);
					main.vars.set(args[1].replaceFirst("\\$", ""), res);
					
				break;
				
				case "@GETSTRLEN":
					
					main.vars.set(args[1].replaceFirst("\\$", ""), args[2].length());
					
				break;
				
				case "@DELVAR": case "@DELOJB":
					
					main.vars.remove(args[1].replaceFirst("\\$", ""));
					
				break;
				
				case "@ADDLIST":
					
					if (!main.vars.containsKey(args[1].replaceFirst("\\@", ""))){
						main.vars.set(args[1].replaceFirst("\\@", ""), new ArrayList<String>(Arrays.asList(VTUtils.createString(args, 2))));
					} else {
						List<String> list = main.vars.getList(args[1].replaceFirst("\\@", ""));
						list.add(VTUtils.createString(args, 2));
						main.vars.set(args[1].replaceFirst("\\@", ""), list);
					}
					
				break;
				
				case "@REMLIST":
					
					if (main.vars.containsKey(args[1].replaceFirst("@", ""))){
						List<String> list = main.vars.getList(args[1].replaceFirst("@", ""));
						if (list.contains(VTUtils.createString(args, 2))){
							list.remove(VTUtils.createString(args, 2));
							main.vars.set(args[1].replaceFirst("@", ""), list);
						}
					}
					
				break;
				
				case "@DELLIST": case "@DELIST":
					
					if (main.vars.containsKey(args[1].replaceFirst("@", ""))){
						main.vars.remove(args[1].replaceFirst("@", ""));
					}
					
				break;
				
				case "@MYSQL":
					
					String query = VTUtils.createString(args, 2);
					String currentDb = main.settings.getStr(VTConfig.MYSQL_DATABASE);
					String user = main.settings.getStr(VTConfig.MYSQL_USERNAME);
					String pass = main.settings.getStr(VTConfig.MYSQL_PASSWORD);
					String ip = main.settings.getStr(VTConfig.MYSQL_IP);
					String port = main.settings.getStr(VTConfig.MYSQL_PORT);
					
					Connection conn;
					Statement s = null;
					ResultSet rs = null;
					
					try {
						
						Class.forName("com.mysql.jdbc.Driver");
						conn = DriverManager.getConnection("jdbc:mysql://" + ip + ":" + port + "/" + currentDb, user, pass);
						s = conn.createStatement();
						
						try {
							s.executeUpdate(query);
						} catch (Exception e){
							s.executeQuery(query);
						}
						
						rs = s.getResultSet();
						int columns = rs.getMetaData().getColumnCount();
						
						List<String> data = main.vars.getList(args[1].replaceFirst("\\$", ""));
						
						if (data.size() == 0){
							data.add("begin data");
						}

						while (rs.next()){
						    for (int i = 1; i <= columns; i++) {
						        data.add(rs.getString(i));
						    }
						}
						
						main.vars.set(args[1].replaceFirst("\\$", ""), data);
						
					} catch (Exception e1) {
						e1.printStackTrace();
						main.debug("MYSQL - connection failed.", scriptName, line, fileName);
					} finally {
						try {
							s.close();
							rs.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
					
				break;
				
				case "@WHILE":
					
					if (main.settings.getBool(VTConfig.ADVANCED_MODE)){
					
						final List<String> whileNode = new ArrayList<String>();
						int neededWhileLevel = new Integer(whileLevel);
						
						for (int i = (line+1); i < script.size(); i++){
							
							if (script.get(i).startsWith("@ENDWHILE")){
								if (neededWhileLevel < whileLevel){
									whileLevel--;
								} else {
									line = i;
									break;
								}
							} else if (i >= script.size()-1){
								main.debug("@WHILE - no @ENDWHILE in sight! Can't run.", scriptName, line, fileName);
								whileNode.clear();
								return;
							}
							
							whileNode.add(script.get(i));
							
							if (script.get(i).startsWith("@WHILE")){
								whileLevel++;
							}
						}
						
						main.vars.set("WhileTaskWait" + scriptName + line, false);
						
						if (whileNode.size() > 0){
							
							main.vars.set("WhileTaskWait" + scriptName + line, true);
							main.vars.set("WhileCount" + scriptName + line, 0);
							main.vars.set("WhileTask" + scriptName + line, Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable(){ public void run(){
								
								if (eval(args[2], parseVars(args[3]), parseVars(args[1]))){
									new VTParser(main, fileName, scriptName, whileNode, triggerLoc, customPlaceHolders, sender).start();
									main.vars.set("WhileCount" + scriptName + line, new Integer(main.vars.getInt("WhileCount" + scriptName + line) + 1));
									if (main.vars.getInt("WhileCount" + scriptName + line) > 2000){
										main.debug("@WHILE - infinite loop detected, stopping!", scriptName, line, fileName);
										Bukkit.getScheduler().cancelTask(main.vars.getInt("WhileTask" + scriptName + line));
										main.vars.set("WhileTaskWait" + scriptName + line, false);
										return;
									}
								} else {
									Bukkit.getScheduler().cancelTask(main.vars.getInt("WhileTask" + scriptName + line));
									main.vars.set("WhileTaskWait" + scriptName + line, false);
								}
							}}, 0L, 1L));
						}
						
						while (main.vars.getBool("WhileTaskWait" + scriptName + line)){} // oh wow this actually worked
						
					} else {
						main.debug("@WHILE requires advanced mode. /vt advanced.", scriptName, line, fileName);
					}
					
				break;
				
				case "@ELSE":
					
					int neededIfLevel = new Integer(ifLevel);
					
					for (int scan = new Integer(line+1); scan < script.size(); scan++){
						switch (script.get(scan).split(" ")[0]){
						
							case "@IF":
								
								ifLevel++;
								
							break;
						
							case "@ENDIF": 
								
								if (neededIfLevel == ifLevel){
									line = scan;
									return;
								}
									
								ifLevel--;
								
							break;
						}
					}
					
				break;
				
				case "@IF": case "@OR": case "@ELSEIF": case "@AND": case "@SWITCH": case "@CASE":
					
					if (args[0].equals("@AND") && !prevSucc){
						return;
					}
					
					String test = new String(currentLine);
					String toTest = "";
					String testType = "";
					String neededResult = "";
					boolean ok = true;
					
					for (String old : Arrays.asList("@IF i ", "@IF b ", "@IF s ", "@IF si ", "@AND i ", "@AND b ", "@AND s ", "@AND si ", "@OR i ", "@OR b ", "@OR s ", "@OR si ", "@SWITCH i ", "@SWITCH b ", "@SWITCH s ", "@SWITCH si ")){
						if (test.startsWith(old)){
							test = test.replace(old, args[0] + " ");
						}
					}
					
					toTest = test.split(" ")[1];
					if (args[0].equals("@CASE")){
						
						for (int scan = new Integer(line+1); scan < script.size(); scan++){
							if (script.get(scan).startsWith("@ENDSWITCH")){
								line = scan;
								break;
							}
						}
						
					} else if (args[0].equals("@SWITCH")){
						
						ok = false;
						
						for (int scan = new Integer(line+1); scan < script.size(); scan++){
							if (script.get(scan).startsWith("@CASE")){
								for (String cas : script.get(scan).split(" ")[1].split(" ")){
									if (cas.equalsIgnoreCase(toTest)){
										line = scan;
										ok = true;
										break;
									}
								}
							}
						}
						
						if (!ok){
							for (int scan = new Integer(line+1); scan < script.size(); scan++){
								if (script.get(scan).startsWith("@ENDSWITCH")){
									line = scan;
									break;
								}
							}
						}
						
					} else {
						
						testType = test.split(" ")[2];
						neededResult = test.split(" ")[3];
						
						testType = parseVars(testType);
						testType = parseHolders(testType);
						testType = parseFunctionalHolders(testType);
						
						neededResult = parseVars(neededResult);
						neededResult = parseHolders(neededResult);
						neededResult = parseFunctionalHolders(neededResult);
						
						ok = eval(testType, neededResult, toTest);
						prevSucc = ok;
						
						if (!ok){
							
							neededIfLevel = new Integer(ifLevel);
							
							for (int scan = new Integer(line+1); scan < script.size(); scan++){
								switch (script.get(scan).split(" ")[0]){
								
									case "@IF":
										
										ifLevel++;
										
									break;
									
									case "@ELSE":
										
										if (neededIfLevel == ifLevel){
											line = scan;
											return;
										}
										
									break;
								
									case "@ENDIF": 
										
										if (neededIfLevel == ifLevel){
											line = scan;
											return;
										}
											
										ifLevel--;
										
									break;
									
									case "@OR":
										
										if (neededIfLevel == ifLevel){
											line = scan-1;
											return;
										}
										
									break;
								}
							}	
						}
					}
					
				break;
			}
		}
	}
	
	private boolean eval(String testType, String neededResult, String toTest){
		
		boolean ok = false;
		
		switch (testType){
		
			case "==":
				
				ok = toTest.equals(neededResult);
				
			break;
			
			case "=":
				
				ok = toTest.equalsIgnoreCase(neededResult);
				
			break;
			
			case "!=":
				
				ok = toTest.equals(neededResult);
				
			break;
			
			case ">":
				
				if ((isInteger(toTest) || isLong(toTest)) && (isInteger(neededResult) || isLong(neededResult))){
					ok = Long.parseLong(toTest) > Long.parseLong(neededResult);
				} else {
					ok = toTest.length() > neededResult.length();
				}
				
			break;
			
			case "<":
				
				if ((isInteger(toTest) || isLong(toTest)) && (isInteger(neededResult) || isLong(neededResult))){
					ok = Long.parseLong(toTest) < Long.parseLong(neededResult);
				} else {
					ok = toTest.length() < neededResult.length();
				}
				
			break;
			
			case ">=":
				
				if ((isInteger(toTest) || isLong(toTest)) && (isInteger(neededResult) || isLong(neededResult))){
					ok = Long.parseLong(toTest) >= Long.parseLong(neededResult);
				} else {
					ok = toTest.length() >= neededResult.length();
				}
				
			break;
			
			case "<=":
				
				if ((isInteger(toTest) || isLong(toTest)) && (isInteger(neededResult) || isLong(neededResult))){
					ok = Long.parseLong(toTest) <= Long.parseLong(neededResult);
				} else {
					ok = toTest.length() <= neededResult.length();
				}
				
			break;
			
			case "?=": case "=?":
	
				if (toTest.startsWith("@")){
					ok = main.vars.getList(toTest.replaceFirst("@", "")).contains(neededResult);
				} else {
					ok = toTest.contains(neededResult);
				}
				
			break;
		}
		
		return ok;
	}
	
	private boolean isInteger(String test){
		
		try {
			int now = Integer.parseInt(test);
		} catch (Exception e){
			return false;
		}
		
		return true;
	}
	
	private boolean isLong(String test){
		
		try {
			long now = Long.parseLong(test);
		} catch (Exception e){
			return false;
		}
		
		return true;
	}
	
	private Location getLocFromString(String loc){
		
		String[] split = loc.contains(",") ? loc.split(",") : loc.split(" ");
		
		if (split.length == 4){
			return new Location(Bukkit.getWorld(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
		} else if (split.length == 6){
			return new Location(Bukkit.getWorld(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5]));
		} else if (split.length == 3){
			return new Location(p.getWorld(), Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
		} else if (split.length == 5){
			return new Location(p.getWorld(), Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Float.parseFloat(split[3]), Float.parseFloat(split[4]));
		}
		
		return null;
	}
	
	private Player getPlayerFromArg(String[] args, int arg){
		
		if (args.length > arg){
			
			if (Bukkit.getPlayer(args[arg]) != null){
				return Bukkit.getPlayer(args[arg]);
			}
			
			main.debug("The player " + args[arg] + " could not be found!", scriptName, line, fileName);
			
		} else {
			main.debug("Could not locate player from the script!", scriptName, line, fileName);
		}
		
		return null;
	}
	
	public String parseVars(String arg){

		String save = new String(arg.split(" ")[0]);
		List<String> args = new ArrayList<String>();
		
		if (Arrays.asList("@SET", "@SETSTR", "@SETINT", "@SETBOOL", "@WHILE", "@ADDINT", "@SUBINT", "@MULTINT", "@DIVINT", "@ADDLIST", "@REMLIST", "@DELLIST", "@DELIST").contains(save) || (!arg.contains("$") && !arg.contains("@"))){
			return arg;
		}
		
		String curr = "";
		String type = "";
		boolean start = false;
		
		for (int ch = 0; ch < arg.toCharArray().length; ch++){
			if (arg.toCharArray()[ch] == '$' || (ch > 0 && arg.toCharArray()[ch] == '@')){
				start = true;
				type = arg.toCharArray()[ch] + "";
				if (args.size() > 0){
					arg = args.get(args.size()-1);
					args = new ArrayList<String>();
					curr = "";
				}
			} else if (start){
				curr += arg.toCharArray()[ch];
				if (main.vars.containsKey(curr)){
					if (ch < arg.toCharArray().length-2 && arg.toCharArray()[ch+1] == '['){
						String index = "";
						for (int c = ch+2; c < arg.toCharArray().length; c++){
							if (arg.toCharArray()[c] != ']'){
								index += arg.toCharArray()[c];
							} else {
								args.add(r(arg, type + curr + "[" + index + "]", main.vars.getList(curr).get(Integer.parseInt(index))));
								break;
							}
						}
					} else {
						args.add(r(arg, type + curr, main.vars.getStr(curr)));
					}
				}
			}
		}

		String toReturn = args.size() > 0 ? args.get(args.size()-1) : arg;
		/*try {
			toReturn = PlaceholderAPI.setPlaceholders(p, toReturn);
		} catch (Exception e){}*/
		return toReturn;
	}
	

	private String parseHolders(String arg){
		
		String[] identifiers = new String[] { "[ ]", "< >" };
		
		for (String id : identifiers){
			if (arg.contains(id.split(" ")[0])){
				for (String holder : main.settings.getList(VTData.PLACEHOLDERS)){
					if (arg.contains(id.split(" ")[0] + holder + id.split(" ")[1])){
						arg = arg.replace(id.split(" ")[0] + holder + id.split(" ")[1], getHolder(holder));
					}
				}
			}
		}

		for (String key : customPlaceHolders.keySet()){
			arg = r(arg, key, customPlaceHolders.get(key));
		}
		
		return arg;
	}
	
	private String getHolder(String holder){
		
		switch (holder){
			case "this": return scriptName;
			case "playername": return sender;
			case "playerdisplayname": return p.getDisplayName();
			case "playerlistname": return p.getPlayerListName();
			case "playerprefix": return main.vault.isChatHooked() ? main.vault.chat.getPlayerPrefix(p) : "null";
			case "playersuffix": return main.vault.isChatHooked() ? main.vault.chat.getPlayerSuffix(p) : "null";
			case "helditemname": return p.getItemInHand().getType().name();
			case "helditemdisplayname": return  p.getItemInHand().hasItemMeta() && p.getItemInHand().getItemMeta().hasDisplayName() ? p.getItemInHand().getItemMeta().getDisplayName() : "null";
			case "itemid": return p.getItemInHand().getTypeId() + "";
			case "playerloc": 
				Location playerLoc = p != null ? p.getLocation() : null;
			return playerLoc.getWorld().getName() + "," + playerLoc.getBlockX() + "," + playerLoc.getBlockY() + "," + playerLoc.getBlockZ();
			case "issneaking": return p.isSneaking() + "";
			case "issprinting": return p.isSprinting() + "";
			case "isflying": return p.isFlying() + "";
			case "health": return ((Damageable)p).getHealth() + "";
			case "gamemode": return p.getGameMode().name();
			case "triggerloc": return triggerLoc.getWorld().getName() + "," + triggerLoc.getBlockX() + "," + triggerLoc.getBlockY() + "," + triggerLoc.getBlockZ();
			case "worldname": return triggerLoc.getWorld().getName();
			case "biome": return triggerLoc.getBlock().getBiome().name();
			case "onlineplayeramount": return Bukkit.getOnlinePlayers().size() + "";
			default: return "null";
		}
	}
	
	private String parseFunctionalHolders(String arg){
		
		String curr = "";
		
		for (char ch : arg.toCharArray()){
			
			if (ch == '<' || !curr.equals("")){
				curr += ch;
			}
			
			if (ch == '>' && !curr.equals("")){
					
				String[] args = curr.contains(":") ? curr.split("\\:") : new String[]{ curr };
				
				for (int i = 0; i < args.length; i++){
					args[i] = args[i].replace(">", "").replace("<", "");
				}
				
				try {
					
					switch (args[0].toLowerCase()){
					
						case "arraysize":
							
							arg = r(arg, curr, main.vars.getList(args[1].replaceFirst("@", "")).size() + "");
							
						break;
						
						case "haspermission":
								
							if (p != null){
								arg = r(arg, curr, p.hasPermission(args[1]) + "");
							}
								
						break;
							
						case "haspotioneffect":
								
							boolean found = false;
								
							if (p != null){
								for (PotionEffectType type : PotionEffectType.values()){
									if (type.toString().equalsIgnoreCase(args[1]) && p.hasPotionEffect(type)){
										found = true;
										break;
									}
								}
							}
								
							arg = r(arg, curr, found + "");
								
						break;
							
						case "currentloc":
							
							if (args.length == 1){
								
								Location l = p.getLocation();
								arg = r(arg, curr, l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ());
								
							} else if (args.length == 2){
								
								if (Bukkit.getPlayer(args[1]) != null){
									Location l = Bukkit.getPlayer(args[1]).getLocation();
									arg = r(arg, curr, l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ());
								} else {
									arg = r(arg, curr, "null");
								}
							}
								
						break;
						
						case "random0to": case "random1to":
							
							arg = r(arg, curr, new Random().nextInt(Integer.parseInt(args[1]) + (args[0].equals("random0to") ? 1 : 2)) + "");
							
						break;
						
						case "random":
							
							arg = r(arg, curr, new Random().nextInt(Integer.parseInt(args[2])+1) + Integer.parseInt(args[1]) + "");
							
						break;
						
						case "health": case "maxhealth":
							
							if (args.length == 1){
								arg = r(arg, curr, (p != null ? (args[0].equals("health") ? ((Damageable) p).getHealth() : ((Damageable) p).getMaxHealth()) : 0) + "");
							} else if (args.length == 2){
								arg = r(arg, curr, (Bukkit.getPlayer(args[1]) != null ? (args[0].equals("health") ? ((Damageable) Bukkit.getPlayer(args[1])).getHealth() : ((Damageable) Bukkit.getPlayer(args[1])).getMaxHealth()) : 0) + "");
							}
							
						break;
						
						case "totalexp": case "totalxp":
							
							if (args.length == 1){
								
								if (p != null){
									arg = r(arg, curr, p.getTotalExperience() + "");
								}
								
							} else if (args.length == 2){
								
								if (Bukkit.getPlayer(args[1]) != null){
									arg = r(arg, curr, Bukkit.getPlayer(args[1]).getTotalExperience() + "");
								} else {
									arg = r(arg, curr, "null");
								}
							}
							
						break;
						
						case "relativeloc":
							
							String[] first = args[1].split(",");
							String[] second = args[2].split(",");
							String fin = "";
							
							for (int i = 0; i < first.length; i++){
								fin += new Integer(Integer.parseInt(first[i]) + Integer.parseInt(second[i])) + (i == first.length -1 ? "" : ",");
							}
							
							arg = r(arg, curr, fin);
							
						break;
						
						case "hasmoney": case "givemoney": case "takemoney":
							
							if (main.vault.isEconHooked()){
								if (args[0].equals("givemoney")){
									EconomyResponse res = main.vault.econ.depositPlayer(args[1], Double.parseDouble(args[2]));
									arg = r(arg, curr, res.transactionSuccess() + "");
								} else if (args[0].equals("takemoney")){
									EconomyResponse res = main.vault.econ.withdrawPlayer(args[1], Double.parseDouble(args[2]));
									arg = r(arg, curr, res.transactionSuccess() + "");
								} else {
									arg = r(arg, curr, main.vault.econ.has(args[1], Double.parseDouble(args[2])) + "");
								}
							} else {
								arg = r(arg, curr, "false");
								main.debug("Vault is not hooked for <has/give/takemoney>!", scriptName, line, fileName);
							}
							
						break;
						
						case "isblocktype":
							
							Block b = getLocFromString(args[1]).getBlock();
							arg = r(arg, curr, (b.getTypeId() == Integer.parseInt(args[2]) && b.getData() == Byte.parseByte(args[3])) + "");
							
						break;
						
						case "distance":
							
							arg = r(arg, curr, getLocFromString(args[1]).toVector().distance(getLocFromString(args[2]).toVector()) + "");
							
						break;
						
						case "startswith":
							
							arg = r(arg, curr, args[1].startsWith(args[2]) + "");
							
						break;
						
						case "endswith":
							
							arg = r(arg, curr, args[1].endsWith(args[2]) + "");
							
						break;
						
						case "contains":
							
							arg = r(arg, curr, args[1].contains(args[2]) + "");
							
						break;
						
						case "direction":

							arg = r(arg, curr, VTUtils.getDirection(args[1].equals("TEXT") ? "txt" : "int", p.getLocation().getYaw()));
							
						break;
						
						case "secondticks":
							
							long time = System.currentTimeMillis();
							arg = r(arg, curr, (time/1000) + Long.parseLong(args[1]) + "");
							
						break;
						
						case "getarea":
							
							try {
								Map<String, List<String>> results = VTUtils.getArea(getLocFromString(args[1]));
								arg = r(arg, curr, ((String) results.keySet().toArray()[0]).replace("_Enter", "").replace("_Exit", ""));
							} catch (Exception e){}
							
						break;
						
						case "hour":
							
							long gametime = triggerLoc.getWorld().getTime();
							int hour = (int) (gametime/1000) + 6;
							
							if (hour > 23){
								hour = hour-24;
							}
							
							arg = r(arg, curr, hour + "");
							
						break;
						
						case "min":
							
							gametime = triggerLoc.getWorld().getTime();
							hour = (int) (gametime/1000) + 6;
							int mins= (int) (((gametime-(hour*1000))/10)*6)/10;
							
							String ret = "";
							
							if (mins < 10){
								ret = "0";
							}
							
							arg = r(arg, curr, ret + Integer.toString(mins));
							
						break;
						
						case "getblocklos":
							
							Set set = null;
							List<Block> blocks = p.getLineOfSight(set, Integer.parseInt(args[2]));
						
							if (blocks.size() > 0){
							
								switch (args[1].toLowerCase()){
								
									case "loc":
										
										Location l = blocks.get(0).getLocation();
										arg = r(arg, curr, l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ());
										
									break;
									
									case "name":
										
										arg = r(arg, curr, blocks.get(0).getType().name());
										
									break;
									
									case "type":
										
										arg = r(arg, curr, blocks.get(0).getTypeId() + ":" + blocks.get(0).getData());
										
									break;
									
									case "id":
										
										arg = r(arg, curr, blocks.get(0).getTypeId() + "");
										
									break;
									
									case "data":
										
										arg = r(arg, curr, blocks.get(0).getData() + "");
										
									break;
								}
								
							} else {
								arg = r(arg, curr, "null");
							}
							
						break;
						
						case "var":
							
							arg = r(arg, curr, parseVars(args[1]));
							
						break;
						
						case "getchar":
							
							if (args[1].toCharArray().length < Integer.parseInt(args[2])){
								arg = r(arg, curr, "null");
							} else {
								arg = r(arg, curr, args[1].toCharArray()[Integer.parseInt(args[2])-1] + "");
							}
							
						break;
						
						case "hasitem": case "takeitem":
							
							found = false;
							
							if (Bukkit.getPlayer(args[1]) != null){
								for (ItemStack i : Bukkit.getPlayer(args[1]).getInventory().getContents()){
									if (i != null && i.getTypeId() == Integer.parseInt(args[2])){
										found = true;
										if (args[0].equals("takeitem")){
											if (i.getAmount() >= Integer.parseInt(args[3])){
												if (i.getAmount() == 1){
													i.setType(Material.AIR);
												} else {
													i.setAmount(i.getAmount() - Integer.parseInt(args[3]));
												}
												Bukkit.getPlayer(args[1]).updateInventory();
											} else {
												found = false;
											}
										}
										break;
									}
								}
								if (!found){
									for (ItemStack i : Bukkit.getPlayer(args[1]).getInventory().getArmorContents()){
										if (i != null && i.getTypeId() == Integer.parseInt(args[2])){
											found = true;
											break;
										}
									}
								}
							}
							
							arg = r(arg, curr, found + "");
							
						break;
						
						case "giveitem":
							
							if (Bukkit.getPlayer(args[1]) != null){
								Bukkit.getPlayer(args[1]).getInventory().addItem(new ItemStack(Integer.parseInt(args[2]), Integer.parseInt(args[3])));
							}
							
							arg = r(arg, curr, args[3]);
							
						break;
						
						case "isop":
							
							if (Bukkit.getPlayer(args[1]) != null){
								arg = r(arg, curr, Bukkit.getPlayer(args[1]).isOp() + "");
							} else {
								arg = r(arg, curr, "null");
							}
							
						break;
						
						case "uuid":
							
							arg = r(arg, curr, VTUtils.getUUID(args[1]));
							
						break;
						
						case "food":
							
							if (Bukkit.getPlayer(args[1]) != null){
								arg = r(arg, curr, Bukkit.getPlayer(args[1]).getFoodLevel() + "");
							}
							
						break;
						
						case "saturation":
							
							if (Bukkit.getPlayer(args[1]) != null){
								arg = r(arg, curr, Bukkit.getPlayer(args[1]).getSaturation() + "");
							}
							
						break;
						
						case "playeruuid":
							
							arg = r(arg, curr, p.getUniqueId().toString());
							
						break;
						
						case "playerloc":
							
							String rep = "";
							
							switch (args[1]){
							
								case "x":
									rep = p.getLocation().getBlockX() + "";
								break;
								
								case "y":
									rep = p.getLocation().getBlockY() + "";
								break;
								
								case "z":
									rep = p.getLocation().getBlockZ() + "";
								break;
								
								case "world":
									rep = p.getLocation().getWorld().getName();
								break;
							}
							
							arg = r(arg, curr, rep);
							
						break;
						
						case "triggerloc":
							
							rep = "";
							
							switch (args[1]){
							
								case "x":
									rep = triggerLoc.getBlockX() + "";
								break;
								
								case "y":
									rep = triggerLoc.getBlockY() + "";
								break;
								
								case "z":
									rep = triggerLoc.getBlockZ() + "";
								break;
								
								case "world":
									rep = triggerLoc.getWorld().getName();
								break;
							}
							
							arg = r(arg, curr, rep);
							
						break;
						
						case "holdingitem":
							
							if (Bukkit.getPlayer(args[1]) != null){
								arg = r(arg, curr, (Bukkit.getPlayer(args[1]).getItemInHand() != null && Bukkit.getPlayer(args[1]).getItemInHand().getType().toString().equalsIgnoreCase(args[2])) + "");
							}
							
						break;
						
						case "eval":
							
							String currTemp = new String(curr.replaceAll("%", " "));
							ScriptEngineManager manager = new ScriptEngineManager();
						    ScriptEngine engine = manager.getEngineByName("js");   
						    Double result = (Double) engine.eval(currTemp.replaceAll(" ", ""));
							
						    arg = r(arg, curr, result + "");
							
						break;
						
						case "sn":
							
							arg = r(arg, curr, "deprecated");
							
						break;
						
						case "signtext":
							
							Sign sign = (Sign) getLocFromString(args[1]).getBlock().getState();
							arg = r(arg, curr, sign.getLine(Integer.parseInt(args[2])));
							
						break;
						
						case "systemtime":
							
							arg = r(arg, curr, VTUtils.getTime(args[1]));
							
						break;
						
					}
					
				} catch (Exception e){
					main.debug("Could not parse functional placeholder " + args[0] + "!", scriptName, line, fileName);
				}
				
				curr = "";
			}
		}
		
		return arg;
	}
	
	private String r(String str, String a, String b){
		return new String(str.replace(a, b));
	}
	
	public boolean args(String message, int required){
		return message.split(" ").length >= required;
	}
}