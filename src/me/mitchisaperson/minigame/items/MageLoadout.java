package me.mitchisaperson.minigame.items;

import java.util.ArrayList;
import java.util.List;

import me.mitchisaperson.minigame.ArenaManager;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MageLoadout implements Listener
{
	private ArenaManager am = ArenaManager.getInstance();
	
	private static ItemStack MAGE_sword = new ItemStack(Material.WOOD_SWORD);
	private static ItemMeta MAGE_sword_meta = MAGE_sword.getItemMeta();
	private static ItemStack MAGE_special = new ItemStack(Material.STICK);
	private static ItemMeta MAGE_special_meta = MAGE_special.getItemMeta();
	private static ItemStack MAGE_item = new ItemStack(Material.SUGAR, 16);
	private static ItemMeta MAGE_item_meta = MAGE_item.getItemMeta();
	
	private static ItemStack MAGE_chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
	private static ItemMeta MAGE_chestplate_meta = MAGE_chestplate.getItemMeta();
	private static ItemStack MAGE_leggings = new ItemStack(Material.LEATHER_CHESTPLATE);
	private static ItemMeta MAGE_leggings_meta = MAGE_leggings.getItemMeta();
	
	private static ItemStack[] loadout = {MAGE_sword, MAGE_special, MAGE_item};
	private static ItemStack[] armor = {new ItemStack(Material.AIR), MAGE_chestplate, MAGE_leggings, new ItemStack(Material.AIR)}; 
	
	public static ItemStack[] getLoadout()
	{
		setItems();
		
		return loadout;
	}
	
	private static void setItems()
	{
		MAGE_sword_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		MAGE_sword_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		MAGE_sword_meta.setDisplayName(ChatColor.RED + "Life" + ChatColor.MAGIC + "hih" + ChatColor.RESET + "" + ChatColor.RED + "Saver");
		List<String> MAGE_sword_lore = new ArrayList<String>();
		MAGE_sword_lore.add(ChatColor.AQUA + "" + ChatColor.ITALIC + "One shot, ");
		MAGE_sword_lore.add(ChatColor.AQUA + "" + ChatColor.ITALIC + "one kill");
		MAGE_sword_meta.setLore(MAGE_sword_lore);
		MAGE_sword_meta.addEnchant(Enchantment.DAMAGE_ALL, 100, true);
		MAGE_sword.setDurability((short) 59);
		MAGE_sword.setItemMeta(MAGE_sword_meta);
		
		MAGE_special_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		MAGE_special_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		MAGE_special_meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.ITALIC + "Magic Wand of Ye Bearded Wizard");
		List<String> MAGE_special_lore = new ArrayList<String>();
		MAGE_special_lore.add(ChatColor.GREEN + "This thing is really");
		MAGE_special_lore.add(ChatColor.GREEN + "dangerous...");
		MAGE_special_meta.setLore(MAGE_special_lore);
		MAGE_special_meta.addEnchant(Enchantment.DAMAGE_ALL, 3, true);
		MAGE_special_meta.addEnchant(Enchantment.FIRE_ASPECT, 2, true);
		MAGE_special.setItemMeta(MAGE_special_meta);
		
		MAGE_item_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		MAGE_item_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		MAGE_item_meta.setDisplayName(ChatColor.WHITE + "" + ChatColor.ITALIC + "SUPER PIXIE DUST! (tm)");
		List<String> MAGE_item_lore = new ArrayList<String>();
		MAGE_item_lore.add(ChatColor.GOLD + "We got this stuff from");
		MAGE_item_lore.add(ChatColor.GOLD + "some guy at a rave..");
		MAGE_item_meta.setLore(MAGE_item_lore);
		MAGE_item_meta.addEnchant(Enchantment.LUCK, 10, true);
		MAGE_item.setItemMeta(MAGE_item_meta);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e)
	{	
		Player player = e.getPlayer();
		
		if(e.getAction().equals(Action.RIGHT_CLICK_AIR))
		{
			if(am.isInArena(player))
			{
				if(e.getItem().getType().equals(Material.SUGAR))
				{
					player.sendMessage(ChatColor.GOLD + "" + ChatColor.ITALIC + "Activated speed buff");
					if(player.getItemInHand().getAmount() == 1) player.getItemInHand().setAmount(0);
					else player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
					
					player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 3));
				}
			}
		}
	}
}