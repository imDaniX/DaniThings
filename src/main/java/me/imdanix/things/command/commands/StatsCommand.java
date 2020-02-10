package me.imdanix.things.command.commands;

import me.imdanix.things.utils.Utils;
import me.imdanix.things.command.FailInfo;
import me.imdanix.things.configuration.ConfigurableCommand;
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
	public void execCommand(CommandSender s, String[] args) {
		if(!s.hasPermission("danithings.command.stats")) {
			s.sendMessage(FailInfo.NO_PERMISSION.toString());
			return;
		}
		if(args.length == 0) {
			if(isConsole(s)) {
				failed(s, FailInfo.NO_CONSOLE);
				return;
			}
			Player p = (Player) s;
			stats.forEach(l -> s.sendMessage(Utils.PAPI_HOOK.setPlaceholders(p, l)));
		} else {
			if(!s.hasPermission("danithings.command.stats.other")) {
				failed(s, FailInfo.NO_PERMISSION);
				return;
			}
			Player p = Bukkit.getPlayer(args[0]);
			if(p == null) {
				failed(s, FailInfo.OFFLINE_PLAYER);
				return;
			}
			stats.forEach(l->s.sendMessage(Utils.PAPI_HOOK.setPlaceholders(p,l)));
		}
	}

}
