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
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.math.BigDecimal;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public class EssentialsHook extends PluginHook {
    private final BiConsumer<Player, Location> teleporter;
    private final BiPredicate<Player, Double> cashier;
    private Trade trade;

    public EssentialsHook() {
        super("Essentials");
        if (isEnabled()) {
            Essentials ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
            trade = new Trade((BigDecimal) null, ess);
            teleporter = (p, l) -> {
                try {
                    ess.getUser(p).getTeleport().teleport(l, trade, PlayerTeleportEvent.TeleportCause.PLUGIN);
                } catch (Exception e) {
                    p.teleport(l);
                }
            };
            cashier = (p, m) -> {
                User user = ess.getUser(p);
                BigDecimal money = BigDecimal.valueOf(m);
                if (!user.canAfford(money)) return false;
                user.takeMoney(money);
                return true;
            };

        } else {
            teleporter = Player::teleport;
            cashier = (p, m) -> true;
        }
    }

    public void teleport(Player player, Location location) {
        teleporter.accept(player, location);
    }

    public boolean takeMoney(Player player, double money) {
        return cashier.test(player, money);
    }
}
