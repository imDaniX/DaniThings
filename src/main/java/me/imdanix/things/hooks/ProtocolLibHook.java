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

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

public class ProtocolLibHook extends PluginHook {
	private ProtocolManager protocolManager;
	
	public ProtocolLibHook() {
		super("ProtocolLib");
		if(isEnabled())
			protocolManager = ProtocolLibrary.getProtocolManager();
	}

	public void strikeThunderbolt(int distance, Location loc) {
		if(!isEnabled()) {
			loc.getWorld().strikeLightningEffect(loc);
			return;
		}
		Set<Player> players=new HashSet<>();
		for(Player p : loc.getWorld().getPlayers())
			if(p.getLocation().distanceSquared(loc) < distance)
				players.add(p);
		strikeThunderbolt(players,loc);
	}
	
	private void strikeThunderbolt(Set<Player> players, Location loc) {
		final PacketContainer lightning = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY_WEATHER);
		lightning.getDoubles()
			.write(0, loc.getX())
			.write(1, loc.getY())
			.write(2, loc.getZ());
		lightning.getIntegers()
			.write(0, 1)
			.write(1, 1);
		for(Player player:players) {
			try {
				player.playSound(loc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1, 1);
			    protocolManager.sendServerPacket(player, lightning);
			} catch (InvocationTargetException e) {
			    throw new RuntimeException(
			        "Cannot send packet " + lightning, e);
			}
		}
	}
}
