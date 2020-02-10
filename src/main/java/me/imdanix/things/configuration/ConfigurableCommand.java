package me.imdanix.things.configuration;

import me.imdanix.things.DaniPlugin;
import me.imdanix.things.command.Command;

public abstract class ConfigurableCommand extends Command implements Configurable {
	public ConfigurableCommand(String id, String description) {
		super(id, description);
		DaniPlugin.config.register("commands." + id, this);
	}
}
