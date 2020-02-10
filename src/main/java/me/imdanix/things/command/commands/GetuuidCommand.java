package me.imdanix.things.command.commands;

import me.imdanix.things.command.Command;
import me.imdanix.things.command.FailInfo;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static me.imdanix.things.utils.Utils.clr;
import static me.imdanix.things.utils.Utils.isConsole;

public class GetuuidCommand extends Command {
	public GetuuidCommand() {
		super("getuuid","&a/getuuid <игрок> &7- Получить UUID &eигрока&7.");
	}

	public void execCommand(CommandSender s, String[] args) {
		if(args.length!=1) {
			failed(s, FailInfo.WRONG_ARG);
			return;
		}
		if(!s.hasPermission("danithings.command.getuuid")) {
			failed(s, FailInfo.NO_PERMISSION);
			return;
		}
		String id=getUUID(args[0]).toString();
		if(isConsole(s))
			s.sendMessage(id);
		else {
			s.sendMessage(clr("&bGetUUID> &fНажмите &eшифт+лкм&f по надписи ниже, чтобы ввести её в чат"));
			BaseComponent text = new TextComponent(id);
			text.setInsertion(id);
			s.spigot().sendMessage(text);
		}
	}
	@SuppressWarnings("deprecation")
	private static UUID getUUID(String p) {
		Player player = Bukkit.getServer().getPlayer(p);
		if (player == null) {
			OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(p);
				return getUUID(offPlayer);
		} else return player.getUniqueId();
	}
	private static UUID getUUID(OfflinePlayer player) {
		if (Bukkit.getOnlineMode()) {
			return UUID.nameUUIDFromBytes(("OfflinePlayer:" + player.getName()).getBytes(StandardCharsets.UTF_8));
		}
		return player.getUniqueId();
	}
}
