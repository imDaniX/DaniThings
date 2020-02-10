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

import me.imdanix.things.DaniPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public abstract class ConfigurableListener implements Configurable, Listener {
	public ConfigurableListener(String id) {
		Bukkit.getPluginManager().registerEvents(this, DaniPlugin.PLUGIN);
		DaniPlugin.config.register(id, this);
	}
}
