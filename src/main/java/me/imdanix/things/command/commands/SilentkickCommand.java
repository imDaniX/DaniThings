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
