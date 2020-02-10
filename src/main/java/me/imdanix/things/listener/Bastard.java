package me.imdanix.things.listener;

import me.imdanix.things.configuration.ConfigurableListener;
import me.imdanix.things.events.PlayerDamageEntityEvent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.permissions.Permissible;

public class Bastard extends ConfigurableListener {
	private boolean enabled;
	private boolean ignoreCancelled;
	private double damageMult, weaknessMult;
	public Bastard() {
		super("things.bastard");
	}

	@Override
	public void load(ConfigurationSection cfg) {
		init(cfg.getBoolean("enabled"), cfg.getBoolean("ignore_cancelled"), cfg.getDouble("damage_multiplier"), cfg.getDouble("weakness_multiplier"));
	}

	private void init(boolean enabled, boolean ignoreCancelled, double damageMult, double weaknessMult) {
		this.enabled = enabled;
		this.ignoreCancelled = ignoreCancelled;
		this.damageMult = damageMult;
		this.weaknessMult = weaknessMult;
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onAttack(PlayerDamageEntityEvent e) {
		if(!enabled)
			return;
		if(e.isCancelled()) {
			if(!ignoreCancelled)
				return;
			e.setCancelled(false);
		}
		if(isBastard(e.getDamaged()))
			e.setDamage(e.getDamage()*damageMult); else
		if(isBastard(e.getPlayer()))
			e.setDamage(e.getDamage()*weaknessMult);
	}

	public static boolean isBastard(Permissible p) {
		return p instanceof Player &&
				p.hasPermission("danithings.bastard.player") &&
				!p.hasPermission("danithings.bastard.exempt");
	}

}
