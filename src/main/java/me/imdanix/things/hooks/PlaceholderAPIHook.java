package me.imdanix.things.hooks;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import java.util.function.BiFunction;

public class PlaceholderAPIHook extends PluginHook {
	private final BiFunction<Player, String, String> replacer;

	public PlaceholderAPIHook() {
		super("PlaceholderAPI");
		replacer = isEnabled() ? PlaceholderAPI::setPlaceholders : (p,s) -> s;
	}

	public String setPlaceholders(Player p, String s) {
		return replacer.apply(p, s);
	}
}
