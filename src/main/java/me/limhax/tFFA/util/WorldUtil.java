// This file is part of TFFA
// Copyright (C) 2025 Limhax
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <http://www.gnu.org/licenses/>.

package me.limhax.tFFA.util;

import org.bukkit.World;

public class WorldUtil {
  public static World getWorld(String worldName) {
    for (World world : org.bukkit.Bukkit.getWorlds()) {
      if (world.getName().equalsIgnoreCase(worldName)) {
        return world;
      }
    }
    return null;
  }
}
