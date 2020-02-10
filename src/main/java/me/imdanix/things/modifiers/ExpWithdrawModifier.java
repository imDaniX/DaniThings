package me.imdanix.things.modifiers;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static me.imdanix.things.utils.Utils.clr;

public class ExpWithdrawModifier extends Modifier {

	private String modifier;
	private int minLevel;

	public ExpWithdrawModifier() {
		super("exp_withdraw");
	}

	@Override
	public void loadModifier(ConfigurationSection cfg) {
		this.modifier = clr(cfg.getString("modifier"));
		this.minLevel = cfg.getInt("min_level");
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if(!this.isEnabled())
			return;
		Player p=e.getPlayer();
		if(p.getLevel()>=minLevel&& containsModifier(e.getItem()) > -1) {
			int xpOnLevel=(int)(p.getExp()*p.getExpToLevel());
			if(xpOnLevel>10) p.giveExp(-10);
			else if(xpOnLevel==10) p.setExp(0);
			else {
				int num=10-xpOnLevel;
				p.setExp(0.0f);
				p.setLevel(p.getLevel()-1);
				float newXp=((float)(p.getExpToLevel()-num)/(float)p.getExpToLevel());
				p.setExp(newXp);}
			p.getWorld().spawnEntity(p.getLocation(), EntityType.THROWN_EXP_BOTTLE);
		}
	}

	@Override
	public int containsModifier(ItemStack is) {
		if(is==null)
			return -1;
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
