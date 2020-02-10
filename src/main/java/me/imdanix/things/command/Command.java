package me.imdanix.things.command;

import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

import static me.imdanix.things.utils.Utils.clr;

public abstract class Command {
	public static Map<String, Command> commands = new HashMap<>();
	private final String description;

	public Command(String id, String description) {
		commands.put(id, this);
		this.description=clr(description);
	}

	public String getDescription() {
		return description;
	}

	protected void failed(CommandSender sender, FailInfo info) {
		sender.sendMessage(info.toString());
		sender.sendMessage(description);
	}

	public abstract void execCommand(CommandSender s, String[] args);
}
