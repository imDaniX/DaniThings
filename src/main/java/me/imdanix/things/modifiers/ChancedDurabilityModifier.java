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

import me.imdanix.things.utils.Rnd;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static me.imdanix.things.utils.Utils.*;

public class ChancedDurabilityModifier extends Modifier implements Scalable {

	private String modifier, replacedModifier;

	public ChancedDurabilityModifier() {
		super("chanced_durability");
	}

	@Override
	public void loadModifier(ConfigurationSection cfg) {
		this.modifier=clr(cfg.getString("modifier"));
		this.replacedModifier=this.modifier.replace("{chance}", "%");
	}

	@EventHandler(ignoreCancelled = true)
	public void onItemDamage(PlayerItemDamageEvent e) {
		ItemStack item=e.getItem();
/*
		if(Utils.checkEnchant(item, Enchantment.DIG_SPEED, 6)&&Math.random()<0.045) {
			e.setDamage((int)(Math.random()*5));
		}
*/
		int line = containsModifier(item);
		if(line > -1) {
			double chance=getDouble(item.getItemMeta().getLore().get(line), true);
			if(chance==0)
				return;
			chance/=100;
			int finalDamage=0;
			for(int i=0; i<e.getDamage(); i++)
				if(Rnd.nextDouble()>chance)
					++finalDamage;
			e.setDamage(finalDamage);
		}
	}

	@Override
	public int containsModifier(ItemStack item) {
		if(item==null)
			return -1;
		ItemMeta meta=item.getItemMeta();
		if(meta==null||!meta.hasLore())
			return -1;
		int line = 0;
		for(String lineStr:meta.getLore()) {
			if(lineStr.replaceAll(PERCENT_PATTERN.pattern(),"").equals(replacedModifier))
				return line;
			line++;
		}
		return -1;
	}

	@Override
	public void setupItem(ItemStack is) {
		ItemMeta im=is.getItemMeta();
		List<String> lore=im.getLore();
		if(lore==null)
			lore=new ArrayList<>();
		lore.add(modifier.replace("{chance}", "100%"));
		im.setLore(lore);
		is.setItemMeta(im);
	}

	@Override
	public void setupItem(ItemStack is, double percent) {
		ItemMeta im=is.getItemMeta();
		List<String> lore=im.getLore();
		if(lore==null)
			lore=new ArrayList<>();
		lore.add(modifier.replace("{chance}", percent+"%"));
		im.setLore(lore);
		is.setItemMeta(im);
	}
}
