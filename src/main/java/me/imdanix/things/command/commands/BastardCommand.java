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

package me.imdanix.things.command.commands;

import me.imdanix.things.command.FailInfo;
import me.imdanix.things.configuration.ConfigurableCommand;
import me.imdanix.things.listener.Bastard;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

import static me.imdanix.things.utils.Utils.clr;
import static me.imdanix.things.utils.Utils.getDouble;

public class BastardCommand extends ConfigurableCommand {
    private List<String> commands, location;
    private String broadcast, broadcastReason;

    public BastardCommand() {
        super("bastard", "&a/bastard <игрок> &7- Посмотреть где находится игрок на данный момент.");
    }

    @Override
    public void load(ConfigurationSection cfg) {
        this.commands = cfg.getStringList("commands");
        this.location = clr(cfg.getStringList("location"));
        this.broadcast = clr(cfg.getString("broadcast"));
        this.broadcastReason = clr(cfg.getString("broadcast_reason"));
    }

    @Override
    public void execCommand(CommandSender s, String[] args) {
        if (!s.hasPermission("danithings.command.bastard")) {
            failed(s, FailInfo.NO_PERMISSION);
            return;
        }
        if (args.length < 1) {
            failed(s, FailInfo.WRONG_ARG);
            return;
        }
        if (args.length == 1) {
            if (!s.hasPermission("danithings.command.bastard.show")) {
                failed(s, FailInfo.NO_PERMISSION);
                return;
            }
            Player p = Bukkit.getPlayer(args[0]);
            if (p == null) {
                failed(s, FailInfo.OFFLINE_PLAYER);
                return;
            }
            if (!Bastard.isBastard(p)) {
                s.sendMessage(clr("&cОшибка>&f Данный игрок не является бастардом!"));
                s.sendMessage(getDescription());
                return;
            }
            location.forEach(s::sendMessage);
        } else {
            if (!s.hasPermission("danithings.command.bastard.set")) {
                failed(s, FailInfo.NO_PERMISSION);
                return;
            }
            double hours = getDouble(args[1], false);
            if (hours <= 0) {
                s.sendMessage(clr("&a/bastard <игрок> <часы> [причина] &7 - Выдать &eигроку&7 бастарда на некоторое &eвремя&f."));
                return;
            }
            Player p = Bukkit.getPlayer(args[0]);
            if (p == null)
                s.sendMessage(clr("&cПредупреждение>&f Данный игрок в настоящее время оффлайн."));
            final String name = p == null ? args[0] : p.getName();
            commands.forEach(c -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), c.replace("{player}", name).replace("{hours}", hours + "")));
            if (args.length == 2) {
                if (!broadcast.equals(""))
                    Bukkit.broadcastMessage(broadcast
                            .replace("{player}", name)
                            .replace("{hours}", hours + ""));
            } else {
                args[0] = "";
                args[1] = "";
                if (!broadcastReason.equals(""))
                    Bukkit.broadcastMessage(broadcastReason
                            .replace("{player}", name)
                            .replace("{hours}", hours + "")
                            .replace("{reason}", clr(String.join(" ", args).substring(2))));
            }
        }
    }
}
