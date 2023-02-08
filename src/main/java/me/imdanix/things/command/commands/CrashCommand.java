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

import me.imdanix.things.command.Command;
import me.imdanix.things.command.FailInfo;
import me.imdanix.things.hooks.Hooks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CrashCommand extends Command {
    private static final String FAKE_ERROR = ChatColor.YELLOW + "OpenGL Error:" + ChatColor.WHITE + " 1286 (Invalid framebuffer operation)";

    public CrashCommand() {
        super("crash", "&a/crash <игрок>&7- Крашнуть клиент игрока.");
    }

    @Override
    public void execCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("danithings.command.crash")) {
            failed(sender, FailInfo.NO_PERMISSION);
            return;
        }
        if (args.length < 1) {
            failed(sender, FailInfo.WRONG_ARG);
            return;
        }
        Player player = null;
        for (Player pl : Bukkit.getOnlinePlayers()) {
            if (pl.getName().equalsIgnoreCase(args[0])) {
                player = pl;
            }
        }
        if (player == null) {
            failed(sender, FailInfo.OFFLINE_PLAYER);
            return;
        }

        Hooks.PL_HOOK.crash(player);
    }
}
