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

package me.imdanix.things.easymode;

import me.imdanix.things.configuration.ConfigurableListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class EasyThings extends ConfigurableListener {
	private boolean shelvesDrop;

	public EasyThings() {
		super("easydrop");
	}

	@Override
	public void load(ConfigurationSection cfg) {
		shelvesDrop = cfg.getBoolean("shelves_drop", true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBreak(BlockBreakEvent e) {
		if(!shelvesDrop || e.getBlock().getType() != Material.BOOKSHELF || !e.isDropItems()) return;
		e.setDropItems(false);
		Location loc = e.getBlock().getLocation();
		loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.BOOKSHELF));
	}
}
