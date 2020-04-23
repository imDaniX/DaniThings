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

package me.imdanix.things.modifiers.spawnerpicking;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.imdanix.things.utils.Utils.clr;

public class SpawnerSetting {
	private static Map<EntityType, SpawnerSetting> settings;
	private final EntityType type;
	private final boolean failing;
	private final String lore;

	public SpawnerSetting(EntityType type, String lore, boolean failing) {
		this.type=type;
		this.lore=lore;
		this.failing=failing;
	}

	public EntityType getType() {
		return type;
	}

	public String getLore() {
		return lore;
	}

	public ItemStack generateItem() {
		ItemStack is=new ItemStack(failing ? Material.COBBLESTONE : Material.SPAWNER);
		ItemMeta im=is.getItemMeta();
		im.setLore(Collections.singletonList(lore));
		is.setItemMeta(im);
		return is;
	}

	public static void setSpawner(Block block, List<String> lore) {
		for(SpawnerSetting setting : settings.values()) {
			if(lore.contains(setting.getLore())) {
				CreatureSpawner sp = (CreatureSpawner) block.getState();
				sp.setSpawnedType(setting.getType());
				sp.update();
			}
		}
	}

	public static ItemStack generateItem(EntityType type) {
		SpawnerSetting setting=settings.get(type);
		return setting==null?null:setting.generateItem();
	}

	public static void generateSettings(ConfigurationSection cfg) {
		settings=new HashMap<>();
		for(String mob:cfg.getKeys(false)) {
			EntityType type=EntityType.valueOf(mob.toUpperCase());
			String lore=clr(cfg.getString(mob+".lore"));
			boolean enabled=cfg.contains(mob+".failing");
			settings.put(type, new SpawnerSetting(type, lore, enabled));
		}
	}
}
