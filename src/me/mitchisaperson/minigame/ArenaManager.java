package me.mitchisaperson.minigame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.imageio.stream.FileImageInputStream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class ArenaManager
{
	private ArenaManager() {}
	private static ArenaManager instance = new ArenaManager();
	public static ArenaManager getInstance() { return instance; }
	
	private CustomChatManager chat = new CustomChatManager();
	
	public List<Arena> arenas = new ArrayList<Arena>();
	private int ids = arenas.size();
	private int timer = 10;
	
	private HashMap<Player, ItemStack[]> inventories = new HashMap<Player, ItemStack[]>();
	private HashMap<Player, ItemStack[]> armorContents = new HashMap<Player, ItemStack[]>();
	private HashMap<Player, Location> locations = new HashMap<Player, Location>();
	
	public Arena getArena(int id)
	{
		for(Arena a : arenas)
		{
			if(a.getId() == id) return a;
		}
		
		return null;
	}
	
	public Arena getArena(Player player)
	{
		for(Arena a : arenas)
		{
			if(a.containsPlayer(player)) return a;
		}
		
		return null;
	}
	
	public Arena randomArena()
	{
		Random rand = new Random();
		int id = rand.nextInt(arenas.size());
		Arena arena = getArena(id);
		
		while(!canJoinArena(arena))
		{
			if(arenas.size() <= 1)
			{
				id = arenas.size();
			}else
			{
				id = rand.nextInt(arenas.size() - 1);
			}
			
			arena = getArena(id);
		}
		
		return arena;
	}
	
	public Location getRandomSpawn(Arena arena)
	{
		if(Minigame.DEV_VERSION) return arena.getSpawns().get(0);
		
		Random rand = new Random();
		
		int spawn = rand.nextInt(arena.getAvaiableSpawns().size());;
		Location playerSpawn = arena.getAvaiableSpawns().get(spawn);
		
		/*if(arena.getSpawns().size() <= 1)
		{
			spawn = 0;
		}else
		{
			spawn = rand.nextInt(arena.getSpawns().size() - 1);
		}*/
		
		return playerSpawn;
	}
	
	public boolean isInArena(Player player)
	{
		for(Arena a : arenas)
		{
			if(a.containsPlayer(player)) return true;
		}
		
		return false;
	}
	
	public boolean canJoinArena(Arena arena)
	{
		if(Minigame.DEV_VERSION) return true;
		
		try
		{
			if(!doesArenaExist(arena)) return false;
		
			if(arena.isActive() || arena.getPlayers().size() == arena.getMaxPlayers() || arena.getAvaiableSpawns().size() <= 0)
			{
				return false;
			}
		
			return true;
		}catch(NullPointerException e)
		{
			return false;
		}

	}
	
	public boolean doesArenaExist(Arena arena)
	{
		if(arenas.contains(arena))
		{
			return true;
		}
		
		return false;
	}
	
	public void broadcastMessage(Arena arena, String message)
	{
		for(Player player : arena.getPlayers())
		{
			player.sendMessage(message);
		}
	}
	
	public int getIds()
	{
		return this.ids;
	}
	
	public void removeAllPlayers(Arena arena)
	{
		Iterator<Player> iterator = arena.getPlayers().iterator();
		
		while(iterator.hasNext())
		{
			Player player = iterator.next();
			
			leave(player);
		}
	}
	
	public boolean doesChestBelongToArena(Location loc)
	{
		for(Arena a : arenas)
		{
			if(a.containsChest(loc)) return true;
		}
		
		return false;
	}
	
	public Arena getChestsArena(Location loc)
	{
		for(Arena a : arenas)
		{
			if(a.containsChest(loc)) return a;
		}
		
		return null;
	}
	
	public List<Location> getAllChests()
	{
		List<Location> chests = new ArrayList<Location>();
		
		for(Arena a : arenas)
		{
			for(Location l : a.getChests())
			{
				chests.add(l);
			}
		}
		
		return chests;
	}
	
	public int newId()
	{
		ids = arenas.size();
		ids++;
		return ids;
	}
	
	//TODO: Finish start/end code
	public void startGame(Arena arena)
	{
		if(arena.getClearInventory() == true) arena.setChests();
		arena.startCountdown();
	}
	
	public void endGame(Arena arena)
	{
		arena.setActive(false);
		removeAllPlayers(arena);
		arena.clearBlocks();
		arena.resetArena();
		arena.clearChests();
	}
	
	public void join(Arena arena, Player player)
	{
		if(isInArena(player))
		{
			player.sendMessage(ChatColor.RED + "You are already in an arena!");
			return;
		}

		if(!canJoinArena(arena))
		{
			player.sendMessage(ChatColor.RED + "I'm sorry, but you cannot join this arena at this time");
			return;
		}
		
		if(arena.getClearInventory())
		{
			inventories.put(player, player.getInventory().getContents());
			armorContents.put(player, player.getInventory().getArmorContents());
			player.getInventory().clear();
			
			player.getInventory().setHelmet(new ItemStack(Material.AIR));
			player.getInventory().setChestplate(new ItemStack(Material.AIR));
			player.getInventory().setLeggings(new ItemStack(Material.AIR));
			player.getInventory().setBoots(new ItemStack(Material.AIR));
		}
		
		locations.put(player, player.getLocation());
		
		player.setFireTicks(0);
		player.setHealth(player.getMaxHealth());
		player.setSaturation((float) 20);
		
		for(PotionEffect e : player.getActivePotionEffects())
		{
			player.removePotionEffect(e.getType());
		}
		
		player.teleport(getRandomSpawn(arena));
		arena.addPlayer(player);
		
		//TODO: Change this to a title/subtitle configuration message
		player.sendMessage(ChatColor.GREEN + "Joined arena " + arena.getName() + "[ID#" + arena.getId() + "]!");
		broadcastMessage(arena, ChatColor.GOLD + player.getName() + " has joined the arena!"  + ChatColor.GOLD + " [" + ChatColor.RED + arena.getPlayers().size() + ChatColor.GRAY + "/" + ChatColor.RED + arena.getMaxPlayers() + ChatColor.GOLD + "]");
		
		if(arena.getPlayers().size() >= arena.getMinPlayers()) startGame(arena);
	}
	
	public void join(Player player)
	{
		if(isInArena(player))
		{
			player.sendMessage(ChatColor.RED + "You are already in an arena!");
			return;
		}
		
		Arena arena = randomArena();
		
		if(arena.getClearInventory())
		{
			inventories.put(player, player.getInventory().getContents());
			armorContents.put(player, player.getInventory().getArmorContents());
			player.getInventory().clear();
			
			player.getInventory().setHelmet(new ItemStack(Material.AIR));
			player.getInventory().setChestplate(new ItemStack(Material.AIR));
			player.getInventory().setLeggings(new ItemStack(Material.AIR));
			player.getInventory().setBoots(new ItemStack(Material.AIR));
		}
		
		locations.put(player, player.getLocation());
		
		player.setFireTicks(0);
		player.setHealth(player.getMaxHealth());
		player.setSaturation((float) 20);
		
		for(PotionEffect e : player.getActivePotionEffects())
		{
			player.removePotionEffect(e.getType());
		}
		if(Minigame.DEV_VERSION) player.teleport(arena.firstSpawn());
		else player.teleport(getRandomSpawn(arena));
		
		arena.addPlayer(player);
		
		//TODO: Change this to a title/subtitle configuration message
		player.sendMessage(ChatColor.GREEN + "Joined arena " + arena.getName() + "[ID#" + arena.getId() + "]!");
		broadcastMessage(arena, ChatColor.GOLD + player.getName() + " has joined the arena!" + ChatColor.GOLD + " [" + ChatColor.RED + arena.getPlayers().size() + ChatColor.GRAY + "/" + ChatColor.RED + arena.getMaxPlayers() + ChatColor.GOLD + "]");
		
		if(arena.getPlayers().size() >= arena.getMinPlayers()) startGame(arena);
	}
	
	public void leave(Player player)
	{
		if(!isInArena(player))
		{
			player.sendMessage(ChatColor.RED + "You aren't in an arena!");
			return;
		}
		
		Arena arena = getArena(player);
		
		player.setFireTicks(0);
		player.setHealth(player.getMaxHealth());
		player.setSaturation((float) 20);
		
		for(PotionEffect e : player.getActivePotionEffects())
		{
			player.removePotionEffect(e.getType());
		}
		
		if(arena.getClearInventory())
		{
			player.getInventory().clear();
			player.getInventory().setHelmet(new ItemStack(Material.AIR));
			player.getInventory().setChestplate(new ItemStack(Material.AIR));
			player.getInventory().setLeggings(new ItemStack(Material.AIR));
			player.getInventory().setBoots(new ItemStack(Material.AIR));
			
			player.getInventory().setContents(inventories.get(player));
			player.getInventory().setArmorContents(armorContents.get(player));
			inventories.remove(player);
			armorContents.remove(player);
		}
		
		player.teleport(locations.get(player));
		locations.remove(player);
		
		arena.removePlayer(player);
		
		if(arena.getPlayers().size() <= 1) endGame(arena);
		
		//TODO: Change this to a title/subtitle configuration message
		player.sendMessage(ChatColor.GREEN + "Left arena " + arena.getName() + "[ID#" + arena.getId() + "]!");
		broadcastMessage(arena, ChatColor.GOLD + player.getName() + " has left the arena!" + ChatColor.GOLD + " [" + ChatColor.RED + arena.getPlayers().size() + ChatColor.GRAY + "/" + ChatColor.RED + arena.getMaxPlayers() + ChatColor.GOLD + "]");
	}
	
	public void lost(Player player)
	{
		if(!isInArena(player))
		{
			player.sendMessage(ChatColor.RED + "You aren't in an arena!");
			return;
		}
		
		Arena arena = getArena(player);
		
		
		player.setFireTicks(0);
		player.setHealth(player.getMaxHealth());
		player.setSaturation((float) 20);
		
		for(PotionEffect e : player.getActivePotionEffects())
		{
			player.removePotionEffect(e.getType());
		}
		
		if(arena.getClearInventory())
		{
			player.getInventory().clear();
			player.getInventory().setHelmet(new ItemStack(Material.AIR));
			player.getInventory().setChestplate(new ItemStack(Material.AIR));
			player.getInventory().setLeggings(new ItemStack(Material.AIR));
			player.getInventory().setBoots(new ItemStack(Material.AIR));
			
			player.getInventory().setContents(inventories.get(player));
			player.getInventory().setArmorContents(armorContents.get(player));
			inventories.remove(player);
			armorContents.remove(player);
		}
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Minigame.getPlugin(Minigame.class), new Runnable()
		{
			@Override
			public void run()
			{
				player.teleport(locations.get(player));
				locations.remove(player);
			}
			
		}, 10L);
		
		arena.removePlayer(player);
		
		if(arena.getPlayers().size() <= 1) endGame(arena);
		
		//TODO: Change this to a title/subtitle configuration message
		player.sendMessage(ChatColor.RED + "You have been eliminated!");
		broadcastMessage(arena, ChatColor.GOLD + player.getName() + " has been eliminated!" + ChatColor.GOLD + " [" + ChatColor.RED + arena.getPlayers().size() + ChatColor.GRAY + "/" + ChatColor.RED + arena.getMaxPlayers() + ChatColor.GOLD + "]");
	}
	
	public void won(Player player)
	{
		if(!isInArena(player))
		{
			player.sendMessage(ChatColor.RED + "You aren't in an arena!");
			return;
		}
		
		Arena arena = getArena(player);
		
		
		player.setFireTicks(0);
		player.setHealth(player.getMaxHealth());
		player.setSaturation((float) 20);
		
		for(PotionEffect e : player.getActivePotionEffects())
		{
			player.removePotionEffect(e.getType());
		}
		
		if(arena.getClearInventory())
		{
			player.getInventory().clear();
			player.getInventory().setHelmet(new ItemStack(Material.AIR));
			player.getInventory().setChestplate(new ItemStack(Material.AIR));
			player.getInventory().setLeggings(new ItemStack(Material.AIR));
			player.getInventory().setBoots(new ItemStack(Material.AIR));
			
			player.getInventory().setContents(inventories.get(player));
			player.getInventory().setArmorContents(armorContents.get(player));
			inventories.remove(player);
			armorContents.remove(player);
		}
		
		player.teleport(locations.get(player));
		locations.remove(player);
		
		arena.removePlayer(player);
		
		endGame(arena);
		
		//TODO: Change this to a title/subtitle configuration message
		player.sendMessage(ChatColor.GREEN + "You have won!");
		Bukkit.broadcastMessage(ChatColor.GOLD + player.getName() + " has won a deathmatch on " + arena.getName() + "!");
	}
	
	public void listArenas(Player player)
	{
		for(Arena a : arenas)
		{
			player.sendMessage(ChatColor.GREEN + a.getName() + "[ID#" + a.getId() + "]");
		}
	}
	
	public void info(Arena arena, Player player)
	{
		player.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "Showing info for: " + arena.getName() + "[ID#" + arena.getId() + "]");
		player.sendMessage(ChatColor.GREEN + "Active: " + ChatColor.AQUA + arena.isActive());
		player.sendMessage(ChatColor.GREEN + "MaxPlayers: " + ChatColor.AQUA + arena.getMaxPlayers());
		player.sendMessage(ChatColor.GREEN + "MinPlayers: " + ChatColor.AQUA + arena.getMinPlayers());
		player.sendMessage(ChatColor.GREEN + "ClearInventory: " + ChatColor.AQUA + arena.getClearInventory());
		player.sendMessage(ChatColor.GREEN + "CanBuild: " + ChatColor.AQUA + arena.canBuild());
		player.sendMessage(ChatColor.GREEN + "CanDestroy: " + ChatColor.AQUA + arena.canDestroy());
		player.sendMessage(ChatColor.GREEN + "Available Spawns: " + ChatColor.AQUA + arena.getAvaiableSpawns().size());
		player.sendMessage(ChatColor.GREEN + "Current Players: " + ChatColor.AQUA + arena.getPlayers().size());
		player.sendMessage(ChatColor.GREEN + "MaxLives: " + ChatColor.AQUA + arena.getMaxLives());
	}
	
	public void addArena(Arena arena, Player player)
	{
		if(doesArenaExist(arena))
		{
			player.sendMessage(ChatColor.RED + "That arena already exists!");
			return;
		}
		
		addArena(arena);
		player.sendMessage(ChatColor.GREEN + "Added arena " + arena.getName() + "[ID#" + arena.getId() + "]!");
	}
	
	public void addArena(Arena arena)
	{
		if(doesArenaExist(arena))
		{
			return;
		}
		
		arenas.add(arena);
	}
	
	public void removeArena(Arena arena)
	{
		removeAllPlayers(arena);
		
		if(doesArenaExist(arena))
		{
			arenas.remove(arena);
			
			ids = 0;
			
			for(Arena a : arenas)
			{
				a.setId(ids + 1);
				ids++;
			}
			
			return;
		}
		
		return;
	}
}