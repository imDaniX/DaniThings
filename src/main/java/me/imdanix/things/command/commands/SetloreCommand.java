package me.imdanix.things.command.commands;

import me.imdanix.things.utils.Utils;
import me.imdanix.things.command.Command;
import me.imdanix.things.command.FailInfo;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class SetloreCommand extends Command {
	public SetloreCommand() {
		super("setlore", "&a/setlore <описание> &7- Изменить &eописание&7 предмета &o(%n для новой строки)");
	}

	public void execCommand(CommandSender s, String[] args) {
		if(!s.hasPermission("danithings.command.setlore")) {
			failed(s, FailInfo.NO_PERMISSION);
			return;
		}
		if(Utils.isConsole(s)) {
			failed(s, FailInfo.NO_CONSOLE);
			return;
		}
		if(args.length<1) {
			failed(s, FailInfo.WRONG_ARG);
			return;
		}
		List<String> lore=Arrays.asList(Utils.clr(String.join(" ", args)).split("%n"));
		ItemStack is=((Player)s).getInventory().getItemInMainHand();
		ItemMeta im=is.getItemMeta();
		im.setLore(lore);
		is.setItemMeta(im);
		s.sendMessage("§aОбновлено описание предмета:\n§7         ------§5§o\n"+String.join("\n§5§o", lore)+"\n§7         ------");
	}
}
