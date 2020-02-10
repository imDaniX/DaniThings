package me.imdanix.things.modifiers;

import me.imdanix.things.utils.Rnd;
import me.imdanix.things.events.PlayerDamageEntityEvent;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static me.imdanix.things.utils.Utils.UNDEAD;
import static me.imdanix.things.utils.Utils.clr;

public class VampireModifier extends Modifier {

	private String modifier;
	private double chance, minDamage, heal;

	public VampireModifier() {
		super("vampire");
	}

	@Override
	public void loadModifier(ConfigurationSection cfg) {
		init(cfg.getString("modifier"), cfg.getDouble("chance"), cfg.getDouble("min_damage"), cfg.getDouble("heal"));
	}

	private void init(String modifier, double chance, double minDamage, double heal) {
		this.modifier=clr(modifier);
		this.chance=chance;
		this.minDamage=minDamage;
		this.heal=heal;
	}

	@EventHandler
	public void onAttack(PlayerDamageEntityEvent e) {
		if(!isEnabled())
			return;
		Player p=e.getPlayer();
		if(UNDEAD.contains(e.getDamaged().getType()))
			return;
		if(containsModifier(p.getInventory().getItemInMainHand()) > -1 && Rnd.nextDouble()<chance&&e.getDamage()>=minDamage) {
			double health=p.getHealth()+e.getDamage()*heal;
			if(health>p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue())
				p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
			else
				p.setHealth(health);
		}
	}

	@Override
	public int containsModifier(ItemStack item) {
		if(item==null)
			return -1;
		ItemMeta meta=item.getItemMeta();
		if(meta==null||!meta.hasLore())
			return -1;
		for(String line:meta.getLore())
			if(line.equals(modifier))
				return 0;
		return -1;
	}

	@Override
	public void setupItem(ItemStack item) {
		ItemMeta meta=item.getItemMeta();
		List<String> lore=meta.getLore();
		if(lore==null)
			lore=new ArrayList<>();
		lore.add(modifier);
		meta.setLore(lore);
		item.setItemMeta(meta);
	}
}
