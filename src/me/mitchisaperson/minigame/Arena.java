package me.mitchisaperson.minigame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import me.mitchisaperson.minigame.items.ArenaChest;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class Arena
{
	private int id;
	private String name;
	private List<Location> spawns = new ArrayList<Location>();
	private List<Location> availableSpawns = spawns;
	private HashMap<Player, Location> playerSpawns = new HashMap<Player, Location>();
	private List<Player> players = new CopyOnWriteArrayList<Player>();
	private boolean active;
	private int maxPlayers;
	private int minPlayers;
	private boolean clearInventory;
	private HashMap<Player, Integer> playerLives = new HashMap<Player, Integer>();
	private int maxLives = 1;
	private int timer = 10;
	private List<Location> chests = new ArrayList<Location>();
	private boolean canBuild;
	private List<Location> placedBlocks = new ArrayList<Location>();
	private Map<Location, HashMap<Material, Byte>> destroyedBlocks = new ConcurrentHashMap<Location, HashMap<Material, Byte>>();
	private boolean canDestroy;
	
	private ArenaChest ac = new ArenaChest();
	
	public Arena(int id)
	{
		this.id = id;
	}
	
	//DO NOT USE UNLESS REMOVING AN ARENA
	public void setId(int id)
	{
		this.id = id;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void addSpawn(Location loc)
	{
		spawns.add(loc);
	}
	
	public void removeSpawn(Location loc)
	{
		spawns.remove(loc);
	}
	
	public void addPlayer(Player player)
	{
		players.add(player);
		playerSpawns.put(player, player.getLocation());
		availableSpawns.remove(playerSpawns.get(player));
		playerLives.put(player, maxLives);
	}
	
	public void removePlayer(Player player)
	{
		availableSpawns.add(playerSpawns.get(player));
		players.remove(player);
		playerSpawns.remove(player);
		playerLives.remove(player);
	}
	
	public void setActive(boolean active)
	{
		this.active = active;
	}
	
	public void setMaxPlayers(int maxPlayers)
	{
		this.maxPlayers = maxPlayers;
	}
	
	public void setMinPlayers(int minPlayers)
	{
		this.minPlayers = minPlayers;
	}
	
	public void setClearInventory(boolean clearInventory)
	{
		this.clearInventory = clearInventory;
	}
	
	public void setPlayerLives(Player player, int lives)
	{
		playerLives.put(player, lives);
	}
	
	public void setMaxLives(int maxLives)
	{
		this.maxLives = maxLives;
	}
	
	public void setCanBuild(boolean canBuild)
	{
		this.canBuild = canBuild;
	}
	
	public void addTempBlock(Location location)
	{
		placedBlocks.add(location);
	}
	
	public void removeTempBlock(Location location)
	{
		placedBlocks.remove(location);
	}
	
	public void addRegenBlock(Location location, Material material, byte materialData)
	{
		HashMap<Material, Byte> data = new HashMap<Material, Byte>();
		data.put(material, materialData);
		destroyedBlocks.put(location, data);
	}
	
	public void removeRegenBlock(Location location)
	{
		destroyedBlocks.remove(location);
	}
	
	public void addChest(Location loc)
	{
		chests.add(loc);
	}
	
	public void removeChest(Location loc)
	{
		chests.remove(loc);
	}
	
	public void setCanDestroy(boolean canDestroy)
	{
		this.canDestroy = canDestroy;
	}
	
	public int getId()
	{
		return this.id;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public List<Location> getSpawns()
	{
		return this.spawns;
	}
	
	public Location getPlayerSpawn(Player player)
	{
		return playerSpawns.get(player);
	}
	
	public HashMap<Player, Location> getPlayerSpawns()
	{
		return this.playerSpawns;
	}
	
	public List<Location> getAvaiableSpawns()
	{
		return this.availableSpawns;
	}
	
	public List<Player> getPlayers()
	{
		return this.players;
	}
	
	public boolean isActive()
	{
		return this.active;
	}
	
	public int getMaxPlayers()
	{
		return this.maxPlayers;
	}
	
	public int getMinPlayers()
	{
		return this.minPlayers;
	}
	
	public boolean getClearInventory()
	{
		return this.clearInventory;
	}
	
	public int getLives(Player player)
	{
		return playerLives.get(player);
	}
	
	public HashMap<Player, Integer> getPlayerLives()
	{
		return playerLives;
	}
	
	public int getMaxLives()
	{
		return this.maxLives;
	}
	
	public boolean canDestroy()
	{
		return this.canDestroy;
	}
	
	public List<Location> getChests()
	{
		return this.chests;
	}
	
	public Location firstSpawn()
	{
		return spawns.get(0);
	}
	
	public boolean containsSpawn(Location loc)
	{
		for(Location l : spawns)
		{
			if(l == loc) return true;
		}
		
		return false;
	}
	
	public boolean isSpawnAvailable(Location loc)
	{
		if(playerSpawns.containsValue(loc))
		{
			return false;
		}
		
		return true;
	}
	
	public boolean containsPlayer(Player player)
	{
		for(Player p : players)
		{
			if(p == player) return true;
		}
		
		return false;
	}
	
	public void broadcastMessage(String message)
	{
		for(Player p : players)
		{
			p.sendMessage(message);
		}
	}
	
	public boolean containsChest(Location loc)
	{
		for(Location l : chests)
		{
			if(l.equals(loc)) return true;
		}
		
		return false;
	}
	
	public boolean canBuild()
	{
		return this.canBuild;
	}
	
	public boolean containsBlock(Location location)
	{
		if(placedBlocks.contains(location)) return true;
		return false;
	}
	
	public void clearBlocks()
	{
		for(Location l : placedBlocks)
		{
			l.getBlock().setType(Material.AIR);
			placedBlocks.remove(l);
		}
	}
	
	public void resetArena()
	{
		Iterator<Location> iterator = destroyedBlocks.keySet().iterator();
		
		while(iterator.hasNext())
		{
			Location l = iterator.next();
			List<Material> material = new ArrayList<Material>(destroyedBlocks.get(l).keySet());
			List<Byte> materialData = new ArrayList<Byte>(destroyedBlocks.get(l).values());
			
			l.getBlock().setType(material.get(0));
			l.getBlock().setData((byte) materialData.get(0));
			destroyedBlocks.remove(l);
		}
	}
	
	public void setChests()
	{
		for(Location l : chests)
		{
			Chest chest = (Chest) l.getBlock().getState();
			
			chest.getInventory().clear();
			chest.getInventory().setContents(ac.getRandomContents());
		}
	}
	
	public void clearChests()
	{
		for(Location l : chests)
		{
			Chest chest = (Chest) l.getBlock().getState();
			
			ItemStack[] itemStack = {new ItemStack(Material.AIR)};
			
			chest.getInventory().setContents(itemStack);
		}
	}
	
	public void startCountdown()
	{
		timer = 10;
		
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Minigame.getPlugin(Minigame.class), new Runnable()
		{
			@Override
			public void run()
			{
				if(timer != -1)
				{
					if(timer != 0)
					{
						broadcastMessage(ChatColor.RED + "" + timer + ChatColor.GOLD + " seconds until the game starts!");
						timer--;
					}else
					{
						broadcastMessage(ChatColor.GOLD + "The game has started, good luck!");
						timer--;
						setActive(true);
					}
				}
			}
		}, 400L, 20L);
		
		timer = 10;
	}
}