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

package me.imdanix.things.listener;

import me.imdanix.things.configuration.Configurable;
import me.imdanix.things.configuration.ConfigurableListener;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;

import java.util.HashMap;
import java.util.Map;

// TODO
public class BetterTNT extends ConfigurableListener {
    private final Map<String, TNTProfile> profiles;
    private TNTProfile defProfile;

    public BetterTNT() {
        super("things.better-tnt");
        profiles = new HashMap<>();
    }

    @Override
    public void load(ConfigurationSection cfg) {
        defProfile = TNTProfile.fromConfig(Configurable.section(cfg, "default"));
        for (String worldStr : cfg.getKeys(false)) {
            if (!worldStr.equals("default"))
                profiles.put(worldStr, TNTProfile.fromConfig(cfg.getConfigurationSection(worldStr)));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onExplodePrepare(ExplosionPrimeEvent event) {
        TNTProfile profile = profiles.getOrDefault(event.getEntity().getWorld().getName(), defProfile);
        if (profile.enable) {
            event.setRadius(event.getRadius() * profile.destroyModifier);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onExplode(EntityExplodeEvent event) {
        TNTProfile profile = profiles.getOrDefault(event.getEntity().getWorld().getName(), defProfile);
        if (profile.enable) {
            if (!profile.destroy || event.getEntity().getLocation().getY() > profile.height) {
                if (profile.chain)
                    event.blockList().removeIf(b -> b.getType() != Material.TNT);
                else
                    event.blockList().clear();
                return;
            }
            event.blockList().removeIf(b -> b.getY() > profile.height);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        TNTProfile profile = profiles.getOrDefault(event.getBlock().getWorld().getName(), defProfile);
        if (profile.enable) {
            if (!profile.allowBlocks || event.getBlock().getY() > profile.height) {
                if (profile.chain)
                    event.blockList().removeIf(b -> b.getType() != Material.TNT);
                else
                    event.blockList().clear();
                return;
            }
            event.blockList().removeIf(b -> b.getY() > profile.height);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        TNTProfile profile = profiles.getOrDefault(event.getEntity().getWorld().getName(), defProfile);
        if (profile.enable && (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION || event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) {
            if (profile.damage) {
                event.setDamage(event.getDamage() * profile.damageModifier);
            } else {
                event.setDamage(0);
            }
        }
    }

    private static class TNTProfile {
        private final boolean enable;

        private final boolean allowBlocks;

        private final boolean destroy;
        private final float destroyModifier;
        private final int height;
        private final boolean chain;

        private final boolean damage;
        private final double damageModifier;

        private TNTProfile(boolean enable, boolean allowBlocks, boolean destroy, float destroyModifier, int height, boolean chain, boolean damage, double damageModifier) {
            this.enable = enable;

            this.allowBlocks = allowBlocks;

            this.destroy = destroy;
            this.destroyModifier = destroyModifier;
            this.height = height;
            this.chain = chain;

            this.damage = damage;
            this.damageModifier = damageModifier;
        }

        public static TNTProfile fromConfig(ConfigurationSection cfg) {
            return new TNTProfile(
                    cfg.getBoolean("enable", false),
                    cfg.getBoolean("allow-blocks", true),
                    cfg.getBoolean("destroy", true),
                    (float) cfg.getDouble("destroy-modifier", 1),
                    cfg.getInt("destroy-height", 64),
                    cfg.getBoolean("allow-chain", true),
                    cfg.getBoolean("damage", true),
                    cfg.getDouble("damage-modifier", 1)
            );
        }
    }
}
