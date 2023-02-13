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
import me.imdanix.things.hooks.Hooks;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

import static me.imdanix.things.utils.Utils.clr;
import static me.imdanix.things.utils.Utils.isConsole;

public class StatsCommand extends ConfigurableCommand {
    private List<String> stats;

    public StatsCommand() {
        super("stats", "&a/stats [игрок] &7- Узнать статистику свою или &eигрока");
    }

    @Override
    public void load(ConfigurationSection cfg) {
        this.stats = clr(cfg.getStringList("list"));
    }

    @Override
    public void execCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("danithings.command.stats")) {
            sender.sendMessage(FailInfo.NO_PERMISSION.toString());
            return;
        }
        if (args.length == 0) {
            if (isConsole(sender)) {
                failed(sender, FailInfo.NO_CONSOLE);
                return;
            }
            Player p = (Player) sender;
            stats.forEach(l -> sender.sendMessage(Hooks.PAPI_HOOK.setPlaceholders(p, l)));
        } else {
            if (!sender.hasPermission("danithings.command.stats.other")) {
                failed(sender, FailInfo.NO_PERMISSION);
                return;
            }
            Player p = Bukkit.getPlayer(args[0]);
            if (p == null) {
                failed(sender, FailInfo.OFFLINE_PLAYER);
                return;
            }
            stats.forEach(l -> sender.sendMessage(clr(Hooks.PAPI_HOOK.setPlaceholders(p, l))));
        }
    }

}
