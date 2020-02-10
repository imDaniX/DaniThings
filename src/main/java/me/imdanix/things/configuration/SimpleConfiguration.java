package me.imdanix.things.configuration;

import me.imdanix.things.DaniPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

public class SimpleConfiguration {
	private final Map<String, Configurable> configurableObjects;
	private final File file;

	private YamlConfiguration yml;

	public SimpleConfiguration(String name) {
		this(name, true);
	}

	public SimpleConfiguration(String name, boolean resource) {
		configurableObjects = new HashMap<>();
		file = new File(DaniPlugin.PLUGIN.getDataFolder(), name + ".yml");
		file.getParentFile().mkdirs();
		if(!file.exists()) {
			if(resource)
				DaniPlugin.PLUGIN.saveResource(name + ".yml", false);
			else
				try {
					file.createNewFile();
				} catch (IOException ignored) {}
		}
		yml = YamlConfiguration.loadConfiguration(file);
	}

	public SimpleConfiguration(File file, boolean resource) {
		configurableObjects = new HashMap<>();
		this.file = file;
		this.file.getParentFile().mkdirs();
		if (resource && !this.file.exists())
			DaniPlugin.PLUGIN.saveResource(file.getName() + ".yml", false);
		yml = YamlConfiguration.loadConfiguration(file);
	}

	public YamlConfiguration getYml() {
		return yml;
	}

	public void saveConfig() {
		try {
			yml.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void reloadConfig() {
		try {
			yml = YamlConfiguration.loadConfiguration(file);
			loadConfigurables();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void register(String path, Configurable cfg) {
		configurableObjects.put(path, cfg);
	}

	public void loadConfigurables() {
		for(String id : configurableObjects.keySet()) {
			if(!yml.isConfigurationSection(id))
				yml.createSection(id);
			configurableObjects.get(id).load(yml.getConfigurationSection(id));
		}
	}

}
