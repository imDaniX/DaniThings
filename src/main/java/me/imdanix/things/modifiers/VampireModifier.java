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
import me.imdanix.things.utils.Rnd;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static me.imdanix.things.utils.Utils.UNDEAD;
import static me.imdanix.things.utils.Utils.clr;

public class VampireModifier extends Modifier {

    private String modifier;
    private double chance, minDamage, heal;

    public VampireModifier() {
        super("vampire");
    }

    @Override
    public void loadModifier(ConfigurationSection cfg) {
        this.modifier = clr(cfg.getString("modifier"));
        this.chance = cfg.getDouble("chance");
        this.minDamage = cfg.getDouble("min_damage");
        this.heal = cfg.getDouble("heal");
    }

    @EventHandler(ignoreCancelled = true)
    public void onAttack(PlayerDamageEntityEvent e) {
        if (!isEnabled())
            return;
        Player p = e.getPlayer();
        if (UNDEAD.contains(e.getDamaged().getType()))
            return;
        if (modifierLine(p.getInventory().getItemInMainHand()) > -1 && Rnd.nextDouble() < chance && e.getDamage() >= minDamage) {
            double health = p.getHealth() + e.getDamage() * heal;
            p.setHealth(Math.min(health, p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
        }
    }

    @Override
    public int modifierLine(ItemStack item) {
        if (item == null)
            return -1;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasLore())
            return -1;
        for (String line : meta.getLore())
            if (line.equals(modifier))
                return 0;
        return -1;
    }

    @Override
    public void setupItem(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null)
            lore = new ArrayList<>();
        lore.add(modifier);
        meta.setLore(lore);
        item.setItemMeta(meta);
    }
}
