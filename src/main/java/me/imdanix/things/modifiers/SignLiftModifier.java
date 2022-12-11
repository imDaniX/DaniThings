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

import me.imdanix.things.hooks.Hooks;
import me.imdanix.things.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static me.imdanix.things.utils.Utils.*;

public class SignLiftModifier extends Modifier {
    private String modifier;
    private double distanceSquared;
    private boolean allowBlocked;
    private Material drop;
    private Map<String, String> messages;

    public SignLiftModifier() {
        super("sign_lift");
    }

    public void loadModifier(ConfigurationSection cfg) {
        this.modifier = clr(cfg.getString("modifier"));
        this.distanceSquared = cfg.getDouble("distance");
        this.distanceSquared *= distanceSquared;
        this.allowBlocked = cfg.getBoolean("allow_blocked");
        this.drop = Material.getMaterial(cfg.getString("drop", "PAPER").toUpperCase());
        if (this.drop == null)
            this.drop = Material.PAPER;
        this.messages = Utils.getMessages(cfg.getConfigurationSection("messages"));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void liftCreation(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if (e.getItem() == null)
            return;
        Player p = e.getPlayer();
        ItemStack is = e.getItem();
        if (containsModifier(is) < 0)
            return;

        Block bl = e.getClickedBlock();
        if (!isSign(bl)) {
            p.sendMessage(messages.get("sign_only"));
            return;
        }
        if (!Hooks.WG_HOOK.canBuild(p, bl.getLocation())) {
            p.sendMessage(messages.get("wrong_region"));
            return;
        }

        ItemStack newIs = is.clone();
        newIs.setAmount(1);
        ItemMeta im = newIs.getItemMeta();
        List<String> lore = im.getLore();

        if (lore.size() == 1) {
            p.sendMessage(messages.get("next_click"));
            lore.add(clr("&7Y = " + bl.getY()));
            im.setLore(lore);
            newIs.setItemMeta(im);
            p.getInventory().addItem(newIs);
        } else {
            Sign sign1 = (Sign) bl.getState();
            int y = Integer.parseInt(lore.get(1).substring(6));
            Block bl2 = bl.getWorld().getBlockAt(bl.getX(), y, bl.getZ());
            if (!isSign(bl2))
                return;
            Sign sign2 = (Sign) bl2.getState();
            if (y > bl.getY()) {
                sign1.setLine(0, clr("&lЛифт"));
                sign1.setLine(2, clr("&lВверх &r(y " + y + ")"));
                sign2.setLine(0, clr("&lЛифт"));
                sign2.setLine(2, clr("&lВниз &r(y " + bl.getY() + ")"));
            } else {
                sign1.setLine(0, clr("&lЛифт"));
                sign1.setLine(2, clr("&lВниз &r(y " + y + ")"));
                sign2.setLine(0, clr("&lЛифт"));
                sign2.setLine(2, clr("&lВверх &r(y " + bl.getY() + ")"));
            }
            sign1.update();
            sign2.update();
            p.sendMessage(messages.get("created"));
        }
        removeOne(is);
        e.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void liftUsing(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if (isSign(e.getClickedBlock())) {
            Sign sign1 = (Sign) e.getClickedBlock().getState();
            if (!sign1.getLine(0).equals(clr("&lЛифт"))) return;
            int y = Integer.parseInt(sign1.getLine(2).replaceAll("\\D", ""));
            Block bl = sign1.getWorld().getBlockAt(sign1.getX(), y, sign1.getZ());
            if (isSign(bl)) {
                Player p = e.getPlayer();
                if (sign1.getLocation().distanceSquared(p.getEyeLocation()) > distanceSquared) {
                    p.sendMessage(messages.get("too_far"));
                    return;
                }
                Location loc = p.getLocation();
                Block check1 = loc.getWorld().getBlockAt(loc.getBlockX(), y, loc.getBlockZ());
                Block check2 = loc.getWorld().getBlockAt(loc.getBlockX(), y - 1, loc.getBlockZ());
                if (allowBlocked || (!isSolid(check1) || isSign(check1)) && (!isSolid(check2) || isSign(check2))) {
                    loc.setY(y - 1);
                    p.teleport(loc);
                    return;
                }
                p.sendMessage(messages.get("blocked"));
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void liftBreaking(BlockBreakEvent e) {
        Block bl = e.getBlock();
        if (isSign(bl)) {
            Sign sign1 = (Sign) bl.getState();
            if (!sign1.getLine(0).equals(clr("&lЛифт"))) return;
            int y = Integer.parseInt(sign1.getLine(2).replaceAll("\\D", ""));
            bl = sign1.getWorld().getBlockAt(sign1.getX(), y, sign1.getZ());
            if (isSign(bl)) {
                Sign sign2 = (Sign) bl.getState();
                sign1.setLine(0, "");
                sign2.setLine(0, "");
                sign1.setLine(2, "");
                sign2.setLine(2, "");
                sign1.update();
                sign2.update();
                Player p = e.getPlayer();
                ItemStack is = new ItemStack(drop);
                List<String> lore = new ArrayList<>();
                lore.add(modifier);
                ItemMeta im = is.getItemMeta();
                im.setLore(lore);
                is.setItemMeta(im);
                is.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                p.getInventory().addItem(is);
                p.sendMessage(messages.get("deleted"));
            }
        }
    }

    private boolean isSolid(Block b) {
        return b.getType().isSolid();
    }

    private boolean isSign(Block b) {
        return SIGN.contains(b.getType());
    }

    @Override
    public int containsModifier(ItemStack is) {
        if (is == null)
            return -1;
        ItemMeta im = is.getItemMeta();
        if (im == null || !im.hasLore())
            return -1;
        for (String line : im.getLore())
            if (line.equals(modifier))
                return 0;
        return -1;
    }

    @Override
    public void setupItem(ItemStack is) {
        ItemMeta im = is.getItemMeta();
        List<String> lore = im.getLore();
        if (lore == null)
            lore = new ArrayList<>();
        lore.add(modifier);
        im.setLore(lore);
        is.setItemMeta(im);
    }
}
