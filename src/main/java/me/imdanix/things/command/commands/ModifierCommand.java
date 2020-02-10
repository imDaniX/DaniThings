package me.imdanix.things.command.commands;

import me.imdanix.things.utils.Utils;
import me.imdanix.things.command.Command;
import me.imdanix.things.command.FailInfo;
import me.imdanix.things.modifiers.Scalable;
import me.imdanix.things.modifiers.Modifier;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static me.imdanix.things.utils.Utils.clr;
import static me.imdanix.things.utils.Utils.getDouble;

public class ModifierCommand extends Command {
	public ModifierCommand() {
		super("modifier","&a/modifier <модификатор> [шанс] &7- Применить &eмодификатор&7 с определенным &eшансом&7&o(если возможно)&7 к предмету в руке.");
	}

	@Override
	public void execCommand(CommandSender s, String[] args) {
		if(!s.hasPermission("danithings.command.modifier")) {
			failed(s, FailInfo.NO_PERMISSION);
			return;
		}
		if(Utils.isConsole(s)) {
			failed(s, FailInfo.NO_CONSOLE);
			return;
		}
		if(args.length==0) {
			s.sendMessage(getDescription());
			s.sendMessage(clr("&aСписок модификаторов:&f ") + String.join(", ", Modifier.modifiers.keySet()));
			return;
		}
		ItemStack is=((Player)s).getInventory().getItemInMainHand();
		Modifier mod=Modifier.modifiers.get(args[0].toLowerCase());
		if(mod == null) {
			failed(s, FailInfo.WRONG_ARG);
			return;
		}
		if(args.length >= 2 && mod instanceof Scalable)
			((Scalable)mod).setupItem(is,getDouble(args[1], false));
		else
			mod.setupItem(is);
	}
}
