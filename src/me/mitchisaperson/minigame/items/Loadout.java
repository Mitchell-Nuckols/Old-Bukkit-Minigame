package me.mitchisaperson.minigame.items;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Loadout
{
	public static enum ClassType {MAGE, ASSASSIN, WARRIOR, TANK, HEALER, GOD}
	
	public static void assignLoadout(Player player, ClassType loadout)
	{
		player.getInventory().clear();
		
		player.getInventory().setHelmet(new ItemStack(Material.AIR));
		player.getInventory().setChestplate(new ItemStack(Material.AIR));
		player.getInventory().setLeggings(new ItemStack(Material.AIR));
		player.getInventory().setBoots(new ItemStack(Material.AIR));
		
		if(loadout == ClassType.MAGE)
		{
			player.getInventory().setContents(MageLoadout.getLoadout());
		}else if(loadout == ClassType.GOD)
		{
			ItemStack god_sword = new ItemStack(Material.DIAMOND_SWORD);
			ItemMeta god_sword_meta = god_sword.getItemMeta();
			god_sword_meta.addEnchant(Enchantment.DAMAGE_ALL, 999999, true);
			god_sword_meta.addEnchant(Enchantment.DURABILITY, 999999, true);
			god_sword_meta.addEnchant(Enchantment.KNOCKBACK, 999999, true);
			god_sword_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			god_sword_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			god_sword_meta.setDisplayName(ChatColor.DARK_RED + "" + ChatColor.ITALIC + "Death" + ChatColor.MAGIC + "yoy" + ChatColor.RESET + "" + ChatColor.DARK_RED + "" + ChatColor.ITALIC + "Sword");
			List<String> god_sword_meta_lore = new ArrayList<String>();
			god_sword_meta_lore.add(ChatColor.GOLD + "VERY DANGEROUS!");
			
			god_sword.setItemMeta(god_sword_meta);
			
			ItemStack[] god_loadout = {god_sword};
			player.getInventory().setContents(god_loadout);
		}
	}
}