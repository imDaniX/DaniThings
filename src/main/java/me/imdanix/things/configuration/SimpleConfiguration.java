/*
 * Copyright (C) 2020 imDaniX
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.imdanix.things.configuration;

import me.imdanix.things.DaniThings;
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
		file = new File(DaniThings.PLUGIN.getDataFolder(), name + ".yml");
		file.getParentFile().mkdirs();
		if(!file.exists()) {
			if(resource)
				DaniThings.PLUGIN.saveResource(name + ".yml", false);
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
			DaniThings.PLUGIN.saveResource(file.getName() + ".yml", false);
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
