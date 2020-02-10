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

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static me.imdanix.things.utils.Utils.*;

public class NoFallModifier extends Modifier implements Scalable {
	private boolean percentUsage;
	private EquipmentSlot slot;
	private String modifier, replacedModifier;

	public NoFallModifier() {
		super("no_fall");
	}

	@Override
	public void loadModifier(ConfigurationSection cfg) {
		this.modifier = clr(cfg.getString("modifier"));
		this.replacedModifier = modifier.replace("{value}", "%");
		this.slot = EquipmentSlot.valueOf(cfg.getString("slot", "FEET").toUpperCase());
		this.percentUsage = cfg.getBoolean("percent_usage");
	}

	@EventHandler
	public void onFall(EntityDamageEvent e) {
		if(!isEnabled()||e.getEntityType()!=EntityType.PLAYER||e.getCause()!=EntityDamageEvent.DamageCause.FALL)
			return;
		ItemStack item;
		PlayerInventory inv=((Player)e.getEntity()).getInventory();
		switch (slot) {
			case HEAD: item=inv.getHelmet(); break;
			case CHEST: item=inv.getChestplate(); break;
			case LEGS: item=inv.getLeggings(); break;
			case FEET: item=inv.getBoots(); break;
			case HAND: item=inv.getItemInMainHand(); break;
			case OFF_HAND: item=inv.getItemInOffHand(); break;
			default: item = null;
		}
		int line = containsModifier(item);
		if(line > -1) {
			if(percentUsage) {
				double value=100-getDouble(item.getItemMeta().getLore().get(line), true);
				if(value!=0)
					e.setDamage(e.getDamage()*(value/100));
			}
			e.setDamage(0);
		}
	}

	@Override
	public int containsModifier(ItemStack item) {
		if(item==null)
			return -1;
		ItemMeta meta=item.getItemMeta();
		if(meta==null||!meta.hasLore())
			return -1;
		if(percentUsage) {
			int line = 0;
			for(String lineStr:meta.getLore()) {
				if(lineStr.replaceAll(PERCENT_PATTERN.pattern(),"").equals(replacedModifier))
					return line;
				line++;
			}
		} else {
			for(String line:meta.getLore())
				if(line.equals(modifier))
					return 0;
		}
		return -1;
	}

	@Override
	public void setupItem(ItemStack is) {
		ItemMeta im=is.getItemMeta();
		List<String> lore=im.getLore();
		if(lore==null)
			lore=new ArrayList<>();
		lore.add(modifier.replace("{value}", "100%"));
		im.setLore(lore);
		is.setItemMeta(im);
	}

	@Override
	public void setupItem(ItemStack is, double percent) {
		ItemMeta im=is.getItemMeta();
		List<String> lore=im.getLore();
		if(lore==null)
			lore=new ArrayList<>();
		lore.add(modifier.replace("{value}", percent+"%"));
		im.setLore(lore);
		is.setItemMeta(im);
	}
}
