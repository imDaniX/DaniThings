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
