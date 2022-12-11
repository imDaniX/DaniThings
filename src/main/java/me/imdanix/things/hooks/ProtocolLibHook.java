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
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldAccessException;
import me.imdanix.things.DaniThings;
import me.imdanix.things.utils.Rnd;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class ProtocolLibHook extends PluginHook {
    private ProtocolManager protocol;
    private static final String FAKE_ERROR = ChatColor.YELLOW + "OpenGL Error:" + ChatColor.WHITE + " 1286 (Invalid framebuffer operation)";
    private PacketContainer demoMsg;

    public ProtocolLibHook() {
        super("ProtocolLib");
        if (isEnabled()) {
            protocol = ProtocolLibrary.getProtocolManager();
            demoMsg = protocol.createPacket(PacketType.Play.Server.GAME_STATE_CHANGE);
            try {
                demoMsg.getIntegers().write(0, 5);
            } catch (Exception e) {
                DaniThings.PLUGIN.getLogger().warning("Failed to write int value for the Crasher.");
            }
            hideSeed();
        }
    }

    private void hideSeed() {
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(DaniThings.PLUGIN, ListenerPriority.NORMAL, PacketType.Play.Server.LOGIN) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        PacketContainer packet = event.getPacket();
                        try {
                            packet.getLongs().write(ThreadLocalRandom.current().nextInt(Byte.MAX_VALUE), 0L);
                        } catch (FieldAccessException ignored) {
                        }
                        event.setPacket(packet);
                    }
                }
        );
    }

    public void crash(Player player) {
        if (isEnabled()) {
            _crash(player);
        } else {
            Bukkit.getScheduler().runTaskTimerAsynchronously(DaniThings.PLUGIN, () -> {
                for (int i = 0; i < 50 && player.isOnline(); i++)
                    player.sendMessage(FAKE_ERROR);
            }, 0, 1);
        }
    }

    private void _crash(Player player) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(DaniThings.PLUGIN, (task) -> {
            if (!player.isOnline()) {
                task.cancel();
                return;
            }
            player.sendMessage(FAKE_ERROR);
        }, 1, 1);
        final PacketContainer state = protocol.createPacket(PacketType.Play.Server.GAME_STATE_CHANGE);
        state.getIntegers()
                .write(0, 7);
        state.getFloat()
                .write(0, 25f);
        try {
            protocol.sendServerPacket(player, state);
        } catch (Exception ignore) {
        }
        Bukkit.getScheduler().runTaskTimerAsynchronously(DaniThings.PLUGIN, (task) -> {
            if (!player.isOnline()) {
                task.cancel();
                return;
            }
            state.getFloat()
                    .write(0, (float) Rnd.nextDouble(2, 10));
            try {
                protocol.sendServerPacket(player, state);
                protocol.sendServerPacket(player, demoMsg);
            } catch (Exception ignore) {
                task.cancel();
            }
        }, 20, 120);

    }

    public void strikeThunderbolt(int distance, Location loc) {
        if (!isEnabled()) {
            loc.getWorld().strikeLightningEffect(loc);
            return;
        }
        Set<Player> players = new HashSet<>();
        for (Player p : loc.getWorld().getPlayers())
            if (p.getLocation().distanceSquared(loc) < distance)
                players.add(p);
        _strikeThunderbolt(players, loc);
    }

    private void _strikeThunderbolt(Set<Player> players, Location loc) {
        final PacketContainer lightning = protocol.createPacket(PacketType.Play.Server.SPAWN_ENTITY_WEATHER);
        lightning.getDoubles()
                .write(0, loc.getX())
                .write(1, loc.getY())
                .write(2, loc.getZ());
        lightning.getIntegers()
                .write(0, 1)
                .write(1, 1);
        for (Player player : players) {
            try {
                player.playSound(loc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1, 1);
                protocol.sendServerPacket(player, lightning);
            } catch (InvocationTargetException ignore) {
            }
        }
    }
}
