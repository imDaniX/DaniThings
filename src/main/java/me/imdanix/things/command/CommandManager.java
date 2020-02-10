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

package me.imdanix.things.command;

import me.imdanix.things.command.commands.BastardCommand;
import me.imdanix.things.command.commands.DanithingsCommand;
import me.imdanix.things.command.commands.GetuuidCommand;
import me.imdanix.things.command.commands.HpeditCommand;
import me.imdanix.things.command.commands.ModifierCommand;
import me.imdanix.things.command.commands.SetloreCommand;
import me.imdanix.things.command.commands.StatsCommand;
import me.imdanix.things.command.commands.SilentkickCommand;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandManager implements CommandExecutor {
	public CommandManager() {
		new GetuuidCommand();
		new HpeditCommand();
		new ModifierCommand();
		new SetloreCommand();
		new StatsCommand();
		new BastardCommand();
		new SilentkickCommand();
		new DanithingsCommand();
	}

	@Override
	public boolean onCommand(CommandSender s, org.bukkit.command.Command cmd, String label, String[] args) {
		Command sCmd = Command.commands.get(cmd.getName());
		if(sCmd!=null)
			sCmd.execCommand(s, args);
		return true;
	}
}
