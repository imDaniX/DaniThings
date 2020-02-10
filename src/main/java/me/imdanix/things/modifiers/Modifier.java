package me.imdanix.things.modifiers;

import me.imdanix.things.configuration.ConfigurableListener;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public abstract class Modifier extends ConfigurableListener {

	private boolean enabled;
	public static Map<String, Modifier> modifiers=new HashMap<>();

	public Modifier(String id) {
		super("modifiers."+id);
		modifiers.put(id, this);
	}

	@Override
	public final void load(ConfigurationSection cfg) {
		this.enabled = cfg.getBoolean("enabled", false);
		loadModifier(cfg);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public abstract void loadModifier(ConfigurationSection cfg);

	// Does item contains skill?
	public abstract int containsModifier(ItemStack is);

	// Add modifier to the item
	abstract public void setupItem(ItemStack is);
}
