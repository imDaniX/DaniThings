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
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.imdanix.things.utils.Utils.clr;
import static me.imdanix.things.utils.Utils.getDouble;

public class HpeditCommand extends Command {
	public HpeditCommand() {
		super("hpedit","&a/hpedit <игрок> <здоровье>&7 - Установить &eигроку&7 максимальное &eздоровье&7.");
	}

	public void execCommand(CommandSender s, String[] args) {
		if(!s.hasPermission("danithings.command.hpedit")) {
			failed(s, FailInfo.NO_PERMISSION);
			return;
		}
		if(args.length<2) {
			failed(s, FailInfo.WRONG_ARG);
			return;
		}
		double hp=getDouble(args[1], false);
		if(hp==0) {
			failed(s, FailInfo.WRONG_ARG);
			return;
		}
		for(Player pl:Bukkit.getOnlinePlayers()){
			if(pl.getName().equalsIgnoreCase(args[0])) {
				pl.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(hp);
				s.sendMessage(clr("&bHpEdit> &fВы установили макс.уровень здоровья &e"+pl.getName()+"&a на &e"+hp));
				return;
			}
		}
		failed(s, FailInfo.OFFLINE_PLAYER);
	}
}
