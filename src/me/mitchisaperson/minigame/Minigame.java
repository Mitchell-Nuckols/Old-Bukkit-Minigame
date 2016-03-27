package me.mitchisaperson.minigame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import me.mitchisaperson.minigame.items.ArenaChest;
import me.mitchisaperson.minigame.items.Loadout;
import me.mitchisaperson.minigame.items.Loadout.ClassType;
import me.mitchisaperson.minigame.items.MageLoadout;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.World;
import org.bukkit.block.Banner;
import org.bukkit.block.Chest;
import org.bukkit.block.Jukebox;
import org.bukkit.block.NoteBlock;
import org.bukkit.block.banner.PatternType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class Minigame extends JavaPlugin
{
	public static final boolean DEV_VERSION = false;
	
	CustomChatManager chat = new CustomChatManager();
	ArenaManager am = ArenaManager.getInstance();
	ArenaChest ac = new ArenaChest();
	
	File arenasFile = new File(getDataFolder().getPath(), "arenas.yml");
	FileConfiguration c = YamlConfiguration.loadConfiguration(arenasFile);
	
	private List<String> thing;
	
	@Override
	public void onEnable()
	{
		if(!arenasFile.exists()) saveYaml(c, arenasFile);
		
		loadArenas();
		
		getServer().getPluginManager().registerEvents(new GameListener(), this);
		getServer().getPluginManager().registerEvents(new MageLoadout(), this);
	}
	
	@Override
	public void onDisable()
	{
		saveArenas();
	}
	
	public Object load(File f)
	{
		try
		{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
			
			Object input = ois.readObject();
			
			ois.close();
			
			return input;
		}catch(Exception e)
		{
			System.out.println("Could not load!");
			return null;
		}
	}
	
	public void save(Object o, File f)
	{
		try
		{
			if(!f.exists())
				f.createNewFile();
			
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
			
			oos.writeObject(o);
			oos.flush();
			oos.close();
		}catch(Exception e)
		{
			System.out.println("Could not save!");
		}
	}
	
	public void saveYaml(FileConfiguration ymlConfig, File ymlFile)
	{
		try
		{
			ymlConfig.save(ymlFile);
			getLogger().info("Saved file " + ymlFile.getName());
		}catch(IOException e)
		{
			getLogger().info("Could not save " + ymlFile.getName());
		}
	}
	
	public void reloadYaml(FileConfiguration ymlConfig, File ymlFile)
	{
		try
		{
			ymlConfig.load(ymlFile);
			getLogger().info("Reloaded file " + ymlFile.getName());
		}catch (IOException | InvalidConfigurationException e)
		{
			getLogger().info("Could not reload file " + ymlFile.getName());
		}
	}
	
	public void loadArenas()
	{
		for(String id : c.getConfigurationSection("arenas").getKeys(false))
		{
			try
			{
			Arena arena = new Arena(Integer.parseInt(id));
			
			String name = c.getString("arenas." + id + ".name");
			arena.setName(name);
			
			for(String spawn : c.getConfigurationSection("arenas." + id + ".spawns").getKeys(false))
			{
				World world = Bukkit.getWorld(c.getString("arenas." + id + ".spawns." + spawn + ".world"));
				double x = c.getDouble("arenas." + id + ".spawns." + spawn + ".x");
				double y = c.getDouble("arenas." + id + ".spawns." + spawn + ".y");
				double z = c.getDouble("arenas." + id + ".spawns." + spawn + ".z");
				
				float pitch = c.getInt("arenas." + id + ".spawns." + spawn + ".pitch");
				float yaw = c.getInt("arenas." + id + ".spawns." + spawn + ".yaw");
				
				Location loc = new Location(world, x, y, z);
				
				loc.setPitch(pitch);
				loc.setYaw(yaw);
				
				arena.addSpawn(loc);
			}
			
			int maxPlayers = c.getInt("arenas." + id + ".maxPlayers");
			int minPlayers = c.getInt("arenas." + id + ".minPlayers");
			arena.setMaxPlayers(maxPlayers);
			arena.setMinPlayers(minPlayers);
			
			int maxLives = c.getInt("arenas." + id + ".maxLives");
			arena.setMaxLives(maxLives);
			
			boolean clearInventory = c.getBoolean("arenas." + id + ".clearInventory");
			arena.setClearInventory(clearInventory);
			
			boolean canBuild = c.getBoolean("arenas." + id + ".canBuild");
			arena.setCanBuild(canBuild);
			
			boolean canDestroy = c.getBoolean("arenas." + id + ".canDestroy");
			arena.setCanDestroy(canDestroy);
			
			if(c.isConfigurationSection("arenas." + id + ".chests"))
			{
			for(String chest : c.getConfigurationSection("arenas." + id + ".chests").getKeys(false))
			{
				World world = Bukkit.getWorld(c.getString("arenas." + id + ".chests." + chest + ".world"));
				int x = c.getInt("arenas." + id + ".chests." + chest + ".x");
				int y = c.getInt("arenas." + id + ".chests." + chest + ".y");
				int z = c.getInt("arenas." + id + ".chests." + chest + ".z");
				
				Location loc = new Location(world, x, y, z);
				
				arena.addChest(loc);;
			}
			}
			
			if(!am.doesArenaExist(arena) && !(am.getIds() > arena.getId())) am.addArena(arena);
			else getLogger().info("Attempted to load duplicate arena!");
			
			}catch(NumberFormatException e)
			{
				getLogger().info("Could not load arena with id " + id);
			}
		}
	}
	
	public void saveArenas()
	{
		for(Arena arena : am.arenas)
		{
			c.set("arenas." + arena.getId() + ".name", arena.getName());;
			
			for(int i = 0; i < arena.getSpawns().size(); i++)
			{
				Location loc = arena.getSpawns().get(i);
				
				c.set("arenas." + arena.getId() + ".spawns." + i + ".world", loc.getWorld().getName());
				c.set("arenas." + arena.getId() + ".spawns." + i + ".x", loc.getBlockX());
				c.set("arenas." + arena.getId() + ".spawns." + i + ".y", loc.getBlockY());
				c.set("arenas." + arena.getId() + ".spawns." + i + ".z", loc.getBlockZ());
				
				c.set("arenas." + arena.getId() + ".spawns." + i + ".pitch", loc.getPitch());
				c.set("arenas." + arena.getId() + ".spawns." + i + ".yaw", loc.getYaw());
			}
			
			c.set("arenas." + arena.getId() + ".maxPlayers", arena.getMaxPlayers());
			c.set("arenas." + arena.getId() + ".minPlayers", arena.getMinPlayers());
			
			c.set("arenas." + arena.getId() + ".maxLives", arena.getMaxLives());
			
			c.set("arenas." + arena.getId() + ".clearInventory", arena.getClearInventory());
			
			c.set("arenas." + arena.getId() + ".canBuild", arena.canBuild());
			c.set("arenas." + arena.getId() + ".canDestroy", arena.canDestroy());
			
			for(int i = 0; i < arena.getChests().size(); i++)
			{
				Location loc = arena.getChests().get(i);
				
				c.set("arenas." + arena.getId() + ".chests." + i + ".world", loc.getWorld().getName());
				c.set("arenas." + arena.getId() + ".chests." + i + ".x", loc.getBlockX());
				c.set("arenas." + arena.getId() + ".chests." + i + ".y", loc.getBlockY());
				c.set("arenas." + arena.getId() + ".chests." + i + ".z", loc.getBlockZ());
			}
		}
		saveYaml(c, arenasFile);
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args)
	{
		Player player = (Player) sender;
		
		if(str.equalsIgnoreCase("mmg"))
		{
			if(args.length < 1)
			{
				player.sendMessage(ChatColor.RED + "/mmg <join|leave|list|play|create|set|info|delete|end|start> [args]");
				return false;
			}
			
			if(args[0].equalsIgnoreCase("join"))
			{
				if(args.length < 2 || args.length > 2)
				{
					player.sendMessage(ChatColor.RED + "/mmg join [arena #]");
					return false;
				}
				
				int id;
				
				try
				{
					id = Integer.parseInt(args[1]);
				}catch(NumberFormatException e)
				{
					player.sendMessage(ChatColor.RED + args[1] + " does not exist!");
					return false;
				}
				
				if(!am.doesArenaExist(am.getArena(id)))
				{
					player.sendMessage(ChatColor.RED + args[1] + " does not exist!");
					return false;
				}
				
				Arena arena = am.getArena(Integer.parseInt(args[1]));
				am.join(arena, player);
				
				return true;
			}else if(args[0].equalsIgnoreCase("leave"))
			{
				if(args.length < 1 || args.length > 1)
				{
					player.sendMessage(ChatColor.RED + "/mmg leave");
					return false;
				}
				
				am.leave(player);
				return true;
			}else if(args[0].equalsIgnoreCase("create"))
			{
				if(args.length < 2 || args.length > 2)
				{
					player.sendMessage(ChatColor.RED + "/mmg create [arena name]");
					return false;
				}
				
				Arena arena = new Arena(am.newId());
				arena.setName(args[1]);
				arena.addSpawn(player.getLocation());
				arena.setMaxPlayers(4);
				arena.setMinPlayers(1);
				arena.setActive(false);
				
				am.addArena(arena, player);
				
				loadArenas();
				
				saveArenas();
				reloadConfig();
				return true;
			}else if(args[0].equalsIgnoreCase("list"))
			{
				if(args.length < 1 || args.length > 1)
				{
					player.sendMessage(ChatColor.RED + "/mmg list");
					return false;
				}
				
				am.listArenas(player);
				return true;
			}else if(args[0].equalsIgnoreCase("play"))
			{
				if(args.length < 1 || args.length > 1)
				{
					player.sendMessage(ChatColor.RED + "/mmg play");
					return false;
				}
				
				am.join(player);
				return true;
			}else if(args[0].equalsIgnoreCase("info"))
			{
				if(args.length < 2 || args.length > 2)
				{
					player.sendMessage(ChatColor.RED + "/mmg info [arena #]");
					return false;
				}
				
				int id;
				
				try
				{
					id = Integer.parseInt(args[1]);
				}catch(NumberFormatException e)
				{
					player.sendMessage(ChatColor.RED + args[1] + " does not exist!");
					return false;
				}
				
				if(!am.doesArenaExist(am.getArena(id)))
				{
					player.sendMessage(ChatColor.RED + args[1] + " does not exist!");
					return false;
				}
				
				am.info(am.getArena(id), player);
				return true;
			}else if(args[0].equalsIgnoreCase("delete"))
			{
				if(args.length < 2 || args.length > 2)
				{
					player.sendMessage(ChatColor.RED + "/mmg delete [arena #]");
					return false;
				}
				
				int id;
				
				try
				{
					id = Integer.parseInt(args[1]);
				}catch(NumberFormatException e)
				{
					player.sendMessage(ChatColor.RED + args[1] + " does not exist!");
					return false;
				}
				
				if(!am.doesArenaExist(am.getArena(id)))
				{
					player.sendMessage(ChatColor.RED + args[1] + " does not exist!");
					return false;
				}
				
				player.sendMessage(ChatColor.GREEN + "Removed arena #" + ChatColor.AQUA + am.getArena(id).getId() + ChatColor.GREEN + "!");
				am.removeArena(am.getArena(id));
				
				c.set("arenas." + id, null);
				
				saveArenas();
				reloadConfig();
				
				loadArenas();
				return true;
			}else if(args[0].equalsIgnoreCase("end"))
			{
				if(args.length < 2 || args.length > 2)
				{
					player.sendMessage(ChatColor.RED + "/mmg end [arena #]");
					return false;
				}
				
				int id;
				
				try
				{
					id = Integer.parseInt(args[1]);
				}catch(NumberFormatException e)
				{
					player.sendMessage(ChatColor.RED + args[1] + " does not exist!");
					return false;
				}
				
				if(!am.doesArenaExist(am.getArena(id)))
				{
					player.sendMessage(ChatColor.RED + args[1] + " does not exist!");
					return false;
				}
				
				am.endGame(am.getArena(id));
				return true;
			}else if(args[0].equalsIgnoreCase("set"))
			{
				if(args.length <= 2 || args.length > 4)
				{
					player.sendMessage(ChatColor.RED + "/mmg set [arena #] <name|maxPlayers|minPlayers|addSpawn|removeSpawn|clearInventory> [parameters]");
					return false;
				}
				
				int id;
				
				try
				{
					id = Integer.parseInt(args[1]);
				}catch(NumberFormatException e)
				{
					player.sendMessage(ChatColor.RED + args[1] + " does not exist!");
					return false;
				}
				
				if(!am.doesArenaExist(am.getArena(id)))
				{
					player.sendMessage(ChatColor.RED + args[1] + " does not exist!");
					return false;
				}
				
				Arena arena = am.getArena(Integer.parseInt(args[1]));
				
				if(args[2].equalsIgnoreCase("name"))
				{
					if(args.length < 4 || args.length > 4)
					{
						player.sendMessage(ChatColor.RED + "/mmg set [arena #] name [new name]");
						return false;
					}
					
					arena.setName(args[3]);
					player.sendMessage(ChatColor.GREEN + "Set arena #" + arena.getId() + "'s name to " + ChatColor.AQUA + arena.getName() + ChatColor.GREEN +  "!");
					
					saveArenas();
					reloadConfig();
					return true;
				}else if(args[2].equalsIgnoreCase("maxPlayers"))
				{
					if(args.length < 4 || args.length > 4)
					{
						player.sendMessage(ChatColor.RED + "/mmg set [arena #] maxPlayers [# of maxPlayers]");
						return false;
					}
					
					int maxPlayers;
					
					try
					{
						maxPlayers = Integer.parseInt(args[3]);
					}catch(NumberFormatException e)
					{
						player.sendMessage(ChatColor.RED + args[3] + " is not a valid number!");
						return false;
					}
					
					arena.setMaxPlayers(maxPlayers);
					player.sendMessage(ChatColor.GREEN + "Set arena #" + arena.getId() + "'s maxPlayers to " + ChatColor.AQUA + arena.getMaxPlayers());
					
					saveArenas();
					reloadConfig();
					return true;
				}else if(args[2].equalsIgnoreCase("minPlayers"))
				{
					if(args.length < 4 || args.length > 4)
					{
						player.sendMessage(ChatColor.RED + "/mmg set [arena #] minPlayers [# of minPlayers]");
						return false;
					}
					
					int minPlayers;
					
					try
					{
						minPlayers = Integer.parseInt(args[3]);
					}catch(NumberFormatException e)
					{
						player.sendMessage(ChatColor.RED + args[3] + " is not a valid number!");
						return false;
					}
					
					arena.setMinPlayers(minPlayers);
					player.sendMessage(ChatColor.GREEN + "Set arena #" + arena.getId() + "'s minPlayers to " + ChatColor.AQUA + arena.getMinPlayers());
					
					saveArenas();
					reloadConfig();
					return true;
				}else if(args[2].equalsIgnoreCase("addSpawn"))
				{
					if(args.length < 3 || args.length > 3)
					{
						player.sendMessage(ChatColor.RED + "/mmg set [arena #] addSpawn");
						return false;
					}
					
					if(arena.getPlayers().size() != 0)
					{
						player.sendMessage(ChatColor.RED + "Cannot add spawn when there are players in a game!");
						return false;
					}
					
					arena.addSpawn(player.getLocation());
					player.sendMessage(ChatColor.GREEN + "Added new spawn at your location for arena #" + arena.getId() + "!");
					return true;
				}else if(args[2].equalsIgnoreCase("removeSpawn"))
				{
					if(args.length < 4 || args.length > 4)
					{
						player.sendMessage(ChatColor.RED + "/mmg set [arena #] removeSpawn [spawn #]");
						return false;
					}
					
					int spawn;
					
					try
					{
						spawn = Integer.parseInt(args[3]);
					}catch(NumberFormatException e)
					{
						player.sendMessage(ChatColor.RED + args[3] + " is not a valid number!");
						return false;
					}
					
					if(arena.getSpawns().size() <= 1)
					{
						player.sendMessage(ChatColor.RED + "Cannot remove the only spawn in the arena!");
						return false;
					}
					
					if(spawn > arena.getSpawns().size())
					{
						player.sendMessage(ChatColor.RED + "That is not a valid spawn number!");
						return false;
					}
					
					arena.removeSpawn(arena.getSpawns().get(spawn - 1));
					player.sendMessage(ChatColor.GREEN + "Removed spawn #" + ChatColor.AQUA +  spawn + ChatColor.GREEN + " in arena #" + arena.getId() + "!");
					
					saveArenas();
					reloadConfig();
					return true;
				}else if(args[2].equalsIgnoreCase("clearInventory"))
				{
					if(args.length < 4 || args.length > 4)
					{
						player.sendMessage(ChatColor.RED + "/mmg set [arena #] clearInventory <true|false>");
						return false;
					}
					
					if(args[3].equalsIgnoreCase("true"))
					{
						arena.setClearInventory(true);
						player.sendMessage(ChatColor.GREEN + "Set clearInventory to " + ChatColor.AQUA +  arena.getClearInventory() + ChatColor.GREEN + " for arena #" + arena.getId() + "!");
						
						saveArenas();
						reloadConfig();
						return true;
					}else if(args[3].equalsIgnoreCase("false"))
					{
						arena.setClearInventory(false);
						player.sendMessage(ChatColor.GREEN + "Set clearInventory to " + ChatColor.AQUA + arena.getClearInventory() + ChatColor.GREEN + " for arena #" + arena.getId() + "!");
						
						saveArenas();
						reloadConfig();
						return true;
					}else
					{
						player.sendMessage(ChatColor.RED + "Only true or false is accepted!");
						return false;
					}
				}else if(args[2].equalsIgnoreCase("canBuild"))
				{
					if(args.length < 4 || args.length > 4)
					{
						player.sendMessage(ChatColor.RED + "/mmg set [arena #] canBuild <true|false>");
						return false;
					}
					
					if(args[3].equalsIgnoreCase("true"))
					{
						arena.setCanBuild(true);
						player.sendMessage(ChatColor.GREEN + "Set canBuild to " + ChatColor.AQUA +  arena.canBuild() + ChatColor.GREEN + " for arena #" + arena.getId() + "!");
						
						saveArenas();
						reloadConfig();
						return true;
					}else if(args[3].equalsIgnoreCase("false"))
					{
						arena.setCanBuild(false);
						player.sendMessage(ChatColor.GREEN + "Set canBuild to " + ChatColor.AQUA + arena.canBuild() + ChatColor.GREEN + " for arena #" + arena.getId() + "!");
						
						saveArenas();
						reloadConfig();
						return true;
					}else
					{
						player.sendMessage(ChatColor.RED + "Only true or false is accepted!");
						return false;
					}
				}else if(args[2].equalsIgnoreCase("canDestroy"))
				{
					if(args.length < 4 || args.length > 4)
					{
						player.sendMessage(ChatColor.RED + "/mmg set [arena #] canDestroy <true|false>");
						return false;
					}
					
					if(args[3].equalsIgnoreCase("true"))
					{
						arena.setCanDestroy(true);
						player.sendMessage(ChatColor.GREEN + "Set canDestroy to " + ChatColor.AQUA +  arena.canDestroy() + ChatColor.GREEN + " for arena #" + arena.getId() + "!");
						
						saveArenas();
						reloadConfig();
						return true;
					}else if(args[3].equalsIgnoreCase("false"))
					{
						arena.setCanDestroy(false);
						player.sendMessage(ChatColor.GREEN + "Set canDestroy to " + ChatColor.AQUA + arena.canDestroy() + ChatColor.GREEN + " for arena #" + arena.getId() + "!");
						
						saveArenas();
						reloadConfig();
						return true;
					}else
					{
						player.sendMessage(ChatColor.RED + "Only true or false is accepted!");
						return false;
					}
				}else if(args[2].equalsIgnoreCase("maxLives"))
				{
					if(args.length < 4 || args.length > 4)
					{
						player.sendMessage(ChatColor.RED + "/mmg set [arena #] maxLives [# of maxLives]");
						return false;
					}
					
					int maxLives;
					
					try
					{
						maxLives = Integer.parseInt(args[3]);
					}catch(NumberFormatException e)
					{
						player.sendMessage(ChatColor.RED + args[3] + " is not a valid number!");
						return false;
					}
					
					arena.setMaxLives(maxLives);
					player.sendMessage(ChatColor.GREEN + "Set arena #" + arena.getId() + "'s maxLives to " + ChatColor.AQUA + arena.getMaxLives());					
					
					saveArenas();
					reloadConfig();
					return true;
				}else if(args[2].equalsIgnoreCase("chest"))
				{
					if(args.length < 3 || args.length > 3)
					{
						player.sendMessage(ChatColor.RED + "/mmg set [arena #] chest");
						return false;
					}
					
					Location playerLoc = player.getLocation();
					
					Location block = new Location(player.getWorld(), playerLoc.getX(), playerLoc.getY(), playerLoc.getZ());
					
					if(!(block.getBlock().getType() == Material.CHEST))
					{
						player.sendMessage(ChatColor.RED + "You must be standing on a chest to use this commad!" + " " + block.getBlockX() + " " + block.getBlockY() + " " + block.getBlockZ());
						return false;
					}
					
					if(am.doesChestBelongToArena(block))
					{
						player.sendMessage(ChatColor.RED + "That chest already belongs to an arena!");
						return false;
					}
					
					arena.addChest(block);
					player.sendMessage(ChatColor.GREEN + "Added chest for arena #" + ChatColor.AQUA + arena.getId() + "" + ChatColor.GREEN + "!");
					
					saveArenas();
					reloadConfig();
					return true;
				}
				
				saveArenas();
				reloadConfig();
			}
		}
		
		if(str.equalsIgnoreCase("crate") && player.isOp())
		{
			ItemStack piston = new ItemStack(Material.PISTON_BASE, 1, (byte) 6);
			
			player.getWorld().getBlockAt(player.getLocation()).setType(piston.getType());
		}
		return false;
	}
}