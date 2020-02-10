package me.imdanix.things.hooks;

import org.bukkit.Bukkit;

public class PluginHook {
	private final boolean enabled;
	public PluginHook(String plugin) {
		this.enabled = Bukkit.getPluginManager().isPluginEnabled(plugin);
	}
	public boolean isEnabled() {
		return enabled;
	}
}
