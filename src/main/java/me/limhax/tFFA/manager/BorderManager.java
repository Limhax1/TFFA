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

package me.limhax.tFFA.manager;

import lombok.Getter;
import lombok.Setter;
import me.limhax.tFFA.TFFA;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class BorderManager {

  private long lastVelocity;
  private static final double EXPAND = 0.15;
  private long velocityCooldown = 250;
  private static final double KNOCKBACK_STRENGTH = 0.2;
  private static final double MIN_Y_VELOCITY = 0.12;

  private final Map<UUID, BukkitRunnable> borderShrinkTasks = new HashMap<>();
  private boolean borderShrinkingEnabled;
  private int startAfterSeconds;
  private int shrinkDuration;
  private double finalSize;
  private int shrinkInterval;

  public void tick(Player player, Location to, Location from) {
    if (!TFFA.getInstance().getEvent().getPlayers().contains(player)) {
      return;
    }
    this.velocityCooldown = TFFA.getInstance().getConfigManager().getInt("border-velocity-cooldown");

    WorldBorder border = player.getWorld().getWorldBorder();

    if (isInsideSafeZone(to, border)) {
      return;
    }

    long now = System.currentTimeMillis();
    if (now - lastVelocity < velocityCooldown) {
      return;
    }
    this.lastVelocity = now;

    Vector knockback = calculateKnockback(to, border);
    player.setVelocity(knockback);
  }

  private boolean isInsideSafeZone(Location location, WorldBorder border) {
    Location center = border.getCenter();
    double safeZoneHalfSize = (border.getSize() / 2.0) - EXPAND;

    double dx = Math.abs(location.getX() - center.getX());
    double dz = Math.abs(location.getZ() - center.getZ());

    return dx <= safeZoneHalfSize && dz <= safeZoneHalfSize;
  }


  private Vector calculateKnockback(Location playerLocation, WorldBorder border) {
    Location center = border.getCenter();
    double safeZoneHalfSize = (border.getSize() / 2.0) - EXPAND;

    Vector offset = playerLocation.toVector().subtract(center.toVector());

    double dx = Math.abs(offset.getX()) - safeZoneHalfSize;
    double dz = Math.abs(offset.getZ()) - safeZoneHalfSize;

    Vector inward;

    if (dx > dz) {
      inward = new Vector(Math.signum(-offset.getX()), 0, 0);
    } else {
      inward = new Vector(0, 0, Math.signum(-offset.getZ()));
    }

    inward.normalize();
    if (inward.getY() < MIN_Y_VELOCITY) {
      inward.setY(MIN_Y_VELOCITY);
    }

    return inward.multiply(KNOCKBACK_STRENGTH);
  }

  public void scheduleBorderShrink(World world) {
    WorldBorder border = world.getWorldBorder();
    int delay = TFFA.getInstance().getConfigManager().getInt("border-shrink-delay");
    int duration = TFFA.getInstance().getConfigManager().getInt("border-shrink-time");
    int finalSize = TFFA.getInstance().getConfigManager().getInt("border-shrink-size");

    Bukkit.getScheduler().runTaskLater(TFFA.getInstance(), () -> {
      border.setSize(finalSize, duration);
    }, delay * 20L);
  }
}