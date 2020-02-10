package me.imdanix.things.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerDamageEntityEvent extends PlayerEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private boolean cancel;
	private final Entity damaged;
	private double damage;

	public PlayerDamageEntityEvent(final Player player, final Entity damaged, double damage, boolean cancel) {
		super(player);
		this.damaged=damaged;
		this.damage=damage;
		this.cancel=cancel;
	}

	public void setDamage(double damage) {
		this.damage=damage;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel=cancel;
	}

	public final Entity getDamaged() {
		return damaged;
	}

	public double getDamage() {
		return damage;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
