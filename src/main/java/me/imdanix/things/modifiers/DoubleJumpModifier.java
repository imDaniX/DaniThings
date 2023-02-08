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

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import me.imdanix.things.DaniThings;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static me.imdanix.things.utils.Utils.clr;

public class DoubleJumpModifier extends Modifier {
    private static final PotionEffect SPEED = new PotionEffect(PotionEffectType.SPEED, 40, 2);
    private static final PotionEffect JUMP = new PotionEffect(PotionEffectType.JUMP, 60, 3, true);
    private static final Vector Y_VECTOR = new Vector(0, 0.8, 0);
    private final Set<UUID> wearing;
    private String modifier;

    public DoubleJumpModifier() {
        super("double_jump");
        wearing = new HashSet<>();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(DaniThings.PLUGIN, () -> {
            Iterator<UUID> iter = wearing.iterator();
            while (iter.hasNext()) {
                Player player = Bukkit.getPlayer(iter.next());
                if (player == null) continue;
                ItemStack boots;
                if ((boots = player.getInventory().getBoots()) == null || modifierLine(boots) < 0) {
                    iter.remove();
                    continue;
                }
                player.addPotionEffect(JUMP);
            }
        }, 40, 40);
    }

    @EventHandler(ignoreCancelled = true)
    public void onWear(PlayerArmorChangeEvent event) {
        if (event.getSlotType() != PlayerArmorChangeEvent.SlotType.FEET || event.getNewItem() == null) return;
        if (modifierLine(event.getNewItem()) > -1)
            wearing.add(event.getPlayer().getUniqueId());
        else wearing.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(ignoreCancelled = true)
    public void onShift(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (!event.isSneaking() || !isEnabled() || !wearing.contains(player.getUniqueId())) return;
        if (!player.getLocation().subtract(0, 0.1, 0).getBlock().getType().isSolid() && checkDelay(player)) {
            player.setVelocity(player.getVelocity().add(Y_VECTOR));
            player.addPotionEffect(SPEED);
            player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 10, 0.5, 0.1, 0.5, 0.1);
        }
    }

    private boolean checkDelay(Player player) {
        if (player.hasMetadata("dt-jump-delay")) {
            if (System.currentTimeMillis() - player.getMetadata("dt-jump-delay").get(0).asLong() <= 2000)
                return false;
        }
        player.setMetadata("dt-jump-delay", new FixedMetadataValue(DaniThings.PLUGIN, System.currentTimeMillis()));
        return true;
    }

    @Override
    public void loadModifier(ConfigurationSection cfg) {
        modifier = clr(cfg.getString("modifier"));
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
        ItemMeta im = item.getItemMeta();
        List<String> lore = im.getLore();
        if (lore == null)
            lore = new ArrayList<>();
        lore.add(modifier);
        im.setLore(lore);
        item.setItemMeta(im);
    }
}
