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

package me.imdanix.things.hooks;

import com.earth2me.essentials.Essentials;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import com.earth2me.essentials.Trade;
import java.math.BigDecimal;

import java.util.function.BiConsumer;

public class EssentialsHook extends PluginHook {
	private final BiConsumer<Player, Location> teleporter;
	private Trade trade;

	public EssentialsHook() {
		super("Essentials");
		if(isEnabled()) {
			Essentials ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
			trade = new Trade((BigDecimal)null, ess);
			teleporter = (p, l) -> {
				try{
					ess.getUser(p).getTeleport().teleport(l, trade, PlayerTeleportEvent.TeleportCause.PLUGIN);
				} catch (Exception e) {
					p.teleport(l);
				}
			};
		} else teleporter = Player::teleport;
	}

	public void teleport(Player player, Location location) {
		teleporter.accept(player, location);
	}
}
