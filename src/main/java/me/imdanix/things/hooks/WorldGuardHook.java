package me.imdanix.things.hooks;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WorldGuardHook extends PluginHook {
	private WorldGuardPlugin wg;
	
	public WorldGuardHook() {
		super("WorldGuard");
		if(isEnabled())
			wg = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
	}
	
	public boolean canBuild(Player p, Location loc) {
		if(!isEnabled())
			return true;
		LocalPlayer player = WorldGuardPlugin.inst().wrapPlayer(p);
		if(wg==null|| WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(player, player.getWorld())) return true;
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionQuery query = container.createQuery();

		return query.testState(BukkitAdapter.adapt(loc), player, Flags.BUILD);
	}
}
