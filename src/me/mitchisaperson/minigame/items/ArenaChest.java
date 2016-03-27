package me.mitchisaperson.minigame.items;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class ArenaChest
{
	HashMap<Material, Integer> materials = new HashMap<Material, Integer>();
	List<Material> mats;
	
	public ItemStack[] getRandomContents()
	{
		materials.put(Material.APPLE, 7);
		materials.put(Material.COOKED_BEEF, 3);
		materials.put(Material.COOKED_CHICKEN, 11);
		materials.put(Material.FISHING_ROD, 1);
		
		materials.put(Material.STONE_SWORD, 1);
		materials.put(Material.WOOD_SWORD, 1);
		materials.put(Material.STONE_AXE, 1);
		
		materials.put(Material.LEATHER_HELMET, 1);
		materials.put(Material.LEATHER_CHESTPLATE, 1);
		materials.put(Material.LEATHER_LEGGINGS, 1);
		materials.put(Material.LEATHER_BOOTS, 1);
		materials.put(Material.CHAINMAIL_HELMET, 1);
		materials.put(Material.CHAINMAIL_CHESTPLATE, 1);
		materials.put(Material.CHAINMAIL_LEGGINGS, 1);
		materials.put(Material.CHAINMAIL_BOOTS, 1);
		
		materials.put(Material.POTION, 1);
		materials.put(Material.STICK, 14);
		materials.put(Material.ARROW, 32);
		materials.put(Material.MUSHROOM_SOUP, 1);
		materials.put(Material.BOW, 1);
		materials.put(Material.WOOD, 17);
		materials.put(Material.COBBLESTONE, 48);
		
		mats = new ArrayList<Material>(materials.keySet());
		
		List<Material> armor = new ArrayList<Material>();
			armor.add(Material.LEATHER_HELMET);
			armor.add(Material.LEATHER_CHESTPLATE);
			armor.add(Material.LEATHER_LEGGINGS);
			armor.add(Material.LEATHER_BOOTS);
			
			armor.add(Material.CHAINMAIL_HELMET);
			armor.add(Material.CHAINMAIL_CHESTPLATE);
			armor.add(Material.CHAINMAIL_LEGGINGS);
			armor.add(Material.CHAINMAIL_BOOTS);
			
			armor.add(Material.IRON_HELMET);
			armor.add(Material.IRON_CHESTPLATE);
			armor.add(Material.IRON_LEGGINGS);
			armor.add(Material.IRON_BOOTS);
		
		List<Material> weapons = new ArrayList<Material>();
		weapons.add(Material.STONE_SWORD);
		weapons.add(Material.WOOD_SWORD);
		weapons.add(Material.STONE_AXE);
			
		Random rand = new Random();
		
		int chestSize = rand.nextInt(20);
		
		while(chestSize < 7)
		{
			chestSize = rand.nextInt(20);
		}
		
		ItemStack[] chest = new ItemStack[chestSize];
		
		for(int i = 0; i < chest.length; i++)
		{
			int randMat = rand.nextInt(materials.size() - 1);
			
			if(mats.get(randMat).getMaxStackSize() == 1)
			{
				chest[i] = new ItemStack(mats.get(randMat));
			}else if(mats.get(randMat) == Material.POTION)
			{
				int type = rand.nextInt(3);
				
				if(type == 0)
				{
					chest[i] = new ItemStack(Material.POTION, 1, (byte) 16385);
				}else if(type == 1)
				{
					chest[i] = new ItemStack(Material.POTION, 1, (byte) 16425);
				}else if(type == 2)
				{
					chest[i] = new ItemStack(Material.POTION, 1, (byte) 16386);
				}else if(type == 3)
				{
					chest[i] = new ItemStack(Material.POTION, 1, (byte) 16460);
				}
			}else if(armor.contains(mats.get(randMat)))
			{
				System.out.println("hi");
				
				if(doesChestContainMaterial(chest, mats.get(randMat)))
				{
					while(doesChestContainMaterial(chest, mats.get(randMat)))
					{
						randMat = rand.nextInt(materials.size() - 1);
					}
					
					chest[i] = new ItemStack(mats.get(randMat));
				}else
				{
					boolean enchanted = true;
					
					if(enchanted)
					{
						int level = rand.nextInt(2);
						
						while(level == 0)
							level = rand.nextInt(2);
						
						ItemStack a = new ItemStack(mats.get(randMat));
						ItemMeta am = a.getItemMeta();
						
						am.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, level, true);
						
						a.setItemMeta(am);
						
						chest[i] = a;
					}
				}
			}else if(weapons.contains(mats.get(randMat)))
			{
				boolean enchanted = true;
				
				if(enchanted)
				{
					int level = rand.nextInt(2);
					
					while(level == 0)
						level = rand.nextInt(2);
					
					ItemStack w = new ItemStack(mats.get(randMat));
					ItemMeta wm = w.getItemMeta();
					
					wm.addEnchant(Enchantment.DAMAGE_ALL, level, true);
					
					w.setItemMeta(wm);
					
					chest[i] = w;
				}else
				{
					chest[i] = new ItemStack(mats.get(randMat));
				}
			}else
			{
				int num = rand.nextInt(materials.get(mats.get(randMat)));
				if(num == 0) chest[i] = new ItemStack(Material.AIR);
				else chest[i] = new ItemStack(mats.get(randMat), num);
			}
		}
		
		return chest;
	}
	
	public boolean doesChestContainMaterial(ItemStack[] itemStack, Material material)
	{
		for(ItemStack i : itemStack)
		{
			if(i == new ItemStack(material)) return true;
		}
		
		return false;
	}
}