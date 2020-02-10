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
