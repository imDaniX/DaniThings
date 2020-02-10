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

package me.imdanix.things.hardmode;

import me.imdanix.things.configuration.ConfigurableListener;
import me.imdanix.things.utils.Rnd;
import me.imdanix.things.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HardElytras extends ConfigurableListener {
	private final Map<UUID, Long> users = new HashMap<>();
	private double chance;
	private long cooldown;
	private boolean enabled;
	private final Map<String, String> messages;

	public HardElytras() {
		super("hardmode.elytras");
		messages = new HashMap<>();
	}

	@Override
	public void load(ConfigurationSection cfg) {
		enabled = cfg.getBoolean("enabled");
		chance = cfg.getDouble("chance");
		cooldown = cfg.getLong("cooldown");
		messages.put("failed_flint", Utils.clr(cfg.getString("messages.failed_flint", "")));
		messages.put("failed_chance", Utils.clr(cfg.getString("messages.failed_chance", "")));
		messages.put("failed_cooldown", Utils.clr(cfg.getString("messages.failed_cooldown", "")));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onFirework(PlayerInteractEvent e) {
		if(!enabled) return;
		Player player = e.getPlayer();
		if((e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_AIR) || !player.isGliding())
			return;

		ItemStack firework = e.getItem();
		ItemStack flint = e.getHand() == EquipmentSlot.HAND ?
				player.getInventory().getItemInOffHand() : player.getInventory().getItemInMainHand();
		if(firework == null) {
			// Already failed
			if(flint.getType() == Material.FIREWORK_ROCKET) {
				e.setCancelled(true);
				player.sendMessage(messages.get("failed_flint"));
			}
			return;
		} else {
			if(firework.getType() == Material.FIREWORK_ROCKET) {
				if(flint.getType() != Material.FLINT_AND_STEEL) {
					e.setCancelled(true);
					player.sendMessage(messages.get("failed_flint"));
					return;
				}
			} else {
				if(flint.getType() == Material.FIREWORK_ROCKET) {
					firework = flint;
					flint = e.getItem();
				} else return;
				if(flint.getType() != Material.FLINT_AND_STEEL) {
					e.setCancelled(true);
					player.sendMessage(messages.get("failed_flint"));
					return;
				}
			}
		}

		long cooldown = tryLaunch(player.getUniqueId());
		if(cooldown == 0) {
			PlayerItemDamageEvent event = new PlayerItemDamageEvent(player, flint, 1);
			Bukkit.getPluginManager().callEvent(event);
			if(chance > Rnd.nextDouble()) {
				e.setCancelled(true);
				player.sendMessage(messages.get("failed_chance"));
				Firework fireworkEnt = player.getWorld().spawn(player.getLocation(), Firework.class);
				FireworkMeta fireMeta = (FireworkMeta) firework.getItemMeta();
				fireMeta.setPower(0);
				fireworkEnt.setFireworkMeta(fireMeta);
				Utils.removeOne(firework);
			}
		} else {
			e.setCancelled(true);
			player.sendMessage(messages.get("failed_cooldown").replace("{cooldown}", Long.toString((this.cooldown - cooldown)/1000)));
		}
	}

	private long tryLaunch(UUID id) {
		Long timestamp = users.get(id);
		if(timestamp != null && timestamp != 0) {
			long cooldown = System.currentTimeMillis() - timestamp;
			if(cooldown < this.cooldown)
				return cooldown;
		}
		users.put(id, System.currentTimeMillis());
		return 0;
	}
}
