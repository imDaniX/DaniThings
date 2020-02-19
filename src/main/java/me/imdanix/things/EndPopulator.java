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

package me.imdanix.things;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

public class EndPopulator extends BlockPopulator {
    @Override
    public void populate(World world, Random random, Chunk source) {
        for(int x = 0; x <= 15; x++) for(int z = 0; z <= 15; z++) if(random.nextDouble() <= 0.1) {
            for(int y = world.getMaxHeight()-1; y > 1; y--) {
                switch(source.getBlock(x, y, z).getType()) {
                    case AIR: continue;
                    case END_STONE: source.getBlock(x, y+1, z).setType(Material.DEAD_BRAIN_CORAL);
                    default: y = 0;
                }
            }
        }
    }
}
