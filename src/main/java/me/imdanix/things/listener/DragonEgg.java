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

import me.imdanix.things.DaniThings;
import me.imdanix.things.configuration.ConfigurableListener;
import me.imdanix.things.utils.Rnd;
import me.imdanix.things.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

public class DragonEgg extends ConfigurableListener {

    private double dragonEgg;
    private String eggSuccess, eggFail;

    public DragonEgg() {
        super("things.dragon_egg");
    }

    @Override
    public void load(ConfigurationSection cfg) {
        dragonEgg = cfg.getDouble("chance", 0.1);
        eggSuccess = Utils.clr(cfg.getString("success", "Success"));
        eggFail = Utils.clr(cfg.getString("fail", "Fail"));
    }

    @EventHandler(ignoreCancelled = true)
    public void onDragonKill(EntityDeathEvent event) {
        World world = event.getEntity().getWorld();
        if (event.getEntityType() == EntityType.ENDER_DRAGON && dragonEgg > 0) {
            Bukkit.getScheduler().runTaskLater(DaniThings.PLUGIN, () -> {
                if (Rnd.nextDouble() < dragonEgg) {
                    world.getBlockAt(0, 100, 0).setType(Material.DRAGON_EGG);
                    Bukkit.broadcastMessage(eggSuccess);
                } else {
                    world.getPlayers().forEach(player -> player.sendMessage(eggFail));
                }
            }, 200);
        }
    }
}
