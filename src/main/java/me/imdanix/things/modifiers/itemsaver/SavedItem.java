package me.imdanix.things.modifiers.itemsaver;

import org.bukkit.inventory.ItemStack;

public class SavedItem extends ItemStack {
	private final int slot;
	
	public SavedItem(ItemStack item, int slot) {
		super(item);
		this.slot=slot;
	}
	
	public int getSlot() {
		return slot;
	}
}