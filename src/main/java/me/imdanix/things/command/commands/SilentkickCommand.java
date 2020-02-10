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
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SilentkickCommand extends Command {
	public SilentkickCommand() {
		super("silentkick", "&a/silentkick <игрок> [причина]&7- Кикнуть &eигрока &7без объявлений.");
	}

	@Override
	public void execCommand(CommandSender s, String[] args) {
		if(!s.hasPermission("danithings.command.silentkick")) {
			failed(s, FailInfo.NO_PERMISSION);
			return;
		}
		if(args.length == 0) {
			s.sendMessage(getDescription());
			return;
		}
		for(Player pl: Bukkit.getOnlinePlayers()) {
			if(pl.getName().equalsIgnoreCase(args[0])) {
				if(args.length > 1) {
					StringBuilder reason = new StringBuilder(args.length - 1);
					for (int i = 1; i < args.length; i++)
						reason.append(args[i]);
					pl.kickPlayer(Utils.clr(reason.toString()));
				} else pl.kickPlayer("");
			}
		}
		failed(s, FailInfo.OFFLINE_PLAYER);
	}
}
