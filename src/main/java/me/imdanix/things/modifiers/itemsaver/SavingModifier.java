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

package me.imdanix.things.modifiers.itemsaver;

import me.imdanix.things.modifiers.Modifier;
import me.imdanix.things.modifiers.Scalable;
import me.imdanix.things.utils.Rnd;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static me.imdanix.things.utils.Utils.*;

public class SavingModifier extends Modifier implements Scalable {

    private static final Map<UUID, Set<SavedItem>> players = new HashMap<>();
    private String modifier, replacedModifier;
    private boolean percentUsage;

    public SavingModifier() {
        super("item_saver");
    }

    @Override
    public void loadModifier(ConfigurationSection cfg) {
        init(cfg.getString("modifier"), cfg.getBoolean("percent_usage"));
    }

    private void init(String modifier, boolean percentUsage) {
        this.modifier = clr(modifier);
        this.replacedModifier = this.modifier.replace("{chance}", "%");
        this.percentUsage = percentUsage;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent e) {
        List<ItemStack> drops = e.getDrops();
        Player player = e.getEntity();
        Inventory inv = player.getInventory();
        Set<SavedItem> items = new HashSet<>();
        int slot = 0;
        for (ItemStack item : inv.getContents()) {
            int line = modifierLine(item);
            if (line > -1 && (!percentUsage || getInteger(item.getItemMeta().getLore().get(line), true) < Rnd.nextInt(100))) {
                items.add(new SavedItem(item, slot));
                drops.remove(item);
            }
            slot++;
        }
        if (items.isEmpty()) return;
        players.put(player.getUniqueId(), items);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        Set<SavedItem> items = players.remove(p.getUniqueId());
        if (items != null) {
            Inventory inv = p.getInventory();
            items.forEach(i -> inv.setItem(i.getSlot(), i));
        }
    }

    @Override
    public int modifierLine(ItemStack item) {
        if (item == null)
            return -1;
        ItemMeta im = item.getItemMeta();
        if (im == null || !im.hasLore())
            return -1;
        if (percentUsage) {
            int line = 0;
            for (String lineStr : im.getLore()) {
                if (lineStr.replaceAll(PERCENT_PATTERN.pattern(), "").equals(replacedModifier))
                    return line;
                line++;
            }
        } else {
            for (String line : im.getLore())
                if (line.equals(modifier))
                    return 0;
        }
        return -1;
    }

    @Override
    public void setupItem(ItemStack item) {
        ItemMeta im = item.getItemMeta();
        List<String> lore = im.getLore();
        if (lore == null)
            lore = new ArrayList<>();
        lore.add(modifier.replace("{chance}", "100%"));
        im.setLore(lore);
        item.setItemMeta(im);
    }

    @Override
    public void setupItem(ItemStack item, double percent) {
        ItemMeta im = item.getItemMeta();
        List<String> lore = im.getLore();
        if (lore == null)
            lore = new ArrayList<>();
        lore.add(modifier.replace("{chance}", percent + "%"));
        im.setLore(lore);
        item.setItemMeta(im);
    }
}
