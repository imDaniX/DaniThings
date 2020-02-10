package me.imdanix.things.modifiers.spawnerpicking;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.imdanix.things.utils.Utils.clr;

public class SpawnerSetting {
	public static Map<EntityType, SpawnerSetting> settings;
	private final EntityType type;
	private final boolean failing;
	private final String lore;

	public SpawnerSetting(EntityType type, String lore, boolean failing) {
		this.type=type;
		this.lore=lore;
		this.failing=failing;
	}

	public EntityType getType() {
		return type;
	}

	public String getLore() {
		return lore;
	}

	public ItemStack generateItem() {
		ItemStack is=new ItemStack(failing ? Material.COBBLESTONE : Material.SPAWNER);
		ItemMeta im=is.getItemMeta();
		im.setLore(Collections.singletonList(lore));
		is.setItemMeta(im);
		return is;
	}

	public static void setSpawner(Block bl, List<String> lore) {
		EntityType type=null;
		for(SpawnerSetting ss:settings.values()) {
			if(lore.contains(ss.getLore()))
				type=ss.getType();
		}
		if(type==null)
			return;
		BlockState bs = bl.getState();
		CreatureSpawner sp = (CreatureSpawner) bs;
		sp.setSpawnedType(type);
		bs.update();
	}

	public static ItemStack generateItem(EntityType type) {
		SpawnerSetting setting=settings.get(type);
		return setting==null?null:setting.generateItem();
	}

	public static void generateSettings(ConfigurationSection cfg) {
		settings=new HashMap<>();
		for(String mob:cfg.getKeys(false)) {
			EntityType type=EntityType.valueOf(mob.toUpperCase());
			String lore=clr(cfg.getString(mob+".lore"));
			boolean enabled=cfg.contains(mob+".failing");
			settings.put(type, new SpawnerSetting(type, lore, enabled));
		}
	}
}
