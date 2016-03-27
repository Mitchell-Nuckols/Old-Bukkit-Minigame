package me.mitchisaperson.minigame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GameListener implements Listener
{
	ArenaManager am = ArenaManager.getInstance();
	
	private HashMap<Location, Integer> signs = new HashMap<Location, Integer>();
	private HashMap<Location, Integer> chests = new HashMap<Location, Integer>();
	private HashMap<Location, Player> tntBlocks = new HashMap<Location, Player>();
	
	@EventHandler
	public void onPlayerPlaceBlock(BlockPlaceEvent e)
	{
		if(am.isInArena(e.getPlayer()))
		{
			if(am.getArena(e.getPlayer()).canBuild() && e.getBlock().getType() != Material.TNT)
			{
				am.getArena(e.getPlayer()).addTempBlock(e.getBlock().getLocation());
			}else if(am.getArena(e.getPlayer()).canBuild() && e.getBlock().getType() == Material.TNT)
			{
				tntBlocks.put(e.getBlock().getLocation(), e.getPlayer());
				e.getBlock().setType(Material.AIR);
				e.getBlock().getWorld().spawnEntity(e.getBlock().getLocation(), EntityType.PRIMED_TNT);
			}else
			{
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		if(am.isInArena(e.getPlayer()))
		{
			am.leave(e.getPlayer());
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e)
	{
		if(am.isInArena(e.getEntity()))
		{
			Arena arena = am.getArena(e.getEntity());
			
			arena.setPlayerLives(e.getEntity(), arena.getLives(e.getEntity()) - 1);
			
			if(arena.getPlayers().size() <= 1) am.endGame(arena);
			
			if(arena.getLives(e.getEntity()) <= 0)
			{
				e.setDeathMessage(ChatColor.RED + e.getEntity().getName() + ChatColor.GOLD + " has been killed!");
				am.lost(e.getEntity());
			}else
			{
				e.getEntity().teleport(arena.getPlayerSpawn(e.getEntity()));
			}
			
			Player killer = e.getEntity().getKiller();
			
			if(arena.containsPlayer(killer))
			{
				if(arena.getPlayers().size() <= 1)
				{
					am.won(killer);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerBreakBlockEvent(BlockBreakEvent e)
	{
		if(am.isInArena(e.getPlayer()))
		{
			if(am.getArena(e.getPlayer()).canDestroy() && !am.getArena(e.getPlayer()).containsBlock(e.getBlock().getLocation()))
			{
				am.getArena(e.getPlayer()).addRegenBlock(e.getBlock().getLocation(), e.getBlock().getType(), e.getBlock().getData());
			}else if(am.getArena(e.getPlayer()).canBuild() && am.getArena(e.getPlayer()).containsBlock(e.getBlock().getLocation()))
			{
				am.getArena(e.getPlayer()).removeTempBlock(e.getBlock().getLocation());
			}else
			{
				e.setCancelled(true);
			}
		}
		
		if(signs.containsKey(e.getBlock().getLocation()))
		{
			e.getPlayer().sendMessage(ChatColor.AQUA + "Removed lobby sign for arena " + ChatColor.GREEN + am.getArena(signs.get(e.getBlock().getLocation())).getName() + "[ID#" + am.getArena(signs.get(e.getBlock().getLocation())).getId() + "]");
			signs.remove(e.getBlock().getLocation());
		}
		
		if(chests.containsKey(e.getBlock().getLocation()))
		{
			Arena arena = am.getArena(chests.get(e.getBlock().getLocation()));
				
			arena.removeChest(e.getBlock().getLocation());
			chests.remove(e.getBlock().getLocation());
			e.getPlayer().sendMessage(ChatColor.GREEN + "Removed chest for arena #" + ChatColor.AQUA + arena.getId() + "" + ChatColor.GREEN + "!");
		}
	}
	
	@EventHandler
	public void onTNTExplode(EntityExplodeEvent e)
	{
		if(tntBlocks.containsKey(e.getLocation()))
		{
			Player player = tntBlocks.get(e.getLocation());
			
			Arena a = am.getArena(player);
			
			for(Block b : e.blockList())
			{
				a.addRegenBlock(b.getLocation(), b.getType(), b.getData());
			}
		}
	}
	
	@EventHandler
	public void onPlayerDamagePlayerEvent(EntityDamageByEntityEvent e)
	{
		if(e.getEntity() instanceof Player && e.getDamager() instanceof Player)
		{
			Player victim = (Player) e.getEntity();
			Player damager = (Player) e.getDamager();
			
			if(am.isInArena(damager))
			{
				Arena arena = am.getArena(damager);
				
				if(!arena.containsPlayer(victim) || !arena.containsPlayer(damager))
				{
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerSignClick(PlayerInteractEvent e)
	{
		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
		{
			if(signs.containsKey(e.getClickedBlock().getLocation()))
			{
				e.getPlayer().sendMessage(ChatColor.AQUA + "Attempting to join arena " + ChatColor.GREEN + am.getArena(signs.get(e.getClickedBlock().getLocation())).getName() + "[ID#" + am.getArena(signs.get(e.getClickedBlock().getLocation())).getId() + "]");
				am.join(am.getArena(signs.get(e.getClickedBlock().getLocation())), e.getPlayer());
			}
		}
	}
	
	@EventHandler
	public void onPlayerPlaceSignEvent(SignChangeEvent e)
	{
		if(e.getLine(0).equalsIgnoreCase("[MMG]") && e.getPlayer().isOp())
		{
			if(am.doesArenaExist(am.getArena(Integer.parseInt(e.getLine(2)))))
			{
				int id = Integer.parseInt(e.getLine(2));
				
				e.setLine(1, ChatColor.GREEN + am.getArena(id).getName());
				e.setLine(0, ChatColor.DARK_AQUA + "[MMG]");
				e.setLine(2, ChatColor.GOLD + "" + am.getArena(id).getId());
				
				signs.put(e.getBlock().getLocation(), id);
				
				e.getPlayer().sendMessage(ChatColor.AQUA + "Created lobby sign for arena " + ChatColor.GREEN + am.getArena(id).getName() + "[ID#" + am.getArena(id).getId() + "]");
			}
		}else if(e.getLine(0).equalsIgnoreCase("[MMG]") && !e.getPlayer().isOp())
		{
			e.setLine(0, "MMG");
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e)
	{
		Player player = e.getPlayer();
		
		if(am.isInArena(player))
		{
			Arena arena = am.getArena(player);
			
			if(!arena.isActive())
			{
				player.teleport(am.getArena(player).getPlayerSpawn(player));
			}
		}
	}
}