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

package me.imdanix.things.modifiers.hottouch;

import me.imdanix.things.events.PlayerDamageEntityEvent;
import me.imdanix.things.modifiers.Modifier;
import me.imdanix.things.modifiers.Scalable;
import me.imdanix.things.utils.Rnd;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.imdanix.things.utils.Utils.*;

public class HotTouchModifier extends Modifier implements Scalable {
    private final Map<Integer, Double> fortuneChances;
    private String modifier, replacedModifier;
    private int fireMobs;
    private boolean percentUsage;

    public HotTouchModifier() {
        super("hot_touch");
        fortuneChances = new HashMap<>();
    }

    @Override
    public void loadModifier(ConfigurationSection cfg) {
        this.modifier = clr(cfg.getString("modifier"));
        this.replacedModifier = this.modifier.replace("{chance}", "%");
        this.fireMobs = cfg.getInt("fire_mobs");
        this.percentUsage = cfg.getBoolean("percent_usage");
        HotItem.generateItems(cfg.getConfigurationSection("blocks"));
        calculateFortune(cfg.getInt("precalculate_fortune"), 1);
    }

    @EventHandler(ignoreCancelled = true)
    public void onAttack(PlayerDamageEntityEvent e) {
        if (!isEnabled() || fireMobs == 0 || e.getDamage() < 4)
            return;
        if (modifierLine(e.getPlayer().getInventory().getItemInMainHand()) > -1)
            e.getDamaged().setFireTicks(fireMobs);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent e) {
        if (!(isEnabled() && e.isDropItems()))
            return;
        Block block = e.getBlock();
        ItemStack is = e.getPlayer().getInventory().getItemInMainHand();
        int line = modifierLine(is);
        if (line > -1) {
            HotItem item = HotItem.getHotItem(block.getType());
            if (item != null) {
                if (percentUsage && getDouble(is.getItemMeta().getLore().get(line), true) < Rnd.nextDouble() * 100)
                    return;
                ItemStack drop = item.generateItem();
                e.setDropItems(false);
                if (item.isFortuneAffects())
                    drop.setAmount(fortunedAmount(drop.getAmount(), is.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS)));
                block.getWorld().dropItem(block.getLocation().add(0.5, 0.5, 0.5), drop);
            }
        }
    }

    @Override
    public int modifierLine(ItemStack item) {
        if (item == null)
            return -1;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasLore())
            return -1;
        if (percentUsage) {
            int line = 0;
            for (String lineStr : meta.getLore()) {
                if (lineStr.replaceAll(PERCENT_PATTERN.pattern(), "").equals(replacedModifier))
                    return line;
                line++;
            }
        } else {
            for (String line : meta.getLore())
                if (line.equals(modifier))
                    return 0;
        }
        return -1;
    }

    @Override
    public void setupItem(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null)
            lore = new ArrayList<>();
        lore.add(modifier.replace("{chance}", "100%"));
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    @Override
    public void setupItem(ItemStack item, double percent) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null)
            lore = new ArrayList<>();
        lore.add(modifier.replace("{chance}", percent + "%"));
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    /**
     * Get chance by level or calculate it
     *
     * @param lvl Level to check
     * @return Chance
     */
    private double getChance(int lvl) {
        Double chance = fortuneChances.get(lvl);
        if (chance == null || chance == 0) {
            calculateFortune(lvl, 1);
            chance = fortuneChances.get(lvl);
        }
        return chance;
    }

    /**
     * Precalculate chances for fortune
     *
     * @param max Where to stop
     * @param now Start of calculating(min 1)
     */
    private void calculateFortune(int max, int now) {
        for (int i = now; i <= max; i++) {
            double res = 1 / (i + 2) + (i + 1) / 2;
            fortuneChances.put(i, res);
        }
    }

    private int fortunedAmount(int amount, int lvl) {
        if (lvl == 0)
            return amount;
        double chance = getChance(lvl);
        for (; chance > 1; --chance)
            amount += amount;
        if (chance > 0)
            if (chance > Rnd.nextDouble())
                amount += amount;
        return amount > 0 ? amount : 1;
    }
}
