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

import me.imdanix.things.events.PlayerDamageEntityEvent;
import me.imdanix.things.hooks.Hooks;
import me.imdanix.things.utils.Rnd;
import me.imdanix.things.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static me.imdanix.things.utils.Utils.clr;

public class ThorModifier extends Modifier {
	private String modifierOn, modifierOff;
	private Map<String, String> messages;
	private int distanceSquared;
	private double minDamage, dealDamage;
	private boolean transform;
	private boolean damagePlayers, damageMonsters, damageAnimals, damageNPCs;


	public ThorModifier() {
		super("thor");
	}

	@Override
	public void loadModifier(ConfigurationSection cfg) {
		this.modifierOn=clr(cfg.getString("modifier.enabled"));
		this.modifierOff=clr(cfg.getString("modifier.disabled"));
		this.distanceSquared=cfg.getInt("distance"); this.distanceSquared *= distanceSquared;
		this.minDamage=cfg.getDouble("min_damage");
		this.dealDamage=cfg.getDouble("deal_damage");
		this.transform=cfg.getBoolean("transform");
		this.damagePlayers=cfg.getBoolean("damage.players");
		this.damageAnimals=cfg.getBoolean("damage.animals");
		this.damageMonsters=cfg.getBoolean("damage.monsters");
		this.damageNPCs=cfg.getBoolean("damage.npcs");
		this.messages=Utils.getMessages(cfg.getConfigurationSection("messages"));
	}

	@EventHandler
	public void onAttack(PlayerDamageEntityEvent e) {
		if(!isEnabled()||e.getDamage()<minDamage)
			return;
		Player damager=e.getPlayer();
		Entity ent=e.getDamaged();
		ItemStack item=damager.getInventory().getItemInMainHand();
		if(containsModifier(item)>-1&&isDamageable(ent)) {
			((Damageable)ent).damage(dealDamage);
			boolean searching=true;
			for(Entity near:ent.getNearbyEntities(2.5, 2.5, 2.5)) {
				if(searching&&damager == near)
					searching=false;
				else
					strikeEntity(near);
			}
			damager.damage(Rnd.nextDouble(2));
			this.strikeEntity(ent);
			Location loc=ent.getLocation();
			Block bl=loc.add(0, -1, 0).getBlock();
			if(!bl.isLiquid()&&bl.getType()!= Material.AIR&&loc.getBlock().getType()==Material.AIR)
				loc.getBlock().setType(Material.FIRE);
			if(Rnd.nextBoolean())
				ent.setFireTicks(Rnd.nextInt(120)+40);
			Hooks.PL_HOOK.strikeThunderbolt(distanceSquared, loc);
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Action act=e.getAction();
		if(!(act == Action.RIGHT_CLICK_AIR || act == Action.RIGHT_CLICK_BLOCK))
			return;
		ItemStack item=e.getItem();
		if(item!=null&&item.hasItemMeta()&&item.getItemMeta().hasLore()) {
			ItemMeta meta=item.getItemMeta();
			List<String> lore=meta.getLore();
			if(lore.contains(modifierOn)) {
				Collections.replaceAll(lore, modifierOn, modifierOff);
				meta.setLore(lore);
				item.setItemMeta(meta);
				e.getPlayer().sendMessage(messages.get("disabled"));
			} else
			if(lore.contains(modifierOff)) {
				Collections.replaceAll(lore, modifierOff, modifierOn);
				meta.setLore(lore);
				item.setItemMeta(meta);
				e.getPlayer().sendMessage(messages.get("enabled"));
			}
		}
	}

	private void strikeEntity(Entity ent) {
		if(!(ent instanceof Damageable)) return;
		if(transform)
			switch(ent.getType()) {
				case ZOMBIFIED_PIGLIN:
					return;
				case PIG: {
					ent.getWorld().spawnEntity(ent.getLocation(), EntityType.ZOMBIFIED_PIGLIN);
					ent.remove();
					return;
				}
				case VILLAGER: {
					ent.getWorld().spawnEntity(ent.getLocation(), EntityType.WITCH);
					ent.remove();
					return;
				}
				case CREEPER:
					((Creeper)ent).setPowered(true);
			}
		((Damageable)ent).damage(Rnd.nextDouble(6));
	}

	private boolean isDamageable(Entity ent) {
		return (damageMonsters&&ent instanceof Monster)||(damageAnimals&&ent instanceof Animals)||(damagePlayers&&ent instanceof Player)||(damageNPCs&&ent instanceof NPC);
	}

	@Override
	public int containsModifier(ItemStack is) {
		if(is==null)
			return -1;
		ItemMeta im=is.getItemMeta();
		if(im==null||!im.hasLore())
			return -1;
		for(String line:im.getLore())
			if(line.equals(modifierOn))
				return 0;
		return -1;
	}

	@Override
	public void setupItem(ItemStack is) {
		ItemMeta im=is.getItemMeta();
		List<String> lore=im.getLore();
		if(lore==null)
			lore=new ArrayList<>();
		lore.add(modifierOn);
		im.setLore(lore);
		is.setItemMeta(im);
	}
}
