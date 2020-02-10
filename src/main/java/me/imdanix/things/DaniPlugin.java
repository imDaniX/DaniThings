/*
 * Copyright (C) 2020 imDaniX
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.imdanix.things;

import me.imdanix.things.command.CommandManager;
import me.imdanix.things.configuration.SimpleConfiguration;
import me.imdanix.things.easymode.EasyThings;
import me.imdanix.things.hardmode.AfkFishing;
import me.imdanix.things.hardmode.HardElytras;
import me.imdanix.things.listener.Bastard;
import me.imdanix.things.listener.Things;
import me.imdanix.things.modifiers.ChancedDurabilityModifier;
import me.imdanix.things.modifiers.ExpWithdrawModifier;
import me.imdanix.things.modifiers.NoFallModifier;
import me.imdanix.things.modifiers.SignLiftModifier;
import me.imdanix.things.modifiers.TeleporterModifier;
import me.imdanix.things.modifiers.ThorModifier;
import me.imdanix.things.modifiers.VampireModifier;
import me.imdanix.things.modifiers.hottouch.HotTouchModifier;
import me.imdanix.things.modifiers.itemsaver.SavingModifier;
import me.imdanix.things.modifiers.spawnerpicking.SpawnerPickModifier;
import org.bukkit.plugin.java.JavaPlugin;

public final class DaniPlugin extends JavaPlugin {
	public static JavaPlugin PLUGIN;
	public static SimpleConfiguration config;

	@Override
	public void onEnable() {
		DaniPlugin.PLUGIN = this;
		config = new SimpleConfiguration("config");

		CommandManager cmd = new CommandManager();
		getCommand("danithings").setExecutor(cmd);
		getCommand("hpedit").setExecutor(cmd);
		getCommand("modifier").setExecutor(cmd);
		getCommand("setlore").setExecutor(cmd);
		getCommand("getuuid").setExecutor(cmd);
		getCommand("stats").setExecutor(cmd);
		getCommand("bastard").setExecutor(cmd);
		getCommand("silentkick").setExecutor(cmd);

		new Bastard();
		new Things();

		new EasyThings();

		new AfkFishing();
		new HardElytras();

		new SavingModifier();
		new SpawnerPickModifier();
		new ChancedDurabilityModifier();
		new ExpWithdrawModifier();
		new SignLiftModifier();
		new ThorModifier();
		new VampireModifier();
		new HotTouchModifier();
		new NoFallModifier();
		new TeleporterModifier();

		config.loadConfigurables();
	}
}
