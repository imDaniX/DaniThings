package me.imdanix.things.modifiers;

import me.imdanix.things.DaniPlugin;
import me.imdanix.things.configuration.SimpleConfiguration;
import me.imdanix.things.utils.UUIDDataType;
import me.imdanix.things.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.io.File;

public class TeleporterModifier extends Modifier implements Scalable {
	private final NamespacedKey SIZE = new NamespacedKey(DaniPlugin.PLUGIN, "teleporter-size");
	private final NamespacedKey TP_UUID = new NamespacedKey(DaniPlugin.PLUGIN, "teleporter-uuid");
	private final SimpleConfiguration locationsCfg;
	private final Map<UUID, Location> locations;
	private final ItemStack matter;
	private String usedModifier, modifier, unusedModifier;

	public TeleporterModifier() {
		super("teleporter");
		locationsCfg = new SimpleConfiguration("teleporter", false);
		locations = new HashMap<>();

		matter = new ItemStack(Material.PURPLE_DYE);
		matter.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		ItemMeta meta = matter.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		matter.setItemMeta(meta);

		File oldTeleporter = new File(DaniPlugin.PLUGIN.getDataFolder(), "old_teleporter.yml");
		if(oldTeleporter.exists())
			Bukkit.getPluginManager().registerEvents(new OldTeleporterListener(oldTeleporter), DaniPlugin.PLUGIN);
	}

