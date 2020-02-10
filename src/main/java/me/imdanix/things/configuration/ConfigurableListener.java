package me.imdanix.things.configuration;

import me.imdanix.things.DaniPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public abstract class ConfigurableListener implements Configurable, Listener {
	public ConfigurableListener(String id) {
		Bukkit.getPluginManager().registerEvents(this, DaniPlugin.PLUGIN);
		DaniPlugin.config.register(id, this);
	}
}
