package me.imdanix.things.modifiers.spawnerpicking;

import me.imdanix.things.modifiers.Modifier;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static me.imdanix.things.utils.Utils.clr;
import static me.imdanix.things.modifiers.spawnerpicking.SpawnerSetting.setSpawner;

public class SpawnerPickModifier extends Modifier {

	private String modifier;
	private boolean oneUse;

	public SpawnerPickModifier() {
		super("spawner_pick");
	}

	@Override
	public void loadModifier(ConfigurationSection cfg) {
		init(cfg.getString("modifier"), cfg.getBoolean("one_use"), cfg.getConfigurationSection("mobs"));
	}

	private void init(String modifier, boolean oneUse, ConfigurationSection mobs) {
		this.modifier=clr(modifier);
		this.oneUse=oneUse;
		SpawnerSetting.generateSettings(mobs);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Block b = e.getBlockPlaced();
		ItemStack item = e.getItemInHand();
		if(item.hasItemMeta()) {
			List<String> lore=item.getItemMeta().getLore();
			if(lore!=null)
				setSpawner(b, lore);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent e) {
		if(!this.isEnabled())
			return;
		Player p=e.getPlayer(); Block bl = e.getBlock();
		
		ItemStack is=p.getInventory().getItemInMainHand();
		
		if(bl.getType() == Material.SPAWNER && containsModifier(is) > -1){
			ItemMeta im=is.getItemMeta();
			int damage=1;
			if(oneUse) {
				List<String> lore=im.getLore();
				lore.remove(modifier);
				im.setLore(lore);
			} else damage=10;
			is.setItemMeta(im);
			if((is.getType().getMaxDurability()-damage)>0)
				is.setDurability((short) (is.getDurability()+damage));
			else
				p.getInventory().removeItem(is);
			CreatureSpawner cs = (CreatureSpawner) bl.getState();
			ItemStack drop = SpawnerSetting.generateItem(cs.getSpawnedType());
			if(drop!=null){
				e.setCancelled(true);
				bl.getWorld().dropItem(bl.getLocation().add(0.5, 0.5, 0.5), drop);
				bl.setType(Material.AIR);
			}
		}
	}

	@Override
	public int containsModifier(ItemStack is) {
		ItemMeta im=is.getItemMeta();
		if(im==null||!im.hasLore())
			return -1;
		for(String line:im.getLore())
			if(line.equals(modifier))
				return 0;
		return -1;
	}

	@Override
	public void setupItem(ItemStack is) {
		ItemMeta im=is.getItemMeta();
		List<String> lore=im.getLore();
		if(lore==null)
			lore=new ArrayList<>();
		lore.add(modifier);
		im.setLore(lore);
		is.setItemMeta(im);
	}
}
