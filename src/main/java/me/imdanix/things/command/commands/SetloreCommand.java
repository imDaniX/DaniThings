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
import me.imdanix.things.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class SetloreCommand extends Command {
    public SetloreCommand() {
        super("setlore", "&a/setlore <описание> &7- Изменить &eописание&7 предмета &o(%n для новой строки)");
    }

    public void execCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("danithings.command.setlore")) {
            failed(sender, FailInfo.NO_PERMISSION);
            return;
        }
        if (Utils.isConsole(sender)) {
            failed(sender, FailInfo.NO_CONSOLE);
            return;
        }
        if (args.length < 1) {
            failed(sender, FailInfo.WRONG_ARG);
            return;
        }
        List<String> lore = Arrays.asList(Utils.clr(String.join(" ", args)).split("%n"));
        ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        meta.setLore(lore);
        item.setItemMeta(meta);
        sender.sendMessage("§aОбновлено описание предмета:\n§7         ------§5§o\n" + String.join("\n§5§o", lore) + "\n§7         ------");
    }
}
