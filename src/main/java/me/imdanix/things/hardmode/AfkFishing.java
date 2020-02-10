package me.imdanix.things.hardmode;

import me.imdanix.things.configuration.ConfigurableListener;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.Sign;
import org.bukkit.block.data.type.Switch;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.util.RayTraceResult;

import java.util.HashSet;
import java.util.Set;

public class AfkFishing extends ConfigurableListener {
	private boolean enabled;
	private Set<Material> badBlocks;
//	private String message;

	public AfkFishing() {
		super("hardmode.fishing.antiafk");
		badBlocks = new HashSet<>();
	}

	@Override
	public void load(ConfigurationSection cfg) {
		enabled = cfg.getBoolean("enabled");
//		message = Utils.clr(cfg.getString("message"));
		badBlocks.clear();
		for(String s : cfg.getStringList("bad_blocks")) {
			Material mat = Material.getMaterial(s.toUpperCase());
			if(mat == null) continue;
			badBlocks.add(mat);
		}

	}

	@EventHandler
	public void onFishing(PlayerFishEvent e) {
		if(!enabled || e.getState() != PlayerFishEvent.State.BITE) return;
		Player player = e.getPlayer();
		RayTraceResult ray = player.rayTraceBlocks(5);
		if(ray == null) return;
		Entity entity = ray.getHitEntity();
		if(entity != null && entity.getType() == EntityType.ITEM_FRAME) {
			e.setCancelled(true);
			return;
		}
		Block hit = ray.getHitBlock();
		if(hit == null) return;
		BlockData data = hit.getBlockData();
		if(badBlocks.contains(hit.getType()) ||
				data instanceof Openable || data instanceof Switch ||
				data instanceof WallSign || data instanceof Sign) {
			/*PlayerInventory inv = player.getInventory();
			ItemStack rod = inv.getItemInMainHand();
			if(rod.getType() == Material.FISHING_ROD) {
				inv.setItemInMainHand(null);
			} else {
				rod = inv.getItemInOffHand();
				inv.setItemInOffHand(null);
			}
			if(rod.getType() != Material.AIR) {
				player.getWorld().dropItem(player.getEyeLocation(), rod);
			}*/
			e.setCancelled(true);
		}
	}
}
