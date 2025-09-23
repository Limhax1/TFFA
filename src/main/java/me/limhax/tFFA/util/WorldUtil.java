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
