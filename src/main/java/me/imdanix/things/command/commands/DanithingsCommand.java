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

import me.imdanix.things.DaniPlugin;
import me.imdanix.things.command.Command;
import me.imdanix.things.command.FailInfo;
import me.imdanix.things.configuration.SimpleConfiguration;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

import static me.imdanix.things.utils.Utils.clr;

public class DanithingsCommand extends Command {
	private final List<String> info;

	public DanithingsCommand() {
		super("danithings", "&a/danithings &7- Открыть список команд плагина.");
		List<String> info = new ArrayList<>();
		info.add(clr("&6DaniThings by imDaniX just for fun."));
		info.add(clr("&a/danithings reload &7- Перезагрузить файл конфигураций."));
		Command.commands.values().forEach(c->info.add(c.getDescription()));
		this.info=info;
	}

	@Override
	public void execCommand(CommandSender s, String[] args) {
		if(args.length==0 || !args[0].equalsIgnoreCase("reload"))
			info.forEach(s::sendMessage);
		else {
			if(!s.hasPermission("danithings.command.reload")) {
				failed(s, FailInfo.NO_PERMISSION);
				return;
			}
			DaniPlugin.config.reloadConfig();
			s.sendMessage(clr("&bDaniThings>&f Плагин был &aуспешно&f перезагружен."));
		}
	}
}
