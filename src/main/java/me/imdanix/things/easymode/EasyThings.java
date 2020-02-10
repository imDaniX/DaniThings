package me.imdanix.things.easymode;

import me.imdanix.things.configuration.ConfigurableListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class EasyThings extends ConfigurableListener {
	private boolean shelvesDrop;

	public EasyThings() {
		super("easydrop");
	}

	@Override
	public void load(ConfigurationSection cfg) {
		shelvesDrop = cfg.getBoolean("shelves_drop", true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBreak(BlockBreakEvent e) {
		if(!shelvesDrop || e.getBlock().getType() != Material.BOOKSHELF) return;
		e.setDropItems(false);
		Location loc = e.getBlock().getLocation();
		loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.BOOKSHELF));
	}
}