	@Override
	public void loadModifier(ConfigurationSection cfg) {
		locations.clear();
		matter.setItemMeta(Utils.setLoreLine(matter.getItemMeta(), 0, Utils.clr(cfg.getString("matter"))));
		usedModifier = Utils.clr(cfg.getString("used", "Used"));
		modifier = Utils.clr(cfg.getString("modifier", "Active"));
		unusedModifier = Utils.clr(cfg.getString("unused", "Unused"));
		locationsCfg.reloadConfig();
		YamlConfiguration locsCfg = locationsCfg.getYml();
		for(String s : locsCfg.getKeys(false))
			locations.put(UUID.fromString(s), Utils.locationFromString(locsCfg.getString(s)));
	}

	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		if(!isEnabled() || !isRight(e.getAction())) return;
		if(e.getHand() != EquipmentSlot.HAND) return;
		ItemStack item = e.getItem();
		int line = containsModifier(item);
		if(line > -1) {
			Player player = e.getPlayer();
			ItemMeta meta = item.getItemMeta();
			PersistentDataContainer container = meta.getPersistentDataContainer();
			if(item.containsEnchantment(Enchantment.DURABILITY)) {										// Active
				int lvl = item.getEnchantmentLevel(Enchantment.DURABILITY);
				if(item.getEnchantmentLevel(Enchantment.DURABILITY) > 1) {
					item.addUnsafeEnchantment(Enchantment.DURABILITY, lvl - 1);
				} else {
					item.setItemMeta(Utils.setLoreLine(meta, line, usedModifier));
					item.removeEnchantment(Enchantment.DURABILITY);
				}
				Utils.ESS_HOOK.teleport(player, locations.get(container.get(TP_UUID, UUIDDataType.UUID)));
			} else if(container.has(TP_UUID, UUIDDataType.UUID)) {
				ItemStack off = player.getInventory().getItemInOffHand();								// Used
				if(player.isSneaking() && off.getType() == Material.COAL) {
					boolean found = false;
					for(ItemStack i : player.getInventory()) {
						if(matter.isSimilar(i)) {
							Utils.removeOne(i);
							found = true;
							break;
						}
					}
					if(!found) return;
					UUID id = container.get(TP_UUID, UUIDDataType.UUID);
					locations.remove(id);
					locationsCfg.getYml().set(id.toString(), null);
					container.remove(TP_UUID);
					item.setItemMeta(Utils.setLoreLine(meta, line, unusedModifier));
				} else if(matter.isSimilar(off)) {
					Utils.removeOne(off);
					item.setItemMeta(Utils.setLoreLine(meta, line, modifier));
					item.addUnsafeEnchantment(Enchantment.DURABILITY, container.get(SIZE, PersistentDataType.INTEGER));
				} else player.playSound(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1.0f, 0.5f);
			} else {																					// Unused
				boolean add = false;
				if(item.getAmount() > 1) {
					add = true;
					item.setAmount(item.getAmount() - 1);
					item = new ItemStack(item);
					item.setAmount(1);
				}
				UUID id = UUID.randomUUID();
				while(locations.containsKey(id)) id = UUID.randomUUID();
				container.set(TP_UUID, UUIDDataType.UUID, id);
				locations.put(id, player.getLocation());
				locationsCfg.getYml().set(id.toString(), Utils.locationToString(player.getLocation()));
				locationsCfg.saveConfig();
				item.setItemMeta(Utils.setLoreLine(meta, line, modifier));
				item.addUnsafeEnchantment(Enchantment.DURABILITY, container.get(SIZE, PersistentDataType.INTEGER));
				if(add) player.getInventory().addItem(item);
			}
		}
	}

	private static boolean isRight(Action act) {
		return act == Action.RIGHT_CLICK_AIR || act == Action.RIGHT_CLICK_BLOCK;
	}

	@Override
	public int containsModifier(ItemStack is) {
		if(is == null || is.getType() != Material.PAPER) return -1;
		ItemMeta meta = is.getItemMeta();
		if(is.getItemMeta().getPersistentDataContainer().has(SIZE, PersistentDataType.INTEGER)) {
			List<String> lore = meta.getLore();
			if(lore == null) return -1;
			int line = 0;
			for(String s : lore) {
				if(s.equals(modifier) || s.equals(unusedModifier) || s.equals(usedModifier)) return line;
				line++;
			}
		}
		return -1;
	}

	@Override
	public void setupItem(ItemStack is) {
		if(is == null || is.getType() == Material.AIR) return;
		ItemMeta meta = is.getItemMeta();
		Utils.setLoreLine(meta, 0, unusedModifier);
		meta.getPersistentDataContainer().set(SIZE, PersistentDataType.INTEGER, 10);
		is.setItemMeta(meta);
	}

	@Override
	public void setupItem(ItemStack is, double value) {
		if(is == null || is.getType() == Material.AIR) return;
		if(value < 1) {
			is.setType(matter.getType());
			is.setItemMeta(matter.getItemMeta());
			return;
		}
		ItemMeta meta = is.getItemMeta();
		Utils.setLoreLine(meta, 0, unusedModifier);
		meta.getPersistentDataContainer().set(SIZE, PersistentDataType.INTEGER, (int) value);
		is.setItemMeta(meta);
	}

	private class OldTeleporterListener implements Listener {
		private final Map<String, Location> oldTeleports;
		private final SimpleConfiguration oldTeleportsCfg;

		OldTeleporterListener(File cfg) {
			oldTeleportsCfg = new SimpleConfiguration(cfg, false);
			YamlConfiguration locsCfg = oldTeleportsCfg.getYml();
			oldTeleports = new HashMap<>();
			for(String id : locsCfg.getKeys(false))
				oldTeleports.put(id, Utils.locationFromString(locsCfg.getString(id)));
		}

		@EventHandler
		public void onClick(PlayerInteractEvent e) {
			if(!isEnabled() || !isRight(e.getAction())) return;
			if(e.getHand() != EquipmentSlot.HAND) return;
			ItemStack item = e.getItem();
			if(item == null || item.getType() != Material.PAPER) return;
			ItemMeta meta = item.getItemMeta();
			PersistentDataContainer container = meta.getPersistentDataContainer();
			if(container.has(TP_UUID, UUIDDataType.UUID) || !meta.hasLore()) return;
			String line = meta.getLore().get(0);
			if(line.equals("§7§oТелепорт не привязан")) {
				meta.setLore(Collections.singletonList(unusedModifier));
				container.set(SIZE, PersistentDataType.INTEGER, 10);
				item.setItemMeta(meta);
			} else {
				boolean off = (line.startsWith("§7§oТелепорт отключен"));
				String oldId = Utils.getInteger(line, true).toString();
				UUID id = UUID.nameUUIDFromBytes(("§7§oТелепорт ID" + oldId).getBytes());
				if(locations.containsKey(id)) {
					item.setType(Material.AIR);
					e.getPlayer().sendMessage("§c§lОшибка>§f Похоже, амулет с таким ID уже существует. " +
											"Скорее всего он был дюпнут. §eК сожалению, придется его удалить.");
					Bukkit.getLogger().info("Амулет телепортации ID"+oldId+" дюпнут ("+e.getPlayer().getName()+")");
				} else {
					meta.setLore(Collections.singletonList(off ? usedModifier : modifier));
					container.set(TP_UUID, UUIDDataType.UUID, id);
					container.set(SIZE, PersistentDataType.INTEGER, 10);
					item.setItemMeta(meta);
					if(!off) item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
					Location loc = oldTeleports.remove(oldId);
					if(loc == null) loc = Bukkit.getWorlds().get(0).getSpawnLocation();
					locations.put(id, loc);
					locationsCfg.getYml().set(id.toString(), Utils.locationToString(loc));
					locationsCfg.saveConfig();
					oldTeleportsCfg.getYml().set(oldId, null);
					oldTeleportsCfg.saveConfig();
				}
			}
			if(oldTeleports.size() < 1) HandlerList.unregisterAll(this);
		}
	}
}
