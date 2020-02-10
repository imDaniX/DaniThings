package me.imdanix.things.modifiers.hottouch;

import me.imdanix.things.utils.Rnd;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class HotItem {
	public static Map<Material, HotItem> hotItems;
	private Material drop;
	private int[] amount;
	private boolean fortune;

	public HotItem(Material drop, int[] amount, boolean fortune) {
		this.drop=drop;
		this.amount=amount;
		this.fortune=fortune;
	}

	public boolean isFortuneAffects() {
		return fortune;
	}

	public ItemStack generateItem() {
		ItemStack is=new ItemStack(drop);
		is.setAmount(getAmount());
		return is;
	}

	public static HotItem getHotItem(Material block) {
		return hotItems.get(block);
	}

	public static void generateItems(ConfigurationSection cfg) {
		hotItems=new HashMap<>();
		for(String block : cfg.getKeys(false)) {
			Material type=Material.getMaterial(block.toUpperCase());
			if(type  == null || type==Material.AIR) continue;
			Material drop = Material.getMaterial(cfg.getString(block+".drop", "").toUpperCase());
			if(drop == null || drop==Material.AIR) continue;
			int[] amount={cfg.getInt(block+".min_amount"),cfg.getInt(block+".max_amount")};
			boolean fortune=cfg.contains(block+".fortune");
			hotItems.put(type, new HotItem(drop, amount, fortune));
		}
	}

	private int getAmount() {
		if(amount[0]-amount[1]<1)
			return 1;
		int result= Rnd.nextInt(amount[0], amount[1]);
		return result<1?1:result;
	}
}
