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

package me.imdanix.things.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
	public static final Set<EntityType> UNDEAD = Utils.createSet(
			EntityType.ZOMBIE,EntityType.ZOMBIE_VILLAGER,EntityType.HUSK,EntityType.PIG_ZOMBIE, EntityType.DROWNED,
			EntityType.STRAY,EntityType.SKELETON,EntityType.WITHER_SKELETON,EntityType.WITHER,
			EntityType.ZOMBIE_HORSE,EntityType.SKELETON_HORSE);
	public static final Set<Material> SIGN = Utils.createSet((mat -> mat.isBlock() && mat.name().contains("SIGN")), Material.values());
	public static final Pattern PERCENT_PATTERN = Pattern.compile("(?<!§)\\d+(\\.\\d+)?(?=%)");
	public static final Pattern NUM_PATTERN = Pattern.compile("(?<!§)\\d+(\\.\\d+)?");
	public static final Set<Material> NON_SOLID = Utils.createSet(Material::isSolid, Material.values());

	public static String clr(String s){
		return s == null ? clr("&4Error") : ChatColor.translateAlternateColorCodes('&', s);
	}
	public static List<String> clr(List<String> list) {
		List<String> clred = new ArrayList<>();
		list.forEach(s -> clred.add(clr(s)));
		return clred;
	}
	
	public static boolean checkEnchant(EnchantmentStorageMeta book, Enchantment ench, int lvl) {
		return (book.hasStoredEnchant(ench)&&book.getStoredEnchantLevel(ench)>lvl);
	}
	
	public static boolean checkEnchant(ItemStack item, Enchantment ench, int lvl) {
		return (item.containsEnchantment(ench)&&item.getEnchantmentLevel(ench)>lvl);
	}

	public static Map<String, String> getMessages(ConfigurationSection cfg) {
		Map<String,String> messages = new HashMap<>();
		if(cfg != null)
			cfg.getKeys(false).forEach(s->messages.put(s, clr(cfg.getString(s))));
		return messages;
	}

	public static boolean isConsole(CommandSender s) {
		return Bukkit.getConsoleSender().equals(s);
	}

	public static Double getDouble(String s, boolean search) {
		try {
			if(search) {
				Matcher matcher = NUM_PATTERN.matcher(s);
				if(matcher.find())
					return Double.valueOf(matcher.group());
			}
			return Double.valueOf(s);
		} catch(NumberFormatException e) {
			return 0.0;
		}
	}

	public static Integer getInteger(String s, boolean search) {
		try {
			if(search) {
				Matcher matcher = NUM_PATTERN.matcher(s);
				if(matcher.find())
					return Integer.valueOf(matcher.group());
			}
			return Integer.valueOf(s);
		} catch(NumberFormatException e) {
			return 0;
		}
	}

	public static void removeOne(ItemStack is) {
		if(is.getAmount()==1) {
			is.setType(Material.AIR);
		} else {
			is.setAmount(is.getAmount()-1);
		}
	}

	public static ItemMeta setLoreLine(ItemMeta meta, int line, String str) {
		List<String> lore = meta.getLore();
		if(lore == null) lore = new ArrayList<>();
		if(lore.size() > line)
			lore.set(line, str);
		else
			lore.add(str);
		meta.setLore(lore);
		return meta;
	}

	public static Location locationFromString(String str) {
		String[] split = str.split(",");
		return new Location(Bukkit.getWorld(split[0]),
				Double.valueOf(split[1]), Double.valueOf(split[2]), Double.valueOf(split[3]),
				Float.valueOf(split[4]), Float.valueOf(split[5]));
	}

	public static String locationToString(Location loc) {
		return loc.getWorld().getName() + "," +
				loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," +
				loc.getYaw() + "," + loc.getPitch();
	}

	@SafeVarargs
	public static <T> Set<T> createSet(T... v) {
		return new HashSet<>(Arrays.asList(v));
	}

	@SafeVarargs
	public static <T> Set<T> createSet(Predicate<T> check, T... v) {
		Set<T> result = new HashSet<>();
		for(T t : v)
			if(check.test(t)) result.add(t);
		return result;
	}

	public static int limit(int a, int b, int limit) {
		int sum = a + b;
		return sum > limit ? limit : sum;
	}

	public static double limit(double a, double b, double limit) {
		double sum = a + b;
		return sum > limit ? limit : sum;
	}
}
