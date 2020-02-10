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
