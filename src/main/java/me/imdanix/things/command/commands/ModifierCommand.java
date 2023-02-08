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
import me.imdanix.things.modifiers.Modifier;
import me.imdanix.things.modifiers.Scalable;
import me.imdanix.things.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static me.imdanix.things.utils.Utils.clr;
import static me.imdanix.things.utils.Utils.getDouble;

public class ModifierCommand extends Command {
    public ModifierCommand() {
        super("modifier", "&a/modifier <модификатор> [шанс] &7- Применить &eмодификатор&7 с определенным &eшансом&7&o(если возможно)&7 к предмету в руке.");
    }

    @Override
    public void execCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("danithings.command.modifier")) {
            failed(sender, FailInfo.NO_PERMISSION);
            return;
        }
        if (Utils.isConsole(sender)) {
            failed(sender, FailInfo.NO_CONSOLE);
            return;
        }
        if (args.length == 0) {
            sender.sendMessage(getDescription());
            sender.sendMessage(clr("&aСписок модификаторов:&f ") + String.join(", ", Modifier.modifiers.keySet()));
            return;
        }
        ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
        Modifier mod = Modifier.modifiers.get(args[0].toLowerCase());
        if (mod == null) {
            failed(sender, FailInfo.WRONG_ARG);
            return;
        }
        if (args.length >= 2 && mod instanceof Scalable scalable)
            scalable.setupItem(item, getDouble(args[1], false));
        else
            mod.setupItem(item);
    }
}
