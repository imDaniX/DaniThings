package me.imdanix.things.hooks;

import com.earth2me.essentials.Essentials;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import com.earth2me.essentials.Trade;
import java.math.BigDecimal;

import java.util.function.BiConsumer;

public class EssentialsHook extends PluginHook {
	private final BiConsumer<Player, Location> teleporter;
	private Trade trade;

	public EssentialsHook() {
		super("Essentials");
		if(isEnabled()) {
			Essentials ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
			trade = new Trade((BigDecimal)null, ess);
			teleporter = (p, l) -> {
				try{
					ess.getUser(p).getTeleport().teleport(l, trade, PlayerTeleportEvent.TeleportCause.PLUGIN);
				} catch (Exception e) {
					p.teleport(l);
				}
			};
		} else teleporter = Player::teleport;
	}

	public void teleport(Player player, Location location) {
		teleporter.accept(player, location);
	}
}
