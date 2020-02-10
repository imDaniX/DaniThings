package me.imdanix.things.configuration;

import org.bukkit.configuration.ConfigurationSection;

public interface Configurable {
	// Preload before init, also used for reload
	void load(ConfigurationSection cfg);
}
