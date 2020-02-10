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
