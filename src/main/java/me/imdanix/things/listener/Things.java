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

package me.imdanix.things.listener;

import me.imdanix.things.DaniThings;
import me.imdanix.things.configuration.ConfigurableListener;
import me.imdanix.things.events.PlayerDamageEntityEvent;
import me.imdanix.things.utils.Rnd;
import me.imdanix.things.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.Statistic;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class Things extends ConfigurableListener {
	private boolean ghostBlocks, teleportDupe, fireworkDamage, enderCrystalExplode, pvpStatistic,
			overEnchant, overEnchantStopper, overEnchantSilverfish, colorRename, shelvesDrop, minecartDrop,
			protectStands, protectFrames;

	public Things() {
		super("things");
	}

	@Override
	public void load(ConfigurationSection cfg) {
		ghostBlocks = cfg.getBoolean("ghost_blocks", false);
		teleportDupe = cfg.getBoolean("teleport_dupe", true);
		fireworkDamage = cfg.getBoolean("firework_damage", true);
		enderCrystalExplode = cfg.getBoolean("endercrystal_block_damage", false);
		pvpStatistic = cfg.getBoolean("pvp_statistic", true);
		overEnchant = cfg.getBoolean("over_enchant", true);
		overEnchantStopper = cfg.getBoolean("over_enchant_stopper", true);
		overEnchantSilverfish = cfg.getBoolean("over_enchant_silverfish", true);
		colorRename = cfg.getBoolean("disallow_colored_rename", false);
		shelvesDrop = cfg.getBoolean("shelves_drop", false);
		minecartDrop = cfg.getBoolean("minecart_drop", false);
		protectFrames = cfg.getBoolean("protect_frames", true);
		protectStands = cfg.getBoolean("protect_stands", true);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onStand(VehicleExitEvent event) {
		if(!minecartDrop) return;
		if(event.getExited().getType() == EntityType.PLAYER && event.getVehicle().getType() == EntityType.MINECART) {
			event.getVehicle().remove();
			((Player)event.getExited()).getInventory().addItem(new ItemStack(Material.MINECART));
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onBreak(BlockBreakEvent e) {
		if(e.isCancelled() || !shelvesDrop || e.getBlock().getType() != Material.BOOKSHELF || !e.isDropItems()) return;
		e.setDropItems(false);
		Location loc = e.getBlock().getLocation();
		loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.BOOKSHELF));
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent e) {
	    Block bl = e.getBlock();
	    if(ghostBlocks)
	    	Bukkit.getScheduler().runTaskLater(DaniThings.PLUGIN, () -> e.getPlayer().sendBlockChange(bl.getLocation(), bl.getBlockData()), 3);

		ItemStack is=e.getPlayer().getInventory().getItemInMainHand();
		if(overEnchantStopper && Utils.checkEnchant(is, Enchantment.DIG_SPEED, 5)) {
			if(Rnd.nextDouble() > 0.05) return;
			e.setDropItems(false);
			Location loc=e.getBlock().getLocation();
			if(overEnchantSilverfish && Rnd.nextDouble() > 0.9) loc.getWorld().spawn(loc, Silverfish.class);
			loc.getWorld().playSound(loc, Sound.ENTITY_GHAST_DEATH, SoundCategory.BLOCKS, 0.2f, 1);
			loc.getWorld().spawnParticle(Particle.SMOKE_LARGE, loc, 6, 0.3, 0.3, 0.3, 0.04);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onEntityTeleport(EntityTeleportEvent e) {
		if(!teleportDupe)
			return;
		Entity ent = e.getEntity();
		if(ent.getType() != EntityType.PLAYER && ent instanceof InventoryHolder &&
				ent instanceof LivingEntity && ((LivingEntity)ent).getHealth() > 4)
			e.setCancelled(true);
	}
	
	@EventHandler(ignoreCancelled = true, priority=EventPriority.HIGHEST)
	public void onEntityFight(EntityDamageByEntityEvent e) {
		if(!fireworkDamage)
			return;
		if(e.getDamager() instanceof Firework) {
			Entity ent=e.getEntity();
			if(!(ent instanceof Monster) || !((LivingEntity) ent).isGliding())
				e.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPrepareAnvil(PrepareAnvilEvent e) {
		ItemStack item1 = e.getInventory().getItem(0);
		ItemStack result = e.getResult();
		if(item1==null || result==null) return;
		
		if (!colorRename && item1.getItemMeta().hasDisplayName()) {
			String name=item1.getItemMeta().getDisplayName();
			if(name.startsWith("§")) {
				ItemMeta meta = result.getItemMeta(); meta.setDisplayName(name);
				result.setItemMeta(meta);
			}
		}

		ItemStack item2 = e.getInventory().getItem(1);
		if (overEnchant && item2 != null && item2.getType() == Material.ENCHANTED_BOOK) {
			EnchantmentStorageMeta book = (EnchantmentStorageMeta)item2.getItemMeta();
			if(book.hasLore() && book.getLore().contains("§7Неразрушимость")) {
				if(result.getType() == Material.AIR)
					result=item1.clone();
				ItemMeta temp = result.getItemMeta(); temp.setUnbreakable(!temp.isUnbreakable());
				result.setItemMeta(temp);
			}
			if(Utils.checkEnchant(book, Enchantment.DIG_SPEED, 5)) 
				result.addUnsafeEnchantment(Enchantment.DIG_SPEED, book.getStoredEnchantLevel(Enchantment.DIG_SPEED));
			if(Utils.checkEnchant(book, Enchantment.LOOT_BONUS_BLOCKS, 3))
				result.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, book.getStoredEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS));
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onPlayerDeath(PlayerDeathEvent e) {
		if(!pvpStatistic) return;
		Player p=e.getEntity(); Player killer=p.getKiller();
		if(killer!=null&&killer.getAddress().getHostName().equals(p.getAddress().getHostName())) {
			int kills=killer.getStatistic(Statistic.PLAYER_KILLS);
			killer.setStatistic(Statistic.PLAYER_KILLS, kills>1?kills-2:0);}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onEntityExplode(EntityExplodeEvent e) {
		if(!enderCrystalExplode) return;
		if(e.getEntityType()==EntityType.ENDER_CRYSTAL)
			e.blockList().clear();
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if(!protectStands) return;
		if(event.getEntityType() == EntityType.ARMOR_STAND) {
			EntityDamageEvent.DamageCause cause = event.getCause();
			if(cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION ||
					cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION ||
					cause == EntityDamageEvent.DamageCause.PROJECTILE)
				event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onHangingDestroy(HangingBreakByEntityEvent event) {
		if(!protectFrames) return;
		if(event.getRemover() == null || event.getRemover().getType() != EntityType.PLAYER)
			event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if(e.getDamager() instanceof Player && e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
			PlayerDamageEntityEvent pE = new PlayerDamageEntityEvent((Player)e.getDamager(), e.getEntity(), e.getDamage(), e.isCancelled());
			Bukkit.getPluginManager().callEvent(pE);
			e.setDamage(pE.getDamage());
			e.setCancelled(pE.isCancelled());
		}
	}
}
