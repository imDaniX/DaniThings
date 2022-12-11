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

package me.imdanix.things.hooks;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import java.util.function.BiFunction;

public class PlaceholderAPIHook extends PluginHook {
    private final BiFunction<Player, String, String> replacer;

    public PlaceholderAPIHook() {
        super("PlaceholderAPI");
        replacer = isEnabled() ? PlaceholderAPI::setPlaceholders : (p, s) -> s;
    }

    public String setPlaceholders(Player p, String s) {
        return replacer.apply(p, s);
    }
}
