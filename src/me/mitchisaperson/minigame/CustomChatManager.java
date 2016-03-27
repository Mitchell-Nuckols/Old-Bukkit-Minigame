package me.mitchisaperson.minigame;

import java.lang.reflect.Field;

import net.minecraft.server.v1_8_R1.BlockPosition;
import net.minecraft.server.v1_8_R1.ChatSerializer;
import net.minecraft.server.v1_8_R1.EnumPlayerInfoAction;
import net.minecraft.server.v1_8_R1.EnumTitleAction;
import net.minecraft.server.v1_8_R1.IChatBaseComponent;
import net.minecraft.server.v1_8_R1.PacketPlayOutBlockBreakAnimation;
import net.minecraft.server.v1_8_R1.PacketPlayOutChat;
import net.minecraft.server.v1_8_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R1.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_8_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R1.PacketPlayOutUpdateSign;
import net.minecraft.server.v1_8_R1.PlayerConnection;
import net.minecraft.server.v1_8_R1.World;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class CustomChatManager
{
	public void sendPlayerTabHeaderFooter(Player player, String headerJSON, String footerJSON)
	{
	    CraftPlayer craftplayer = (CraftPlayer) player;
	    PlayerConnection connection = craftplayer.getHandle().playerConnection;
		IChatBaseComponent footer = ChatSerializer.a(footerJSON);
	    IChatBaseComponent header = ChatSerializer.a(headerJSON);
	    PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();

	    try
	    {
	        Field headerField = packet.getClass().getDeclaredField("a");
	        headerField.setAccessible(true);
	        headerField.set(packet, header);
	        headerField.setAccessible(!headerField.isAccessible());
	        
	        Field footerField = packet.getClass().getDeclaredField("b");
	        footerField.setAccessible(true);
	        footerField.set(packet, footer);
	        footerField.setAccessible(!footerField.isAccessible());
	    }catch(Exception err)
	    {
	        err.printStackTrace();
	    }

	    connection.sendPacket(packet);
	}
	
	public void sendPlayerMessageJSON(Player player, String messageJSON)
	{
	    CraftPlayer craftplayer = (CraftPlayer) player;
	    PlayerConnection connection = craftplayer.getHandle().playerConnection;
	    IChatBaseComponent message = ChatSerializer.a(messageJSON);
	    PacketPlayOutChat packet = new PacketPlayOutChat(message);
	    
	    connection.sendPacket(packet);
	}
	
	public void sendPlayerActionBarMessage(Player player, String packetText)
	{
	    CraftPlayer craftplayer = (CraftPlayer) player;
	    PlayerConnection connection = craftplayer.getHandle().playerConnection;
	    IChatBaseComponent message = ChatSerializer.a(packetText);
	    PacketPlayOutChat packet = new PacketPlayOutChat(message, (byte) 2);
	    
	    connection.sendPacket(packet);
	}
	
	public void sendPlayerTitleSubtitle(Player player, String titleJSON, String subtitleJSON)
	{
	    CraftPlayer craftplayer = (CraftPlayer) player;
	    PlayerConnection connection = craftplayer.getHandle().playerConnection;
	    IChatBaseComponent titleMessage = ChatSerializer.a(titleJSON);
	    IChatBaseComponent subtitleMessage = ChatSerializer.a(subtitleJSON);
	    PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, titleMessage);
	    PacketPlayOutTitle subtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, subtitleMessage);
	    
	    connection.sendPacket(title);
	    connection.sendPacket(subtitle);
	}
	
	/*public String variableReplace(Player player, String entry)
	{
		String message = settings.getConfig().getString(entry);
		
		try
		{
			if(message.contains("@p"))
			{
				message = message.replace("@p", player.getName());
			}
			
			if(message.contains("@w"))
			{
				String world = player.getWorld().getName();
				world = world.replace("_", " ");
				message = message.replace("@w", world);
			}
		}catch(NullPointerException e)
		{
			e.printStackTrace();
		}

		return message;
	}*/
}