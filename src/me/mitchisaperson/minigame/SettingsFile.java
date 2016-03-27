package me.mitchisaperson.minigame;

import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

public class SettingsFile
{
	private SettingsFile() {}
	private static SettingsFile instance = new SettingsFile();
	
	public static SettingsFile getInstance()
	{
		return instance;
	}
	
	private Plugin plugin;
	private FileConfiguration config;
	private File cfile;
	
	public void setup(Plugin plugin)
	{
		config = plugin.getConfig();
		config.options().copyDefaults(true);
		cfile = new File(plugin.getDataFolder(), "arenas.yml");
		saveConfig();
	}
	
	public FileConfiguration getConfig()
	{
		return config;
	}
	
	public void saveConfig()
	{
		try
		{
			config.save(cfile);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void reloadConfig()
	{
		config = YamlConfiguration.loadConfiguration(cfile);
	}
	
	public PluginDescriptionFile getDescription()
	{
		return plugin.getDescription();
	}
}
